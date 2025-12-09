/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.connector

import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.io.source.SourceValidator
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import spock.lang.Specification

class Transformer2WInputTest extends Specification {

  def "An Transformer2WInput should return possible fields correctly"() {
    when:
    List<Set<String>> fields = Transformer2WInput.getFields().fields()

    then:
    fields == [
      [
        "autoTap",
        "id",
        "nodeA",
        "nodeB",
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
    Try<Void, ValidationException> input = validator.validate(actualFields, Transformer2WInput)

    then:
    input.failure
    input.exception.get().message == "The provided fields [uuid] are invalid for instance of 'Transformer2WInput'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'Transformer2WInput' are possible (NOT case-sensitive!):\n" +
        "0: [autoTap, id, nodeA, nodeB, parallelDevices, tapPos, type, uuid] or [auto_tap, id, node_a, node_b, parallel_devices, tap_pos, type, uuid]\n" +
        "1: [autoTap, id, nodeA, nodeB, operator, parallelDevices, tapPos, type, uuid] or [auto_tap, id, node_a, node_b, operator, parallel_devices, tap_pos, type, uuid]\n" +
        "2: [autoTap, id, nodeA, nodeB, operatesFrom, parallelDevices, tapPos, type, uuid] or [auto_tap, id, node_a, node_b, operates_from, parallel_devices, tap_pos, type, uuid]\n" +
        "3: [autoTap, id, nodeA, nodeB, operatesFrom, operator, parallelDevices, tapPos, type, uuid] or [auto_tap, id, node_a, node_b, operates_from, operator, parallel_devices, tap_pos, type, uuid]\n" +
        "4: [autoTap, id, nodeA, nodeB, operatesUntil, parallelDevices, tapPos, type, uuid] or [auto_tap, id, node_a, node_b, operates_until, parallel_devices, tap_pos, type, uuid]\n" +
        "5: [autoTap, id, nodeA, nodeB, operatesUntil, operator, parallelDevices, tapPos, type, uuid] or [auto_tap, id, node_a, node_b, operates_until, operator, parallel_devices, tap_pos, type, uuid]\n" +
        "6: [autoTap, id, nodeA, nodeB, operatesFrom, operatesUntil, parallelDevices, tapPos, type, uuid] or [auto_tap, id, node_a, node_b, operates_from, operates_until, parallel_devices, tap_pos, type, uuid]\n" +
        "7: [autoTap, id, nodeA, nodeB, operatesFrom, operatesUntil, operator, parallelDevices, tapPos, type, uuid] or [auto_tap, id, node_a, node_b, operates_from, operates_until, operator, parallel_devices, tap_pos, type, uuid]\n"
  }

  def "A Transformer2WInput copy method should work as expected"() {
    given:
    def trafo2w = GridTestData.transformerBtoD

    when:
    def alteredUnit = trafo2w.copy().id("trafo2w").nodeA(GridTestData.nodeA).nodeB(GridTestData.nodeB)
        .type(GridTestData.transformerTypeBtoD).tapPos(10).autoTap(false).build()

    then:
    alteredUnit.with {
      assert uuid == trafo2w.uuid
      assert operationTime == trafo2w.operationTime
      assert operator == OperatorInput.NO_OPERATOR_ASSIGNED
      assert id == "trafo2w"
      assert nodeA == GridTestData.nodeA
      assert nodeB == GridTestData.nodeB
      assert type == GridTestData.transformerTypeBtoD
      assert tapPos == 10
      assert !autoTap
    }
  }
}
