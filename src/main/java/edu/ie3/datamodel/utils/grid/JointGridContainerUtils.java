/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.grid;

import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.exceptions.TopologyException;
import edu.ie3.datamodel.graph.SubGridGate;
import edu.ie3.datamodel.graph.SubGridTopologyGraph;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.ConnectorPort;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.container.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jgrapht.graph.DirectedMultigraph;

/** Class that provides utilities for {@link JointGridContainer}. */
public class JointGridContainerUtils extends ContainerUtils {
  private JointGridContainerUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
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

    Map<Integer, SubGridContainer> subGridMapping =
        subGridContainers.stream()
            .collect(Collectors.toMap(SubGridContainer::getSubnet, Function.identity()));

    SubGridTopologyGraph subGridTopologyGraph = buildSubGridTopologyGraph(subGridMapping, rawGrid);

    return new JointGridContainer(
        gridName, rawGrid, systemParticipants, graphicElements, subGridTopologyGraph);
  }

  /**
   * Updates the provided {@link JointGridContainer} with the provided mapping of old to new {@link
   * NodeInput} entities. When used, one carefully has to check that the mapping is valid. No
   * further sanity checks are provided and if an invalid mapping is passed in, unexpected behavior
   * might occur. All entities holding reference to the old nodes are updates with this method.
   *
   * <p>If the geoPosition of one transformer node is altered, all other transformer nodes
   * geoPositions are updated as well based on the update definition defined in {@link
   * #updateTransformers(Set, Set, Map)} as by convention transformer nodes always needs to have the
   * same geoPosition. If a chain of transformers is present e.g. nodeA - trafoAtoD - nodeD -
   * trafoDtoG - nodeG all affected transformer nodes geoPosition is set to the same location as
   * defined by the update rule defined in {@link #updateTransformers(Set, Set, Map)}
   *
   * @param grid the grid that should be updated
   * @param oldToNewNodes a mapping of old nodes to their corresponding new or updated nodes
   * @return a copy of the provided grid with updated nodes as provided
   */
  public static JointGridContainer updateGridWithNodes(
      JointGridContainer grid, Map<NodeInput, NodeInput> oldToNewNodes)
      throws InvalidGridException {
    UpdatedEntities updatedEntities =
        updateEntities(
            grid.getRawGrid(), grid.getSystemParticipants(), grid.getGraphics(), oldToNewNodes);

    return new JointGridContainer(
        grid.getGridName(),
        updatedEntities.rawGridElements(),
        updatedEntities.systemParticipants(),
        updatedEntities.graphicElements());
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // subgrid topology graph utils

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
      GraphicElements graphics)
      throws InvalidGridException {
    /* Collect the different sub nets. Through the validation of lines, it is ensured, that no galvanically connected
     * grid has more than one subnet number assigned */
    SortedSet<Integer> subnetNumbers = determineSubnetNumbers(rawGrid.getNodes());

    /* Build the single sub grid models */
    HashMap<Integer, SubGridContainer> subgrids =
        buildSubGridContainers(gridName, subnetNumbers, rawGrid, systemParticipants, graphics);

    /* Build the graph structure denoting the topology of the grid */
    return buildSubGridTopologyGraph(subgrids, rawGrid);
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
            buildTransformerSubGridContainers(transformer, rawGridElements, subGrids);
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
            buildTransformerSubGridContainers(transformer, rawGridElements, subGrids);
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
}
