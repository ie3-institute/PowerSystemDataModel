/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.update;

import static edu.ie3.datamodel.utils.update.AssetUpdateUtils.*;

import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.exceptions.MissingTypeException;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.*;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.measure.quantity.ElectricPotential;
import tech.units.indriya.ComparableQuantity;

/** Utilities for updating {@link GridContainer}. */
public class ContainerUpdateUtils extends ContainerNodeUpdateUtil {
  private ContainerUpdateUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  private static final Function<NodeInput, ComparableQuantity<ElectricPotential>> rating =
      node -> node.getVoltLvl().getNominalVoltage();

  private static final BiFunction<ConnectorInput, Integer, Boolean> connectedToSubnet =
      (connector, subnet) ->
          connector.allNodes().stream().anyMatch(node -> node.getSubnet() == subnet);

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // methods for updating grid containers

  /**
   * This method can be used to update a given {@link JointGridContainer} with elements from another
   * {@link GridContainer}. For elements that are common in both grid containers, the element in the
   * second container is used in the returned grid container.
   *
   * <p>OPTIONAL: If specified all elements that only occur in the second container are added
   *
   * @param toUpdate first container that should be updated
   * @param updatedElements second container that may container updated elements
   * @param addMissing if {@code true} elements that only occur in the second container are added to
   *     the returned grid container
   * @return a new {@link JointGridContainer}
   */
  public static JointGridContainer update(
      JointGridContainer toUpdate, GridContainer updatedElements, boolean addMissing)
      throws InvalidGridException {
    UpdatedEntities updatedEntities = updateContainers(toUpdate, updatedElements, addMissing);

    return new JointGridContainer(
        toUpdate.getGridName(),
        updatedEntities.rawGridElements(),
        updatedEntities.systemParticipants(),
        updatedEntities.graphicElements());
  }

  /**
   * Method for updating the voltage level of one subnet. This method will automatically adjust the
   * types of {@link ConnectorInput}s. If the new type requires a different amount of {@link
   * ConnectorInput#getParallelDevices()}, this number is also automatically adjusted.
   *
   * @param container with at least one subnet
   * @param subnet number of the subgrid
   * @param newLevel new voltage level of the subnet
   * @param types all known types
   * @return a grid container with the updated subgrid
   */
  public static JointGridContainer updateSubgridVoltage(
      JointGridContainer container, int subnet, VoltageLevel newLevel, Types types)
      throws MissingTypeException, InvalidGridException {

    // entities with updated voltage levels
    UpdatedEntities updatedEntities = updateVoltage(container, subnet, newLevel, types);

    return new JointGridContainer(
        container.getGridName(),
        updatedEntities.rawGridElements(),
        updatedEntities.systemParticipants(),
        updatedEntities.graphicElements());
  }

