/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.connector

import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.test.common.GridTestData
import spock.lang.Specification


class SwitchInputTest extends Specification {

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
