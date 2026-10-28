/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import static tech.units.indriya.unit.Units.METRE

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.FieldMap
import edu.ie3.datamodel.models.input.SoilLayer
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import spock.lang.Specification

class SoilLayerInputFactoryTest extends Specification implements FactoryTestHelper {
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

  def "A SoilLayerInputFactory should contain exactly the expected class for parsing"() {
    given:
    def inputFactory = new SoilLayerInputFactory()
    def expectedClasses = [SoilLayer]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A SoilLayerInputFactory should parse a valid SoilLayer correctly"() {
    given:
    def inputFactory = new SoilLayerInputFactory()
    Map<String, String> parameter = [
      "uuid"    : LAYER_UUID.toString(),
      "geometry": POLYGON_JSON,
      "zFrom"   : "0",
      "zTo"     : "-2",
      "soilType": TYPE_UUID.toString()
    ]
    def inputClass = SoilLayer

    when:
    Try<SoilLayer, FactoryException> result =
        inputFactory.get(new FieldMap(parameter))

    then:
    result.success
    result.data.get().getClass() == inputClass
    result.data.get().with {
      uuid == LAYER_UUID
      geometry.equals(GEOMETRY)
      zFrom == getQuant("0", METRE)
      zTo == getQuant("-2", METRE)
      soilType == TYPE_UUID
    }
  }

  def "A SoilLayerInputFactory throws FactoryException when a required field is missing"() {
    given:
    def inputFactory = new SoilLayerInputFactory()
    Map<String, String> parameter = [
      "uuid"    : LAYER_UUID.toString(),
      "geometry": POLYGON_JSON,
      "zFrom"   : "0",
      // zTo and soilType intentionally missing
    ]

    when:
    Try<SoilLayer, FactoryException> result =
        inputFactory.get(new FieldMap(parameter))

    then:
    result.failure
    result.exception.get() instanceof FactoryException
  }

  def "A SoilLayerInputFactory throws FactoryException for invalid uuid"() {
    given:
    def inputFactory = new SoilLayerInputFactory()
    Map<String, String> parameter = [
      "uuid"    : "not-a-valid-uuid",
      "geometry": POLYGON_JSON,
      "zFrom"   : "0",
      "zTo"     : "-2",
      "soilType": TYPE_UUID.toString()
    ]

    when:
    Try<SoilLayer, FactoryException> result =
        inputFactory.get(new FieldMap(parameter))

    then:
    result.failure
    result.exception.get() instanceof FactoryException
  }

  def "A SoilLayerInputFactory throws FactoryException for malformed geometry"() {
    given:
    def inputFactory = new SoilLayerInputFactory()
    Map<String, String> parameter = [
      "uuid"    : LAYER_UUID.toString(),
      "geometry": "not-json",
      "zFrom"   : "0",
      "zTo"     : "-2",
      "soilType": TYPE_UUID.toString()
    ]

    when:
    Try<SoilLayer, FactoryException> result =
        inputFactory.get(new FieldMap(parameter))

    then:
    result.failure
    result.exception.get() instanceof FactoryException
    result.exception.get().cause.message.contains("Exception while trying to parse geometry")
  }

  def "A SoilLayerInputFactory throws FactoryException for non-closed polygon"() {
    given:
    def inputFactory = new SoilLayerInputFactory()
    Map<String, String> parameter = [
      "uuid"    : LAYER_UUID.toString(),
      "geometry": '{"type":"Polygon","coordinates":[[[0,0],[1,0],[1,1],[0,1]]]}',
      "zFrom"   : "0",
      "zTo"     : "-2",
      "soilType": TYPE_UUID.toString()
    ]

    when:
    Try<SoilLayer, FactoryException> result =
        inputFactory.get(new FieldMap(parameter))

    then:
    result.failure
    result.exception.get() instanceof FactoryException
    result.exception.get().cause.message.contains("Exception while trying to parse geometry")
  }

  def "A SoilLayerInputFactory throws FactoryException for unsupported geometry type"() {
    given:
    def inputFactory = new SoilLayerInputFactory()
    Map<String, String> parameter = [
      "uuid"    : LAYER_UUID.toString(),
      "geometry": '{"type":"Point","coordinates":[0,0]}',
      "zFrom"   : "0",
      "zTo"     : "-2",
      "soilType": TYPE_UUID.toString()
    ]

    when:
    Try<SoilLayer, FactoryException> result =
        inputFactory.get(new FieldMap(parameter))

    then:
    result.failure
    result.exception.get() instanceof FactoryException
    result.exception.get().cause.message.contains("Unsupported GeoJSON type")
  }

  def "A SoilLayerInputFactory stores additional information"() {
    given:
    def inputFactory = new SoilLayerInputFactory()
    Map<String, String> parameter = [
      "uuid"    : LAYER_UUID.toString(),
      "geometry": POLYGON_JSON,
      "zFrom"   : "0",
      "zTo"     : "-2",
      "soilType": TYPE_UUID.toString(),
      "extraField": "extraValue"
    ]

    when:
    Try<SoilLayer, FactoryException> result =
        inputFactory.get(new FieldMap(parameter))

    then:
    result.success
    result.data.get().additionalInformation["extraField"] == "extraValue"
  }
}