  /**
   * Method for updating the voltage level of one subnet. This method will automatically adjust the
   * types of {@link ConnectorInput}s. If the new type requires a different amount of {@link
   * ConnectorInput#getParallelDevices()}, this number is also automatically adjusted.
   *
   * @param container with at least one subnet
   * @param subnet number of the subgrid
   * @param newLevel new voltage level of the subnet
   * @param types all known types
   * @return a grid container with the updated subgrid
   */
  public static SubGridContainer updateSubgridVoltage(
      SubGridContainer container, int subnet, VoltageLevel newLevel, Types types)
      throws MissingTypeException, InvalidGridException {

    // entities with updated voltage levels
    UpdatedEntities updatedEntities = updateVoltage(container, subnet, newLevel, types);

    return new SubGridContainer(
        container.getGridName(),
        container.getSubnet(),
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

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // methods for changing subnet voltage

  /**
   * Method for updating the voltage level for the given subnet. This method will also update the
   * types for all connected {@link ConnectorInput}s.
   *
   * @param container grid container
   * @param subnet number of the subnet
   * @param newLevel new voltage level of the subnet
   * @param types all known types
   * @return a {@link UpdatedEntities}
   * @throws MissingTypeException if a required type is missing
   */
  protected static UpdatedEntities updateVoltage(
      GridContainer container, int subnet, VoltageLevel newLevel, Types types)
      throws MissingTypeException {
    /* RawGridElements */
    RawGridElementsNodeUpdateResult updateResult =
        updateVoltage(container.getRawGrid(), subnet, newLevel, types);

    RawGridElements updatedRawGridElements = updateResult.rawGridElements();
    Map<NodeInput, NodeInput> updatedOldToNewNodes = updateResult.updatedOldToNewNodes();

    /* SystemParticipants */
    SystemParticipants updatedSystemParticipants =
        updateSystemParticipantsWithNodes(container.getSystemParticipants(), updatedOldToNewNodes);

    /* GraphicElements */
    GraphicElements updateGraphicElements =
        updateGraphicElementsWithNodes(
            container.getGraphics(), updatedOldToNewNodes, updatedRawGridElements.getLines());

    return new UpdatedEntities(
        updatedRawGridElements, updatedSystemParticipants, updateGraphicElements);
  }

  /**
   * Method for updating the voltage level for the given subnet. This method will also update the
   * types for all connected {@link ConnectorInput}s.
   *
   * @param rawGridElements to be updated
   * @param subnet number of subnet
   * @param newLevel new voltage level of the subnet
   * @param types all known types
   * @return a {@link RawGridElementsNodeUpdateResult}
   * @throws MissingTypeException if a required type is missing
   */
  protected static RawGridElementsNodeUpdateResult updateVoltage(
      RawGridElements rawGridElements, int subnet, VoltageLevel newLevel, Types types)
      throws MissingTypeException {
    Set<NodeInput> inSubnet =
        rawGridElements.getNodes().stream()
            .filter(node -> node.getSubnet() == subnet)
            .collect(Collectors.toSet());
    Map<NodeInput, NodeInput> oldToNew = updateNodes(inSubnet, newLevel);

    // result after updating node voltages
    RawGridElementsNodeUpdateResult rawGridUpdateResult =
        updateRawGridElementsWithNodes(rawGridElements, oldToNew);

    RawGridElements updatedRawGridElements = rawGridUpdateResult.rawGridElements();
    Map<NodeInput, NodeInput> updatedOldToNewNodes = rawGridUpdateResult.updatedOldToNewNodes();

    // updated all connector to the new voltage level
    RawGridElements finalRawGridElements =
        new RawGridElements(
            updatedRawGridElements.getNodes(),
            updateLineVoltages(updatedRawGridElements.getLines(), subnet, types.lineTypes),
            updateTransformer2WVoltages(
                updatedRawGridElements.getTransformer2Ws(), subnet, types.transformer2WTypes),
            updateTransformer3WVoltages(
                updatedRawGridElements.getTransformer3Ws(), subnet, types.transformer3WTypes),
            updatedRawGridElements.getSwitches(),
            updatedRawGridElements.getMeasurementUnits());

    return new RawGridElementsNodeUpdateResult(finalRawGridElements, updatedOldToNewNodes);
  }

  /**
   * Updates the types of all line inside the given subnet. All other lines are just returned.
   *
   * @param lines all lines
   * @param subnet number of subnet
   * @param types all known line types
   * @return a set of updated lines
   * @throws MissingTypeException if no suitable type was found
   */
  protected static Set<LineInput> updateLineVoltages(
      Set<LineInput> lines, int subnet, Set<LineTypeInput> types) throws MissingTypeException {
    Set<LineInput> updatedLines = new HashSet<>();
    for (LineInput line : lines) {
      if (connectedToSubnet.apply(line, subnet)) {
        updatedLines.add(updateLineVoltage(line, rating.apply(line.getNodeA()), types));
      } else {
        updatedLines.add(line);
      }
    }

    return updatedLines;
  }

  /**
   * Updates the types of all transformers connected the given subnet. All other transformers are
   * just returned.
   *
   * @param transformers all lines
   * @param subnet number of subnet
   * @param types all known transformer types
   * @return a set of updated transformers
   * @throws MissingTypeException if no suitable type was found
   */
  protected static Set<Transformer2WInput> updateTransformer2WVoltages(
      Set<Transformer2WInput> transformers, int subnet, Set<Transformer2WTypeInput> types)
      throws MissingTypeException {
    Set<Transformer2WInput> updatedTransformers = new HashSet<>();
    for (Transformer2WInput transformer : transformers) {
      if (connectedToSubnet.apply(transformer, subnet)) {
        updatedTransformers.add(
            updateTransformerVoltage(
                transformer,
                rating.apply(transformer.getNodeA()),
                rating.apply(transformer.getNodeB()),
                types));
      } else {
        updatedTransformers.add(transformer);
      }
    }

    return updatedTransformers;
  }

  /**
   * Updates the types of all transformers connected the given subnet. All other transformers are
   * just returned.
   *
   * @param transformers all lines
   * @param subnet number of subnet
   * @param types all known transformer types
   * @return a set of updated transformers
   * @throws MissingTypeException if no suitable type was found
   */
  protected static Set<Transformer3WInput> updateTransformer3WVoltages(
      Set<Transformer3WInput> transformers, int subnet, Set<Transformer3WTypeInput> types)
      throws MissingTypeException {
    Set<Transformer3WInput> updatedTransformers = new HashSet<>();
    for (Transformer3WInput transformer : transformers) {
      if (connectedToSubnet.apply(transformer, subnet)) {
        updatedTransformers.add(
            updateTransformerVoltage(
                transformer,
                rating.apply(transformer.getNodeA()),
                rating.apply(transformer.getNodeB()),
                rating.apply(transformer.getNodeC()),
                types));
      } else {
        updatedTransformers.add(transformer);
      }
    }

    return updatedTransformers;
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // general utils

  /**
   * Method to update a {@link GridContainer} with another.
   *
   * @param toUpdate container to update
   * @param updatedElements container with updated elements
   * @param addMissing if false the first container is just updated
   * @return a {@link UpdatedEntities}
   */
  protected static UpdatedEntities updateContainers(
      GridContainer toUpdate, GridContainer updatedElements, boolean addMissing) {
    RawGridElements rawGridElementsToUpdate = toUpdate.getRawGrid();
    RawGridElements rawGridElementsMaybeUpdate = updatedElements.getRawGrid();

    // updating all elements
    RawGridElements updatedRawGridElements =
        new RawGridElements(
            combineElements(
                rawGridElementsToUpdate.allEntitiesAsList(),
                rawGridElementsMaybeUpdate.allEntitiesAsList(),
                addMissing));

    SystemParticipants updatedParticipants =
        new SystemParticipants(
            combineElements(
                toUpdate.getSystemParticipants().allEntitiesAsList(),
                updatedElements.getSystemParticipants().allEntitiesAsList(),
                addMissing));

    GraphicElements updatedGraphicElements =
        new GraphicElements(
            combineElements(
                toUpdate.getGraphics().allEntitiesAsList(),
                updatedElements.getGraphics().allEntitiesAsList(),
                addMissing));

    // updating the nodes
    Map<NodeInput, NodeInput> updatedNodes =
        getUpdateMap(rawGridElementsToUpdate.getNodes(), rawGridElementsMaybeUpdate.getNodes());
    return updateEntities(
        updatedRawGridElements, updatedParticipants, updatedGraphicElements, updatedNodes);
  }

  /**
   * Method to combine two collections of elements. Elements of the second collection have a higher
   * priority. If `addMissing` is set to {@code false} the elements of the first collection are just
   * updated with the second collection.
   *
   * @param first collection
   * @param second collection
   * @param addMissing if true sll elements of the second collection are in the returned collection
   * @return a combined list
   * @param <T> type of elements
   */
  protected static <T extends UniqueEntity> List<T> combineElements(
      Collection<T> first, Collection<T> second, boolean addMissing) {
    Map<UUID, T> mapOld =
        first.stream().collect(Collectors.toMap(UniqueEntity::getUuid, Function.identity()));
    Map<UUID, T> mapNew =
        second.stream().collect(Collectors.toMap(UniqueEntity::getUuid, Function.identity()));

    Set<UUID> uuids = new HashSet<>(mapOld.keySet());

    // add missing uuids
    if (addMissing) {
      uuids.addAll(mapNew.keySet());
    }

    return uuids.stream()
        .map(
            e -> {
              if (mapNew.containsKey(e)) {
                return mapNew.get(e);
              } else {
                return mapOld.get(e);
              }
            })
        .toList();
  }

  /**
   * Method to check to given collections for updates. This method uses {@link
   * UniqueEntity#getUuid()} to get common elements and then uses {@link UniqueEntity#equals} to
   * check if the element was updated.
   *
   * @param elements collection with elements
   * @param maybeUpdates collection that may contain updated elements
   * @return a map: old to updated element
   * @param <T> type of element
   */
  protected static <T extends UniqueEntity> Map<T, T> getUpdateMap(
      Collection<T> elements, Collection<T> maybeUpdates) {
    Map<UUID, T> first =
        elements.stream().collect(Collectors.toMap(UniqueEntity::getUuid, Function.identity()));
    Set<UUID> firstIds = first.keySet();
    Map<UUID, T> second =
        maybeUpdates.stream().collect(Collectors.toMap(UniqueEntity::getUuid, Function.identity()));

    Map<T, T> updateMap = new HashMap<>();

    second.keySet().stream()
        .filter(firstIds::contains)
        .forEach(
            uuid -> {
              T one = first.get(uuid);
              T two = second.get(uuid);

              if (!one.equals(two)) {
                updateMap.put(one, two);
              }
            });

    return updateMap;
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // records

  /** Record containing all {@link AssetTypeInput}s. */
  public record Types(
      Set<LineTypeInput> lineTypes,
      Set<Transformer2WTypeInput> transformer2WTypes,
      Set<Transformer3WTypeInput> transformer3WTypes) {}
}
