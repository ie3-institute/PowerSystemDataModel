/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import edu.ie3.datamodel.exceptions.InvalidGridException
import edu.ie3.datamodel.graph.DistanceWeightedGraph
import edu.ie3.datamodel.graph.ImpedanceWeightedGraph
import edu.ie3.datamodel.graph.SubGridTopologyGraph
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.SwitchInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.Transformer3WInput
import edu.ie3.datamodel.models.input.container.*
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel
import edu.ie3.test.common.ComplexTopology
import edu.ie3.test.common.GridTestData
import edu.ie3.test.helper.DoubleTestHelper
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import static edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils.*
import static edu.ie3.util.quantities.PowerSystemUnits.PU

class ContainerUtilsTest extends Specification {

  @Shared
  GridContainer complexTopology = ComplexTopology.grid

  def "The container utils filter raw grid elements correctly for a given subnet"() {
    when:
    RawGridElements actual = ContainerUtils.filterForSubnet(complexTopology.rawGrid, subnet)

    then:
    actual.nodes == expectedNodes
    actual.transformer2Ws == expectedTransformers2W
    actual.transformer3Ws == expectedTransformers3W
    /* TODO: Add lines, switches etc. to testing data */

    where:
    subnet || expectedNodes               || expectedTransformers2W || expectedTransformers3W
    1      || [
      ComplexTopology.nodeA,
      ComplexTopology.nodeB,
      ComplexTopology.nodeC
    ] as Set || [] as Set              || [
      ComplexTopology.transformerAtoBtoC
    ] as Set
    2      || [
      ComplexTopology.nodeA,
      ComplexTopology.nodeB,
      ComplexTopology.nodeC
    ] as Set || [] as Set              || [
      ComplexTopology.transformerAtoBtoC
    ] as Set
    3      || [
      ComplexTopology.nodeA,
      ComplexTopology.nodeB,
      ComplexTopology.nodeC
    ] as Set || [] as Set              || [
      ComplexTopology.transformerAtoBtoC
    ] as Set
    4      || [
      ComplexTopology.nodeB,
      ComplexTopology.nodeD
    ] as Set || [
      ComplexTopology.transformerBtoD
    ] as Set                 || [] as Set
    5      || [
      ComplexTopology.nodeB,
      ComplexTopology.nodeC,
      ComplexTopology.nodeE
    ] as Set || [
      ComplexTopology.transformerBtoE,
      ComplexTopology.transformerCtoE
    ] as Set                 || [] as Set
    6      || [
      ComplexTopology.nodeC,
      ComplexTopology.nodeF,
      ComplexTopology.nodeG
    ] as Set || [
      ComplexTopology.transformerCtoF,
      ComplexTopology.transformerCtoG
    ] as Set                 || [] as Set
  }

  def "The container utils are able to derive the predominant voltage level in a setup w/o switchgear"() {
    given:
    RawGridElements rawGrid = ContainerUtils.filterForSubnet(complexTopology.rawGrid, subnet)

    when:
    VoltageLevel actual = ContainerUtils.determinePredominantVoltLvl(rawGrid, subnet)

    then:
    actual == expected

    where:
    subnet || expected
    1      || EHV_380KV
    2      || HV
    3      || MV_20KV
    4      || MV_20KV
    5      || MV_10KV
    6      || LV
  }

