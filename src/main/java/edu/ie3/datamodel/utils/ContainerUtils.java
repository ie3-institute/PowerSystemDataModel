/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.models.input.container.SubGridContainer;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import java.util.*;
import java.util.stream.Collectors;

/** Offers functionality useful for grouping different models together */
public class ContainerUtils {
  private ContainerUtils() {
    throw new IllegalStateException("Don't try and instantiate a utility class");
  }

  /**
   * Filters all raw grid elements for the provided subnet. The equivalent nodes of transformers are
   * added as well. Two winding transformers are counted, if the low voltage node is in the queried
   * subnet. Three winding transformers are counted, as long as any of the three nodes is in the
   * queried subnet.
   *
   * <p>TODO: As objects now are immutable, no copies of the transformer nodes seem to be necessary.
   * If there is any cruel behaviour ongoing, check for this.
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
    /* Add the higher voltage node to the set of nodes */
    nodes.addAll(
        transformer3w.stream()
            .map(Transformer3WInput::getNodeInternal)
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
   * <p>TODO: Currently electric vehicle charging systems have no nodal reference and therefore
   * cannot be filtered TODO: As objects now are immutable, no copies of the transformer nodes seem
   * to be necessary. If there is any cruel behaviour ongoing, check for this.
   *
   * @param input The model to filter
   * @param subnet The filter criterion
   * @return A {@link SystemParticipants} filtered for the subnet
   */
  public static SystemParticipants filterForSubnet(SystemParticipants input, int subnet) {
    Set<BmInput> bmPlants =
        input.getBmPlants().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());
    Set<ChpInput> chpPlants =
        input.getChpPlants().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());
    /* Electric vehicle charging systems are currently dummy implementations without nodal reverence */
    Set<FixedFeedInInput> fixedFeedIns =
        input.getFixedFeedIns().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());
    Set<HpInput> heatpumps =
        input.getHeatPumps().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());
    Set<LoadInput> loads =
        input.getLoads().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());
    Set<PvInput> pvs =
        input.getPvPlants().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());
    Set<StorageInput> storages =
        input.getStorages().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());
    Set<WecInput> wecPlants =
        input.getWecPlants().stream()
            .filter(entity -> entity.getNode().getSubnet() == subnet)
            .collect(Collectors.toSet());

    return new SystemParticipants(
        bmPlants,
        chpPlants,
        new HashSet<>(),
        fixedFeedIns,
        heatpumps,
        loads,
        pvs,
        storages,
        wecPlants);
  }
  /**
   * Filters all graphic elements for the provided subnet.
   *
   * <p>TODO: As objects now are immutable, no copies of the transformer nodes seem to be necessary.
   * If there is any cruel behaviour ongoing, check for this.
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
   * @param rawGrid Raw grid elements
   * @return The predominant voltage level in this grid
   * @throws InvalidGridException If not a single, predominant voltage level can be determined
   */
  public static VoltageLevel determinePredominantVoltLvl(RawGridElements rawGrid) {
    return rawGrid.getNodes().stream()
        .map(NodeInput::getVoltLvl)
        .collect(Collectors.groupingBy(voltLvl -> voltLvl, Collectors.counting()))
        .entrySet()
        .stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElseThrow(
            () -> new InvalidGridException("Cannot determine the predominant voltage level."));
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
  public static ImmutableGraph<SubGridContainer> buildSubGridTopology(
      String gridName,
      RawGridElements rawGrid,
      SystemParticipants systemParticipants,
      GraphicElements graphics) {
    /* Collect the different sub nets. Through the validation of lines, it is ensured, no calvanically connected grid
     * has more than one subnet number assigned */
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
  private static ImmutableGraph<SubGridContainer> buildSubGridTopologyGraph(
      Map<Integer, SubGridContainer> subgrids,
      Set<Transformer2WInput> transformer2ws,
      Set<Transformer3WInput> transformer3ws) {
    ImmutableGraph.Builder<SubGridContainer> graphBuilder =
        GraphBuilder.directed().nodeOrder(ElementOrder.insertion()).immutable();
    /* Add connections of two winding transformers */
    for (Transformer2WInput transformer : transformer2ws) {
      SubGridContainer from = subgrids.get(transformer.getNodeA().getSubnet());
      SubGridContainer to = subgrids.get(transformer.getNodeB().getSubnet());
      if (from == null)
        throwSubGridModelMissingException(transformer, transformer.getNodeA().getSubnet());
      if (to == null)
        throwSubGridModelMissingException(transformer, transformer.getNodeB().getSubnet());
      graphBuilder.putEdge(from, to);
    }

    /* Add connections of three winding transformers */
    for (Transformer3WInput transformer : transformer3ws) {
      SubGridContainer from = subgrids.get(transformer.getNodeA().getSubnet());
      SubGridContainer to0 = subgrids.get(transformer.getNodeB().getSubnet());
      SubGridContainer to1 = subgrids.get(transformer.getNodeC().getSubnet());
      if (from == null)
        throwSubGridModelMissingException(transformer, transformer.getNodeA().getSubnet());
      if (to0 == null)
        throwSubGridModelMissingException(transformer, transformer.getNodeB().getSubnet());
      if (to1 == null)
        throwSubGridModelMissingException(transformer, transformer.getNodeC().getSubnet());
      graphBuilder.putEdge(from, to0);
      graphBuilder.putEdge(from, to1);
    }

    return graphBuilder.build();
  }

  private static InvalidGridException throwSubGridModelMissingException(
      ConnectorInput connector, int subnet) {
    throw new InvalidGridException(
        "Transformer "
            + connector
            + " connects two sub grids, but the sub grid model "
            + subnet
            + " cannot be found");
  }
}
