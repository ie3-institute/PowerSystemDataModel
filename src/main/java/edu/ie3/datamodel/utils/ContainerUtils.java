/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import static edu.ie3.util.quantities.PowerSystemUnits.KILOMETRE;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static tech.units.indriya.unit.Units.OHM;

import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.exceptions.TopologyException;
import edu.ie3.datamodel.graph.*;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.container.*;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import edu.ie3.util.quantities.interfaces.SpecificResistance;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Length;
import org.jgrapht.graph.DirectedMultigraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

/** Offers functionality useful for grouping different models together */
public class ContainerUtils {

  private static final Logger log = LoggerFactory.getLogger(ContainerUtils.class);

  private ContainerUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  /**
   * Returns the topology of the provided grid container as a {@link DistanceWeightedGraph} if the
   * provided grid container's {@link RawGridElements} allows the creation of a valid topology graph
   * or an empty optional otherwise.
   *
   * @param grid the grid container that should be converted into topology graph
   * @return either an optional holding the distance topology graph instance or an empty optional
   */
  public static Optional<DistanceWeightedGraph> getDistanceTopologyGraph(GridContainer grid) {
    return getDistanceTopologyGraph(grid.getRawGrid());
  }

  /**
   * Returns the topology of the provided {@link RawGridElements} as a {@link
   * DistanceWeightedGraph}, if they allow the creation of a valid topology graph or an empty
   * optional otherwise.
   *
   * @param rawGridElements raw grids elements as base of the distance weighted topology graph
   * @return either an optional holding the distance topology graph instance or an empty optional
   */
  public static Optional<DistanceWeightedGraph> getDistanceTopologyGraph(
      RawGridElements rawGridElements) {

    DistanceWeightedGraph graph = new DistanceWeightedGraph();

    try {
      rawGridElements.getNodes().forEach(graph::addVertex);
    } catch (NullPointerException ex) {
      log.error("At least one node entity of provided RawGridElements is null. ", ex);
      return Optional.empty();
    }

    try {
      rawGridElements
          .getLines()
          .forEach(
              line -> {
                graph.addEdge(line.getNodeA(), line.getNodeB());
                graph.setEdgeWeight(
                    graph.getEdge(line.getNodeA(), line.getNodeB()), line.getLength());
              });
    } catch (NullPointerException | IllegalArgumentException | UnsupportedOperationException ex) {
      log.error("Error adding line edges to graph: ", ex);
      return Optional.empty();
    }

    try {
      rawGridElements
          .getSwitches()
          .forEach(
              switchInput ->
                  addDistanceGraphEdge(graph, switchInput.getNodeA(), switchInput.getNodeB()));
    } catch (NullPointerException | IllegalArgumentException | UnsupportedOperationException ex) {
      log.error("Error adding switch edges to graph: ", ex);
      return Optional.empty();
    }

    try {
      rawGridElements
          .getTransformer2Ws()
          .forEach(trafo2w -> addDistanceGraphEdge(graph, trafo2w.getNodeA(), trafo2w.getNodeB()));
    } catch (NullPointerException | IllegalArgumentException | UnsupportedOperationException ex) {
      log.error("Error adding 2 winding transformer edges to graph: ", ex);
      return Optional.empty();
    }

    try {
      rawGridElements
          .getTransformer3Ws()
          .forEach(
              trafo3w -> {
                addDistanceGraphEdge(graph, trafo3w.getNodeA(), trafo3w.getNodeB());
                addDistanceGraphEdge(graph, trafo3w.getNodeA(), trafo3w.getNodeC());
              });
    } catch (NullPointerException | IllegalArgumentException | UnsupportedOperationException ex) {
      log.error("Error adding 3 winding transformer edges to graph: ", ex);
      return Optional.empty();
    }

    // if we reached this point, we can safely return a valid graph
    return Optional.of(graph);
  }

  /**
   * Adds an {@link DistanceWeightedEdge} to the provided graph between the provided nodes a and b.
   * By implementation of jGraphT this side effect cannot be removed. :(
   *
   * @param graph the graph to be altered
   * @param nodeA start node of the new edge
   * @param nodeB end node of the new edge
   */
  private static void addDistanceGraphEdge(
      DistanceWeightedGraph graph, NodeInput nodeA, NodeInput nodeB) {
    graph.addEdge(nodeA, nodeB);
    graph.setEdgeWeight(
        graph.getEdge(nodeA, nodeB), GridAndGeoUtils.distanceBetweenNodes(nodeA, nodeB));
  }

  /**
   * Returns the topology of the provided grid container as a {@link ImpedanceWeightedGraph} if the
   * provided grid container's {@link RawGridElements} allows the creation of a valid topology graph
   * or an empty optional otherwise.
   *
   * @param grid the grid container that should be converted into topology graph
   * @return either an optional holding the impedance topology graph instance or an empty optional
   */
  public static Optional<ImpedanceWeightedGraph> getImpedanceTopologyGraph(GridContainer grid) {
    return getImpedanceTopologyGraph(grid.getRawGrid());
  }