  def "The container utils are able to derive the predominant voltage level in a simple setup w/ switchgear"() {
    given:
    def nodeA = Mock(NodeInput)
    nodeA.getUuid() >> UUID.fromString("0f2db57e-371d-4685-b92c-908ba6ca83d7")
    nodeA.getSubnet() >> 1
    nodeA.getVoltLvl() >> HV

    def nodeB = Mock(NodeInput)
    nodeB.getUuid() >> UUID.fromString("bca6d245-bf5f-4173-be53-a8d90afba072")
    nodeB.getSubnet() >> 1
    nodeB.getVoltLvl() >> HV

    def nodeC = Mock(NodeInput)
    nodeC.getUuid() >> UUID.fromString("a9c5e9d7-9f1d-48ab-93c5-b1d5d70c7913")
    nodeC.getSubnet() >> 1
    nodeC.getVoltLvl() >> HV

    def nodeD = Mock(NodeInput)
    nodeD.getUuid() >> UUID.fromString("440ea785-de2a-4822-99c1-7cea553a4e0b")
    nodeD.getSubnet() >> 1
    nodeD.getVoltLvl() >> HV

    def nodeE = Mock(NodeInput)
    nodeE.getUuid() >> UUID.fromString("df9fd3f4-95bd-4ca8-b7ef-bfc750a512bc")
    nodeE.getSubnet() >> 2
    nodeE.getVoltLvl() >> MV_10KV

    def switchAB = Mock(SwitchInput)
    switchAB.getUuid() >> UUID.fromString("f076f7b5-3d17-401a-a11e-7b310f866649")
    switchAB.allNodes() >> List.of(nodeA, nodeB)

    def switchBC = Mock(SwitchInput)
    switchBC.getUuid() >> UUID.fromString("1bfd0c52-7122-4677-a075-6c97a691d4b4")
    switchBC.allNodes() >> List.of(nodeB, nodeC)

    def switchCD = Mock(SwitchInput)
    switchCD.getUuid() >> UUID.fromString("01e6828b-8b7c-4d21-85cd-20891b64d530")
    switchCD.allNodes() >> List.of(nodeC, nodeD)

    def transformerDE = Mock(Transformer2WInput)
    transformerDE.getUuid() >> UUID.fromString("8ec5667a-437e-43ac-b6f4-948810c785be")
    transformerDE.getNodeA() >> nodeD
    transformerDE.allNodes() >> List.of(nodeD, nodeE)

    def rawGridElements = new RawGridElements([
      nodeA,
      nodeB,
      nodeC,
      nodeD,
      nodeE,
      switchAB,
      switchBC,
      switchCD,
      transformerDE
    ])

    def expected = MV_10KV

    when:
    def actual = ContainerUtils.determinePredominantVoltLvl(rawGridElements, 2)

    then:
    actual == expected
  }

  def "The container utils are able to derive the predominant voltage level in a simple three winding setup w/ switchgear"() {
    given:
    def nodeA = Mock(NodeInput)
    nodeA.getUuid() >> UUID.fromString("0f2db57e-371d-4685-b92c-908ba6ca83d7")
    nodeA.getSubnet() >> 1
    nodeA.getVoltLvl() >> HV

    def nodeB = Mock(NodeInput)
    nodeB.getUuid() >> UUID.fromString("bca6d245-bf5f-4173-be53-a8d90afba072")
    nodeB.getSubnet() >> 1
    nodeB.getVoltLvl() >> HV

    def nodeC = Mock(NodeInput)
    nodeC.getUuid() >> UUID.fromString("a9c5e9d7-9f1d-48ab-93c5-b1d5d70c7913")
    nodeC.getSubnet() >> 1
    nodeC.getVoltLvl() >> HV

    def nodeD = Mock(NodeInput)
    nodeD.getUuid() >> UUID.fromString("440ea785-de2a-4822-99c1-7cea553a4e0b")
    nodeD.getSubnet() >> 1
    nodeD.getVoltLvl() >> HV

    def nodeE = Mock(NodeInput)
    nodeE.getUuid() >> UUID.fromString("df9fd3f4-95bd-4ca8-b7ef-bfc750a512bc")
    nodeE.getSubnet() >> 2
    nodeE.getVoltLvl() >> MV_10KV

    def nodeF = Mock(NodeInput)
    nodeF.getUuid() >> UUID.fromString("d301d524-5638-4ee2-888a-f865c90ade71")
    nodeF.getSubnet() >> 3
    nodeF.getVoltLvl() >> LV

    def switchAB = Mock(SwitchInput)
    switchAB.getUuid() >> UUID.fromString("f076f7b5-3d17-401a-a11e-7b310f866649")
    switchAB.allNodes() >> List.of(nodeA, nodeB)

    def switchBC = Mock(SwitchInput)
    switchBC.getUuid() >> UUID.fromString("1bfd0c52-7122-4677-a075-6c97a691d4b4")
    switchBC.allNodes() >> List.of(nodeB, nodeC)

    def switchCD = Mock(SwitchInput)
    switchCD.getUuid() >> UUID.fromString("01e6828b-8b7c-4d21-85cd-20891b64d530")
    switchCD.allNodes() >> List.of(nodeC, nodeD)

    def transformerDEF = Mock(Transformer3WInput)
    transformerDEF.getUuid() >> UUID.fromString("8ec5667a-437e-43ac-b6f4-948810c785be")
    transformerDEF.getNodeA() >> nodeD
    transformerDEF.getNodeB() >> nodeE
    transformerDEF.getNodeC() >> nodeF
    transformerDEF.allNodes() >> List.of(nodeD, nodeE, nodeF)

    def rawGridElements = new RawGridElements([
      nodeA,
      nodeB,
      nodeC,
      nodeD,
      nodeE,
      nodeF,
      switchAB,
      switchBC,
      switchCD,
      transformerDEF
    ])

    when:
    def actual = ContainerUtils.determinePredominantVoltLvl(rawGridElements, 2)

    then:
    actual == MV_10KV

    when:
    def actual1 = ContainerUtils.determinePredominantVoltLvl(rawGridElements, 3)

    then:
    actual1 == LV
  }

