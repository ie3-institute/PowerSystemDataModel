/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.util.quantities.PowerSystemUnits.KELVIN_METRE_PER_WATT
import static tech.units.indriya.unit.Units.METRE

import edu.ie3.datamodel.exceptions.FailedValidationException
import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.source.csv.CsvDataSource
import edu.ie3.datamodel.utils.validation.ValidationUtils
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path

class SoilDataSourceSpec extends Specification {
  static final UUID LAYER_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
  static final UUID TYPE_UUID = UUID.fromString("32b43a78-7721-431d-b1c2-56975a123670")
  static final String POLYGON_JSON = '{"type":"Polygon","coordinates":[[[0,0],[1,0],[1,1],[0,1],[0,0]]]}'
  static final Geometry GEOMETRY = new GeometryFactory().createPolygon(
  ([
    new Coordinate(0d, 0d),
    new Coordinate(1d, 0d),
    new Coordinate(1d, 1d),
    new Coordinate(0d, 1d),
    new Coordinate(0d, 0d)
  ] as Coordinate[]))

  @TempDir
  Path tempDir

  private SoilDataSource soilSource() {
    return new SoilDataSource(new CsvDataSource(",", tempDir, new FileNamingStrategy()))
  }

  def "SoilDataSource reads soil types and layers"() {
    given:
    writeSoilTypesFile()
    writeSoilLayersFile()
    def source = soilSource()

    when:
    def types = source.getSoilTypes()
    def layers = source.getSoilLayers()

    then:
    types.size() == 1
    types[TYPE_UUID].id == "loam"
    Math.abs(types[TYPE_UUID].thermalResistivityWet.to(KELVIN_METRE_PER_WATT).value.doubleValue() - 0.30d) < 1e-12
    layers.size() == 1
    layers[LAYER_UUID].geometry.equals(GEOMETRY)
    Math.abs(layers[LAYER_UUID].thickness().to(METRE).value.doubleValue() - 2.0d) < 1e-12
  }

  def "SoilDataSource reports malformed soil rows"() {
    given:
    Files.writeString(tempDir.resolve("soil_type.csv"), "uuid,id\ninvalid,bad\n")
    Files.writeString(
        tempDir.resolve("soil_layer.csv"),
        "uuid,geometry,z_from,z_to,soil_type\ninvalid,not-json,0,-1,invalid\n")
    def source = soilSource()

    when:
    source.getSoilTypes()
    then:
    thrown(SourceException)

    when:
    source.getSoilLayers()
    then:
    thrown(SourceException)
  }

  def "SoilDataSource reports a source error that references the soil type entity"() {
    given:
    Files.writeString(
        tempDir.resolve("soil_type.csv"),
        "uuid,id,thermal_resistivity_wet,thermal_resistivity_dry,specific_heat_capacity,critical_temperature_difference\n" +
        TYPE_UUID + ",loam,invalid,0.4,0.0015,15\n")
    def source = soilSource()

    when:
    source.getSoilTypes()

    then:
    def exception = thrown(SourceException)
    exception.message.contains("SoilType")
  }

  def "SoilDataSource validates valid soil data"() {
    given:
    writeSoilTypesFile()
    writeSoilLayersFile()
    def source = soilSource()
    when:
    source.validate()

    then:
    noExceptionThrown()
  }

  def "SoilDataSource validates headers"() {
    given:
    writeSoilTypesFile()
    Files.writeString(
        tempDir.resolve("soil_layer.csv"),
        "uuid,geometry,z_from,z_to,soil_type\ninvalid,not-json,0,-1,invalid\n")
    def source = soilSource()

    when:
    source.validate()

    then:
    noExceptionThrown()
  }

  def "SoilDataSource rejects missing required headers"() {
    given:
    Files.writeString(tempDir.resolve("soil_type.csv"), "uuid,id\n")
    writeSoilLayersFile()
    def source = soilSource()

    when:
    source.validate()

    then:
    thrown(FailedValidationException)
  }

  def "ValidationUtils rejects semantically invalid soil types"() {
    given:
    Files.writeString(
        tempDir.resolve("soil_type.csv"),
        "uuid,id,thermal_resistivity_wet,thermal_resistivity_dry,specific_heat_capacity,critical_temperature_difference\n" +
        TYPE_UUID + ",loam,-0.30,0.40,0.0015,15.0\n")
    def soilType = soilSource().getSoilTypes()[TYPE_UUID]
    when:
    ValidationUtils.check(soilType)
    then:
    def exception = thrown(FailedValidationException)
    exception.message.contains("positive")
  }

  def "ValidationUtils rejects semantically invalid soil layers"() {
    given:
    String escapedGeometry = POLYGON_JSON.replace("\"", "\"\"")
    Files.writeString(
        tempDir.resolve("soil_layer.csv"),
        "uuid,geometry,z_from,z_to,soil_type\n" +
        LAYER_UUID + ",\"" + escapedGeometry + "\",0.0,0.0," + TYPE_UUID + "\n")
    def soilLayer = soilSource().getSoilLayers()[LAYER_UUID]
    when:
    ValidationUtils.check(soilLayer)
    then:
    def exception = thrown(FailedValidationException)
    exception.message.contains("non-positive thickness")
  }

  private void writeSoilTypesFile() {
    Files.writeString(
        tempDir.resolve("soil_type.csv"),
        "uuid,id,thermal_resistivity_wet,thermal_resistivity_dry,specific_heat_capacity,critical_temperature_difference\n" +
        TYPE_UUID + ",loam,0.30,0.40,0.0015,15.0\n")
  }

  private void writeSoilLayersFile() {
    String escapedGeometry = POLYGON_JSON.replace("\"", "\"\"")
    Files.writeString(
        tempDir.resolve("soil_layer.csv"),
        "uuid,geometry,z_from,z_to,soil_type\n" +
        LAYER_UUID + ",\"" + escapedGeometry + "\",0.0,-2.0," + TYPE_UUID + "\n")
  }
}
