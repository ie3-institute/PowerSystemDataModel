/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.update

import static edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils.MV_10KV
import static edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils.MV_20KV

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.container.*
import edu.ie3.datamodel.utils.ContainerUtils
import edu.ie3.test.common.ComplexTopology
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.SampleJointGrid
import edu.ie3.test.common.TypeTestData
import spock.lang.Shared
import spock.lang.Specification

class ContainerUpdateUtilsTest extends Specification {

  @Shared
  private JointGridContainer changedContainer

  @Shared
  private NodeInput changedNodeE

  @Shared
  private Set<LineInput> changedLines


  def setupSpec() {
    // build a changed container

    def jointGrid = SampleJointGrid.grid()
    def rawGrid = jointGrid.rawGrid
    def nodes = rawGrid.nodes
    changedNodeE = nodes.find { it.id == "nodeE"}.copy().id("changedNode").build()

    def oldLines = rawGrid.lines
    def lineDECopy = oldLines.find {it.getId() == "lineDtoE" }.copy().nodeB(changedNodeE)
    changedLines = [
      lineDECopy.id("changedId").build(),
      lineDECopy.uuid(UUID.randomUUID()).id("changedUuid").build()
    ] as Set

    def changedNodes = ContainerUpdateUtils.combineElements(nodes, [changedNodeE] as Set, true).toSet()
    def changeLines = ContainerUpdateUtils.combineElements(oldLines, changedLines, true).toSet()

    def changedRawGrid = jointGrid.rawGrid
        .copy().nodes(changedNodes)
        .lines(changeLines)
        .build()

    changedContainer = SampleJointGrid.grid().copy().rawGrid(changedRawGrid).build()
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // methods for updating grid containers


  def "The ContainerUtils returns copy of provided sub grids with slack nodes marked as expected"() {
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

  def "The ContainerUtils can update a subgrid voltage correctly"() {
    given:
    def jointGrid = SampleJointGrid.grid()
    def types = new ContainerUpdateUtils.Types(
        TypeTestData.lineTypes(),
        TypeTestData.transformer2WTypes(),
        TypeTestData.transformer3WTypes()
        )

    def changedNodeD = SampleJointGrid.nodeD.copy().voltLvl(MV_20KV).build()
    def changedNodeE = SampleJointGrid.nodeE.copy().voltLvl(MV_20KV).build()
    def changedNodeF = SampleJointGrid.nodeF.copy().voltLvl(MV_20KV).build()

    def expectedNodes = [
      SampleJointGrid.nodeA,
      SampleJointGrid.nodeB,
      SampleJointGrid.nodeC,
      changedNodeD,
      changedNodeE,
      changedNodeF,
      SampleJointGrid.nodeE.copy().voltLvl(MV_20KV).build(),
      SampleJointGrid.nodeF.copy().voltLvl(MV_20KV).build(),
      SampleJointGrid.nodeG,
    ] as Set

    def expectedLines = [
      SampleJointGrid.lineAB,
      SampleJointGrid.lineAC,
      SampleJointGrid.lineBC,
      SampleJointGrid.lineDE.copy().nodeA(changedNodeD).nodeB(changedNodeE).type(TypeTestData.lineType20kV_400).build(),
      SampleJointGrid.lineDF.copy().nodeA(changedNodeD).nodeB(changedNodeF).type(TypeTestData.lineType20kV_400).build(),
      SampleJointGrid.lineEF.copy().nodeA(changedNodeE).nodeB(changedNodeF).type(TypeTestData.lineType20kV_400).build()
    ] as Set


    def expectedTransformer2Ws =  [
      SampleJointGrid.transformerDtoA.copy().nodeA(changedNodeD).type(TypeTestData.transformerType20kV_LV_10).build(),
      SampleJointGrid.transformerGtoD.copy().nodeB(changedNodeD).type(TypeTestData.transformerTypeHV_20kV_40).build()
    ] as Set


    when:
    def updatedGrid = ContainerUpdateUtils.updateVoltage(jointGrid, 2, MV_20KV, types)

    then:
    RawGridElements rawGridElements = updatedGrid.rawGridElements()
    rawGridElements.nodes == expectedNodes
    rawGridElements.lines == expectedLines
    rawGridElements.transformer2Ws == expectedTransformer2Ws
    rawGridElements.transformer3Ws == jointGrid.rawGrid.transformer3Ws
    rawGridElements.switches == jointGrid.rawGrid.switches
    rawGridElements.measurementUnits == jointGrid.rawGrid.measurementUnits

    updatedGrid.systemParticipants() == jointGrid.systemParticipants
    updatedGrid.graphicElements() == jointGrid.graphics
  }

  def "The ContainerUtils can update a raw grid voltage correctly"() {
    given:
    def rawGrid = SampleJointGrid.grid().rawGrid
    def types = new ContainerUpdateUtils.Types(
        TypeTestData.lineTypes(),
        TypeTestData.transformer2WTypes(),
        TypeTestData.transformer3WTypes()
        )

    def changedNodeD = SampleJointGrid.nodeD.copy().voltLvl(MV_20KV).build()
    def changedNodeE = SampleJointGrid.nodeE.copy().voltLvl(MV_20KV).build()
    def changedNodeF = SampleJointGrid.nodeF.copy().voltLvl(MV_20KV).build()

    def expectedNodes = [
      SampleJointGrid.nodeA,
      SampleJointGrid.nodeB,
      SampleJointGrid.nodeC,
      changedNodeD,
      changedNodeE,
      changedNodeF,
      SampleJointGrid.nodeG,
    ] as Set

    def expectedLines = [
      SampleJointGrid.lineAB,
      SampleJointGrid.lineAC,
      SampleJointGrid.lineBC,
      SampleJointGrid.lineDE.copy().nodeA(changedNodeD).nodeB(changedNodeE).type(TypeTestData.lineType20kV_400).build(),
      SampleJointGrid.lineDF.copy().nodeA(changedNodeD).nodeB(changedNodeF).type(TypeTestData.lineType20kV_400).build(),
      SampleJointGrid.lineEF.copy().nodeA(changedNodeE).nodeB(changedNodeF).type(TypeTestData.lineType20kV_400).build()
    ] as Set


    def expectedTransformer2Ws =  [
      SampleJointGrid.transformerDtoA.copy().nodeA(changedNodeD).type(TypeTestData.transformerType20kV_LV_10).build(),
      SampleJointGrid.transformerGtoD.copy().nodeB(changedNodeD).type(TypeTestData.transformerTypeHV_20kV_40).build()
    ] as Set

    when:
    def updated = ContainerUpdateUtils.updateVoltage(rawGrid, 2, MV_20KV, types)

    then:
    Map<NodeInput, NodeInput> updatedOldToNew = updated.updatedOldToNewNodes()
    updatedOldToNew.keySet() == [
      SampleJointGrid.nodeA,
      SampleJointGrid.nodeD,
      SampleJointGrid.nodeE,
      SampleJointGrid.nodeF,
      SampleJointGrid.nodeG
    ] as Set

    updatedOldToNew.values().toSet() == [
      SampleJointGrid.nodeA,
      changedNodeD,
      changedNodeE,
      changedNodeF,
      SampleJointGrid.nodeG
    ] as Set

    RawGridElements rawGridElements = updated.rawGridElements()
    rawGridElements.nodes == expectedNodes
    rawGridElements.lines == expectedLines
    rawGridElements.transformer2Ws == expectedTransformer2Ws
    rawGridElements.transformer3Ws == rawGrid.transformer3Ws
    rawGridElements.switches == rawGrid.switches
    rawGridElements.measurementUnits == rawGrid.measurementUnits
  }

  def "The ContainerUtils can update a line voltages correctly"() {
    given:
    def line = GridTestData.lineFtoG
    def types = TypeTestData.lineTypes()
    def nodeA = line.nodeA.copy().voltLvl(MV_20KV).build()
    def nodeB = line.nodeB.copy().voltLvl(MV_20KV).build()

    when:
    def changedLine = line.copy().nodeA(nodeA).nodeB(nodeB).build()

    def actual = ContainerUpdateUtils.updateLineVoltages([changedLine] as Set, nodeB.getSubnet(), types)

    then:
    actual == [
      changedLine.copy().type(TypeTestData.lineType20kV_400).build()
    ] as Set
  }

  def "The ContainerUtils can update a transformer2W voltages correctly"() {
    given:
    def transformer = GridTestData.transformerBtoE // hv to 10 kV
    def types = TypeTestData.transformer2WTypes()
    def nodeB = transformer.nodeB.copy().voltLvl(MV_20KV).build()

    when:
    def changedTransformer = transformer.copy().nodeB(nodeB).build()

    def actual = ContainerUpdateUtils.updateTransformer2WVoltages([changedTransformer] as Set, nodeB.getSubnet(), types)

    then:
    actual == [
      changedTransformer.copy().type(TypeTestData.transformerTypeHV_20kV_40).build()
    ] as Set
  }

  def "The ContainerUtils can update a transformer3W voltages correctly"() {
    given:
    def transformer = GridTestData.transformerAtoBtoC // ehv to hv to 20 kV
    def types = TypeTestData.transformer3WTypes()
    def nodeB = transformer.nodeB.copy().voltLvl(MV_20KV).build()
    def nodeC = transformer.nodeB.copy().voltLvl(MV_10KV).build()

    when:
    def changedTransformer = transformer.copy().nodeB(nodeB).nodeC(nodeC).build()

    def actual = ContainerUpdateUtils.updateTransformer3WVoltages([changedTransformer] as Set, nodeB.getSubnet(), types)

    then:
    actual[0].type == TypeTestData.transformerTypeEHV_20kV_10kV
  }


  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // general utils

  def "The ContainerUtils can update a grid container with another correctly"() {
    given:
    def jointGrid = SampleJointGrid.grid()

    def expectedLineChanges = new HashSet(changedLines)
    expectedLineChanges.add( jointGrid.rawGrid.lines.find { it.getId() == "lineEtoF" }.copy().nodeA(changedNodeE).build())

    def expectedNodes = ContainerUpdateUtils.combineElements(jointGrid.rawGrid.nodes, [changedNodeE] as Set, addMissing).toSet()
    def expectedLines = ContainerUpdateUtils.combineElements(jointGrid.rawGrid.lines, expectedLineChanges, addMissing).toSet()


    when:
    def actual = ContainerUpdateUtils.updateContainers(jointGrid, changedContainer, addMissing)


    then:
    actual.rawGridElements().allEntitiesAsList().size() == expectedSize

    actual.rawGridElements().nodes == expectedNodes
    actual.rawGridElements().lines == expectedLines

    actual.systemParticipants() == jointGrid.systemParticipants
    actual.graphicElements() == jointGrid.graphics


    where:
    addMissing || expectedSize
    false      || 15
    true       || 16
  }

  def "The ContainerUtils can combine two collections of elements correctly"() {
    given:
    def jointGrid = SampleJointGrid.grid()
    def rawGrid = jointGrid.rawGrid
    def lines = rawGrid.lines

    def changedId = lines.first().copy().id("changedId").build()
    def changedUuid = lines.first().copy().uuid(UUID.randomUUID()).id("changedUuid").build()

    when:
    def updatedLines = ContainerUpdateUtils.combineElements(lines, [changedId, changedUuid], addMissing).toSet()

    then:
    updatedLines.size() == expectedSize
    updatedLines.contains(changedUuid) == addMissing

    where:
    addMissing || expectedSize
    false      || 6
    true       || 7
  }

  def "The ContainerUtils can get an update map correctly"() {
    given:
    def nodeA = SampleJointGrid.nodeA
    def nodeC = SampleJointGrid.nodeC
    def nodeF = SampleJointGrid.nodeF

    def changedNodeA = nodeA.copy().id("nodeA_changed").build()
    def changedNodeC = nodeC.copy().id("nodeC_changed").build()
    def changedNodeF = nodeF.copy().id("nodeF_changed").build()

    when:
    def actual = ContainerUpdateUtils.getUpdateMap([nodeA, nodeC, nodeF], [
      changedNodeA,
      changedNodeC,
      changedNodeF
    ])

    then:
    actual.size() == 3
    actual.get(nodeA) == changedNodeA
    actual.get(nodeC) == changedNodeC
    actual.get(nodeF) == changedNodeF
  }
}
