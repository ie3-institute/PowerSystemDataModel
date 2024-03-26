/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import edu.ie3.datamodel.graph.SubGridTopologyGraph
import edu.ie3.datamodel.models.input.container.GraphicElements
import edu.ie3.datamodel.models.input.container.JointGridContainer
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.models.input.container.SubGridContainer
import edu.ie3.datamodel.models.input.container.SystemParticipants
import edu.ie3.datamodel.utils.JointGridContainerUtils
import edu.ie3.test.common.ComplexTopology
import spock.lang.Specification

class JointGridContainerUtilsTest extends Specification {


  def "The container utils build a joint model correctly from sub grids"() {
    given:
    Collection<SubGridContainer> subGridContainers = ComplexTopology.expectedSubGrids.values()
    JointGridContainer expected = ComplexTopology.grid

    when:
    JointGridContainer actual = JointGridContainerUtils.combineToJointGrid(subGridContainers)

    then:
    actual == expected
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // subgrid topology graph utils

  def "The container util builds the correct assembly of sub grids from basic information"() {
    given:
    String gridName = ComplexTopology.gridName
    RawGridElements rawGrid = ComplexTopology.grid.rawGrid
    SystemParticipants systemParticpants = ComplexTopology.grid.systemParticipants
    GraphicElements graphics = ComplexTopology.grid.graphics
    SubGridTopologyGraph expectedSubGridTopology = ComplexTopology.expectedSubGridTopology

    when:
    SubGridTopologyGraph actual = JointGridContainerUtils.buildSubGridTopologyGraph(
        gridName,
        rawGrid,
        systemParticpants,
        graphics)

    then:
    actual == expectedSubGridTopology
  }

  def "The container util builds the correct sub grid dependency graph"() {
    given:
    String gridName = ComplexTopology.grid.gridName
    Set<Integer> subNetNumbers = JointGridContainerUtils.determineSubnetNumbers(ComplexTopology.grid.rawGrid.nodes)
    RawGridElements rawGrid = ComplexTopology.grid.rawGrid
    SystemParticipants systemParticipants = ComplexTopology.grid.systemParticipants
    GraphicElements graphics = ComplexTopology.grid.graphics
    Map<Integer, SubGridContainer> subgrids = JointGridContainerUtils.buildSubGridContainers(
        gridName,
        subNetNumbers,
        rawGrid,
        systemParticipants,
        graphics)
    SubGridTopologyGraph expectedSubGridTopology = ComplexTopology.expectedSubGridTopology

    when:
    SubGridTopologyGraph actual = JointGridContainerUtils.buildSubGridTopologyGraph(
        subgrids,
        ComplexTopology.grid.rawGrid)

    then:
    actual == expectedSubGridTopology
  }
}
