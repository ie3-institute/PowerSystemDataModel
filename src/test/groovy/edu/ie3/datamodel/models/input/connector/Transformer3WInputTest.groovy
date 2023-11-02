/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.connector

import edu.ie3.test.common.GridTestData
import spock.lang.Specification


class Transformer3WInputTest extends Specification {

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
