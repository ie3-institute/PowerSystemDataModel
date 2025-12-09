/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.connector

import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.io.source.SourceValidator
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import edu.ie3.test.helper.FactoryTestHelper
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.io.geojson.GeoJsonReader
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units

class LineInputTest extends Specification implements FactoryTestHelper {

  @Shared
  private static final GeoJsonReader geoJsonReader = new GeoJsonReader()

  def "A LineInput should return possible fields correctly"() {
    when:
    List<Set<String>> fields = LineInput.getFields().fields()

    then:
    fields == [
      [
        "geoPosition",
        "id",
        "length",
        "nodeA",
        "nodeB",
        "olmCharacteristic",
        "parallelDevices",
        "type",
        "uuid"
      ] as Set,
      [
        "geoPosition",
        "id",
        "length",
        "nodeA",
        "nodeB",
        "olmCharacteristic",
        "operator",
        "parallelDevices",
        "type",
        "uuid"
      ] as Set,
      [
        "geoPosition",
        "id",
        "length",
        "nodeA",
        "nodeB",
        "olmCharacteristic",
        "operatesFrom",
        "parallelDevices",
        "type",
        "uuid"
      ] as Set,
      [
        "geoPosition",
        "id",
        "length",
        "nodeA",
        "nodeB",
        "olmCharacteristic",
        "operatesFrom",
        "operator",
        "parallelDevices",
        "type",
        "uuid"
      ] as Set,
      [
        "geoPosition",
        "id",
        "length",
        "nodeA",
        "nodeB",
        "olmCharacteristic",
        "operatesUntil",
        "parallelDevices",
        "type",
        "uuid"
      ] as Set,
      [
        "geoPosition",
        "id",
        "length",
        "nodeA",
        "nodeB",
        "olmCharacteristic",
        "operatesUntil",
        "operator",
        "parallelDevices",
        "type",
        "uuid"
      ] as Set,
      [
        "geoPosition",
        "id",
        "length",
        "nodeA",
        "nodeB",
        "olmCharacteristic",
        "operatesFrom",
        "operatesUntil",
        "parallelDevices",
        "type",
        "uuid"
      ] as Set,
      [
        "geoPosition",
        "id",
        "length",
        "nodeA",
        "nodeB",
        "olmCharacteristic",
        "operatesFrom",
        "operatesUntil",
        "operator",
        "parallelDevices",
        "type",
        "uuid"
      ] as Set
    ]
  }

  def "A LineInput should throw an exception on incorrect fields correctly"() {
    given:
    def actualFields = SourceValidator.newSet("uuid")
    def validator = new SourceValidator()

    when:
    Try<Void, ValidationException> input = validator.validate(actualFields, LineInput)

    then:
    input.failure
    input.exception.get().message == "The provided fields [uuid] are invalid for instance of 'LineInput'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'LineInput' are possible (NOT case-sensitive!):\n" +
        "0: [geoPosition, id, length, nodeA, nodeB, olmCharacteristic, parallelDevices, type, uuid] or [geo_position, id, length, node_a, node_b, olm_characteristic, parallel_devices, type, uuid]\n" +
        "1: [geoPosition, id, length, nodeA, nodeB, olmCharacteristic, operator, parallelDevices, type, uuid] or [geo_position, id, length, node_a, node_b, olm_characteristic, operator, parallel_devices, type, uuid]\n" +
        "2: [geoPosition, id, length, nodeA, nodeB, olmCharacteristic, operatesFrom, parallelDevices, type, uuid] or [geo_position, id, length, node_a, node_b, olm_characteristic, operates_from, parallel_devices, type, uuid]\n" +
        "3: [geoPosition, id, length, nodeA, nodeB, olmCharacteristic, operatesFrom, operator, parallelDevices, type, uuid] or [geo_position, id, length, node_a, node_b, olm_characteristic, operates_from, operator, parallel_devices, type, uuid]\n" +
        "4: [geoPosition, id, length, nodeA, nodeB, olmCharacteristic, operatesUntil, parallelDevices, type, uuid] or [geo_position, id, length, node_a, node_b, olm_characteristic, operates_until, parallel_devices, type, uuid]\n" +
        "5: [geoPosition, id, length, nodeA, nodeB, olmCharacteristic, operatesUntil, operator, parallelDevices, type, uuid] or [geo_position, id, length, node_a, node_b, olm_characteristic, operates_until, operator, parallel_devices, type, uuid]\n" +
        "6: [geoPosition, id, length, nodeA, nodeB, olmCharacteristic, operatesFrom, operatesUntil, parallelDevices, type, uuid] or [geo_position, id, length, node_a, node_b, olm_characteristic, operates_from, operates_until, parallel_devices, type, uuid]\n" +
        "7: [geoPosition, id, length, nodeA, nodeB, olmCharacteristic, operatesFrom, operatesUntil, operator, parallelDevices, type, uuid] or [geo_position, id, length, node_a, node_b, olm_characteristic, operates_from, operates_until, operator, parallel_devices, type, uuid]\n"
  }

