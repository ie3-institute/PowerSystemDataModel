/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.container

import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification

import static edu.ie3.test.common.SystemParticipantTestData.emptySystemParticipants

class JointGridContainerTest extends Specification {
  private static final GRID_NAME = "single_grid"

  private static final RawGridElements RAW_GRID = new RawGridElements(
  [GridTestData.nodeA] as Set,
  [] as Set,
  [] as Set,
  [] as Set,
  [] as Set,
  [] as Set)


  private static final GraphicElements GRAPHIC_ELEMENTS = new GraphicElements(
  [] as Set,
  [] as Set)

  def "A single subgrid can be used to build a JointGridContainer"() {
    when:
    def jointGridContainer = new JointGridContainer(GRID_NAME, RAW_GRID, emptySystemParticipants, GRAPHIC_ELEMENTS)

    then:
    noExceptionThrown()
    jointGridContainer.subGridTopologyGraph.vertexSet().size() == 1
  }

  def "A JointGridContainer's copy method should work as expected"() {
    given:
    def jointGridContainer = new JointGridContainer(GRID_NAME, RAW_GRID, emptySystemParticipants, GRAPHIC_ELEMENTS)
    def rawGrid = new RawGridElements(List.of(GridTestData.lineAtoB, GridTestData.transformerAtoBtoC))
    def systemParticipants = new SystemParticipants(List.of(SystemParticipantTestData.bmInput))
    def graphics = new GraphicElements(Set.of(GridTestData.nodeGraphicD), Set.of(GridTestData.lineGraphicCtoD))

    when:
    def modifiedJointGridContainer = jointGridContainer.copy()
        .gridName("new grid name")
        .rawGrid(rawGrid)
        .systemParticipants(systemParticipants)
        .graphics(graphics)
        .build()

    then:
    modifiedJointGridContainer.gridName == "new grid name"
    modifiedJointGridContainer.rawGrid == rawGrid
    modifiedJointGridContainer.systemParticipants == systemParticipants
    modifiedJointGridContainer.graphics == graphics
  }
}
