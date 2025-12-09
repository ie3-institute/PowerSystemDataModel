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

class SwitchInputTest extends Specification {

  def "An SwitchInput should return possible fields correctly"() {
    when:
    List<Set<String>> fields = SwitchInput.getFields().fields()

    then:
    fields == [
      [
        "closed",
        "id",
        "nodeA",
        "nodeB",
        "uuid"
      ] as Set,
      [
        "closed",
        "id",
        "nodeA",
        "nodeB",
        "operator",
        "uuid"
      ] as Set,
      [
        "closed",
        "id",
        "nodeA",
        "nodeB",
        "operatesFrom",
        "uuid"
      ] as Set,
      [
        "closed",
        "id",
        "nodeA",
        "nodeB",
        "operatesFrom",
        "operator",
        "uuid"
      ] as Set,
      [
        "closed",
        "id",
        "nodeA",
        "nodeB",
        "operatesUntil",
        "uuid"
      ] as Set,
      [
        "closed",
        "id",
        "nodeA",
        "nodeB",
        "operatesUntil",
        "operator",
        "uuid"
      ] as Set,
      [
        "closed",
        "id",
        "nodeA",
        "nodeB",
        "operatesFrom",
        "operatesUntil",
        "uuid"
      ] as Set,
      [
        "closed",
        "id",
        "nodeA",
        "nodeB",
        "operatesFrom",
        "operatesUntil",
        "operator",
        "uuid"
      ] as Set,
    ]
  }

  def "A SwitchInput should throw an exception on incorrect fields correctly"() {
    given:
    def actualFields = SourceValidator.newSet("uuid")
    def validator = new SourceValidator()

    when:
    Try<Void, ValidationException> input = validator.validate(actualFields, SwitchInput)

    then:
    input.failure
    input.exception.get().message == "The provided fields [uuid] are invalid for instance of 'SwitchInput'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'SwitchInput' are possible (NOT case-sensitive!):\n" +
        "0: [closed, id, nodeA, nodeB, uuid] or [closed, id, node_a, node_b, uuid]\n" +
        "1: [closed, id, nodeA, nodeB, operator, uuid] or [closed, id, node_a, node_b, operator, uuid]\n" +
        "2: [closed, id, nodeA, nodeB, operatesFrom, uuid] or [closed, id, node_a, node_b, operates_from, uuid]\n" +
        "3: [closed, id, nodeA, nodeB, operatesFrom, operator, uuid] or [closed, id, node_a, node_b, operates_from, operator, uuid]\n" +
        "4: [closed, id, nodeA, nodeB, operatesUntil, uuid] or [closed, id, node_a, node_b, operates_until, uuid]\n" +
        "5: [closed, id, nodeA, nodeB, operatesUntil, operator, uuid] or [closed, id, node_a, node_b, operates_until, operator, uuid]\n" +
        "6: [closed, id, nodeA, nodeB, operatesFrom, operatesUntil, uuid] or [closed, id, node_a, node_b, operates_from, operates_until, uuid]\n" +
        "7: [closed, id, nodeA, nodeB, operatesFrom, operatesUntil, operator, uuid] or [closed, id, node_a, node_b, operates_from, operates_until, operator, uuid]\n"
  }

  def "A SwitchInput copy method should work as expected"() {
    given:
    def switchInput = GridTestData.switchAtoB

    when:
    def alteredUnit = switchInput.copy().id("switch_A_C").operator(OperatorInput.NO_OPERATOR_ASSIGNED)
        .closed(false).build()

    then:
    alteredUnit.with {
      assert uuid == switchInput.uuid
      assert operationTime == switchInput.operationTime
      assert operator == OperatorInput.NO_OPERATOR_ASSIGNED
      assert id == "switch_A_C"
      assert !closed
    }
  }
}
