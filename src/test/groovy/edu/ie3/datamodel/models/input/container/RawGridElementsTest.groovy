/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.container

import edu.ie3.test.common.ComplexTopology
import edu.ie3.test.common.GridTestData
import spock.lang.Specification


class RawGridElementsTest extends Specification {

  def "A valid collection of asset entities can be used to build a valid instance of RawGridElements"() {
    given:
    def rawGrid = ComplexTopology.grid.rawGrid

    when:
    def newlyCreatedRawGrid = new RawGridElements(rawGrid.allEntitiesAsList())

    then:
    newlyCreatedRawGrid == rawGrid
  }

  def "A RawGridElements' copy method should work as expected"() {
    given:
    def emptyRawGrid = new RawGridElements([] as Set, [] as Set, [] as Set, [] as Set, [] as Set, [] as Set)

    when:
    def modifiedRawGrid = emptyRawGrid.copy()
        .nodes(Set.of(GridTestData.nodeA))
        .lines(Set.of(GridTestData.lineAtoB))
        .transformers2Ws(Set.of(GridTestData.transformerBtoD))
        .transformer3Ws(Set.of(GridTestData.transformerAtoBtoC))
        .switches(Set.of(GridTestData.switchAtoB))
        .measurementUnits(Set.of(GridTestData.measurementUnitInput))
        .build()

    then:
    modifiedRawGrid.nodes.first() == GridTestData.nodeA
    modifiedRawGrid.lines.first() == GridTestData.lineAtoB
    modifiedRawGrid.transformer2Ws.first() == GridTestData.transformerBtoD
    modifiedRawGrid.transformer3Ws.first() == GridTestData.transformerAtoBtoC
    modifiedRawGrid.switches.first() == GridTestData.switchAtoB
    modifiedRawGrid.measurementUnits.first() == GridTestData.measurementUnitInput
  }
}
