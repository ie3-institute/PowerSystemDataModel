/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.container.*;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import edu.ie3.datamodel.models.input.system.*;
import java.util.*;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Point;

public class ContainerNodeUpdateUtil {

  private ContainerNodeUpdateUtil() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  /**
   * Updates the provided {@link GridContainer} with the provided mapping of old to new {@link
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
  public static GridContainer updateGridWithNodes(
      GridContainer grid, Map<NodeInput, NodeInput> oldToNewNodes) {
    if (grid instanceof JointGridContainer) {
      return updateGridWithNodes((JointGridContainer) grid, oldToNewNodes);
    } else {
      return updateGridWithNodes((SubGridContainer) grid, oldToNewNodes);
    }
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
      JointGridContainer grid, Map<NodeInput, NodeInput> oldToNewNodes) {
    UpdatedEntities updatedEntities =
        updateEntities(
            grid.getRawGrid(), grid.getSystemParticipants(), grid.getGraphics(), oldToNewNodes);

    return new JointGridContainer(
        grid.getGridName(),
        updatedEntities.rawGridElements,
        updatedEntities.systemParticipants,
        updatedEntities.graphicElements);
  }

  /**
   * Updates the provided {@link SubGridContainer} with the provided mapping of old to new {@link
   * NodeInput} entities. When used, one carefully has to check that the mapping is valid. No
   * further sanity checks are provided and if an invalid mapping is passed in, unexpected behavior
   * might occur. Furthermore, if the subgrid to be updated is part of a {@link JointGridContainer}
   * it is highly advised NOT to update a single subgrid, but the whole joint grid, because in case
   * of transformer node updates on a single subgrid, inconsistency of the overall joint grid might
   * occur. To update the whole joint grid use {@link #updateGridWithNodes(JointGridContainer, Map)}
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
  public static SubGridContainer updateGridWithNodes(
      SubGridContainer grid, Map<NodeInput, NodeInput> oldToNewNodes) {

    UpdatedEntities updatedEntities =
        updateEntities(
            grid.getRawGrid(), grid.getSystemParticipants(), grid.getGraphics(), oldToNewNodes);

    return new SubGridContainer(
        grid.getGridName(),
        grid.getSubnet(),
        updatedEntities.rawGridElements,
        updatedEntities.systemParticipants,
        updatedEntities.graphicElements);
  }

  /**
   * Update the provided parts of a {@link GridContainer} with the provided oldToNew nodes mapping
   *
   * @param rawGridElements the {@link RawGridElements} instance of the grid to be updated
   * @param systemParticipants the {@link SystemParticipants} instance of the grid to be updated
   * @param graphicElements the {@link GraphicElements} instance of the grid to be updated
   * @param oldToNewNodes a mapping of old nodes to their corresponding new or updated nodes
   * @return instance of {@link UpdatedEntities} with copies of the provided grid parts with updated
   *     nodes as provided
   */
  private static UpdatedEntities updateEntities(
      RawGridElements rawGridElements,
      SystemParticipants systemParticipants,
      GraphicElements graphicElements,
      Map<NodeInput, NodeInput> oldToNewNodes) {
    /* RawGridElements */
    RawGridElementsNodeUpdateResult rawGridUpdateResult =
        updateRawGridElementsWithNodes(rawGridElements, oldToNewNodes);
    RawGridElements updatedRawGridElements = rawGridUpdateResult.rawGridElements;

    Map<NodeInput, NodeInput> updatedOldToNewNodes = rawGridUpdateResult.updatedOldToNewNodes;

    /* SystemParticipants */
    SystemParticipants updatedSystemParticipants =
        updateSystemParticipantsWithNodes(systemParticipants, updatedOldToNewNodes);

    /* GraphicElements */
    GraphicElements updateGraphicElements =
        updateGraphicElementsWithNodes(
            graphicElements, updatedOldToNewNodes, updatedRawGridElements.getLines());

    return new UpdatedEntities(
        updatedRawGridElements, updatedSystemParticipants, updateGraphicElements);
  }

