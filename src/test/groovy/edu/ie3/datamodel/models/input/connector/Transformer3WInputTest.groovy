/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.connector

import edu.ie3.test.common.GridTestData
import spock.lang.Specification


class Transformer3WInputTest extends Specification {

  def "A Transformer3WInput copy should preserve metadata and internal node semantics"() {
    given:
    def original = GridTestData.transformerAtoBtoC.copy()
        .additionalInformation([source: "original"])
        .build()
    def originalSlack = original.nodeInternal.slack

    when:
    def copied = original.copy().build()
    def changed = original.copy().internalSlack(!originalSlack).build()

    then:
    copied.additionalInformation == [source: "original"]
    copied.nodeInternal.uuid == original.nodeInternal.uuid
    copied.nodeInternal.slack == originalSlack
    changed.additionalInformation == [source: "original"]
    changed.nodeInternal.uuid == original.nodeInternal.uuid
    changed.nodeInternal.slack == !originalSlack
    original.nodeInternal.slack == originalSlack
  }

  def "A Transformer3WInput copy method should work as expected"() {
    given:
    def trafo3w = GridTestData.transformerAtoBtoC

    when:
    def alteredUnit = trafo3w.copy().id("trafo3w").nodeA(GridTestData.nodeC).nodeB(GridTestData.nodeD)
        .nodeC(GridTestData.nodeE).type(GridTestData.transformerTypeAtoBtoC).tapPos(10).autoTap(false).build()

    then:
    alteredUnit.with {
      uuid == trafo3w.uuid
      operationTime == trafo3w.operationTime
      operator == GridTestData.profBroccoli
      id == "trafo3w"
      nodeA == GridTestData.nodeC
      nodeB == GridTestData.nodeD
      nodeC == GridTestData.nodeE
      type == GridTestData.transformerTypeAtoBtoC
      tapPos == 10
      !autoTap
    }
  }
}