  def "The container utils throw an exception, when there is an ambiguous voltage level in the grid w/o switchgear"() {
    given:
    RawGridElements rawGrid = ContainerUtils.filterForSubnet(complexTopology.rawGrid, 4)

    NodeInput corruptNode = new NodeInput(
        UUID.randomUUID(), "node_e", OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited()
        ,
        Quantities.getQuantity(1d, PU),
        false,
        null,
        MV_10KV,
        4)

    Set<NodeInput> corruptNodes = [corruptNode] as Set
    corruptNodes.addAll(rawGrid.nodes)

    RawGridElements dut = new RawGridElements(corruptNodes, rawGrid.lines, rawGrid.transformer2Ws,
        rawGrid.transformer3Ws, rawGrid.switches, rawGrid.measurementUnits)

    when:
    ContainerUtils.determinePredominantVoltLvl(dut, 4)

    then:
    InvalidGridException ex = thrown()
    ex.message == "There are 2 voltage levels apparent, although only one is expected. Following voltage levels" +
        " are present: CommonVoltageLevel{id='Mittelspannung', nominalVoltage=10 kV, synonymousIds=" +
        "[Mittelspannung, ms, ms_10kv, mv, mv_10kV], voltageRange=Interval [10 kV, 20 kV)}, CommonVoltageLevel" +
        "{id='Mittelspannung', nominalVoltage=20 kV, synonymousIds=[Mittelspannung, ms, ms_20kv, mv, mv_20kV], " +
        "voltageRange=Interval [20 kV, 30 kV)}"
  }

  def "The container util determines the set of subnet number correctly"() {
    expect:
    ContainerUtils.determineSubnetNumbers(ComplexTopology.grid.rawGrid.nodes) == [1, 2, 3, 4, 5, 6] as Set
  }

  def "The container util builds the sub grid containers correctly"() {
    given:
    String gridName = ComplexTopology.grid.gridName
    Set<Integer> subNetNumbers = ContainerUtils.determineSubnetNumbers(ComplexTopology.grid.rawGrid.nodes)
    RawGridElements rawGrid = ComplexTopology.grid.rawGrid
    SystemParticipants systemParticipants = ComplexTopology.grid.systemParticipants
    GraphicElements graphics = ComplexTopology.grid.graphics

    HashMap<Integer, SubGridContainer> expectedSubGrids = ComplexTopology.expectedSubGrids

    when:
    HashMap<Integer, SubGridContainer> actual = ContainerUtils.buildSubGridContainers(
        gridName,
        subNetNumbers,
        rawGrid,
        systemParticipants,
        graphics)

    then:
    actual.size() == 6
    for (Map.Entry<Integer, SubGridContainer> entry : actual) {
      int subnetNo = entry.key
      SubGridContainer actualSubGrid = entry.value
      SubGridContainer expectedSubGrid = expectedSubGrids.get(subnetNo)

      assert actualSubGrid == expectedSubGrid
    }
  }

