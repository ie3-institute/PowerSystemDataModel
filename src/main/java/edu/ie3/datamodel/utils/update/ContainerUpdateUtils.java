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
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.*;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import edu.ie3.datamodel.utils.ContainerUtils;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
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
  // method for updating grid containers

  /**
   * Method to update the given grid with a list of asset updates.
   *
   * @param grid that should be updated
   * @param assets list of asset updates
   * @return a new {@link JointGridContainer}
   * @throws InvalidGridException if the joint grid cannot be build
   */
  public static JointGridContainer updateAssets(GridContainer grid, List<AssetInput> assets)
      throws InvalidGridException {
    return updateGrid(
            grid,
            ContainerUtils.buildJointGrid(
                grid.getGridName(), assets, Collections.emptyList(), Collections.emptyList()),
            false)
        .build(grid.getGridName());
  }

  /**
   * Method to update the given grid with a list of participant updates.
   *
   * @param grid that should be updated
   * @param participants list of participant updates
   * @return a new {@link JointGridContainer}
   * @throws InvalidGridException if the joint grid cannot be build
   */
  public static JointGridContainer updateParticipants(
      GridContainer grid, List<SystemParticipantInput> participants) throws InvalidGridException {
    return updateGrid(
            grid,
            ContainerUtils.buildJointGrid(
                grid.getGridName(), Collections.emptyList(), participants, Collections.emptyList()),
            false)
        .build(grid.getGridName());
  }

  /**
   * Method for updating the voltage level of one subnet. This method will automatically adjust the
   * types of {@link ConnectorInput}s. If the new type requires a different amount of {@link
   * ConnectorInput#getParallelDevices()}, this number is also automatically adjusted.
   *
   * @param grid with at least one subnet
   * @param subnet number of the subgrid
   * @param newLevel new voltage level of the subnet
   * @param types all known types
   * @return a grid container with the updated subgrid
   */
  public static JointGridContainer updateSubgridVoltage(
      JointGridContainer grid, int subnet, VoltageLevel newLevel, Types types)
      throws MissingTypeException, InvalidGridException {
    // entities with updated voltage levels
    return updateVoltage(grid, subnet, newLevel, types).build(grid.getGridName());
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // common update methods

  /**
   * This method can be used to update a given {@link GridContainer} with elements from another
   * {@link GridContainer}. For elements that are common in both grid containers, the element in the
   * second container is used in the returned grid container.
   *
   * <p>This method uses the {@link GridContainer#getGridName()} of the first container as its
   * returned grid name
   *
   * <p>OPTIONAL: If specified all elements that only occur in the second container are added
   *
   * @param grid container to update
   * @param updatedElements container with updated elements
   * @param addMissing if false the first container is just updated
   * @return a {@link UpdatedEntities}
   */
  protected static UpdatedEntities updateGrid(
      GridContainer grid, GridContainer updatedElements, boolean addMissing) {
    RawGridElements rawGridElementsToUpdate = grid.getRawGrid();
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
                grid.getSystemParticipants().allEntitiesAsList(),
                updatedElements.getSystemParticipants().allEntitiesAsList(),
                addMissing));

    GraphicElements updatedGraphicElements =
        new GraphicElements(
            combineElements(
                grid.getGraphics().allEntitiesAsList(),
                updatedElements.getGraphics().allEntitiesAsList(),
                addMissing));

    // updating the nodes
    Map<NodeInput, NodeInput> updatedNodes =
        getUpdateMap(rawGridElementsToUpdate.getNodes(), rawGridElementsMaybeUpdate.getNodes());
    return updateEntities(
        updatedRawGridElements, updatedParticipants, updatedGraphicElements, updatedNodes);
  }

  /**
   * Method for updating the voltage level for the given subnet. This method will also update the
   * types for all connected {@link ConnectorInput}s.
   *
   * @param grid grid container
   * @param subnet number of the subnet
   * @param newLevel new voltage level of the subnet
   * @param types all known types
   * @return a {@link UpdatedEntities}
   * @throws MissingTypeException if a required type is missing
   */
  protected static UpdatedEntities updateVoltage(
      GridContainer grid, int subnet, VoltageLevel newLevel, Types types)
      throws MissingTypeException {
    Set<NodeInput> inSubnet =
        grid.getRawGrid().getNodes().stream()
            .filter(node -> node.getSubnet() == subnet)
            .collect(Collectors.toSet());
    Map<NodeInput, NodeInput> oldToNew = updateNodes(inSubnet, newLevel);

    // result after updating node voltages
    RawGridElementsNodeUpdateResult rawGridUpdateResult =
        updateRawGridElementsWithNodes(grid.getRawGrid(), oldToNew);

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

    /* SystemParticipants */
    SystemParticipants updatedSystemParticipants =
        updateSystemParticipantsWithNodes(grid.getSystemParticipants(), updatedOldToNewNodes);

    /* GraphicElements */
    GraphicElements updateGraphicElements =
        updateGraphicElementsWithNodes(
            grid.getGraphics(), updatedOldToNewNodes, finalRawGridElements.getLines());

    return new UpdatedEntities(
        finalRawGridElements, updatedSystemParticipants, updateGraphicElements);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // general utils

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
