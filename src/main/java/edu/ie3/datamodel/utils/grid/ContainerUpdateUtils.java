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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Power;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.ComparableQuantity;

/** Utilities for updating {@link GridContainer}. */
public class ContainerUpdateUtils extends ContainerNodeUpdateUtil {
  private ContainerUpdateUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  private static final Logger log = LoggerFactory.getLogger(ContainerUpdateUtils.class);

  private static final Function<NodeInput, ComparableQuantity<ElectricPotential>> rating =
      node -> node.getVoltLvl().getNominalVoltage();

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
  // methods for updating connectors

  /**
   * Method for updating the rated current of the given {@link LineInput}. For changing the current
   * and voltage rating use {@link #updateLine(LineInput, ComparableQuantity, ComparableQuantity,
   * Collection)} instead.
   *
   * @param line given line
   * @param iMin minimum rated current
   * @param types all known line types
   * @return an updated line
   * @throws MissingTypeException if not suitable line type was found
   */
  public static LineInput updateLineCurrent(
      LineInput line, ComparableQuantity<ElectricCurrent> iMin, Collection<LineTypeInput> types)
      throws MissingTypeException {
    return updateLine(line, iMin, line.getNodeB().getVoltLvl().getNominalVoltage(), types);
  }

  /**
   * Method for updating the rated voltage of the given {@link LineInput}. For changing the current
   * and voltage rating use {@link #updateLine(LineInput, ComparableQuantity, ComparableQuantity,
   * Collection)} instead.
   *
   * @param line given line
   * @param vRated rated voltage
   * @param types all known line types
   * @return an updated line
   * @throws MissingTypeException if not suitable line type was found
   */
  public static LineInput updateLineVoltage(
      LineInput line, ComparableQuantity<ElectricPotential> vRated, Collection<LineTypeInput> types)
      throws MissingTypeException {
    return updateLine(line, line.getType().getiMax(), vRated, types);
  }

  /**
   * Method for updating the current and voltage rating of the given {@link LineInput}.
   *
   * <p>NOTE: This method automatically increase the number of {@link
   * ConnectorInput#getParallelDevices()} in order to fulfill the given current requirement.
   *
   * @param line given line
   * @param iMin minimum required current
   * @param vRated rated voltage
   * @param types all known line types
   * @return an updated line
   * @throws MissingTypeException if not suitable line type was found
   */
  public static LineInput updateLine(
      LineInput line,
      ComparableQuantity<ElectricCurrent> iMin,
      ComparableQuantity<ElectricPotential> vRated,
      Collection<LineTypeInput> types)
      throws MissingTypeException {
    List<LineTypeInput> suitableTypes = TypeUtils.findSuitableTypes(types, vRated);

    if (suitableTypes.isEmpty()) {
      // throws an exception if no line type is suitable for the required voltage rating
      throw new MissingTypeException("No suitable line type found for rating: " + vRated);
    }

    return TypeUtils.findSuitableLineType(suitableTypes, iMin)
        .map(suitableType -> line.copy().type(suitableType).build())
        .orElseGet(
            () -> {
              LineTypeInput type = suitableTypes.get(suitableTypes.size() - 1);
              int parallelDevices = TypeUtils.calculateNeededParallelDevices(type, iMin);

              log.debug(
                  "Increased the number of parallel devices of line '{}' to {} in order to carry a current of '{}'.",
                  line.getUuid(),
                  parallelDevices,
                  iMin);

              return line.copy().type(type).parallelDevices(parallelDevices).build();
            });
  }

  /**
   * Method for updating rated power of the given {@link Transformer2WInput}.For changing the power
   * and voltage rating use {@link #updateTransformer(Transformer2WInput, ComparableQuantity,
   * ComparableQuantity, ComparableQuantity, Collection)} instead.
   *
   * <p>NOTE: This method automatically increase the number of {@link
   * ConnectorInput#getParallelDevices()} in order to fulfill the given power requirement.
   *
   * @param transformer given two winding transformer
   * @param sMin minimum rated power
   * @param types all known two winding transformer types
   * @return an updated transformer
   * @throws MissingTypeException if not suitable transformer type was found
   */
  public static Transformer2WInput updateTransformerPower(
      Transformer2WInput transformer,
      ComparableQuantity<Power> sMin,
      Collection<Transformer2WTypeInput> types)
      throws MissingTypeException {
    return updateTransformer(
        transformer,
        sMin,
        rating.apply(transformer.getNodeA()),
        rating.apply(transformer.getNodeB()),
        types);
  }