  def "The container util returns copy of provided subgrids with slack nodes marked as expected"() {
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
    def computableSubgrids = subgrids.collectEntries {[(it.key): ContainerUtils.withTrafoNodeAsSlack(it.value)]} as HashMap<Integer, SubGridContainer>

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

  def "The container util builds the correct sub grid dependency graph"() {
    given:
    String gridName = ComplexTopology.grid.gridName
    Set<Integer> subNetNumbers = ContainerUtils.determineSubnetNumbers(ComplexTopology.grid.rawGrid.nodes)
    RawGridElements rawGrid = ComplexTopology.grid.rawGrid
    SystemParticipants systemParticipants = ComplexTopology.grid.systemParticipants
    GraphicElements graphics = ComplexTopology.grid.graphics
    Map<Integer, SubGridContainer> subgrids = ContainerUtils.buildSubGridContainers(
        gridName,
        subNetNumbers,
        rawGrid,
        systemParticipants,
        graphics)
    SubGridTopologyGraph expectedSubGridTopology = ComplexTopology.expectedSubGridTopology

    when:
    SubGridTopologyGraph actual = ContainerUtils.buildSubGridTopologyGraph(
        subgrids,
        ComplexTopology.grid.rawGrid)

    then:
    actual == expectedSubGridTopology
  }

  def "The container util builds the correct assembly of sub grids from basic information"() {
    given:
    String gridName = ComplexTopology.gridName
    RawGridElements rawGrid = ComplexTopology.grid.rawGrid
    SystemParticipants systemParticpants = ComplexTopology.grid.systemParticipants
    GraphicElements graphics = ComplexTopology.grid.graphics
    SubGridTopologyGraph expectedSubGridTopology = ComplexTopology.expectedSubGridTopology

    when:
    SubGridTopologyGraph actual = ContainerUtils.buildSubGridTopologyGraph(
        gridName,
        rawGrid,
        systemParticpants,
        graphics)

    then:
    actual == expectedSubGridTopology
  }

  def "The container utils build a joint model correctly from sub grids"() {
    given:
    Collection<SubGridContainer> subGridContainers = ComplexTopology.expectedSubGrids.values()
    JointGridContainer expected = ComplexTopology.grid

    when:
    JointGridContainer actual = ContainerUtils.combineToJointGrid(subGridContainers)

    then:
    actual == expected
  }

  def "The container utils build a valid distance weighted graph model correctly"(){
    given:
    def grid = ComplexTopology.grid

    when:
    def resultingGraphOpt = ContainerUtils.getDistanceTopologyGraph(grid)

    then:
    resultingGraphOpt.present
    def resultingGraph = resultingGraphOpt.get()

    resultingGraph.vertexSet() == ComplexTopology.grid.getRawGrid().getNodes()

    resultingGraph.edgeSet().size() == 7
  }

  def "The container utils build a valid instance of DistanceWeightedEdge during graph generation"(){
    given:
    def graph = new DistanceWeightedGraph()
    def nodeA = GridTestData.nodeA
    def nodeB = GridTestData.nodeB

    when:
    graph.addVertex(nodeA)
    graph.addVertex(nodeB)

    and:
    ContainerUtils.addDistanceGraphEdge(graph, nodeA, nodeB)

    then:
    graph.getEdge(nodeA, nodeB).with {
      assert weight == 913.5678707610981d
      assert source == nodeA
      assert target == nodeB
    }
  }

  def "The container utils build a valid impedance weighted graph model correctly"(){
    given:
    def grid = ComplexTopology.grid

    when:
    def resultingGraphOpt = ContainerUtils.getImpedanceTopologyGraph(grid)

    then:
    resultingGraphOpt.present
    def resultingGraph = resultingGraphOpt.get()

    resultingGraph.vertexSet() == ComplexTopology.grid.getRawGrid().getNodes()

    resultingGraph.edgeSet().size() == 7
  }

  def "The container utils build a valid instance of ImpedanceWeightedEdge during graph generation"(){
    given:
    def graph = new ImpedanceWeightedGraph()

    when:
    /* Add a transformer */
    def transformer = GridTestData.transformerBtoD
    graph.addVertex(transformer.getNodeA())
    graph.addVertex(transformer.getNodeB())
    ContainerUtils.addImpedanceGraphEdge(graph, transformer)

    and:
    /* Add a line */
    def line = GridTestData.lineCtoD
    graph.addVertex(line.getNodeA())
    graph.addVertex(line.getNodeB())
    ContainerUtils.addImpedanceGraphEdge(graph, line)

    and:
    /* Add a closed switch */
    def swtchClosed = new SwitchInput(UUID.randomUUID(), "test_switch", GridTestData.nodeD, GridTestData.nodeE, true)
    graph.addVertex(swtchClosed.getNodeA())
    graph.addVertex(swtchClosed.getNodeB())
    ContainerUtils.addImpedanceGraphEdge(graph, swtchClosed)
    def swtchOpen = new SwitchInput(UUID.randomUUID(), "test_switch", GridTestData.nodeE, GridTestData.nodeF, false)
    graph.addVertex(swtchOpen.getNodeA())
    graph.addVertex(swtchOpen.getNodeB())
    ContainerUtils.addImpedanceGraphEdge(graph, swtchOpen)

    and:
    /* Add a three winding transformer */
    def transformer3w = GridTestData.transformerAtoBtoC
    graph.addVertex(transformer3w.getNodeA())
    graph.addVertex(transformer3w.getNodeB())
    ContainerUtils.addImpedanceGraphEdge(graph, transformer3w)

    then:
    graph.vertexSet().size() == 6
    graph.edgeSet().size() == 5

    /* Check impedance of two winding transformer */
    graph.getEdge(transformer.nodeA, transformer.nodeB).with {
      assert DoubleTestHelper.equalsWithTolerance(weight, 112.33121875062159d, 1E-6)
    }
    /* Check impedance of three winding transformer */
    graph.getEdge(transformer3w.nodeA, transformer3w.nodeB).with {
      assert DoubleTestHelper.equalsWithTolerance(weight, 1.1278408575681236d, 1E-6)
    }
    graph.getEdge(transformer3w.nodeA, transformer3w.nodeC).with {
      assert DoubleTestHelper.equalsWithTolerance(weight, 1.0471340124358486d, 1E-6)
    }
    /* Check impedance of line */
    graph.getEdge(line.nodeA, line.nodeB).with {
      assert DoubleTestHelper.equalsWithTolerance(weight, 0.0016909597866300665d, 1E-6)
    }
    /* Check impedance of switch */
    graph.getEdge(swtchClosed.nodeA, swtchClosed.nodeB).with {
      assert DoubleTestHelper.equalsWithTolerance(weight, 1.0d, 1E-6)
    }
  }

  /* TODO: Extend testing data so that,
   *   - filtering of system participants can be tested
   *   - filtering of graphic elements can be tested */

  def "Traversing along a simple switch chain returns the correct list of traveled nodes"() {
    given:
    def nodeA = Mock(NodeInput)
    def nodeB = Mock(NodeInput)
    def nodeC = Mock(NodeInput)
    def nodeD = Mock(NodeInput)

    def switchAB = Mock(SwitchInput)
    switchAB.getNodeA() >> nodeA
    switchAB.getNodeB() >> nodeB
    switchAB.allNodes() >> List.of(nodeA, nodeB)
    def switchBC = Mock(SwitchInput)
    switchBC.getNodeA() >> nodeB
    switchBC.getNodeB() >> nodeC
    switchBC.allNodes() >> List.of(nodeB, nodeC)
    def switchCD = Mock(SwitchInput)
    switchCD.getNodeA() >> nodeC
    switchCD.getNodeB() >> nodeD
    switchCD.allNodes() >> List.of(nodeC, nodeD)

    def switches = new HashSet<SwitchInput>()
    switches.add(switchAB)
    switches.add(switchBC)
    switches.add(switchCD)

    def possibleJunctions = new HashSet<NodeInput>()

    def expected = new LinkedList<NodeInput>()
    expected.addFirst(nodeA)
    expected.addLast(nodeB)
    expected.addLast(nodeC)
    expected.addLast(nodeD)

    when:
    def actual = ContainerUtils.traverseAlongSwitchChain(nodeA, switches, possibleJunctions)

    then:
    actual == expected
  }

  def "Traversing along a switch chain with intermediate junction returns the correct list of traveled nodes"() {
    given:
    def nodeA = Mock(NodeInput)
    def nodeB = Mock(NodeInput)
    def nodeC = Mock(NodeInput)
    def nodeD = Mock(NodeInput)

    def switchAB = Mock(SwitchInput)
    switchAB.getNodeA() >> nodeA
    switchAB.getNodeB() >> nodeB
    switchAB.allNodes() >> List.of(nodeA, nodeB)
    def switchBC = Mock(SwitchInput)
    switchBC.getNodeA() >> nodeB
    switchBC.getNodeB() >> nodeC
    switchBC.allNodes() >> List.of(nodeB, nodeC)
    def switchCD = Mock(SwitchInput)
    switchCD.getNodeA() >> nodeC
    switchCD.getNodeB() >> nodeD
    switchCD.allNodes() >> List.of(nodeC, nodeD)

    def switches = new HashSet<SwitchInput>()
    switches.add(switchAB)
    switches.add(switchBC)
    switches.add(switchCD)

    def possibleJunctions = new HashSet<NodeInput>()
    possibleJunctions.add(nodeC)

    def expected = new LinkedList<NodeInput>()
    expected.addFirst(nodeA)
    expected.addLast(nodeB)
    expected.addLast(nodeC)

    when:
    def actual = ContainerUtils.traverseAlongSwitchChain(nodeA, switches, possibleJunctions)

    then:
    actual == expected
  }

  def "Traversing along a non existing switch chain returns the correct list of traveled nodes"() {
    given:
    def nodeA = Mock(NodeInput)

    def switches = new HashSet<SwitchInput>()

    def possibleJunctions = new HashSet<NodeInput>()

    def expected = new LinkedList<NodeInput>()
    expected.addFirst(nodeA)

    when:
    def actual = ContainerUtils.traverseAlongSwitchChain(nodeA, switches, possibleJunctions)

    then:
    actual == expected
  }

  def "Traversing along a cyclic switch chain throws an exception"() {
    given:
    def nodeA = Mock(NodeInput)
    def nodeB = Mock(NodeInput)
    def nodeC = Mock(NodeInput)

    def switchAB = Mock(SwitchInput)
    switchAB.getNodeA() >> nodeA
    switchAB.getNodeB() >> nodeB
    switchAB.allNodes() >> List.of(nodeA, nodeB)
    def switchBC = Mock(SwitchInput)
    switchBC.getNodeA() >> nodeB
    switchBC.getNodeB() >> nodeC
    switchBC.allNodes() >> List.of(nodeB, nodeC)
    def switchCA = Mock(SwitchInput)
    switchCA.getNodeA() >> nodeC
    switchCA.getNodeB() >> nodeA
    switchCA.allNodes() >> List.of(nodeC, nodeA)

    def switches = new HashSet<SwitchInput>()
    switches.add(switchAB)
    switches.add(switchBC)
    switches.add(switchCA)

    def possibleJunctions = new HashSet<NodeInput>()

    when:
    ContainerUtils.traverseAlongSwitchChain(nodeA, switches, possibleJunctions)

    then:
    IllegalArgumentException ex = thrown()
    ex.message == "Cannot traverse along switch chain, as there is a junction included at node Mock for type " +
        "'NodeInput' named 'nodeA'"
  }

  def "Traversing along a switch chain with switch junction throws an exception"() {
    given:
    def nodeA = Mock(NodeInput)
    def nodeB = Mock(NodeInput)
    def nodeC = Mock(NodeInput)
    def nodeD = Mock(NodeInput)

    def switchAB = Mock(SwitchInput)
    switchAB.getNodeA() >> nodeA
    switchAB.getNodeB() >> nodeB
    switchAB.allNodes() >> List.of(nodeA, nodeB)
    def switchBC = Mock(SwitchInput)
    switchBC.getNodeA() >> nodeB
    switchBC.getNodeB() >> nodeC
    switchBC.allNodes() >> List.of(nodeB, nodeC)
    def switchBD = Mock(SwitchInput)
    switchBD.getNodeA() >> nodeB
    switchBD.getNodeB() >> nodeD
    switchBD.allNodes() >> List.of(nodeB, nodeD)

    def switches = new HashSet<SwitchInput>()
    switches.add(switchAB)
    switches.add(switchBC)
    switches.add(switchBD)

    def possibleJunctions = new HashSet<NodeInput>()

    when:
    ContainerUtils.traverseAlongSwitchChain(nodeA, switches, possibleJunctions)

    then:
    IllegalArgumentException ex = thrown()
    ex.message == "Cannot traverse along switch chain, as there is a junction included at node Mock for type " +
        "'NodeInput' named 'nodeB'"
  }

  def "Determining the surrounding sub grid containers of a two winding transformer w/o switchgear works fine"() {
    given:
    def nodeD = Mock(NodeInput)
    nodeD.getUuid() >> UUID.fromString("ae4869d5-3551-4cce-a101-d61629716c4f")
    nodeD.getSubnet() >> 1
    def nodeE = Mock(NodeInput)
    nodeE.getUuid() >> UUID.fromString("5d4107b2-385b-40fe-a668-19414bf45d9d")
    nodeE.getSubnet() >> 2

    def transformer = Mock(Transformer2WInput)
    transformer.getUuid() >> UUID.fromString("ddcdd72a-5f97-4bef-913b-d32d31216e27")
    transformer.getNodeA() >> nodeD
    transformer.getNodeB() >> nodeE
    transformer.allNodes() >> List.of(nodeD, nodeE)

    def rawGridElements = new RawGridElements([nodeD, nodeE, transformer])

    def subGrid1 = Mock(SubGridContainer)
    def subGrid2 = Mock(SubGridContainer)
    def subGridMapping = [
      1: subGrid1,
      2: subGrid2
    ]

    def expected = new ContainerUtils.TransformerSubGridContainers(subGrid1, subGrid2)

    when:
    def actual = ContainerUtils.getSubGridContainers(transformer, rawGridElements, subGridMapping)

    then:
    actual == expected
  }

  def "Determining the surrounding sub grid containers of a two winding transformer w/ switchgear works fine"() {
    given:
    def nodeA = Mock(NodeInput)
    nodeA.getUuid() >> UUID.fromString("a37b2501-70c5-479f-92f9-d5b0e4628b2b")
    nodeA.getSubnet() >> 1
    def nodeB = Mock(NodeInput)
    nodeB.getUuid() >> UUID.fromString("8361b082-9d4c-4c54-97d0-2df9ac35333c")
    nodeB.getSubnet() >> 2
    def nodeC = Mock(NodeInput)
    nodeC.getUuid() >> UUID.fromString("b9e4f16b-0317-4794-9f53-339db45a2092")
    nodeC.getSubnet() >> 2
    def nodeD = Mock(NodeInput)
    nodeD.getUuid() >> UUID.fromString("ae4869d5-3551-4cce-a101-d61629716c4f")
    nodeD.getSubnet() >> 2
    def nodeE = Mock(NodeInput)
    nodeE.getUuid() >> UUID.fromString("5d4107b2-385b-40fe-a668-19414bf45d9d")
    nodeE.getSubnet() >> 2

    def transformer = Mock(Transformer2WInput)
    transformer.getUuid() >> UUID.fromString("ddcdd72a-5f97-4bef-913b-d32d31216e27")
    transformer.getNodeA() >> nodeD
    transformer.getNodeB() >> nodeE
    transformer.allNodes() >> List.of(nodeD, nodeE)

    def switchAB = Mock(SwitchInput)
    switchAB.getUuid() >> UUID.fromString("5fcb8705-1436-4fbe-97b3-d2dcaf6a783b")
    switchAB.allNodes() >> List.of(nodeA, nodeB)
    def switchBC = Mock(SwitchInput)
    switchBC.getUuid() >> UUID.fromString("4ca81b0b-e06d-408e-a991-de140f4e229b")
    switchBC.allNodes() >> List.of(nodeB, nodeC)
    def switchCD = Mock(SwitchInput)
    switchCD.getUuid() >> UUID.fromString("92ce075e-9e3b-4ee6-89b6-19e6372fba01")
    switchCD.allNodes() >> List.of(nodeC, nodeD)

    def rawGridElements = new RawGridElements([
      nodeA,
      nodeB,
      nodeC,
      nodeD,
      nodeE,
      transformer,
      switchAB,
      switchBC,
      switchCD
    ])

    def subGrid1 = Mock(SubGridContainer)
    def subGrid2 = Mock(SubGridContainer)
    def subGridMapping = [
      1: subGrid1,
      2: subGrid2
    ]

    def expected = new ContainerUtils.TransformerSubGridContainers(subGrid1, subGrid2)

    when:
    def actual = ContainerUtils.getSubGridContainers(transformer, rawGridElements, subGridMapping)

    then:
    actual == expected
  }
}
