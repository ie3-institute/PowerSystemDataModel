/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.grid;

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
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import edu.ie3.datamodel.utils.ExceptionUtils;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.measure.quantity.ElectricPotential;
import tech.units.indriya.ComparableQuantity;

/** Utilities for updating {@link GridContainer}. */
public class ContainerUpdateUtils extends ContainerNodeUpdateUtil {
  private ContainerUpdateUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  /**
   * Method for updating the voltage level of one subnet.
   *
   * @param container with at least one subnet
   * @param subnet number of the subgrid
   * @param newLevel new voltage level of the subnet
   * @param types all known types
   * @return a grid container with the updated subgrid
   */
  public static JointGridContainer enhanceVoltage(
      JointGridContainer container, int subnet, VoltageLevel newLevel, Types types)
      throws MissingTypeException, InvalidGridException {
    RawGridElements rawGridElements =
        ContainerUtils.filterForSubnet(container.getRawGrid(), subnet, true);

    // check if the provided types contains all types needed to change the voltage
    checkVoltageChange(rawGridElements, subnet, newLevel.getNominalVoltage(), types);

    // uuids of all connectors than needs to be adapted
    Set<UUID> connectorIds = getConnectorIds(rawGridElements);

    // entities with updated voltage levels
    UpdatedEntities updatedEntities =
        updateVoltageLevel(container, subnet, newLevel, connectorIds, types);

    return new JointGridContainer(
        container.getGridName(),
        updatedEntities.rawGridElements(),
        updatedEntities.systemParticipants(),
        updatedEntities.graphicElements());
  }