  /**
   * Method for updating rated voltage of the given {@link Transformer2WInput}.For changing the
   * power and voltage rating use {@link #updateTransformer(Transformer2WInput, ComparableQuantity,
   * ComparableQuantity, ComparableQuantity, Collection)} instead.
   *
   * <p>NOTE: This method automatically increase the number of {@link
   * ConnectorInput#getParallelDevices()} in order to fulfill the given power requirement.
   *
   * @param transformer given three winding transformer
   * @param vRatedA rated voltage at port A
   * @param vRatedB rated voltage at port B
   * @param types all known three winding transformer types
   * @return an updated transformer
   * @throws MissingTypeException if not suitable transformer type was found
   */
  public static Transformer2WInput updateTransformerVoltage(
      Transformer2WInput transformer,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB,
      Collection<Transformer2WTypeInput> types)
      throws MissingTypeException {
    return updateTransformer(
        transformer, transformer.getType().getsRated(), vRatedA, vRatedB, types);
  }

  /**
   * Method for updating the power and voltage rating of the given {@link Transformer2WInput}.
   *
   * <p>NOTE: This method automatically increase the number of {@link
   * ConnectorInput#getParallelDevices()} in order to fulfill the given power requirement.
   *
   * @param transformer given two winding transformer
   * @param sMin minimum required power
   * @param vRatedA rated voltage at port A
   * @param vRatedB rated voltage at port B
   * @param types all known two winding transformer types
   * @return an updated transformer
   * @throws MissingTypeException if not suitable transformer type was found
   */
  public static Transformer2WInput updateTransformer(
      Transformer2WInput transformer,
      ComparableQuantity<Power> sMin,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB,
      Collection<Transformer2WTypeInput> types)
      throws MissingTypeException {
    List<Transformer2WTypeInput> suitableTypes =
        TypeUtils.findSuitableTypes(types, vRatedA, vRatedB);

    if (suitableTypes.isEmpty()) {
      // throws an exception if no transformer type is suitable for the required voltage ratings
      throw new MissingTypeException(
          "No suitable two winding transformer type found for rating: " + vRatedA + ", " + vRatedB);
    }

    return TypeUtils.findSuitableTransformerType(types, sMin)
        .map(suitableType -> transformer.copy().type(suitableType).build())
        .orElseGet(
            () -> {
              Transformer2WTypeInput type = suitableTypes.get(suitableTypes.size() - 1);
              int parallelDevices = TypeUtils.calculateNeededParallelDevices(type, sMin);

              log.debug(
                  "Increased the number of parallel devices of two winding transformer '{}' to {} in order to carry a power of '{}'.",
                  transformer.getUuid(),
                  parallelDevices,
                  sMin);

              return transformer.copy().type(type).parallelDevices(parallelDevices).build();
            });
  }

  /**
   * Method for updating rated power of the given {@link Transformer3WInput}.For changing the power
   * and voltage rating use {@link #updateTransformer(Transformer3WInput, ComparableQuantity,
   * ComparableQuantity, ComparableQuantity, ComparableQuantity, ComparableQuantity,
   * ComparableQuantity, Collection)} instead.
   *
   * <p>NOTE: This method automatically increase the number of {@link
   * ConnectorInput#getParallelDevices()} in order to fulfill the given power requirement.
   *
   * @param transformer given three winding transformer
   * @param sMinA minimum required power at port A
   * @param sMinB minimum required power at port B
   * @param sMinC minimum required power at port C
   * @param types all known three winding transformer types
   * @return an updated transformer
   * @throws MissingTypeException if not suitable transformer type was found
   */
  public static Transformer3WInput updateTransformerPower(
      Transformer3WInput transformer,
      ComparableQuantity<Power> sMinA,
      ComparableQuantity<Power> sMinB,
      ComparableQuantity<Power> sMinC,
      Collection<Transformer3WTypeInput> types)
      throws MissingTypeException {
    return updateTransformer(
        transformer,
        sMinA,
        sMinB,
        sMinC,
        rating.apply(transformer.getNodeA()),
        rating.apply(transformer.getNodeB()),
        rating.apply(transformer.getNodeC()),
        types);
  }

