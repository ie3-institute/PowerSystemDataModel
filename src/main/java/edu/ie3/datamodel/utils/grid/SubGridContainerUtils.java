/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.grid;

import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.container.JointGridContainer;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.models.input.container.SubGridContainer;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Class that provides utilities for {@link SubGridContainer}. */
public class SubGridContainerUtils extends ContainerUtils {
  private SubGridContainerUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  /**
   * Updates the provided {@link SubGridContainer} with the provided mapping of old to new {@link
   * NodeInput} entities. When used, one carefully has to check that the mapping is valid. No
   * further sanity checks are provided and if an invalid mapping is passed in, unexpected behavior
   * might occur. Furthermore, if the subgrid to be updated is part of a {@link JointGridContainer}
   * it is highly advised NOT to update a single subgrid, but the whole joint grid, because in case
   * of transformer node updates on a single subgrid, inconsistency of the overall joint grid might
   * occur. To update the whole joint grid use {@link
   * JointGridContainerUtils#updateGridWithNodes(JointGridContainer, Map)}
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
      SubGridContainer grid, Map<NodeInput, NodeInput> oldToNewNodes) throws InvalidGridException {

    UpdatedEntities updatedEntities =
        updateEntities(
            grid.getRawGrid(), grid.getSystemParticipants(), grid.getGraphics(), oldToNewNodes);

    return new SubGridContainer(
        grid.getGridName(),
        grid.getSubnet(),
        updatedEntities.rawGridElements(),
        updatedEntities.systemParticipants(),
        updatedEntities.graphicElements());
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
        new GraphicElements(newNodeGraphics, subGridContainer.getGraphics().getLineGraphics()));
  }
}
