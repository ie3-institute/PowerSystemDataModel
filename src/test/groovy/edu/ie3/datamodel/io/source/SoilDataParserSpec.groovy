/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.util.quantities.PowerSystemUnits.KELVIN_METRE_PER_WATT
import static tech.units.indriya.unit.Units.METRE

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.exceptions.ParsingException
import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.source.csv.CsvSoilDataSource
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path

class SoilDataParserSpec extends Specification {
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

  def "CsvSoilDataSource reads soil types and layers"() {
    given:
    writeSoilTypesFile()
    writeSoilLayersFile()
    def source = new CsvSoilDataSource(",", tempDir, new FileNamingStrategy())

    when:
    def types = source.getSoilTypes()
    def layers = source.getSoilLayers()

    then:
    types.size() == 1
    types.first().uuid == TYPE_UUID
    types.first().id == "loam"
    Math.abs(types.first().thermalResistivityWet.to(KELVIN_METRE_PER_WATT).value.doubleValue() - 0.30d) < 1e-12
    layers.size() == 1
    layers.first().geometry.equals(GEOMETRY)
    Math.abs(layers.first().thickness().to(METRE).value.doubleValue() - 2.0d) < 1e-12
  }

  def "CsvSoilDataSource reports malformed soil rows"() {
    given:
    Files.writeString(tempDir.resolve("soil_type.csv"), "uuid,id\ninvalid,bad\n")
    Files.writeString(
        tempDir.resolve("soil_layer.csv"),
        "uuid,geometry,z_from,z_to,soil_type\ninvalid,not-json,0,-1,invalid\n")
    def source = new CsvSoilDataSource(",", tempDir, new FileNamingStrategy())

    when:
    source.getSoilTypes()
    then:
    thrown(SourceException)

    when:
    source.getSoilLayers()
    then:
    thrown(SourceException)
  }

  def "CsvSoilDataSource reports malformed rows with row context and cause"() {
    given:
    Files.writeString(
        tempDir.resolve("soil_type.csv"),
        "uuid,id,thermal_resistivity_wet,thermal_resistivity_dry,specific_heat_capacity,critical_temperature_difference\n" +
        TYPE_UUID + ",loam,0.3,0.4,0.0015,15\n" +
        TYPE_UUID + ",sand,invalid,0.4,0.0015,15\n")
    def source = new CsvSoilDataSource(",", tempDir, new FileNamingStrategy())

    when:
    source.getSoilTypes()

    then:
    def exception = thrown(SourceException)
    exception.message.contains("soil types row 2")
    def parsingException = exception.cause as ParsingException
    parsingException.message.contains("soil type")
    parsingException.cause instanceof FactoryException
  }

  def "parseSoilType includes factory field details and cause for invalid uuid"() {
    given:
    def data = new HashMap<>(soilTypeRow())
    data.put("uuid", "invalid")

    when:
    SoilDataParser.parseSoilType(data)

    then:
    def exception = thrown(ParsingException)
    exception.message.contains("soil type")
    exception.message.contains("UUID")
    exception.cause instanceof FactoryException
  }

  def "parseSoilType rejects blank required text fields"() {
    given:
    def data = new HashMap<>(soilTypeRow())
    data.put("id", "  ")

    when:
    SoilDataParser.parseSoilType(data)

    then:
    def exception = thrown(ParsingException)
    exception.message.contains("Missing or empty field id")
  }

  def "parseSoilLayer rejects malformed soil polygon geo json"() {
    given:
    def data = new HashMap<>(soilLayerRow())
    data.put("geometry", "not-json")

    when:
    SoilDataParser.parseSoilLayer(data)

    then:
    def exception = thrown(ParsingException)
    exception.message.contains("Unable to parse geometry")
    exception.cause != null
  }

  def "parser decodes mapped soil rows"() {
    when:
    def soilType = SoilDataParser.parseSoilType(soilTypeRow())
    def soilLayer = SoilDataParser.parseSoilLayer(soilLayerRow())

    then:
    soilType.uuid == TYPE_UUID
    soilLayer.uuid == LAYER_UUID
    soilLayer.geometry.equals(GEOMETRY)
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

  private static Map<String, String> soilTypeRow() {
    [
      uuid                          : TYPE_UUID.toString(),
      id                            : "loam",
      thermalResistivityWet         : "0.3",
      thermalResistivityDry         : "0.4",
      specificHeatCapacity          : "0.0015",
      criticalTemperatureDifference : "15"
    ]
  }

  private static Map<String, String> soilLayerRow() {
    [
      uuid    : LAYER_UUID.toString(),
      geometry: POLYGON_JSON,
      zFrom   : "0",
      zTo     : "-2",
      soilType: TYPE_UUID.toString()
    ]
  }
}