  /**
   * Returns the topology of the provided {@link RawGridElements} as a {@link
   * ImpedanceWeightedGraph}, if they allow the creation of a valid topology graph or an empty
   * optional otherwise.
   *
   * @param rawGridElements raw grids elements as base of the distance weighted topology graph
   * @return either an optional holding the impedance topology graph instance or an empty optional
   */
  public static Optional<ImpedanceWeightedGraph> getImpedanceTopologyGraph(
      RawGridElements rawGridElements) {

    ImpedanceWeightedGraph graph = new ImpedanceWeightedGraph();

    try {
      rawGridElements.getNodes().forEach(graph::addVertex);
    } catch (NullPointerException ex) {
      log.error("At least one node entity of provided RawGridElements is null. ", ex);
      return Optional.empty();
    }

    try {
      rawGridElements.getLines().forEach(line -> addImpedanceGraphEdge(graph, line));
    } catch (NullPointerException | IllegalArgumentException | UnsupportedOperationException ex) {
      log.error("Error adding line edges to graph: ", ex);
      return Optional.empty();
    }

    try {
      rawGridElements
          .getSwitches()
          .forEach(
              switchInput -> {
                if (switchInput.isClosed()) addImpedanceGraphEdge(graph, switchInput);
              });
    } catch (NullPointerException | IllegalArgumentException | UnsupportedOperationException ex) {
      log.error("Error adding switch edges to graph: ", ex);
      return Optional.empty();
    }

    try {
      rawGridElements.getTransformer2Ws().forEach(trafo2w -> addImpedanceGraphEdge(graph, trafo2w));
    } catch (NullPointerException | IllegalArgumentException | UnsupportedOperationException ex) {
      log.error("Error adding 2 winding transformer edges to graph: ", ex);
      return Optional.empty();
    }

    try {
      rawGridElements.getTransformer3Ws().forEach(trafo3w -> addImpedanceGraphEdge(graph, trafo3w));
    } catch (NullPointerException | IllegalArgumentException | UnsupportedOperationException ex) {
      log.error("Error adding 3 winding transformer edges to graph: ", ex);
      return Optional.empty();
    }

    // if we reached this point, we can safely return a valid graph
    return Optional.of(graph);
  }

  /**
   * Adds an {@link ImpedanceWeightedEdge} to the provided graph between the provided nodes a and b.
   * By implementation of jGraphT this side effect cannot be removed. :(
   *
   * @param graph the graph to be altered
   * @param connectorInput the connector input element
   */
  private static void addImpedanceGraphEdge(
      ImpedanceWeightedGraph graph, ConnectorInput connectorInput) {
    NodeInput nodeA = connectorInput.getNodeA();
    NodeInput nodeB = connectorInput.getNodeB();
    /* Add an edge if it is not a switch or the switch is closed */
    if (!(connectorInput instanceof SwitchInput sw) || sw.isClosed()) graph.addEdge(nodeA, nodeB);

    if (connectorInput instanceof LineInput line) {
      graph.setEdgeWeightQuantity(
          graph.getEdge(nodeA, nodeB),
          calcImpedance(line.getType().getR(), line.getType().getX(), line.getLength()));
    }
    if (connectorInput instanceof SwitchInput sw && sw.isClosed()) {
      // assumption: closed switch has a resistance of 1 OHM
      graph.setEdgeWeightQuantity(graph.getEdge(nodeA, nodeB), Quantities.getQuantity(1d, OHM));
    }
    if (connectorInput instanceof Transformer2WInput trafo2w) {
      graph.setEdgeWeightQuantity(
          graph.getEdge(nodeA, nodeB),
          calcImpedance(trafo2w.getType().getrSc(), trafo2w.getType().getxSc()));
    }
    if (connectorInput instanceof Transformer3WInput trafo3w) {
      graph.addEdge(nodeA, trafo3w.getNodeC());

      graph.setEdgeWeightQuantity(
          graph.getEdge(nodeA, nodeB),
          calcImpedance(
              trafo3w.getType().getrScA().add(trafo3w.getType().getrScB()),
              trafo3w.getType().getxScA().add(trafo3w.getType().getxScB())));

      graph.setEdgeWeightQuantity(
          graph.getEdge(nodeA, trafo3w.getNodeC()),
          calcImpedance(
              trafo3w.getType().getrScA().add(trafo3w.getType().getrScC()),
              trafo3w.getType().getxScA().add(trafo3w.getType().getxScC())));
    }
  }

  /**
   * Calculate the total magnitude of the complex impedance, defined by relative resistance,
   * reactance and an equivalent length
   *
   * @param r Relative resistance
   * @param x Relative reactance
   * @param length Length of the element
   * @return Magnitude of the complex impedance
   */
  private static ComparableQuantity<ElectricResistance> calcImpedance(
      ComparableQuantity<SpecificResistance> r,
      ComparableQuantity<SpecificResistance> x,
      ComparableQuantity<Length> length) {
    return calcImpedance(
        r.multiply(length.to(KILOMETRE)).asType(ElectricResistance.class).to(OHM),
        x.multiply(length.to(KILOMETRE)).asType(ElectricResistance.class).to(OHM));
  }

