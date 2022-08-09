/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.graphics

import edu.ie3.test.common.GridTestData
import spock.lang.Specification


class NodeGraphicInputTest extends Specification {

  def "A LineGraphicInput copy method should work as expected"() {
    given:
    def nodeGraphic = GridTestData.nodeGraphicC

    when:
    def alteredUnit = nodeGraphic.copy().node(GridTestData.nodeG).path(null).graphicLayer("second").build()

    then:
    alteredUnit.with {
      assert uuid == nodeGraphic.uuid
      assert graphicLayer == "second"
      assert path == null
      assert node == GridTestData.nodeG
    }
  }
}