  def "Two LineInputs should be equal if all attributes are equal"() {
    given:
    def line1 = new LineInput(
        UUID.fromString("91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"),
        "test_line_CtoD",
        GridTestData.profBroccoli,
        GridTestData.defaultOperationTime,
        GridTestData.nodeC,
        GridTestData.nodeD,
        2,
        GridTestData.lineTypeInputCtoD,
        Quantities.getQuantity(3, Units.METRE),
        geoJsonReader.read(lineString) as LineString,
        OlmCharacteristicInput.CONSTANT_CHARACTERISTIC)

    def line2 = new LineInput(
        UUID.fromString("91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"),
        "test_line_CtoD",
        GridTestData.profBroccoli,
        GridTestData.defaultOperationTime,
        GridTestData.nodeC,
        GridTestData.nodeD,
        2,
        GridTestData.lineTypeInputCtoD,
        Quantities.getQuantity(3, Units.METRE),
        geoJsonReader.read(lineString) as LineString,
        OlmCharacteristicInput.CONSTANT_CHARACTERISTIC)

    expect:
    // do NOT change this equals to '==' as this yields true even if it's false
    line1.equals(line2)

    where:
    lineString                                                                                                                            | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228]]}"                                           | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228]]}" | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.311111, 51.49228],[7.511111, 51.49228]]}" | _
  }

  def "A LineInput copy method should work as expected"() {
    given:
    def line = GridTestData.lineAtoB

    when:
    def alteredUnit = line.copy().id("line_A_C").nodeA(GridTestData.nodeA)
        .nodeB(GridTestData.nodeC).type(GridTestData.lineTypeInputCtoD).length(Quantities.getQuantity(10, Units.METRE))
        .build()

    then:
    alteredUnit.with {
      assert uuid == line.uuid
      assert operationTime == line.operationTime
      assert operator == GridTestData.profBroccoli
      assert id == "line_A_C"
      assert nodeA == GridTestData.nodeA
      assert nodeB == GridTestData.nodeC
      assert length == Quantities.getQuantity(10, Units.METRE)
    }
  }

  def "toString test"() {
    expect:
    def line = GridTestData.lineAtoB

    line.toString() ==
        "LineInput{" +
        "uuid=92ec3bcf-1777-4d38-af67-0bf7c9fa73c7" +
        ", id=test_line_AtoB" +
        ", operator=f15105c4-a2de-4ab8-a621-4bc98e372d92" +
        ", operationTime=OperationTime{startDate=2020-03-24T15:11:31Z, endDate=2020-03-25T15:11:31Z, isLimited=true}" +
        ", nodeA=4ca90220-74c2-4369-9afa-a18bf068840d" +
        ", nodeB=47d29df0-ba2d-4d23-8e75-c82229c5c758" +
        ", noOfParallelDevices=2" +
        ", type=3bed3eb3-9790-4874-89b5-a5434d408088" +
        ", length=0.003 km" +
        ", geoPosition=LINESTRING (7.411111 51.492528, 7.414116 51.484136)" +
        ", olmCharacteristic=OlmCharacteristicInput{points=[CharacteristicCoordinate{x=0.0 m/s, y=1 p.u.}]}}"
  }
}
