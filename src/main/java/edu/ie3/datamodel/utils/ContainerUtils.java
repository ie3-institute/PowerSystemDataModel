/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.graph.SubGridGate;
import edu.ie3.datamodel.graph.SubGridTopologyGraph;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.container.*;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jgrapht.graph.DirectedMultigraph;

/** Offers functionality useful for grouping different models together */
public class ContainerUtils {
  private ContainerUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  /**
   * Filters all raw grid elements for the provided subnet. For each transformer all nodes (and not
   * only the the node of the grid the transformer is located in) are added as well. Two winding
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
    /* Electric vehicle charging systems are currently dummy implementations without nodal reverence */
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
        new HashSet<>(),
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
  public static VoltageLevel determinePredominantVoltLvl(RawGridElements rawGrid, int subnet) {
    /* Exclude all nodes, that are at the high voltage side of the transformer */
    Set<NodeInput> gridNodes = new HashSet<>(rawGrid.getNodes());
    gridNodes.removeAll(
        rawGrid.getTransformer2Ws().stream()
            .map(ConnectorInput::getNodeA)
            .collect(Collectors.toSet()));
    gridNodes.removeAll(
        rawGrid.getTransformer3Ws().stream()
            .flatMap(
                transformer -> {
                  if (transformer.getNodeA().getSubnet() == subnet)
                    return Stream.of(transformer.getNodeB(), transformer.getNodeC());
                  else if (transformer.getNodeB().getSubnet() == subnet)
                    return Stream.of(
                        transformer.getNodeA(),
                        transformer.getNodeC(),
                        transformer.getNodeInternal());
                  else
                    return Stream.of(
                        transformer.getNodeA(),
                        transformer.getNodeB(),
                        transformer.getNodeInternal());
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
      GraphicElements graphics) {
    /* Collect the different sub nets. Through the validation of lines, it is ensured, that no galvanically connected
     * grid has more than one subnet number assigned */
    SortedSet<Integer> subnetNumbers = determineSubnetNumbers(rawGrid.getNodes());

    /* Build the single sub grid models */
    HashMap<Integer, SubGridContainer> subgrids =
        buildSubGridContainers(gridName, subnetNumbers, rawGrid, systemParticipants, graphics);

    /* Build the graph structure denoting the topology of the grid */
    return buildSubGridTopologyGraph(
        subgrids, rawGrid.getTransformer2Ws(), rawGrid.getTransformer3Ws());
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
   * @param subnetNumbers Set of available subne numbers
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
      GraphicElements graphics) {
    HashMap<Integer, SubGridContainer> subGrids = new HashMap<>(subnetNumbers.size());
    for (int subnetNumber : subnetNumbers) {
      RawGridElements rawGridElements = ContainerUtils.filterForSubnet(rawGrid, subnetNumber);
      SystemParticipants systemParticipantElements =
          ContainerUtils.filterForSubnet(systemParticipants, subnetNumber);
      GraphicElements graphicElements = ContainerUtils.filterForSubnet(graphics, subnetNumber);

      subGrids.put(
          subnetNumber,
          new SubGridContainer(
              gridName, subnetNumber, rawGridElements, systemParticipantElements, graphicElements));
    }
    return subGrids;
  }

  /**
   * Build an immutable graph of the galvanically separated sub grid topology
   *
   * @param subgrids Mapping from sub net number to container model
   * @param transformer2ws Set of two winding transformers
   * @param transformer3ws Set of three winding transformers
   * @return An immutable graph of the sub grid topology
   */
  private static SubGridTopologyGraph buildSubGridTopologyGraph(
      Map<Integer, SubGridContainer> subgrids,
      Set<Transformer2WInput> transformer2ws,
      Set<Transformer3WInput> transformer3ws) {
    /* Building a mutable graph, that is boxed as immutable later */
    DirectedMultigraph<SubGridContainer, SubGridGate> mutableGraph =
        new DirectedMultigraph<>(SubGridGate.class);

    /* Add all edges */
    subgrids.values().forEach(mutableGraph::addVertex);

    /* Add connections of two winding transformers */
    for (Transformer2WInput transformer : transformer2ws) {
      SubGridContainer from = getSubGridContainer(transformer, ConnectorPort.A, subgrids);
      SubGridContainer to = getSubGridContainer(transformer, ConnectorPort.B, subgrids);
      mutableGraph.addEdge(from, to, new SubGridGate(transformer));
    }

    /* Add connections of three winding transformers */
    for (Transformer3WInput transformer : transformer3ws) {
      SubGridContainer from = getSubGridContainer(transformer, ConnectorPort.A, subgrids);
      SubGridContainer toB = getSubGridContainer(transformer, ConnectorPort.B, subgrids);
      SubGridContainer toC = getSubGridContainer(transformer, ConnectorPort.C, subgrids);
      mutableGraph.addEdge(from, toB, new SubGridGate(transformer, ConnectorPort.B));
      mutableGraph.addEdge(from, toC, new SubGridGate(transformer, ConnectorPort.C));
    }

    return new SubGridTopologyGraph(mutableGraph);
  }

  /**
   * Extracts the {@link SubGridContainer} of the map from sub grid number to sub grid model and
   * checks for its availability.
   *
   * @param connector The connector to use
   * @param port The port of the connector, that is referred to
   * @param subGrids A mapping from sub grid number to sub grid model
   * @return The queried sub grid container
   */
  private static SubGridContainer getSubGridContainer(
      ConnectorInput connector, ConnectorPort port, Map<Integer, SubGridContainer> subGrids) {
    int subGrid;
    switch (port) {
      case A:
        subGrid = connector.getNodeA().getSubnet();
        break;
      case B:
        subGrid = connector.getNodeB().getSubnet();
        break;
      case C:
        if (connector instanceof Transformer3WInput)
          subGrid = ((Transformer3WInput) connector).getNodeC().getSubnet();
        else
          throw new IllegalArgumentException(
              "The connector " + connector + " has no port " + port + ".");
        break;
      default:
        throw new IllegalArgumentException(
            "Cannot determine the sub grid number of connector "
                + connector
                + " at port "
                + port
                + ".");
    }

    SubGridContainer container = subGrids.get(subGrid);
    if (container == null)
      throw new InvalidGridException(
          "Transformer "
              + connector
              + " connects two sub grids, but the sub grid model "
              + subGrid
              + " cannot be found");
    else return container;
  }

  /**
   * Combines a given collection of sub grid containers to a joint model. If the single models do
   * not fit together, exceptions are thrown.
   *
   * @param subGridContainers Collections of already existing sub grid models
   * @return A joint model
   */
  public static JointGridContainer combineToJointGrid(
      Collection<SubGridContainer> subGridContainers) {
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

    Map<Integer, SubGridContainer> subGridMapping =
        subGridContainers.stream()
            .collect(Collectors.toMap(SubGridContainer::getSubnet, Function.identity()));

    SubGridTopologyGraph subGridTopologyGraph =
        buildSubGridTopologyGraph(
            subGridMapping, rawGrid.getTransformer2Ws(), rawGrid.getTransformer3Ws());

    return new JointGridContainer(
        gridName, rawGrid, systemParticipants, graphicElements, subGridTopologyGraph);
  }

  public static boolean distinctUuids(Collection<? extends UniqueEntity> entities) {
    return entities.stream()
            .filter(distinctByKey(UniqueEntity::getUuid))
            .collect(Collectors.toSet())
            .size()
        == entities.size();
  }

  private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }
}