  /**
   * Update the provided {@link GraphicElements} with the provided oldToNew nodes mapping
   *
   * @param graphics the graphic elements that should be updated
   * @param oldToNewNodes mapping of old nodes to their corresponding new or updated nodes
   * @param lines the previously already updated lines
   * @return copy of the provided graphic elements with updated nodes as provided
   */
  private static GraphicElements updateGraphicElementsWithNodes(
      GraphicElements graphics, Map<NodeInput, NodeInput> oldToNewNodes, Set<LineInput> lines) {

    Set<NodeGraphicInput> updatedNodeGraphics =
        graphics.getNodeGraphics().stream()
            .map(
                nodeGraphic -> {
                  if (oldToNewNodes.containsKey(nodeGraphic.getNode())) {
                    NodeInput updatedNode = oldToNewNodes.get(nodeGraphic.getNode());
                    return nodeGraphic.copy().node(updatedNode).build();
                  } else {
                    return nodeGraphic;
                  }
                })
            .collect(Collectors.toSet());

    Set<LineGraphicInput> updatedLineGraphics =
        graphics.getLineGraphics().stream()
            .map(
                lineGraphic -> {
                  Optional<LineInput> line =
                      lines.stream()
                          .filter(
                              lineInput ->
                                  lineInput.getUuid().equals(lineGraphic.getLine().getUuid()))
                          .findFirst();
                  return line.map(
                          lineInput ->
                              new LineGraphicInput(
                                  lineGraphic.getUuid(),
                                  lineGraphic.getGraphicLayer(),
                                  lineGraphic.getPath(),
                                  lineInput))
                      .orElse(lineGraphic);
                })
            .collect(Collectors.toSet());

    return new GraphicElements(updatedNodeGraphics, updatedLineGraphics);
  }

  /**
   * Update the provided {@link SystemParticipants} with the provided oldToNew nodes mapping
   *
   * @param systemParticipants the system participants that should be updated
   * @param oldToNewNodes mapping of old nodes to their corresponding new or updated nodes
   * @return copy of the provided system participants with updated nodes as provided
   */
  private static SystemParticipants updateSystemParticipantsWithNodes(
      SystemParticipants systemParticipants, Map<NodeInput, NodeInput> oldToNewNodes) {

    List<SystemParticipantInput> sysParts =
        systemParticipants.allEntitiesAsList().parallelStream()
            .map(
                sysPart -> {
                  if (oldToNewNodes.containsKey(sysPart.getNode())) {
                    return sysPart.copy().node(oldToNewNodes.get(sysPart.getNode())).build();
                  } else {
                    return sysPart;
                  }
                })
            .collect(Collectors.toList());
    return new SystemParticipants(sysParts);
  }

  /**
   * Update the provided {@link RawGridElements} with the provided oldToNew nodes mapping
   *
   * @param rawGridElements the raw grid elements that should be updated
   * @param oldToNewNodes mapping of old nodes to their corresponding new or updated nodes
   * @return instance of {@link RawGridElementsNodeUpdateResult} with all entity data necessary for
   *     further updates
   */
  private static RawGridElementsNodeUpdateResult updateRawGridElementsWithNodes(
      RawGridElements rawGridElements, Map<NodeInput, NodeInput> oldToNewNodes) {

    /* update 2w and 3w transformers */
    // note: if transformers nodeA or nodeB geoPosition got an update, we need to update the geo
    // position of the respectively other node as well as transformer nodes ALWAYS have the same
    // geoPosition
    TransformerNodeUpdateResult transformerNodeUpdateResult =
        updateTransformers(
            rawGridElements.getTransformer2Ws(),
            rawGridElements.getTransformer3Ws(),
            oldToNewNodes);

    Set<Transformer3WInput> updatedTrafo3wInputs =
        transformerNodeUpdateResult.updatedTransformer3WInputs;
    Set<Transformer2WInput> updatedTrafo2wInputs =
        transformerNodeUpdateResult.updatedTransformer2WInputs;

    Map<NodeInput, NodeInput> updatedOldToNewNodes =
        transformerNodeUpdateResult.updatedOldToNewNodes;

    /* update nodes */
    Set<NodeInput> updatedNodeSet =
        rawGridElements.getNodes().stream()
            .map(existingNode -> updatedOldToNewNodes.getOrDefault(existingNode, existingNode))
            .collect(Collectors.toSet());

    /* update lines */
    Set<LineInput> updatedLines = updateLines(rawGridElements.getLines(), updatedOldToNewNodes);

    /* update switches */
    Set<SwitchInput> updatedSwitches =
        rawGridElements.getSwitches().stream()
            .map(
                switchInput -> {
                  NodeInput oldNodeA = switchInput.getNodeA();
                  NodeInput oldNodeB = switchInput.getNodeB();

                  NodeInput updatedNodeA = updatedOldToNewNodes.getOrDefault(oldNodeA, oldNodeA);
                  NodeInput updatedNodeB = updatedOldToNewNodes.getOrDefault(oldNodeB, oldNodeB);
                  if (oldNodeA.equals(updatedNodeA) && oldNodeB.equals(updatedNodeB)) {
                    return switchInput;
                  } else {
                    // even if only nodeA or only nodeB have changed, we just create an updated
                    // switch model with both nodes updated
                    return switchInput.copy().nodeA(updatedNodeA).nodeB(updatedNodeB).build();
                  }
                })
            .collect(Collectors.toSet());

    /* update measurement units */
    Set<MeasurementUnitInput> updatedMeasurementUnits =
        rawGridElements.getMeasurementUnits().stream()
            .map(
                measurement -> {
                  if (updatedOldToNewNodes.containsKey(measurement.getNode())) {
                    return measurement
                        .copy()
                        .node(updatedOldToNewNodes.get(measurement.getNode()))
                        .build();
                  } else {
                    return measurement;
                  }
                })
            .collect(Collectors.toSet());

    return new RawGridElementsNodeUpdateResult(
        new RawGridElements(
            updatedNodeSet,
            updatedLines,
            updatedTrafo2wInputs,
            updatedTrafo3wInputs,
            updatedSwitches,
            updatedMeasurementUnits),
        updatedOldToNewNodes);
  }

