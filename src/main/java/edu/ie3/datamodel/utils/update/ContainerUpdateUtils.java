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
import edu.ie3.datamodel.models.input.connector.ConnectorInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.*;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import edu.ie3.datamodel.utils.ContainerUtils;
import java.util.*;
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

    // getting the uuids of all connectors that needs to be updated
    Set<UUID> connectorIds =
        getConnectorIds(ContainerUtils.filterConnectors(rawGridElements, subnet));

    UpdatedConnectors updatedConnectors =
        updatedConnectors(updatedRawGridElements, connectorIds, types);

    RawGridElements finalRawGridElements =
        new RawGridElements(
            rawGridElements.getNodes(),
            updatedConnectors.lines,
            updatedConnectors.transformer2Ws,
            updatedConnectors.transformer3Ws,
            rawGridElements.getSwitches(),
            rawGridElements.getMeasurementUnits());

    return new RawGridElementsNodeUpdateResult(finalRawGridElements, updatedOldToNewNodes);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // general utils

  /**
   * Method for updating all connector in the given {@link RawGridElements}.
   *
   * @param rawGridElements given raw grid elements
   * @param connectorIds uuids of all connectors that need to be updated
   * @param types all known types
   * @return an {@link UpdatedConnectors}
   * @throws MissingTypeException if a required type is missing
   */
  protected static UpdatedConnectors updatedConnectors(
      RawGridElements rawGridElements, Set<UUID> connectorIds, Types types)
      throws MissingTypeException {
    // updating lines
    Set<LineInput> updatedLines = new HashSet<>();
    for (LineInput line : rawGridElements.getLines()) {
      if (connectorIds.contains(line.getUuid())) {
        updatedLines.add(updateLineVoltage(line, rating.apply(line.getNodeA()), types.lineTypes));
      } else {
        updatedLines.add(line);
      }
    }

    // updating two winding transformers
    Set<Transformer2WInput> updatedTransformer2Ws = new HashSet<>();
    for (Transformer2WInput transformer2W : rawGridElements.getTransformer2Ws()) {
      if (connectorIds.contains(transformer2W.getUuid())) {
        updatedTransformer2Ws.add(
            updateTransformerVoltage(
                transformer2W,
                rating.apply(transformer2W.getNodeA()),
                rating.apply(transformer2W.getNodeB()),
                types.transformer2WTypes));
      } else {
        updatedTransformer2Ws.add(transformer2W);
      }
    }

    // updating three winding transformers
    Set<Transformer3WInput> updatedTransformer3Ws = new HashSet<>();
    for (Transformer3WInput transformer3W : rawGridElements.getTransformer3Ws()) {
      if (connectorIds.contains(transformer3W.getUuid())) {
        updatedTransformer3Ws.add(
            updateTransformerVoltage(
                transformer3W,
                rating.apply(transformer3W.getNodeA()),
                rating.apply(transformer3W.getNodeB()),
                rating.apply(transformer3W.getNodeC()),
                types.transformer3WTypes));
      } else {
        updatedTransformer3Ws.add(transformer3W);
      }
    }

    return new UpdatedConnectors(updatedLines, updatedTransformer2Ws, updatedTransformer3Ws);
  }

  /**
   * @param rawGridElements raw grid elements
   * @return a set of all connector uuids
   */
  protected static Set<UUID> getConnectorIds(RawGridElements rawGridElements) {
    return Stream.of(
            rawGridElements.getLines(),
            rawGridElements.getTransformer2Ws(),
            rawGridElements.getTransformer3Ws())
        .flatMap(Collection::stream)
        .map(UniqueEntity::getUuid)
        .collect(Collectors.toSet());
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // records

  /** Record containing all {@link AssetTypeInput}s. */
  public record Types(
      Set<LineTypeInput> lineTypes,
      Set<Transformer2WTypeInput> transformer2WTypes,
      Set<Transformer3WTypeInput> transformer3WTypes) {}

  /** Record containing all updated {@link ConnectorInput}s. */
  protected record UpdatedConnectors(
      Set<LineInput> lines,
      Set<Transformer2WInput> transformer2Ws,
      Set<Transformer3WInput> transformer3Ws) {}
}
