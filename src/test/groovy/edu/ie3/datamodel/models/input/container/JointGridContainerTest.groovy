/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.container

import static edu.ie3.test.common.EnergyManagementTestData.emptyEnergyManagementUnits
import static edu.ie3.test.common.SystemParticipantTestData.emptySystemParticipants

import edu.ie3.test.common.EnergyManagementTestData
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification

class JointGridContainerTest extends Specification {
  private static final GRID_NAME = "single_grid"

  private static final RawGridElements RAW_GRID = new RawGridElements(
  [GridTestData.nodeA] as Set,
  [] as Set,
  [] as Set,
  [] as Set,
  [] as Set,
  [] as Set
  )

  private static final ExtendedSystemParticipants EMPTY_EXTENDED_SYSTEM_PARTICIPANTS =
  new ExtendedSystemParticipants(Collections.emptyList())

  def "A single subgrid can be used to build a JointGridContainer"() {
    when:
    def jointGridContainer = new JointGridContainer(
        GRID_NAME,
        RAW_GRID,
        emptySystemParticipants,
        EMPTY_EXTENDED_SYSTEM_PARTICIPANTS,
        emptyEnergyManagementUnits
    )

    then:
    noExceptionThrown()
    jointGridContainer.subGridTopologyGraph.vertexSet().size() == 1
  }

  def "A JointGridContainer's copy method should work as expected"() {
    given:
    def jointGridContainer = new JointGridContainer(
        GRID_NAME,
        RAW_GRID,
        emptySystemParticipants,
        EMPTY_EXTENDED_SYSTEM_PARTICIPANTS,
        emptyEnergyManagementUnits
    )

    def rawGrid =
        new RawGridElements(List.of(GridTestData.lineAtoB, GridTestData.transformerAtoBtoC))
    def systemParticipants =
        new SystemParticipants(Set.of(SystemParticipantTestData.evInput))
    def extendedSystemParticipants =
        new ExtendedSystemParticipants([
          SystemParticipantTestData.bmInput
        ])
    def emUnits =
        new EnergyManagementUnits(Set.of(EnergyManagementTestData.emInput))

    when:
    def modifiedJointGridContainer = jointGridContainer.copy()
        .gridName("new grid name")
        .rawGrid(rawGrid)
        .systemParticipants(systemParticipants)
        .extendedSystemParticipants(extendedSystemParticipants)
        .emUnits(emUnits)
        .build()

    then:
    modifiedJointGridContainer.gridName == "new grid name"
    modifiedJointGridContainer.rawGrid == rawGrid
    modifiedJointGridContainer.systemParticipants == systemParticipants
    modifiedJointGridContainer.extendedSystemParticipants == extendedSystemParticipants
    modifiedJointGridContainer.emUnits == emUnits
  }
}