  /**
   * Update the provided set of {@link LineInput} with the provided oldToNew nodes mapping if
   * affected
   *
   * @param lines the lines to be updated
   * @param updatedOldToNewNodes mapping of old nodes to their corresponding new or updated nodes
   * @return copy of the provided line set with updated nodes if affected
   */
  private static Set<LineInput> updateLines(
      Set<LineInput> lines, Map<NodeInput, NodeInput> updatedOldToNewNodes) {
    return lines.stream()
        .map(
            line -> {
              NodeInput oldNodeA = line.getNodeA();
              NodeInput oldNodeB = line.getNodeB();

              NodeInput updatedNodeA = updatedOldToNewNodes.getOrDefault(oldNodeA, oldNodeA);
              NodeInput updatedNodeB = updatedOldToNewNodes.getOrDefault(oldNodeB, oldNodeB);

              if (oldNodeA.equals(updatedNodeA) && oldNodeB.equals(updatedNodeB)) {
                return line;
              } else {
                // even if only nodeA or only nodeB have changed, we just create an updated
                // line model with both nodes updated

                return line.copy()
                    .nodeA(updatedNodeA)
                    .nodeB(updatedNodeB)
                    .length(GridAndGeoUtils.distanceBetweenNodes(updatedNodeA, updatedNodeB))
                    .geoPosition(
                        GridAndGeoUtils.buildSafeLineStringBetweenNodes(updatedNodeA, updatedNodeB))
                    .build();
              }
            })
        .collect(Collectors.toSet());
  }

