/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.update

import edu.ie3.datamodel.models.input.container.GraphicElements
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.models.input.container.SubGridContainer
import edu.ie3.datamodel.models.input.container.SystemParticipants
import edu.ie3.datamodel.utils.ContainerUtils
import edu.ie3.test.common.ComplexTopology
import spock.lang.Specification

class ContainerUpdateUtilsTest extends Specification {

  def "The container util returns copy of provided sub grids with slack nodes marked as expected"() {
    given:
    String gridName = ComplexTopology.grid.gridName
    Set<Integer> subNetNumbers = ContainerUtils.determineSubnetNumbers(ComplexTopology.grid.rawGrid.nodes)
    RawGridElements rawGridInput= ComplexTopology.grid.rawGrid
    SystemParticipants systemParticipantsInput = ComplexTopology.grid.systemParticipants
    GraphicElements graphicsInput = ComplexTopology.grid.graphics

    HashMap<Integer, SubGridContainer> unmodifiedSubGrids = ComplexTopology.expectedSubGrids

    HashMap<Integer, SubGridContainer> subgrids = ContainerUtils.buildSubGridContainers(
        gridName,
        subNetNumbers,
        rawGridInput,
        systemParticipantsInput,
        graphicsInput)

    when:
    def computableSubgrids = subgrids.collectEntries {[(it.key): ContainerUpdateUtils.withTrafoNodeAsSlack(it.value)]} as HashMap<Integer, SubGridContainer>

    then:
    computableSubgrids.size() == 6
    computableSubgrids.each {
      SubGridContainer computableSubGrid = it.value
      SubGridContainer unmodifiedSubGrid = unmodifiedSubGrids.get(it.key)

      computableSubGrid.with {
        assert subnet == unmodifiedSubGrid.subnet
        assert predominantVoltageLevel == unmodifiedSubGrid.predominantVoltageLevel

        // 2 winding transformer hv nodes must be marked as slacks
        rawGrid.transformer2Ws.each {
          def trafo2w = it
          trafo2w.with {
            assert nodeA.slack
          }
        }

        // all adapted trafo2w nodes must be part of the nodes set
        assert rawGrid.nodes.containsAll(rawGrid.transformer2Ws.collect{it.nodeA})

        // 3 winding transformer slack nodes must be mapped correctly
        rawGrid.transformer3Ws.each {
          def trafo3w = it
          if(trafo3w.nodeA.subnet == subnet) {
            // subnet 1 is highest grid in test set + trafo 3w -> nodeA must be slack
            assert subnet == 1 ? trafo3w.nodeA.slack : !trafo3w.nodeA.slack
            assert !trafo3w.nodeInternal.slack
            assert rawGrid.nodes.contains(trafo3w.nodeInternal)
          } else {
            assert trafo3w.nodeInternal.slack
            assert !trafo3w.nodeA.slack
            assert !trafo3w.nodeB.slack
            assert !trafo3w.nodeC.slack
            assert rawGrid.nodes.contains(trafo3w.nodeInternal)
          }
        }

        assert systemParticipants == unmodifiedSubGrid.systemParticipants
      }
    }
  }






  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // methods for changing subnet voltage



  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // general utils
}