  /**
   * Calculate the magnitude of the complex impedance from given resistance and reactance
   *
   * @param r Resistance (real part of the complex impedance)
   * @param x Reactance (complex part of the complex impedance)
   * @return Magnitude of the complex impedance
   */
  private static ComparableQuantity<ElectricResistance> calcImpedance(
      ComparableQuantity<ElectricResistance> r, ComparableQuantity<ElectricResistance> x) {
    double zValue =
        sqrt(
            pow(r.to(OHM).getValue().doubleValue(), 2)
                + pow(x.to(OHM).getValue().doubleValue(), 2));
    return Quantities.getQuantity(zValue, OHM);
  }

  /**
   * Filters all raw grid elements for the provided subnet. For each transformer all nodes (and not
   * only the node of the grid the transformer is located in) are added as well. Two winding
   * transformers are counted, if the low voltage node is in the queried subnet. Three winding
   * transformers are counted, as long as any of the three nodes is in the queried subnet.
   *
   * @param input The model to filter
   * @param subnet The filter criterion
   * @return A {@link RawGridElements} filtered for the subnet
   */
  public static RawGridElements filterForSubnet(RawGridElements input, int subnet) {
    Set<NodeInput> nodes =
        input.getNodes().stream()
            .filter(node -> node.getSubnet() == subnet)
            .collect(Collectors.toSet());

    Set<LineInput> lines =
        input.getLines().stream()
            .filter(line -> line.getNodeB().getSubnet() == subnet)
            .collect(Collectors.toSet());

    Set<Transformer2WInput> transformer2w =
        input.getTransformer2Ws().stream()
            .filter(transformer -> transformer.getNodeB().getSubnet() == subnet)
            .collect(Collectors.toSet());
    /* Add the higher voltage node to the set of nodes */
    nodes.addAll(
        transformer2w.stream().map(Transformer2WInput::getNodeA).collect(Collectors.toSet()));

    Set<Transformer3WInput> transformer3w =
        input.getTransformer3Ws().stream()
            .filter(
                transformer ->
                    transformer.getNodeA().getSubnet() == subnet
                        || transformer.getNodeB().getSubnet() == subnet
                        || transformer.getNodeC().getSubnet() == subnet)
            .collect(Collectors.toSet());
    /* Add all nodes of a three winding transformer node to the set of nodes */
    nodes.addAll(
        transformer3w.stream()
            .flatMap(
                transformer ->
                    Stream.of(
                        transformer.getNodeA(), transformer.getNodeB(), transformer.getNodeC()))
            .collect(Collectors.toSet()));

    Set<SwitchInput> switches =
        input.getSwitches().stream()
            .filter(switcher -> switcher.getNodeB().getSubnet() == subnet)
            .collect(Collectors.toSet());

    Set<MeasurementUnitInput> measurements =
        input.getMeasurementUnits().stream()
            .filter(measurement -> measurement.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());

    return new RawGridElements(nodes, lines, transformer2w, transformer3w, switches, measurements);
  }

  /**
   * Filters all system participants for the provided subnet.
   *
   * @param input The model to filter
   * @param subnet The filter criterion
   * @return A {@link SystemParticipants} filtered for the subnet
   */
  public static SystemParticipants filterForSubnet(SystemParticipants input, int subnet) {
    Set<BmInput> bmPlants = filterParticipants(input.getBmPlants(), subnet);
    Set<ChpInput> chpPlants = filterParticipants(input.getChpPlants(), subnet);
    Set<EvcsInput> evcsInputs = filterParticipants(input.getEvcs(), subnet);
    Set<EvInput> evs = filterParticipants(input.getEvs(), subnet);
    Set<FixedFeedInInput> fixedFeedIns = filterParticipants(input.getFixedFeedIns(), subnet);
    Set<HpInput> heatpumps = filterParticipants(input.getHeatPumps(), subnet);
    Set<LoadInput> loads = filterParticipants(input.getLoads(), subnet);
    Set<PvInput> pvs = filterParticipants(input.getPvPlants(), subnet);
    Set<StorageInput> storages = filterParticipants(input.getStorages(), subnet);
    Set<WecInput> wecPlants = filterParticipants(input.getWecPlants(), subnet);

    return new SystemParticipants(
        bmPlants,
        chpPlants,
        evcsInputs,
        evs,
        fixedFeedIns,
        heatpumps,
        loads,
        pvs,
        storages,
        wecPlants);
  }

  /**
   * Filter sets of system participants for their subnet,
   *
   * @param systemParticipantInputs Set of SystemParticipantInputs
   * @param subnet Filter criterion
   * @param <T> Type parameter of the system participant
   * @return A filtered set
   */
  private static <T extends SystemParticipantInput> Set<T> filterParticipants(
      Set<T> systemParticipantInputs, int subnet) {
    return systemParticipantInputs.stream()
        .filter(entity -> entity.getNode().getSubnet() == subnet)
        .collect(Collectors.toSet());
  }