  /**
   * Method for updating the voltage level of one subnet.
   *
   * @param container with at least one subnet
   * @param subnet number of the subgrid
   * @param newLevel new voltage level of the subnet
   * @param types all known types
   * @return a grid container with the updated subgrid
   */
  public static SubGridContainer enhanceVoltage(
      SubGridContainer container, int subnet, VoltageLevel newLevel, Types types)
      throws MissingTypeException, InvalidGridException {
    RawGridElements rawGridElements = container.getRawGrid();

    // check if the provided types contains all types needed to change the voltage
    checkVoltageChange(container.getRawGrid(), subnet, newLevel.getNominalVoltage(), types);

    // uuids of all connectors than needs to be adapted
    Set<UUID> connectorIds = getConnectorIds(rawGridElements);

    // entities with updated voltage levels
    UpdatedEntities updatedEntities =
        updateVoltageLevel(container, subnet, newLevel, connectorIds, types);

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
   * types for the given {@link ConnectorInput}s.
   *
   * @param container grid container
   * @param subnet number of the subnet
   * @param newLevel new voltage level of the subnet
   * @param connectorIds a set with the uuids of all connector, which type needs to be adapted
   * @param types all known types
   * @return a {@link UpdatedEntities}
   */
  protected static UpdatedEntities updateVoltageLevel(
      GridContainer container,
      int subnet,
      VoltageLevel newLevel,
      Set<UUID> connectorIds,
      Types types) {

    RawGridElements rawGridElements = container.getRawGrid();

    Set<NodeInput> inSubnet =
        rawGridElements.getNodes().stream()
            .filter(node -> node.getSubnet() == subnet)
            .collect(Collectors.toSet());
    Map<NodeInput, NodeInput> oldToNew = AssetUpdateUtils.enhanceVoltage(inSubnet, newLevel);

    // result after updating node voltages
    RawGridElementsNodeUpdateResult rawGridUpdateResult =
        updateRawGridElementsWithNodes(rawGridElements, oldToNew);

    RawGridElements updatedRawGridElements = rawGridUpdateResult.rawGridElements();
    Map<NodeInput, NodeInput> updatedOldToNewNodes = rawGridUpdateResult.updatedOldToNewNodes();

    // updating line types
    Set<LineInput> updatedLines =
        updatedRawGridElements.getLines().stream()
            .filter(line -> connectorIds.contains(line.getUuid()))
            .map(line -> AssetUpdateUtils.enhanceLine(line, types.lineTypes))
            .collect(Collectors.toSet());

    // updating transformer2W types
    Set<Transformer2WInput> updatedTransformers2W =
        updatedRawGridElements.getTransformer2Ws().stream()
            .filter(transformer -> connectorIds.contains(transformer.getUuid()))
            .map(
                transformer ->
                    AssetUpdateUtils.enhanceTransformer2W(transformer, types.transformer2WTypes))
            .collect(Collectors.toSet());

    // updating transformer3W types
    Set<Transformer3WInput> updatedTransformers3W =
        updatedRawGridElements.getTransformer3Ws().stream()
            .filter(transformer -> connectorIds.contains(transformer.getUuid()))
            .map(
                transformer ->
                    AssetUpdateUtils.enhanceTransformer3W(transformer, types.transformer3WTypes))
            .collect(Collectors.toSet());

    RawGridElements finalRawGridElements =
        new RawGridElements(
            rawGridElements.getNodes(),
            updatedLines,
            updatedTransformers2W,
            updatedTransformers3W,
            rawGridElements.getSwitches(),
            rawGridElements.getMeasurementUnits());

    /* SystemParticipants */
    SystemParticipants updatedSystemParticipants =
        updateSystemParticipantsWithNodes(container.getSystemParticipants(), updatedOldToNewNodes);

    /* GraphicElements */
    GraphicElements updateGraphicElements =
        updateGraphicElementsWithNodes(
            container.getGraphics(), updatedOldToNewNodes, updatedRawGridElements.getLines());

    return new UpdatedEntities(
        finalRawGridElements, updatedSystemParticipants, updateGraphicElements);
  }

  /**
   * Method to check if all necessary type are provided to change the voltage.
   *
   * @param rawGrid with elements
   * @param subnet number of subnet
   * @param vRatedNew the new voltage rating
   * @param types all known types
   * @throws MissingTypeException if at least one needed type is missing
   */
  protected static void checkVoltageChange(
      RawGridElements rawGrid,
      int subnet,
      ComparableQuantity<ElectricPotential> vRatedNew,
      Types types)
      throws MissingTypeException {
    Set<MissingTypeException> exceptions = new HashSet<>();

    if (!AssetUpdateUtils.checkLineTypes(types.lineTypes, vRatedNew)) {
      exceptions.add(new MissingTypeException("Missing line type with rating: " + vRatedNew));
    }

    rawGrid
        .getTransformer2Ws()
        .forEach(
            transformer -> {
              ComparableQuantity<ElectricPotential> portA;
              ComparableQuantity<ElectricPotential> portB;

              if (transformer.getNodeA().getSubnet() == subnet) {
                portA = vRatedNew;
                portB = transformer.getNodeB().getVoltLvl().getNominalVoltage();
              } else {
                portA = transformer.getNodeA().getVoltLvl().getNominalVoltage();
                portB = vRatedNew;
              }

              if (!AssetUpdateUtils.checkTransformer2WTypes(
                  types.transformer2WTypes, portA, portB)) {
                exceptions.add(
                    new MissingTypeException(
                        "Missing line type with ratings: " + portA + " , " + portB));
              }
            });

    rawGrid
        .getTransformer3Ws()
        .forEach(
            transformer -> {
              ComparableQuantity<ElectricPotential> portA;
              ComparableQuantity<ElectricPotential> portB;
              ComparableQuantity<ElectricPotential> portC;

              if (transformer.getNodeA().getSubnet() == subnet) {
                portA = vRatedNew;
                portB = transformer.getNodeB().getVoltLvl().getNominalVoltage();
                portC = transformer.getNodeC().getVoltLvl().getNominalVoltage();
              } else if (transformer.getNodeB().getSubnet() == subnet) {
                portA = transformer.getNodeA().getVoltLvl().getNominalVoltage();
                portB = vRatedNew;
                portC = transformer.getNodeC().getVoltLvl().getNominalVoltage();
              } else {
                portA = transformer.getNodeA().getVoltLvl().getNominalVoltage();
                portB = transformer.getNodeB().getVoltLvl().getNominalVoltage();
                portC = vRatedNew;
              }

              if (!AssetUpdateUtils.checkTransformer3WTypes(
                  types.transformer3WTypes, portA, portB, portC)) {
                exceptions.add(
                    new MissingTypeException(
                        "Missing line type with ratings: "
                            + portA
                            + " , "
                            + portB
                            + " , "
                            + portC));
              }
            });

    if (!exceptions.isEmpty()) {
      throw new MissingTypeException(
          "There not enough type for changing the voltage! "
              + ExceptionUtils.getMessages(exceptions));
    }
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // non public utils

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

  /** Record containing all {@link AssetTypeInput}s. */
  public record Types(
      Set<LineTypeInput> lineTypes,
      Set<Transformer2WTypeInput> transformer2WTypes,
      Set<Transformer3WTypeInput> transformer3WTypes) {}
}