  /**
   * Method for updating rated voltage of the given {@link Transformer3WInput}.For changing the
   * power and voltage rating use {@link #updateTransformer(Transformer3WInput, ComparableQuantity,
   * ComparableQuantity, ComparableQuantity, ComparableQuantity, ComparableQuantity,
   * ComparableQuantity, Collection)} instead.
   *
   * <p>NOTE: This method automatically increase the number of {@link
   * ConnectorInput#getParallelDevices()} in order to fulfill the given power requirement.
   *
   * @param transformer given three winding transformer
   * @param vRatedA rated voltage at port A
   * @param vRatedB rated voltage at port B
   * @param vRatedC rated voltage at port C
   * @param types all known three winding transformer types
   * @return an updated transformer
   * @throws MissingTypeException if not suitable transformer type was found
   */
  public static Transformer3WInput updateTransformerVoltage(
      Transformer3WInput transformer,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB,
      ComparableQuantity<ElectricPotential> vRatedC,
      Collection<Transformer3WTypeInput> types)
      throws MissingTypeException {
    Transformer3WTypeInput current = transformer.getType();

    return updateTransformer(
        transformer,
        current.getsRatedA(),
        current.getsRatedB(),
        current.getsRatedC(),
        vRatedA,
        vRatedB,
        vRatedC,
        types);
  }

  /**
   * Method for updating the power and voltage rating of the given {@link Transformer3WInput}.
   *
   * <p>NOTE: This method automatically increase the number of {@link
   * ConnectorInput#getParallelDevices()} in order to fulfill the given power requirement.
   *
   * @param transformer given three winding transformer
   * @param sMinA minimum required power at port A
   * @param sMinB minimum required power at port B
   * @param sMinC minimum required power at port C
   * @param vRatedA rated voltage at port A
   * @param vRatedB rated voltage at port B
   * @param vRatedC rated voltage at port C
   * @param types all known three winding transformer types
   * @return an updated transformer
   * @throws MissingTypeException if not suitable transformer type was found
   */
  public static Transformer3WInput updateTransformer(
      Transformer3WInput transformer,
      ComparableQuantity<Power> sMinA,
      ComparableQuantity<Power> sMinB,
      ComparableQuantity<Power> sMinC,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB,
      ComparableQuantity<ElectricPotential> vRatedC,
      Collection<Transformer3WTypeInput> types)
      throws MissingTypeException {
    List<Transformer3WTypeInput> suitableTypes =
        TypeUtils.findSuitableTypes(types, vRatedA, vRatedB, vRatedC);

    if (suitableTypes.isEmpty()) {
      // throws an exception if no transformer type is suitable for the required voltage ratings
      throw new MissingTypeException(
          "No suitable three winding transformer type found for rating: "
              + vRatedA
              + ", "
              + vRatedB);
    }

    return TypeUtils.findSuitableTransformerType(types, sMinA, sMinB, sMinC)
        .map(suitableType -> transformer.copy().type(suitableType).build())
        .orElseGet(
            () -> {
              Transformer3WTypeInput type = suitableTypes.get(suitableTypes.size() - 1);
              int parallelDevices =
                  TypeUtils.calculateNeededParallelDevices(type, sMinA, sMinB, sMinC);

              log.debug(
                  "Increased the number of parallel devices of three winding transformer '{}' to {} in order to carry a power of '{}, {}, {}'.",
                  transformer.getUuid(),
                  parallelDevices,
                  sMinA,
                  sMinB,
                  sMinC);

              return transformer.copy().type(type).parallelDevices(parallelDevices).build();
            });
  }

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

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // methods for changing subnet voltage

  /**
   * Method for changing the given {@link VoltageLevel} of a set of {@link NodeInput}s.
   *
   * <p>NOTE: This method will only change the voltage level without any checks.
   *
   * @param nodes given nodes
   * @param newLvl the new voltage level
   * @return a map: old to new nodes
   */
  public static Map<NodeInput, NodeInput> enhanceVoltage(
      Set<NodeInput> nodes, VoltageLevel newLvl) {
    return nodes.parallelStream()
        .map(node -> Map.entry(node, node.copy().voltLvl(newLvl).build()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

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
    Map<NodeInput, NodeInput> oldToNew = enhanceVoltage(inSubnet, newLevel);

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
