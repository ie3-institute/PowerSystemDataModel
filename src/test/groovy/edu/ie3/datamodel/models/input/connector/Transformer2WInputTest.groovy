/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.connector

import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.test.common.GridTestData
import spock.lang.Specification


class Transformer2WInputTest extends Specification {

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