  /**
   * Update the provided sets of {@link Transformer3WInput} and {@link Transformer2WInput} with the
   * provided old to new nodes mapping.
   *
   * <p>As transformers always needs to hold the same geoPosition the following update rule applied
   * when a geoPosition of a transformer is altered:
   *
   * <p>if oldToNewNodes.size() == 1, the leading geoPosition that is applied to all chained
   * transformers comes from the provided node if oldToNewNodes.size() > 1, the leading geoPosition
   * is applied from the highest transformer nodeA (the node @ the highest voltage level)
   *
   * @param transformer2Ws set of 2 winding transformers that should be considered for an update
   * @param transformer3Ws set of 3 winding transformers that should be considered for an update
   * @param oldToNewNodes mapping of old nodes to their corresponding new or updated nodes
   * @return instance of {@link TransformerNodeUpdateResult}
   */
  private static TransformerNodeUpdateResult updateTransformers(
      Set<Transformer2WInput> transformer2Ws,
      Set<Transformer3WInput> transformer3Ws,
      Map<NodeInput, NodeInput> oldToNewNodes) {

    /* 1. get all affected nodes */
    Set<NodeInput> oldAffectedTrafoNodes =
        findAllRelatedTransformerNodes(
            transformer2Ws, transformer3Ws, oldToNewNodes, new HashSet<>());

    /* 2. define the winning geoPosition, this value is set when at least one transformer received an update
     * of at least one of its nodes (not necessarily the geoPosition)*/
    Optional<Point> leadGeoPos = Optional.empty();
    if (!oldAffectedTrafoNodes.isEmpty()) {
      if (oldToNewNodes.size() == 1) {
        // only one node got an update -> leading geoPosition comes from this node (enables updates
        // of inferior nodes
        // on cascading transformer configurations)
        leadGeoPos = Optional.of(oldToNewNodes.values().iterator().next().getGeoPosition());
      } else {
        // multiple transformer nodes got an update -> leading geoPos is node on highest level
        NodeInput oldLeadGeoPosNodeInput =
            sortNodesByVoltageLevel(oldAffectedTrafoNodes).iterator().next();
        Point updatedLeadGeoPos =
            oldToNewNodes
                .getOrDefault(oldLeadGeoPosNodeInput, oldLeadGeoPosNodeInput)
                .getGeoPosition();
        leadGeoPos = Optional.of(updatedLeadGeoPos);
      }
    }

    /* 3. update affected nodes with leadGeoPos */
    Map<NodeInput, NodeInput> updatedOldToNewNodes =
        leadGeoPos
            .map(
                leadGeoPosition ->
                    oldAffectedTrafoNodes.stream()
                        .map(
                            oldAffectedTrafoNode ->
                                new AbstractMap.SimpleEntry<>(
                                    oldAffectedTrafoNode,
                                    oldToNewNodes
                                        .getOrDefault(oldAffectedTrafoNode, oldAffectedTrafoNode)
                                        .copy()
                                        .geoPosition(leadGeoPosition)
                                        .build()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
            .orElse(oldToNewNodes);

    /* 4. update the transformers, if there is something to update */
    /* start with the 2w transformers */
    Set<Transformer2WInput> updated2wTransformers =
        leadGeoPos
            .map(
                leadGeoPosition ->
                    update2wTransformers(transformer2Ws, updatedOldToNewNodes, leadGeoPosition))
            .orElse(transformer2Ws);
    /* go on with the 3w transformers */
    Set<Transformer3WInput> updated3wTransformers =
        leadGeoPos
            .map(
                leadGeoPosition ->
                    update3wTransformers(transformer3Ws, updatedOldToNewNodes, leadGeoPosition))
            .orElse(transformer3Ws);

    // put all oldNode -> newNode in the resulting map
    Map<NodeInput, NodeInput> updatedTransformerNodes = new HashMap<>(oldToNewNodes);
    updatedTransformerNodes.putAll(updatedOldToNewNodes);

    return new TransformerNodeUpdateResult(
        updated2wTransformers, updated3wTransformers, updatedTransformerNodes);
  }

  /**
   * Update the provided set of {@link Transformer2WInput} with the provided oldToNew nodes mapping
   * if affected
   *
   * @param transformer2Ws the transformers to be updated
   * @param oldToNewNodes mapping of old nodes to their corresponding new or updated nodes
   * @param leadGeoPosition the leading geoPosition that should be set to all transformer nodes
   * @return copy of the provided transformer set with updated nodes if affected
   */
  private static Set<Transformer2WInput> update2wTransformers(
      Set<Transformer2WInput> transformer2Ws,
      Map<NodeInput, NodeInput> oldToNewNodes,
      Point leadGeoPosition) {
    return transformer2Ws.stream()
        .map(
            trafo2w -> {
              NodeInput oldNodeA = trafo2w.getNodeA();
              NodeInput oldNodeB = trafo2w.getNodeB();

              NodeInput updatedNodeA = oldToNewNodes.getOrDefault(oldNodeA, oldNodeA);
              NodeInput updatedNodeB = oldToNewNodes.getOrDefault(oldNodeB, oldNodeB);

              // oldNodes == newNodes -> no need to update anything
              if (oldNodeA.equals(updatedNodeA) && oldNodeB.equals(updatedNodeB)) {
                return trafo2w;
              } else {
                // even if only nodeA or only nodeB have changed, we just create an
                // updated
                // transformer model with both nodes updated

                // geoPosition is always set to the lead geoPosition for all nodes
                NodeInput updatedNodeALeadGeoPos =
                    updatedNodeA.copy().geoPosition(leadGeoPosition).build();
                NodeInput updatedNodeBLeadGeoPos =
                    updatedNodeB.copy().geoPosition(leadGeoPosition).build();

                return trafo2w
                    .copy()
                    .nodeA(updatedNodeALeadGeoPos)
                    .nodeB(updatedNodeBLeadGeoPos)
                    .build();
              }
            })
        .collect(Collectors.toSet());
  }

  /**
   * Update the provided set of {@link Transformer3WInput} with the provided oldToNew nodes mapping
   * if affected
   *
   * @param transformer3Ws the transformers to be updated
   * @param oldToNewNodes mapping of old nodes to their corresponding new or updated nodes
   * @param leadGeoPosition the leading geoPosition that should be set to all transformer nodes
   * @return copy of the provided transformer set with updated nodes if affected
   */
  private static Set<Transformer3WInput> update3wTransformers(
      Set<Transformer3WInput> transformer3Ws,
      Map<NodeInput, NodeInput> oldToNewNodes,
      Point leadGeoPosition) {

    return transformer3Ws.stream()
        .map(
            trafo3w -> {
              NodeInput oldNodeA = trafo3w.getNodeA();
              NodeInput oldNodeB = trafo3w.getNodeB();
              NodeInput oldNodeC = trafo3w.getNodeC();

              NodeInput updatedNodeA = oldToNewNodes.getOrDefault(oldNodeA, oldNodeA);
              NodeInput updatedNodeB = oldToNewNodes.getOrDefault(oldNodeB, oldNodeB);
              NodeInput updatedNodeC = oldToNewNodes.getOrDefault(oldNodeC, oldNodeC);

              // oldNodes == newNodes -> no need to update anything
              if (oldNodeA.equals(updatedNodeA)
                  && oldNodeB.equals(updatedNodeB)
                  && oldNodeC.equals(updatedNodeC)) {
                return trafo3w;
              } else {
                // even if only nodeA or only nodeB or only nodeC have changed, we
                // just create
                // an updated transformer model with all three nodes updated

                // geoPosition is always set to the lead geoPosition for all nodes
                NodeInput updatedNodeALeadGeoPos =
                    updatedNodeA.copy().geoPosition(leadGeoPosition).build();
                NodeInput updatedNodeBLeadGeoPos =
                    updatedNodeB.copy().geoPosition(leadGeoPosition).build();
                NodeInput updatedNodeCLeadGeoPos =
                    updatedNodeC.copy().geoPosition(leadGeoPosition).build();

                return trafo3w
                    .copy()
                    .nodeA(updatedNodeALeadGeoPos)
                    .nodeB(updatedNodeBLeadGeoPos)
                    .nodeC(updatedNodeCLeadGeoPos)
                    .build();
              }
            })
        .collect(Collectors.toSet());
  }

  /**
   * Recursive functions that determines chains of transformers in a grid, if the provided old to
   * new nodes mapping affects at least one transformer. This is necessary because by policy, the
   * geoPosition of a transformer is determined by its nodeA. If multiple transformers are now
   * chained together e.g. nodeA - trafoAtoD - nodeD - trafoDtoG - nodeG than all transformer nodes
   * needs to be updated if at least one of the provided nodes is affected. Otherwise inconsistency
   * would occur because transformers would end up with multiple geoPositions which is physically
   * not possible.
   *
   * <p>Basically, this method executes a graph path search in upper and lower direction as long as
   * connected transformers can be found from the start node
   *
   * @param transformer2Ws set of 2 winding transformers that should be considered for an update
   * @param transformer3Ws set of 3 winding transformers that should be considered for an update
   * @param oldToNewNodes mapping of old nodes to their corresponding new or updated nodes
   * @param affectedTrafoNodes already affected transformer nodes
   * @return set of all affected transformer nodes
   */
  private static Set<NodeInput> findAllRelatedTransformerNodes(
      Set<Transformer2WInput> transformer2Ws,
      Set<Transformer3WInput> transformer3Ws,
      Map<NodeInput, NodeInput> oldToNewNodes,
      Set<NodeInput> affectedTrafoNodes) {

    Set<NodeInput> newFoundOldAffectedTrafoNodes = new HashSet<>(affectedTrafoNodes);
    for (Transformer2WInput trafo2w : transformer2Ws) {
      if (oldToNewNodes.containsKey(trafo2w.getNodeA())
          || oldToNewNodes.containsKey(trafo2w.getNodeB())
          || newFoundOldAffectedTrafoNodes.contains(trafo2w.getNodeA())
          || newFoundOldAffectedTrafoNodes.contains(trafo2w.getNodeB())) {
        newFoundOldAffectedTrafoNodes.add(trafo2w.getNodeA());
        newFoundOldAffectedTrafoNodes.add(trafo2w.getNodeB());
      }
    }

    for (Transformer3WInput trafo3w : transformer3Ws) {
      if (oldToNewNodes.containsKey(trafo3w.getNodeA())
          || oldToNewNodes.containsKey(trafo3w.getNodeB())
          || oldToNewNodes.containsKey(trafo3w.getNodeC())
          || newFoundOldAffectedTrafoNodes.contains(trafo3w.getNodeA())
          || newFoundOldAffectedTrafoNodes.contains(trafo3w.getNodeB())
          || newFoundOldAffectedTrafoNodes.contains(trafo3w.getNodeC())) {
        newFoundOldAffectedTrafoNodes.add(trafo3w.getNodeA());
        newFoundOldAffectedTrafoNodes.add(trafo3w.getNodeB());
        newFoundOldAffectedTrafoNodes.add(trafo3w.getNodeC());
      }
    }

    if (affectedTrafoNodes.size() != newFoundOldAffectedTrafoNodes.size()) {
      return findAllRelatedTransformerNodes(
          transformer2Ws, transformer3Ws, oldToNewNodes, newFoundOldAffectedTrafoNodes);
    } else {
      return newFoundOldAffectedTrafoNodes;
    }
  }

  private static List<NodeInput> sortNodesByVoltageLevel(Set<NodeInput> nodes) {
    List<NodeInput> allNodes = new ArrayList<>(nodes);
    allNodes.sort(
        (o1, o2) ->
            Double.compare(
                o2.getVoltLvl().getNominalVoltage().getValue().doubleValue(),
                o1.getVoltLvl().getNominalVoltage().getValue().doubleValue()));
    return allNodes;
  }

  /**
   * Class that is used to provide data after calling {@link #updateTransformers(Set, Set, Map)}.
   * This is necessary because of the need to maybe alter more nodes than initially provided for
   * updates when updating the transformers. Hence, for further processing it is advised to use the
   * updatedOldToNewNodes instead of the original ones.
   */
  private static class TransformerNodeUpdateResult {
    private final Set<Transformer2WInput> updatedTransformer2WInputs;
    private final Set<Transformer3WInput> updatedTransformer3WInputs;

    private final Map<NodeInput, NodeInput> updatedOldToNewNodes;

    public TransformerNodeUpdateResult(
        Set<Transformer2WInput> updatedTransformer2WInputs,
        Set<Transformer3WInput> updatedTransformer3WInputs,
        Map<NodeInput, NodeInput> updatedOldToNewNodes) {
      this.updatedTransformer2WInputs = updatedTransformer2WInputs;
      this.updatedTransformer3WInputs = updatedTransformer3WInputs;
      this.updatedOldToNewNodes = updatedOldToNewNodes;
    }
  }

  /**
   * Class that is used to provide data after calling {@link
   * #updateRawGridElementsWithNodes(RawGridElements, Map)} This is necessary because of the need to
   * maybe alter more nodes than initially provided for updates when updating the transformers.
   * Hence, for further processing it is advised to use the updatedOldToNewNodes instead of the
   * original ones
   */
  private static class RawGridElementsNodeUpdateResult {
    private final RawGridElements rawGridElements;
    private final Map<NodeInput, NodeInput> updatedOldToNewNodes;

    public RawGridElementsNodeUpdateResult(
        RawGridElements rawGridElements, Map<NodeInput, NodeInput> updatedOldToNewNodes) {
      this.rawGridElements = rawGridElements;
      this.updatedOldToNewNodes = updatedOldToNewNodes;
    }
  }

  /** Wrapper class for updated entities hold by an instance of {@link GridContainer} */
  private static class UpdatedEntities {
    private final RawGridElements rawGridElements;
    private final SystemParticipants systemParticipants;
    private final GraphicElements graphicElements;

    public UpdatedEntities(
        RawGridElements rawGridElements,
        SystemParticipants systemParticipants,
        GraphicElements graphicElements) {
      this.rawGridElements = rawGridElements;
      this.systemParticipants = systemParticipants;
      this.graphicElements = graphicElements;
    }
  }
}