  /**
   * Filters all graphic elements for the provided subnet.
   *
   * @param input The model to filter
   * @param subnet The filter criterion
   * @return A {@link GraphicElements} filtered for the subnet
   */
  public static GraphicElements filterForSubnet(GraphicElements input, int subnet) {
    Set<NodeGraphicInput> nodeGraphics =
        input.getNodeGraphics().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());
    Set<LineGraphicInput> lineGraphics =
        input.getLineGraphics().stream()
            .filter(entity -> entity.getLine().getNodeB().getSubnet() == subnet)
            .collect(Collectors.toSet());

    return new GraphicElements(nodeGraphics, lineGraphics);
  }

  /**
   * Determining the predominant voltage level in this grid by counting the occurrences of the
   * different voltage levels
   *
   * @param rawGrid Raw grid elements of the specified sub grid
   * @param subnet Subnet number of the subnet
   * @return The predominant voltage level in this grid
   * @throws InvalidGridException If not a single, predominant voltage level can be determined
   */
  public static VoltageLevel determinePredominantVoltLvl(RawGridElements rawGrid, int subnet)
      throws InvalidGridException {
    /* Exclude all nodes, that are at the high voltage side of the transformer */
    Set<NodeInput> gridNodes = new HashSet<>(rawGrid.getNodes());
    gridNodes.removeAll(
        /* Remove all nodes, that are upstream of transformers, this comprises all those, that are connected by
         * switches */
        rawGrid.getTransformer2Ws().stream()
            .flatMap(
                transformer ->
                    ContainerUtils.traverseAlongSwitchChain(transformer.getNodeA(), rawGrid)
                        .stream())
            .collect(Collectors.toSet()));
    gridNodes.removeAll(
        rawGrid.getTransformer3Ws().stream()
            .flatMap(
                transformer -> {
                  if (transformer.getNodeA().getSubnet() == subnet)
                    return Stream.of(transformer.getNodeB(), transformer.getNodeC());
                  else if (transformer.getNodeB().getSubnet() == subnet)
                    return Stream.concat(
                        ContainerUtils.traverseAlongSwitchChain(transformer.getNodeA(), rawGrid)
                            .stream(),
                        Stream.of(transformer.getNodeC(), transformer.getNodeInternal()));
                  else
                    return Stream.concat(
                        ContainerUtils.traverseAlongSwitchChain(transformer.getNodeA(), rawGrid)
                            .stream(),
                        Stream.of(transformer.getNodeB(), transformer.getNodeInternal()));
                })
            .collect(Collectors.toSet()));

    /* Build a mapping, which voltage level appears how often */
    Map<VoltageLevel, Long> voltageLevelCount =
        gridNodes.stream()
            .map(NodeInput::getVoltLvl)
            .collect(Collectors.groupingBy(voltLvl -> voltLvl, Collectors.counting()));

    /* At this point only one voltage level should be apparent */
    int amountOfVoltLvl = voltageLevelCount.size();
    if (amountOfVoltLvl > 1)
      throw new InvalidGridException(
          "There are "
              + amountOfVoltLvl
              + " voltage levels apparent, although only one is expected. Following voltage levels are present: "
              + voltageLevelCount.keySet().stream()
                  .sorted(Comparator.comparing(VoltageLevel::getNominalVoltage))
                  .map(VoltageLevel::toString)
                  .collect(Collectors.joining(", ")));

    return voltageLevelCount.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElseThrow(
            () ->
                new InvalidGridException(
                    "Cannot determine the predominant voltage level. Following voltage levels are present: "
                        + voltageLevelCount.keySet().stream()
                            .sorted(Comparator.comparing(VoltageLevel::getNominalVoltage))
                            .map(VoltageLevel::toString)
                            .collect(Collectors.joining(", "))));
  }

  /**
   * Disassembles this grid model into sub grid models and returns a topology of the sub grids as a
   * directed, immutable graph. The direction points from higher to lower voltage level.
   *
   * @param gridName Name of the grid
   * @param rawGrid Container model of raw grid elements
   * @param systemParticipants Container model of system participants
   * @param graphics Container element of graphic elements
   * @return An immutable, directed graph of sub grid topologies.
   */
  public static SubGridTopologyGraph buildSubGridTopologyGraph(
      String gridName,
      RawGridElements rawGrid,
      SystemParticipants systemParticipants,
      EnergyManagementUnits energyManagementUnits,
      GraphicElements graphics)
      throws InvalidGridException {
    /* Collect the different sub nets. Through the validation of lines, it is ensured, that no galvanically connected
     * grid has more than one subnet number assigned */
    SortedSet<Integer> subnetNumbers = determineSubnetNumbers(rawGrid.getNodes());

    /* Build the single sub grid models */
    HashMap<Integer, SubGridContainer> subgrids =
        buildSubGridContainers(
            gridName, subnetNumbers, rawGrid, systemParticipants, energyManagementUnits, graphics);

    /* Build the graph structure denoting the topology of the grid */
    return buildSubGridTopologyGraph(subgrids, rawGrid);
  }

  /**
   * Determine a distinct set of apparent subnet numbers
   *
   * @param nodes Set of nodes
   * @return A sorted set of subnet numbers
   */
  private static SortedSet<Integer> determineSubnetNumbers(Set<NodeInput> nodes) {
    return nodes.stream().map(NodeInput::getSubnet).collect(Collectors.toCollection(TreeSet::new));
  }

  /**
   * Build a mapping from sub net number to actual {@link SubGridContainer}
   *
   * @param gridName Name of the grid
   * @param subnetNumbers Set of available subnet numbers
   * @param rawGrid Container model with all raw grid elements
   * @param systemParticipants Container model with all system participant inputs
   * @param graphics Container model with all graphic elements
   * @return A mapping from subnet number to container model with sub grid elements
   */
  private static HashMap<Integer, SubGridContainer> buildSubGridContainers(
      String gridName,
      SortedSet<Integer> subnetNumbers,
      RawGridElements rawGrid,
      SystemParticipants systemParticipants,
      EnergyManagementUnits energyManagementUnits,
      GraphicElements graphics)
      throws InvalidGridException {
    HashMap<Integer, SubGridContainer> subGrids = new HashMap<>(subnetNumbers.size());
    for (int subnetNumber : subnetNumbers) {
      RawGridElements rawGridElements = ContainerUtils.filterForSubnet(rawGrid, subnetNumber);
      SystemParticipants systemParticipantElements =
          ContainerUtils.filterForSubnet(systemParticipants, subnetNumber);
      GraphicElements graphicElements = ContainerUtils.filterForSubnet(graphics, subnetNumber);

      subGrids.put(
          subnetNumber,
          new SubGridContainer(
              gridName,
              subnetNumber,
              rawGridElements,
              systemParticipantElements,
              energyManagementUnits, // TODO filtering (part of #957)
              graphicElements));
    }
    return subGrids;
  }

  /**
   * Build an immutable graph of the galvanically separated sub grid topology
   *
   * @param subGrids Mapping from sub net number to container model
   * @param rawGridElements Collection of all grid elements
   * @return An immutable graph of the sub grid topology
   */
  private static SubGridTopologyGraph buildSubGridTopologyGraph(
      Map<Integer, SubGridContainer> subGrids, RawGridElements rawGridElements)
      throws InvalidGridException {
    /* Building a mutable graph, that is boxed as immutable later */
    DirectedMultigraph<SubGridContainer, SubGridGate> mutableGraph =
        new DirectedMultigraph<>(SubGridGate.class);

    /* Add all edges */
    subGrids.values().forEach(mutableGraph::addVertex);

    /* Add connections of two winding transformers */
    for (Transformer2WInput transformer : rawGridElements.getTransformer2Ws()) {
      try {
        TransformerSubGridContainers subGridContainers =
            getSubGridContainers(transformer, rawGridElements, subGrids);
        mutableGraph.addEdge(
            subGridContainers.containerA,
            subGridContainers.containerB,
            SubGridGate.fromTransformer2W(transformer));
      } catch (TopologyException e) {
        throw new InvalidGridException(
            "Cannot build sub grid topology graph, as the sub grids, that are connected by transformer '"
                + transformer.getId()
                + "' ("
                + transformer.getUuid()
                + ") cannot be determined.");
      }
    }

    /* Add connections of three winding transformers */
    for (Transformer3WInput transformer : rawGridElements.getTransformer3Ws()) {
      try {
        TransformerSubGridContainers subGridContainers =
            getSubGridContainers(transformer, rawGridElements, subGrids);
        mutableGraph.addEdge(
            subGridContainers.containerA,
            subGridContainers.containerB,
            SubGridGate.fromTransformer3W(transformer, ConnectorPort.B));
        mutableGraph.addEdge(
            subGridContainers.containerA,
            subGridContainers.maybeContainerC.orElseThrow(
                () ->
                    new InvalidGridException(
                        "Cannot build sub grid topology graph, as the sub grid, that is connected to port C of transformer '"
                            + transformer.getId()
                            + "' ("
                            + transformer.getUuid()
                            + ") cannot be determined.")),
            SubGridGate.fromTransformer3W(transformer, ConnectorPort.C));
      } catch (TopologyException e) {
        throw new InvalidGridException(
            "Cannot build sub grid topology graph, as the sub grids, that are connected by transformer '"
                + transformer.getId()
                + "' ("
                + transformer.getUuid()
                + ") cannot be determined.");
      }
    }

    return new SubGridTopologyGraph(mutableGraph);
  }

  /** Private utility class to be able to return multiple {@link SubGridContainer}s */
  private static class TransformerSubGridContainers {
    private final SubGridContainer containerA;
    private final SubGridContainer containerB;
    private final Optional<SubGridContainer> maybeContainerC;

    public TransformerSubGridContainers(SubGridContainer containerA, SubGridContainer containerB) {
      this.containerA = containerA;
      this.containerB = containerB;
      this.maybeContainerC = Optional.empty();
    }

    public TransformerSubGridContainers(
        SubGridContainer containerA, SubGridContainer containerB, SubGridContainer containerC) {
      this.containerA = containerA;
      this.containerB = containerB;
      this.maybeContainerC = Optional.ofNullable(containerC);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof TransformerSubGridContainers that)) return false;
      return containerA.equals(that.containerA)
          && containerB.equals(that.containerB)
          && maybeContainerC.equals(that.maybeContainerC);
    }

    @Override
    public int hashCode() {
      return Objects.hash(containerA, containerB, maybeContainerC);
    }
  }

  /**
   * Transformers' purpose is to couple different sub grids. This method is meant to determine the
   * {@link SubGridContainer}s a specific {@link TransformerInput} connects. Therefore, surrounding
   * switch gears are reflected as well.
   *
   * @param transformer Specific transformer to determine sub grid containers for
   * @param rawGridElements Collection of all grid elements
   * @param subGrids Mapping from sub grid number to sub grid container
   * @return All surrounding sub grid containers
   * @throws TopologyException If the most upstream node (considering switchgear) cannot be
   *     determined
   */
  private static TransformerSubGridContainers getSubGridContainers(
      TransformerInput transformer,
      RawGridElements rawGridElements,
      Map<Integer, SubGridContainer> subGrids)
      throws TopologyException {
    /* Get the sub grid container at port A - travel upstream as long as nodes are connected
     * _only_ by switches */
    NodeInput topNode = traverseAlongSwitchChain(transformer.getNodeA(), rawGridElements).getLast();
    if (Objects.isNull(topNode))
      throw new TopologyException(
          "Cannot find most upstream node of transformer '" + transformer + "'");

    SubGridContainer containerA = subGrids.get(topNode.getSubnet());

    /* Get the sub grid container at port B */
    SubGridContainer containerB = subGrids.get(transformer.getNodeB().getSubnet());

    /* Get the sub grid container at port C, if this is a three winding transformer */
    if (transformer instanceof Transformer3WInput transformer3WInput) {
      SubGridContainer containerC = subGrids.get(transformer3WInput.getNodeC().getSubnet());
      return new TransformerSubGridContainers(containerA, containerB, containerC);
    } else return new TransformerSubGridContainers(containerA, containerB);
  }

  /**
   * Traversing along a chain of switches and return the traveled nodes. The end thereby is defined
   * by a node, that either is a dead end or is connected to any other type of connector (e.g.
   * lines, transformers) and therefore leads to other parts of a "real" grid. If the starting node
   * is not part of any switch, the starting node is returned.
   *
   * @param startNode Node that is meant to be the start of the switch chain
   * @param rawGridElements Elements of the pure grid structure.
   * @return The end node of the switch chain
   */
  public static LinkedList<NodeInput> traverseAlongSwitchChain(
      NodeInput startNode, RawGridElements rawGridElements) {
    Set<NodeInput> possibleJunctions =
        Stream.concat(
                Stream.concat(
                    rawGridElements.getLines().parallelStream(),
                    rawGridElements.getTransformer2Ws().parallelStream()),
                rawGridElements.getTransformer3Ws().parallelStream())
            .flatMap(connector -> connector.allNodes().parallelStream())
            .collect(Collectors.toSet());
    return traverseAlongSwitchChain(startNode, rawGridElements.getSwitches(), possibleJunctions);
  }

  /**
   * Traversing along a chain of switches and return the traveled nodes. The end thereby is defined
   * by a node, that either is a dead end or part of the provided node set. If the starting node is
   * not part of any switch, the starting node is returned.
   *
   * @param startNode Node that is meant to be the start of the switch chain
   * @param switches Set of available switches
   * @param possibleJunctions Set of nodes that denote possible junctions to "real" grid
   * @return The end node of the switch chain
   */
  private static LinkedList<NodeInput> traverseAlongSwitchChain(
      NodeInput startNode, Set<SwitchInput> switches, Set<NodeInput> possibleJunctions) {
    LinkedList<NodeInput> traveledNodes = new LinkedList<>();
    traveledNodes.addFirst(startNode);

    /* Get the switch, that is connected to the starting node and determine the next node */
    List<SwitchInput> nextSwitches =
        switches.stream().filter(switcher -> switcher.allNodes().contains(startNode)).toList();
    switch (nextSwitches.size()) {
      case 0:
        /* No further switch found -> Return the starting node */
        break;
      case 1:
        /* One next switch has been found -> Travel in this direction */
        SwitchInput nextSwitch = nextSwitches.get(0);
        Optional<NodeInput> candidateNodes =
            nextSwitch.allNodes().stream().filter(node -> node != startNode).findFirst();
        NodeInput nextNode =
            candidateNodes.orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "There is no further node available at switch " + nextSwitch));
        if (possibleJunctions.contains(nextNode)) {
          /* This is a junction, leading to another Connector than a switch */
          traveledNodes.addLast(nextNode);
        } else {
          /* Add the traveled nodes to the nodes to be excluded, to avoid endless loops in cyclic switch topologies */
          HashSet<NodeInput> newNodesToExclude = new HashSet<>(possibleJunctions);
          newNodesToExclude.add(nextNode);
          HashSet<SwitchInput> newSwitches = new HashSet<>(switches);
          newSwitches.remove(nextSwitch);
          traveledNodes.addAll(traverseAlongSwitchChain(nextNode, newSwitches, newNodesToExclude));
        }
        break;
      default:
        throw new IllegalArgumentException(
            "Cannot traverse along switch chain, as there is a junction included at node "
                + startNode);
    }
    return traveledNodes;
  }

  /**
   * Combines a given collection of sub grid containers to a joint model. If the single models do
   * not fit together, exceptions are thrown.
   *
   * @param subGridContainers Collections of already existing sub grid models
   * @return A joint model
   */
  public static JointGridContainer combineToJointGrid(
      Collection<SubGridContainer> subGridContainers) throws InvalidGridException {
    if (subGridContainers.stream().map(SubGridContainer::getGridName).distinct().count() > 1)
      throw new InvalidGridException(
          "You are trying to combine sub grids of different grid models");

    String gridName =
        subGridContainers.stream()
            .map(SubGridContainer::getGridName)
            .findFirst()
            .orElseThrow(
                () ->
                    new InvalidGridException(
                        "Cannot determine a joint name of the provided sub grid models."));

    RawGridElements rawGrid =
        new RawGridElements(
            subGridContainers.stream().map(GridContainer::getRawGrid).collect(Collectors.toSet()));
    SystemParticipants systemParticipants =
        new SystemParticipants(
            subGridContainers.stream()
                .map(GridContainer::getSystemParticipants)
                .collect(Collectors.toSet()));
    GraphicElements graphicElements =
        new GraphicElements(
            subGridContainers.stream().map(GridContainer::getGraphics).collect(Collectors.toSet()));
    EnergyManagementUnits energyManagementUnits =
        new EnergyManagementUnits(
            subGridContainers.stream().map(GridContainer::getEmUnits).collect(Collectors.toSet()));

    Map<Integer, SubGridContainer> subGridMapping =
        subGridContainers.stream()
            .collect(Collectors.toMap(SubGridContainer::getSubnet, Function.identity()));

    SubGridTopologyGraph subGridTopologyGraph = buildSubGridTopologyGraph(subGridMapping, rawGrid);

    return new JointGridContainer(
        gridName,
        rawGrid,
        systemParticipants,
        energyManagementUnits,
        graphicElements,
        subGridTopologyGraph);
  }

  /**
   * Returns a copy {@link SubGridContainer} based on the provided subgrid with a certain set of
   * nodes marked as slack nodes. In general, the grid is modified in a way that slack nodes are
   * added at transformer nodes based on assumptions about the grid, as well as all other affect
   * entities of the grid are accordingly.
   *
   * <p>This step is necessary for power flow calculations, as by default, when the container is
   * derived from {@link JointGridContainer}, only the original slack nodes are incorporated in the
   * different sub containers. Thereby, most of the standard power flow calculations cannot be
   * carried out right away.
   *
   * <p>The following modifications are made:
   *
   * <ul>
   *   <li>2 winding transformer handling
   *       <ul>
   *         <li>high voltage nodes are marked as slack nodes
   *         <li>high voltage nodes in the {@link RawGridElements#getNodes()} set are replaced with
   *             the new slack marked high voltage nodes
   *         <li>high voltage nodes as part of {@link GraphicElements#getNodeGraphics()} are
   *             replaced with the new slack marked high voltage nodes
   *       </ul>
   *   <li>3 winding transformer handling
   *       <ul>
   *         <li>if node a is located in this subgrid, no changes on 3 winding transformer nodes are
   *             made
   *         <li>if node b or c is located in this grid, the transformers internal node is marked as
   *             slack node and if node a is marked as slack node, this node is unmarked as slack
   *             node
   *         <li>if node a got unmarked as slack, the {@link RawGridElements#getNodes()} gets
   *             adapted accordingly
   *         <li>in any case the internal node of the transformer is added to the {@link
   *             RawGridElements#getNodes()} set
   *       </ul>
   * </ul>
   *
   * @param subGridContainer the subgrid container to be altered
   * @return a copy of the given {@link SubGridContainer} with transformer nodes marked as slack
   */
  public static SubGridContainer withTrafoNodeAsSlack(final SubGridContainer subGridContainer)
      throws InvalidGridException {

    // transformer 3w
    Map<NodeInput, NodeInput> oldToNewTrafo3WANodes = new HashMap<>();
    Map<Transformer3WInput, NodeInput> newTrafos3wToInternalNode =
        subGridContainer.getRawGrid().getTransformer3Ws().stream()
            .map(
                oldTrafo3w -> {
                  AbstractMap.SimpleEntry<Transformer3WInput, NodeInput> resTrafo3wToInternal;
                  if (oldTrafo3w.getNodeA().getSubnet() == subGridContainer.getSubnet()) {
                    // node A is part of this subgrid -> no slack needed
                    // internal node is needed for node admittance matrix and will be added

                    // add the same transformer again to the new transformer set as nothing has
                    // changed for this transformer
                    resTrafo3wToInternal =
                        new AbstractMap.SimpleEntry<>(oldTrafo3w, oldTrafo3w.getNodeInternal());

                  } else {
                    // node B or C is part of this subgrid -> internal node becomes the new slack

                    // if node A is marked as slack, unmark it as slack
                    NodeInput oldTrafo3wNodeA = oldTrafo3w.getNodeA();
                    NodeInput newNodeA =
                        oldTrafo3wNodeA.isSlack()
                            ? oldTrafo3wNodeA.copy().slack(false).build()
                            : oldTrafo3w.getNodeA();

                    // we need to take care for this node in our node sets afterwards
                    // (needs to be replaced by the new nodeA which might have been a slack before)
                    oldToNewTrafo3WANodes.put(oldTrafo3w.getNodeA(), newNodeA);

                    // create an update version of this transformer with internal node as slack and
                    // add it to the newTrafos3wToInternalNode set
                    Transformer3WInput newTrafo3w =
                        oldTrafo3w.copy().nodeA(newNodeA).internalSlack(true).build();

                    // add the slack
                    resTrafo3wToInternal =
                        new AbstractMap.SimpleEntry<>(newTrafo3w, newTrafo3w.getNodeInternal());
                  }
                  return resTrafo3wToInternal;
                })
            .collect(
                Collectors.toMap(
                    AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

    // get old transformer2w high voltage nodes (nodeA)
    Map<NodeInput, NodeInput> oldToNewTrafo2WANodes =
        subGridContainer.getRawGrid().getTransformer2Ws().stream()
            .map(
                oldTrafo2w -> {
                  NodeInput oldNodeA = oldTrafo2w.getNodeA();
                  NodeInput newNodeA = oldNodeA.copy().slack(true).build();

                  return new AbstractMap.SimpleEntry<>(oldNodeA, newNodeA);
                })
            .distinct()
            .collect(
                Collectors.toMap(
                    AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

    // build the updated 2w transformer with nodeA as slack and add it to our new set
    Set<Transformer2WInput> newTrafos2w =
        subGridContainer.getRawGrid().getTransformer2Ws().stream()
            .map(
                oldTrafo2w -> {
                  NodeInput newNodeA = oldToNewTrafo2WANodes.get(oldTrafo2w.getNodeA());
                  return oldTrafo2w.copy().nodeA(newNodeA).build();
                })
            .collect(Collectors.toSet());

    // update node input graphics (2 winding transformers and 3 winding transformers)
    /// map old to new
    Map<NodeGraphicInput, NodeGraphicInput> oldToNewNodeGraphics =
        subGridContainer.getGraphics().getNodeGraphics().stream()
            .filter(nodeGraphic -> oldToNewTrafo2WANodes.containsKey(nodeGraphic.getNode()))
            .filter(nodeGraphic -> oldToNewTrafo3WANodes.containsKey(nodeGraphic.getNode()))
            .map(
                oldNodeGraphic -> {
                  NodeInput newNode =
                      oldToNewTrafo2WANodes.containsKey(oldNodeGraphic.getNode())
                          ? oldToNewTrafo2WANodes.get(oldNodeGraphic.getNode())
                          : oldToNewTrafo3WANodes.get(oldNodeGraphic.getNode());

                  return new AbstractMap.SimpleEntry<>(
                      oldNodeGraphic, oldNodeGraphic.copy().node(newNode).build());
                })
            .collect(
                Collectors.toMap(
                    AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

    /// remove old node graphics, add new ones
    Set<NodeGraphicInput> newNodeGraphics =
        Stream.concat(
                // filter old ones
                subGridContainer.getGraphics().getNodeGraphics().stream()
                    .filter(nodeGraphic -> !oldToNewNodeGraphics.containsKey(nodeGraphic)),
                // add the new trafo2w ones
                oldToNewNodeGraphics.values().stream())
            .collect(Collectors.toSet());

    // update nodes in raw grid by removing the old transformer nodes and add all new ones
    Set<NodeInput> newNodes =
        Stream.concat(
                // filter the old ones (trafo2w, trafo3wToBeRemoved)
                subGridContainer.getRawGrid().getNodes().stream()
                    .filter(node -> !oldToNewTrafo2WANodes.containsKey(node))
                    .filter(node -> !oldToNewTrafo3WANodes.containsKey(node)),
                // add the new ones (trafo2w, trafo3w internal and updated trafo3w nodeA (previous
                // slacks))
                Stream.concat(
                    oldToNewTrafo2WANodes.values().stream(),
                    Stream.concat(
                        newTrafos3wToInternalNode.values().stream(),
                        oldToNewTrafo3WANodes.values().stream())))
            .collect(Collectors.toSet());

    return new SubGridContainer(
        subGridContainer.getGridName(),
        subGridContainer.getSubnet(),
        new RawGridElements(
            newNodes,
            subGridContainer.getRawGrid().getLines(),
            newTrafos2w,
            // HashSet$KeySet is not serializable, thus create new set
            new HashSet<>(newTrafos3wToInternalNode.keySet()),
            subGridContainer.getRawGrid().getSwitches(),
            subGridContainer.getRawGrid().getMeasurementUnits()),
        subGridContainer.getSystemParticipants(),
        subGridContainer.getEmUnits(),
        new GraphicElements(newNodeGraphics, subGridContainer.getGraphics().getLineGraphics()));
  }
}
