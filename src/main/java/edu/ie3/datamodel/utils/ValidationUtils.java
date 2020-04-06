/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.exceptions.UnsafeEntityException;
import edu.ie3.datamodel.exceptions.VoltageLevelException;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.container.GridContainer;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.measure.Quantity;

/** Basic Sanity validation tools for entities */
public class ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private ValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Checks a complete grid data container
   *
   * @param gridContainer Grid model to check
   */
  public static void checkGrid(GridContainer gridContainer) {
    checkRawGridElements(gridContainer.getRawGrid());
    checkSystemParticipants(
        gridContainer.getSystemParticipants(), gridContainer.getRawGrid().getNodes());
    checkGraphicElements(
        gridContainer.getGraphics(),
        gridContainer.getRawGrid().getNodes(),
        gridContainer.getRawGrid().getLines());
  }

  /**
   * Checks the validity of given {@link RawGridElements}. The single elements are checked as well
   * as the fact, that none of the assets is connected to a node, that is not in the set of nodes.
   *
   * @param rawGridElements Raw grid elements
   * @throws InvalidGridException If something is wrong
   */
  public static void checkRawGridElements(RawGridElements rawGridElements) {
    if (rawGridElements == null)
      throw new NullPointerException("Expected raw grid elements, but got nothing. :-(");

    /* Checking nodes */
    Set<NodeInput> nodes = rawGridElements.getNodes();
    nodes.forEach(ValidationUtils::checkNode);

    /* Checking lines */
    rawGridElements
        .getLines()
        .forEach(
            line -> {
              checkNodeAvailability(line, nodes);
              checkLine(line);
            });

    /* Checking two winding transformers */
    rawGridElements
        .getTransformer2Ws()
        .forEach(
            transformer -> {
              checkNodeAvailability(transformer, nodes);
              checkTransformer2W(transformer);
            });

    /* Checking three winding transformers */
    rawGridElements
        .getTransformer3Ws()
        .forEach(
            transformer -> {
              checkNodeAvailability(transformer, nodes);
              checkTransformer3W(transformer);
            });

    /* Checking switches */
    rawGridElements
        .getSwitches()
        .forEach(
            switcher -> {
              checkNodeAvailability(switcher, nodes);
              checkSwitch(switcher);
            });

    /* Checking measurement units */
    rawGridElements
        .getMeasurementUnits()
        .forEach(
            measurement -> {
              checkNodeAvailability(measurement, nodes);
              checkMeasurementUnit(measurement);
            });
  }

  /**
   * Checks the validity of each and every system participant. Moreover, it checks, if the systems
   * are connected to an node that is not in the provided set
   *
   * @param systemParticipants The system participants
   * @param nodes Set of already known nodes
   */
  public static void checkSystemParticipants(
      SystemParticipants systemParticipants, Set<NodeInput> nodes) {
    if (systemParticipants == null)
      throw new NullPointerException("Expected system participants, but got nothing. :-(");

    systemParticipants.getBmPlants().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getChpPlants().forEach(entity -> checkNodeAvailability(entity, nodes));

    /* TODO: Electric vehicle charging systems are currently only dummy implementation. if this has changed, the whole
     *   method can be aggregated */

    systemParticipants.getFixedFeedIns().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getHeatPumps().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getLoads().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getPvPlants().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getStorages().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getWecPlants().forEach(entity -> checkNodeAvailability(entity, nodes));
  }

  /**
   * Checks the given graphic elements for validity
   *
   * @param graphicElements Elements to check
   * @param nodes Already known and checked nodes
   * @param lines Already known and checked lines
   */
  public static void checkGraphicElements(
      GraphicElements graphicElements, Set<NodeInput> nodes, Set<LineInput> lines) {
    if (graphicElements == null)
      throw new NullPointerException("Expected graphic elements, but got nothing. :-(");

    graphicElements
        .getNodeGraphics()
        .forEach(
            graphic -> {
              if (!nodes.contains(graphic.getNode()))
                throw new InvalidEntityException(
                    "The node graphic refers to a node, that is not among the provided ones.",
                    graphic);
            });

    graphicElements
        .getLineGraphics()
        .forEach(
            graphic -> {
              if (!lines.contains(graphic.getLine()))
                throw new InvalidEntityException(
                    "The line graphic refers to a line, that is not among the provided ones.",
                    graphic);
            });
  }

  /**
   * Validates a node if: <br>
   * - it is not null <br>
   * - subnet is not null <br>
   * - vRated and vTarget are neither null nor 0
   */
  public static void checkNode(NodeInput node) {
    if (node == null) throw new NullPointerException("Expected a node, but got nothing. :-(");
    try {
      checkVoltageLevel(node.getVoltLvl());
    } catch (VoltageLevelException e) {
      throw new InvalidEntityException("Element has invalid voltage level", node);
    }

    if (node.getvTarget() == null)
      throw new InvalidEntityException("vRated or vTarget is null", node);
    if (node.getvTarget().getValue().doubleValue() <= 0d)
      throw new UnsafeEntityException("vTarget is not a positive value", node);
  }

  /**
   * Validates a voltage level
   *
   * @param voltageLevel Element to validate
   * @throws VoltageLevelException If nominal voltage is not apparent or not a positive value
   */
  private static void checkVoltageLevel(VoltageLevel voltageLevel) throws VoltageLevelException {
    if (voltageLevel == null)
      throw new NullPointerException("Expected a voltage level, but got nothing. :-(");
    if (voltageLevel.getNominalVoltage() == null)
      throw new VoltageLevelException(
          "The nominal voltage of voltage level " + voltageLevel + " is null");
    if (voltageLevel.getNominalVoltage().getValue().doubleValue() <= 0d)
      throw new VoltageLevelException(
          "The nominal voltage of voltage level " + voltageLevel + " must be positive!");
  }

  /**
   * Validates a connector if: <br>
   * - it is not null <br>
   * - both of its nodes are not null
   */
  public static void checkConnector(ConnectorInput connector) {
    if (connector == null)
      throw new NullPointerException("Expected a connector, but got nothing. :-(");
    if (connector.getNodeA() == null || connector.getNodeB() == null)
      throw new InvalidEntityException("at least one node of this connector is null ", connector);
  }

  /**
   * Checks, if the nodes of the {@link ConnectorInput} are in the collection of provided, already
   * determined nodes
   *
   * @param connector Connector to examine
   * @param nodes Permissible, already known nodes
   */
  private static void checkNodeAvailability(ConnectorInput connector, Collection<NodeInput> nodes) {
    if (!nodes.contains(connector.getNodeA()) || !nodes.contains(connector.getNodeB()))
      throw getMissingNodeException(connector);
  }

  /**
   * Checks, if the nodes of the {@link Transformer3WInput} are in the collection of provided,
   * already determined nodes
   *
   * @param transformer Transformer to examine
   * @param nodes Permissible, already known nodes
   */
  private static void checkNodeAvailability(
      Transformer3WInput transformer, Collection<NodeInput> nodes) {
    if (!nodes.contains(transformer.getNodeA())
        || !nodes.contains(transformer.getNodeB())
        || !nodes.contains(transformer.getNodeC())) throw getMissingNodeException(transformer);
  }

  /**
   * Checks, if the node of the {@link SystemParticipantInput} are in the collection of provided,
   * already determined nodes
   *
   * @param participant Connector to examine
   * @param nodes Permissible, already known nodes
   */
  private static void checkNodeAvailability(
      SystemParticipantInput participant, Collection<NodeInput> nodes) {
    if (!nodes.contains(participant.getNode())) throw getMissingNodeException(participant);
  }

  /**
   * Checks, if the node of the {@link MeasurementUnitInput} are in the collection of provided,
   * already determined nodes
   *
   * @param measurementUnit Connector to examine
   * @param nodes Permissible, already known nodes
   */
  private static void checkNodeAvailability(
      MeasurementUnitInput measurementUnit, Collection<NodeInput> nodes) {
    if (!nodes.contains(measurementUnit.getNode())) throw getMissingNodeException(measurementUnit);
  }

  /**
   * Validates a line if: <br>
   * - it is not null <br>
   * - line type is not null <br>
   * - {@link ValidationUtils#checkLineType(LineTypeInput)} and {@link
   * ValidationUtils#checkConnector(ConnectorInput)} confirm a valid type and valid connector
   * properties
   */
  public static void checkLine(LineInput line) {
    if (line == null) throw new NullPointerException("Expected a line, but got nothing. :-(");
    checkConnector(line);
    checkLineType(line.getType());
    if (line.getNodeA().getSubnet() != line.getNodeB().getSubnet())
      throw new InvalidEntityException("the line {} connects to different subnets", line);
    if (line.getNodeA().getVoltLvl() != line.getNodeB().getVoltLvl())
      throw new InvalidEntityException("the line {} connects to different voltage levels", line);
  }

  /**
   * Validates a line type if: <br>
   * - it is not null <br>
   * - none of its values are null or 0 <br>
   */
  public static void checkLineType(LineTypeInput lineType) {
    if (lineType == null)
      throw new NullPointerException("Expected a line type, but got nothing. :-(");
    if (lineType.getvRated() == null
        || lineType.getiMax() == null
        || lineType.getB() == null
        || lineType.getX() == null
        || lineType.getR() == null
        || lineType.getG() == null)
      throw new InvalidEntityException("at least one value of line type is null", lineType);

    detectNegativeQuantities(new Quantity<?>[] {lineType.getB(), lineType.getG()}, lineType);
    detectZeroOrNegativeQuantities(
        new Quantity<?>[] {
          lineType.getvRated(), lineType.getiMax(), lineType.getX(), lineType.getR()
        },
        lineType);
  }

  /**
   * Validates a transformer if: <br>
   * - it is not null <br>
   * - transformer type is not null <br>
   * - {@link ValidationUtils#checkTransformer2WType(Transformer2WTypeInput)} and {@link
   * ValidationUtils#checkConnector(ConnectorInput)} confirm a valid type and valid connector
   * properties
   */
  public static void checkTransformer2W(Transformer2WInput trafo) {
    if (trafo == null)
      throw new NullPointerException("Expected a two winding transformer, but got nothing. :-(");
    checkConnector(trafo);
    checkTransformer2WType(trafo.getType());
  }

  /**
   * Validates a transformer type if: <br>
   * - it is not null <br>
   * - none of its values are null or 0 <br>
   */
  public static void checkTransformer2WType(Transformer2WTypeInput trafoType) {
    if (trafoType == null)
      throw new NullPointerException(
          "Expected a two winding transformer type, but got nothing. :-(");
    if ((trafoType.getsRated() == null)
        || (trafoType.getvRatedA() == null)
        || (trafoType.getvRatedB() == null)
        || (trafoType.getrSc() == null)
        || (trafoType.getxSc() == null)
        || (trafoType.getgM() == null)
        || (trafoType.getbM() == null)
        || (trafoType.getdV() == null)
        || (trafoType.getdPhi() == null))
      throw new InvalidEntityException("at least one value of trafo2w type is null", trafoType);

    detectNegativeQuantities(
        new Quantity<?>[] {trafoType.getgM(), trafoType.getbM(), trafoType.getdPhi()}, trafoType);
    detectZeroOrNegativeQuantities(
        new Quantity<?>[] {
          trafoType.getsRated(),
          trafoType.getvRatedA(),
          trafoType.getvRatedB(),
          trafoType.getxSc(),
          trafoType.getdV()
        },
        trafoType);
  }

  /**
   * Validates a transformer if: <br>
   * - it is not null <br>
   * - transformer type is not null <br>
   * - {@link ValidationUtils#checkTransformer3WType(Transformer3WTypeInput)} and {@link
   * ValidationUtils#checkConnector(ConnectorInput)} confirm a valid type and valid connector
   * properties
   */
  public static void checkTransformer3W(Transformer3WInput trafo) {
    if (trafo == null)
      throw new NullPointerException("Expected a three winding transformer, but got nothing. :-(");
    checkConnector(trafo);
    if (trafo.getNodeC() == null)
      throw new InvalidEntityException("at least one node of this connector is null", trafo);
    checkTransformer3WType(trafo.getType());
  }

  /**
   * Validates a transformer type if: <br>
   * - it is not null <br>
   * - none of its values are null or 0 <br>
   */
  public static void checkTransformer3WType(Transformer3WTypeInput trafoType) {
    if (trafoType == null)
      throw new NullPointerException(
          "Expected a three winding transformer type, but got nothing. :-(");
    if ((trafoType.getsRatedA() == null)
        || (trafoType.getsRatedB() == null)
        || (trafoType.getsRatedC() == null)
        || (trafoType.getvRatedA() == null)
        || (trafoType.getvRatedB() == null)
        || (trafoType.getvRatedC() == null)
        || (trafoType.getrScA() == null)
        || (trafoType.getrScB() == null)
        || (trafoType.getrScC() == null)
        || (trafoType.getxScA() == null)
        || (trafoType.getxScB() == null)
        || (trafoType.getxScC() == null)
        || (trafoType.getgM() == null)
        || (trafoType.getbM() == null)
        || (trafoType.getdV() == null)
        || (trafoType.getdPhi() == null))
      throw new InvalidEntityException("at least one value of trafo3w type is null", trafoType);

    detectNegativeQuantities(
        new Quantity<?>[] {trafoType.getgM(), trafoType.getbM(), trafoType.getdPhi()}, trafoType);
    detectZeroOrNegativeQuantities(
        new Quantity<?>[] {
          trafoType.getsRatedA(),
          trafoType.getsRatedB(),
          trafoType.getsRatedC(),
          trafoType.getvRatedA(),
          trafoType.getvRatedB(),
          trafoType.getvRatedC(),
          trafoType.getxScA(),
          trafoType.getxScB(),
          trafoType.getxScC(),
          trafoType.getdV()
        },
        trafoType);
  }

  /**
   * Validates a measurement unit if: <br>
   * - it is not null <br>
   * - its node is not nul
   */
  public static void checkMeasurementUnit(MeasurementUnitInput measurementUnit) {
    if (measurementUnit == null)
      throw new NullPointerException("Expected a measurement unit, but got nothing. :-(");
    if (measurementUnit.getNode() == null)
      throw new InvalidEntityException("node is null", measurementUnit);
  }

  /**
   * Validates a measurement unit if: <br>
   * - it is not null <br>
   * - its node is not nul
   */
  public static void checkSwitch(SwitchInput switchInput) {
    if (switchInput == null)
      throw new NullPointerException("Expected a switch, but got nothing. :-(");
    checkConnector(switchInput);
    if (switchInput.getNodeA().getSubnet() != switchInput.getNodeB().getSubnet())
      throw new InvalidEntityException("the switch {} connects to different subnets", switchInput);
    if (switchInput.getNodeA().getVoltLvl() != switchInput.getNodeB().getVoltLvl())
      throw new InvalidEntityException(
          "the switch {} connects to different voltage levels", switchInput);
  }

  /**
   * Builds an exception, that announces, that the given input is connected to a node, that is not
   * in the set of nodes provided.
   *
   * @param input Input model
   * @return Exception for a missing node
   */
  private static InvalidGridException getMissingNodeException(AssetInput input) {
    return new InvalidGridException(
        input.getClass().getSimpleName()
            + " "
            + input
            + " is connected to a node, that is not in the set of nodes.");
  }

  /**
   * Goes through the provided quantities and reports those, that have negative value via synoptic
   * {@link UnsafeEntityException}
   *
   * @param quantities Array of quantities to check
   * @param entity Unique entity holding the malformed quantities
   */
  private static void detectNegativeQuantities(Quantity<?>[] quantities, UniqueEntity entity) {
    Predicate<Quantity<?>> predicate = quantity -> quantity.getValue().doubleValue() < 0;
    detectMalformedQuantities(
        quantities, entity, predicate, "The following quantities have to be zero or positive");
  }

  /**
   * Goes through the provided quantities and reports those, that are zero or have negative value
   * via synoptic {@link UnsafeEntityException}
   *
   * @param quantities Array of quantities to check
   * @param entity Unique entity holding the malformed quantities
   */
  private static void detectZeroOrNegativeQuantities(
      Quantity<?>[] quantities, UniqueEntity entity) {
    Predicate<Quantity<?>> predicate = quantity -> quantity.getValue().doubleValue() <= 0;
    detectMalformedQuantities(
        quantities, entity, predicate, "The following quantities have to be positive");
  }

  /**
   * Goes through the provided quantities and reports those, that do fulfill the given predicate via
   * synoptic {@link UnsafeEntityException}
   *
   * @param quantities Array of quantities to check
   * @param entity Unique entity holding the malformed quantities
   * @param predicate Predicate to detect the malformed quantities
   * @param msg Message prefix to use for the exception message: [msg]: [malformedQuantities]
   */
  private static void detectMalformedQuantities(
      Quantity<?>[] quantities, UniqueEntity entity, Predicate<Quantity<?>> predicate, String msg) {
    String malformedQuantities =
        Arrays.stream(quantities)
            .filter(predicate)
            .map(Quantity::toString)
            .collect(Collectors.joining(", "));
    if (!malformedQuantities.isEmpty()) {
      throw new UnsafeEntityException(msg + ": " + malformedQuantities, entity);
    }
  }

  public static boolean distinctUuids(Collection<UniqueEntity> entities) {
    return entities.stream()
            .filter(distinctByKey(UniqueEntity::getUuid))
            .collect(Collectors.toSet())
            .size()
        == entities.size();
  }

  public static Collection<UniqueEntity> distinctUuidSet(Collection<UniqueEntity> entities) {
    return entities.stream()
        .filter(distinctByKey(UniqueEntity::getUuid))
        .collect(Collectors.toSet());
  }

  private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  public static void checkForDuplicateUuids(
      String containerClassName, Collection<UniqueEntity> entities) {
    if (!distinctUuids(entities)) {
      Collection<UniqueEntity> duplicateUuids =
          entities.stream()
              .filter(entity -> distinctUuidSet(entities).contains(entity))
              .collect(Collectors.toSet());

      String exceptionString =
          duplicateUuids.stream()
              .map(entity -> entity.getUuid().toString())
              .collect(Collectors.joining("\n"));

      throw new InvalidGridException(
          "The provided entities in "
              + containerClassName
              + "contain duplicate uuids. "
              + "This is not allowed.\nDuplicate entries:\n"
              + exceptionString);
    }
  }
}
