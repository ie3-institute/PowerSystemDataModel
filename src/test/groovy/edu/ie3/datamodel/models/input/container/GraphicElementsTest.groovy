/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.container

import edu.ie3.test.common.GridTestData
import spock.lang.Specification


class GraphicElementsTest extends Specification {

  def "A valid collection of asset entities can be used to build a valid instance of GraphicElements"() {
    given:
    def graphicElements = new GraphicElements(Collections.singleton(GridTestData.nodeGraphicC),
        Collections.singleton(GridTestData.lineGraphicCtoD))

    when:
    def newlyCreatedGraphicElements = new GraphicElements(graphicElements.allEntitiesAsList())

    then:
    newlyCreatedGraphicElements == graphicElements
  }
}
