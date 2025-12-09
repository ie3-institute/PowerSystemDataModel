/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.connector

import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.io.source.SourceValidator
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import spock.lang.Specification

class Transformer3WInputTest extends Specification {

  def "An Transformer2WInput should return possible fields correctly"() {
    when:
    List<Set<String>> fields = Transformer3WInput.getFields().fields()

    then:
    fields == [
      [
        "autoTap",
        "id",
        "nodeA",
        "nodeB",
        "nodeC",
        "parallelDevices",
        "tapPos",
        "type",
        "uuid"
      ] as Set,
      [
        "autoTap",
        "id",
        "nodeA",
        "nodeB",
        "nodeC",
        "operator",
        "parallelDevices",
        "tapPos",
        "type",
        "uuid"
      ] as Set,
      [
        "autoTap",
        "id",
        "nodeA",
        "nodeB",
        "nodeC",
        "operatesFrom",
        "parallelDevices",
        "tapPos",
        "type",
        "uuid"
      ] as Set,
      [
        "autoTap",
        "id",
        "nodeA",
        "nodeB",
        "nodeC",
        "operatesFrom",
        "operator",
        "parallelDevices",
        "tapPos",
        "type",
        "uuid"
      ] as Set,
      [
        "autoTap",
        "id",
        "nodeA",
        "nodeB",
        "nodeC",
        "operatesUntil",
        "parallelDevices",
        "tapPos",
        "type",
        "uuid"
      ] as Set,
      [
        "autoTap",
        "id",
        "nodeA",
        "nodeB",
        "nodeC",
        "operatesUntil",
        "operator",
        "parallelDevices",
        "tapPos",
        "type",
        "uuid"
      ] as Set,
      [
        "autoTap",
        "id",
        "nodeA",
        "nodeB",
        "nodeC",
        "operatesFrom",
        "operatesUntil",
        "parallelDevices",
        "tapPos",
        "type",
        "uuid"
      ] as Set,
      [
        "autoTap",
        "id",
        "nodeA",
        "nodeB",
        "nodeC",
        "operatesFrom",
        "operatesUntil",
        "operator",
        "parallelDevices",
        "tapPos",
        "type",
        "uuid"
      ] as Set
    ]
  }

  def "A Transformer2WInput should throw an exception on incorrect fields correctly"() {
    given:
    def actualFields = SourceValidator.newSet("uuid")
    def validator = new SourceValidator()

    when:
    Try<Void, ValidationException> input = validator.validate(actualFields, Transformer3WInput)

    then:
    input.failure
    input.exception.get().message == "The provided fields [uuid] are invalid for instance of 'Transformer3WInput'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'Transformer3WInput' are possible (NOT case-sensitive!):\n" +
        "0: [autoTap, id, nodeA, nodeB, nodeC, parallelDevices, tapPos, type, uuid] or [auto_tap, id, node_a, node_b, node_c, parallel_devices, tap_pos, type, uuid]\n" +
        "1: [autoTap, id, nodeA, nodeB, nodeC, operator, parallelDevices, tapPos, type, uuid] or [auto_tap, id, node_a, node_b, node_c, operator, parallel_devices, tap_pos, type, uuid]\n" +
        "2: [autoTap, id, nodeA, nodeB, nodeC, operatesFrom, parallelDevices, tapPos, type, uuid] or [auto_tap, id, node_a, node_b, node_c, operates_from, parallel_devices, tap_pos, type, uuid]\n" +
        "3: [autoTap, id, nodeA, nodeB, nodeC, operatesFrom, operator, parallelDevices, tapPos, type, uuid] or [auto_tap, id, node_a, node_b, node_c, operates_from, operator, parallel_devices, tap_pos, type, uuid]\n" +
        "4: [autoTap, id, nodeA, nodeB, nodeC, operatesUntil, parallelDevices, tapPos, type, uuid] or [auto_tap, id, node_a, node_b, node_c, operates_until, parallel_devices, tap_pos, type, uuid]\n" +
        "5: [autoTap, id, nodeA, nodeB, nodeC, operatesUntil, operator, parallelDevices, tapPos, type, uuid] or [auto_tap, id, node_a, node_b, node_c, operates_until, operator, parallel_devices, tap_pos, type, uuid]\n" +
        "6: [autoTap, id, nodeA, nodeB, nodeC, operatesFrom, operatesUntil, parallelDevices, tapPos, type, uuid] or [auto_tap, id, node_a, node_b, node_c, operates_from, operates_until, parallel_devices, tap_pos, type, uuid]\n" +
        "7: [autoTap, id, nodeA, nodeB, nodeC, operatesFrom, operatesUntil, operator, parallelDevices, tapPos, type, uuid] or [auto_tap, id, node_a, node_b, node_c, operates_from, operates_until, operator, parallel_devices, tap_pos, type, uuid]\n"
  }

  def "A Transformer3WInput copy method should work as expected"() {
    given:
    def trafo3w = GridTestData.transformerAtoBtoC

    when:
    def alteredUnit = trafo3w.copy().id("trafo3w").nodeA(GridTestData.nodeC).nodeB(GridTestData.nodeD)
        .nodeC(GridTestData.nodeE).type(GridTestData.transformerTypeAtoBtoC).tapPos(10).autoTap(false).build()

    then:
    alteredUnit.with {
      assert uuid == trafo3w.uuid
      assert operationTime == trafo3w.operationTime
      assert operator == GridTestData.profBroccoli
      assert id == "trafo3w"
      assert nodeA == GridTestData.nodeC
      assert nodeB == GridTestData.nodeD
      assert nodeC == GridTestData.nodeE
      assert type == GridTestData.transformerTypeAtoBtoC
      assert tapPos == 10
      assert !autoTap
    }
  }
}
