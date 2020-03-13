/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.exceptions.UnsafeEntityException;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import java.util.Collection;
import java.util.Set;

/** Basic Sanity validation tools for entities */
public class ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private ValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Validates the entity based on the entity specific validation, returns false if entity is null
   */
  public static boolean checkEntity(UniqueEntity entity) {
    if (entity == null) return false;
    if (entity instanceof NodeInput) return checkNode((NodeInput) entity);
    if (entity instanceof LineInput) return checkLine((LineInput) entity);
    if (entity instanceof LineTypeInput) return checkLineType((LineTypeInput) entity);
    if (entity instanceof Transformer2WInput)
      return checkTransformer2W((Transformer2WInput) entity);
    if (entity instanceof Transformer2WTypeInput)
      return checkTransformer2WType((Transformer2WTypeInput) entity);
    if (entity instanceof Transformer3WInput)
      return checkTransformer3W((Transformer3WInput) entity);
    if (entity instanceof Transformer3WTypeInput)
      return checkTransformer3WType((Transformer3WTypeInput) entity);
    if (entity instanceof MeasurementUnitInput)
      return checkMeasurementUnit((MeasurementUnitInput) entity);
    return true;
  }

  /**
   * Validates a node if: <br>
   * - it is not null <br>
   * - subnet is not null <br>
   * - vRated and vTarget are neither null nor 0
   */
  public static boolean checkNode(NodeInput node) {
    if (node == null) return false;
    if (node.getVoltLvl().getNominalVoltage() == null || node.getvTarget() == null)
      throw new InvalidEntityException("vRated or vTarget is null", node);
    if (node.getVoltLvl().getNominalVoltage().getValue().doubleValue() == 0d
        || node.getvTarget().getValue().doubleValue() == 0d)
      throw new UnsafeEntityException("vRated or vTarget is 0", node);
    return true;
  }

  /**
   * Validates a connector if: <br>
   * - it is not null <br>
   * - both of its nodes are not null
   */
  public static boolean checkConnector(ConnectorInput connector) {
    if (connector == null) return false;
    if (connector.getNodeA() == null || connector.getNodeB() == null)
      throw new InvalidEntityException("at least one node of this connector is null ", connector);
    return true;
  }

  /**
   * Checks, if the nodes of the {@link ConnectorInput} are in the collection of provided, already
   * determined nodes
   *
   * @param connector Connector to examine
   * @param nodes Permissible, already known nodes
   * @return true, if everything is fine
   */
  private static boolean checkNodeAvailability(
      ConnectorInput connector, Collection<NodeInput> nodes) {
    if (!nodes.contains(connector.getNodeA()) || !nodes.contains(connector.getNodeB()))
      throw getMissingNodeException(connector);
    return true;
  }

  /**
   * Checks, if the node of the {@link SystemParticipantInput} are in the collection of provided,
   * already determined nodes
   *
   * @param participant Connector to examine
   * @param nodes Permissible, already known nodes
   * @return true, if everything is fine
   */
  private static boolean checkNodeAvailability(
      SystemParticipantInput participant, Collection<NodeInput> nodes) {
    if (!nodes.contains(participant.getNode())) throw getMissingNodeException(participant);
    return true;
  }

  /**
   * Validates a line if: <br>
   * - it is not null <br>
   * - line type is not null <br>
   * - {@link ValidationUtils#checkLineType(LineTypeInput)} and {@link
   * ValidationUtils#checkConnector(ConnectorInput)} confirm a valid type and valid connector
   * properties
   */
  public static boolean checkLine(LineInput line) {
    if (line == null) return false;
    if (line.getType() == null) throw new InvalidEntityException("line type is null", line);
    checkConnector(line);
    if (line.getNodeA().getSubnet() != line.getNodeB().getSubnet())
      throw new InvalidEntityException("the line {} connects to different subnets", line);
    if (line.getNodeA().getVoltLvl() != line.getNodeB().getVoltLvl())
      throw new InvalidEntityException("the line {} connects to different voltage levels", line);
    return checkLineType(line.getType());
  }

  /**
   * Validates a line type if: <br>
   * - it is not null <br>
   * - none of its values are null or 0 <br>
   */
  public static boolean checkLineType(LineTypeInput lineType) {
    if (lineType == null) return false;
    if (lineType.getvRated() == null
        || lineType.getiMax() == null
        || lineType.getB() == null
        || lineType.getX() == null
        || lineType.getR() == null
        || lineType.getG() == null)
      throw new InvalidEntityException("at least one value of line type is null", lineType);

    if (lineType.getvRated().getValue().doubleValue() == 0d
        || lineType.getiMax().getValue().doubleValue() == 0d
        || lineType.getB().getValue().doubleValue() == 0d
        || lineType.getX().getValue().doubleValue() == 0d
        || lineType.getR().getValue().doubleValue() == 0d
        || lineType.getG().getValue().doubleValue() == 0d)
      throw new UnsafeEntityException("at least one value of line type is 0", lineType);
    return true;
  }

  /**
   * Validates a transformer if: <br>
   * - it is not null <br>
   * - transformer type is not null <br>
   * - {@link ValidationUtils#checkTransformer2WType(Transformer2WTypeInput)} and {@link
   * ValidationUtils#checkConnector(ConnectorInput)} confirm a valid type and valid connector
   * properties
   */
  public static boolean checkTransformer2W(Transformer2WInput trafo) {
    if (trafo == null) return false;
    if (trafo.getType() == null) throw new InvalidEntityException("trafo2w type is null", trafo);
    return checkTransformer2WType(trafo.getType()) && checkConnector(trafo);
  }

  /**
   * Validates a transformer type if: <br>
   * - it is not null <br>
   * - none of its values are null or 0 <br>
   */
  public static boolean checkTransformer2WType(Transformer2WTypeInput trafoType) {
    if (trafoType == null) return false;
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

    if ((trafoType.getsRated().getValue().doubleValue() == 0d)
        || (trafoType.getvRatedA().getValue().doubleValue() == 0d)
        || (trafoType.getvRatedB().getValue().doubleValue() == 0d)
        || (trafoType.getxSc().getValue().doubleValue() == 0d)
        || (trafoType.getdV().getValue().doubleValue() == 0d))
      throw new UnsafeEntityException("at least one value of trafo2w type is 0", trafoType);
    return true;
  }

  /**
   * Validates a transformer if: <br>
   * - it is not null <br>
   * - transformer type is not null <br>
   * - {@link ValidationUtils#checkTransformer3WType(Transformer3WTypeInput)} and {@link
   * ValidationUtils#checkConnector(ConnectorInput)} confirm a valid type and valid connector
   * properties
   */
  public static boolean checkTransformer3W(Transformer3WInput trafo) {
    if (trafo == null) return false;
    if (trafo.getNodeC() == null)
      throw new InvalidEntityException("at least one node of this connector is null", trafo);
    if (trafo.getType() == null) throw new InvalidEntityException("trafo3w type is null", trafo);
    return checkTransformer3WType(trafo.getType()) && checkConnector(trafo);
  }

  /**
   * Validates a transformer type if: <br>
   * - it is not null <br>
   * - none of its values are null or 0 <br>
   */
  public static boolean checkTransformer3WType(Transformer3WTypeInput trafoType) {
    if (trafoType == null) return false;
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

    if ((trafoType.getsRatedA().getValue().doubleValue() == 0d)
        || (trafoType.getsRatedB().getValue().doubleValue() == 0d)
        || (trafoType.getsRatedC().getValue().doubleValue() == 0d)
        || (trafoType.getvRatedA().getValue().doubleValue() == 0d)
        || (trafoType.getvRatedB().getValue().doubleValue() == 0d)
        || (trafoType.getvRatedC().getValue().doubleValue() == 0d)
        || (trafoType.getxScA().getValue().doubleValue() == 0d)
        || (trafoType.getxScB().getValue().doubleValue() == 0d)
        || (trafoType.getxScC().getValue().doubleValue() == 0d)
        || (trafoType.getdV().getValue().doubleValue() == 0d))
      throw new UnsafeEntityException("at least one value of trafo3w type is 0", trafoType);
    return true;
  }

  /**
   * Validates a measurement unit if: <br>
   * - it is not null <br>
   * - its node is not nul
   */
  public static boolean checkMeasurementUnit(MeasurementUnitInput measurementUnit) {
    if (measurementUnit == null) return false;
    if (measurementUnit.getNode() == null)
      throw new InvalidEntityException("node is null", measurementUnit);
    return true;
  }

  /**
   * Validates a measurement unit if: <br>
   * - it is not null <br>
   * - its node is not nul
   */
  public static boolean checkSwitch(SwitchInput switchInput) {
    if (switchInput == null) return false;
    if (switchInput.getNodeA() == null)
      throw new InvalidEntityException("node A is null", switchInput);
    if (switchInput.getNodeB() == null)
      throw new InvalidEntityException("node B is null", switchInput);
    return true;
  }

  /**
   * Checks the validity of given {@link RawGridElements}. The single elements are checked as well
   * as the fact, that none of the assets is connected to a node, that is not in the set of nodes.
   *
   * @param rawGridElements Raw grid elements
   * @return true, if no failure has been found
   * @throws InvalidGridException If something is wrong
   */
  public static boolean checkRawGridElements(RawGridElements rawGridElements) {
    if (rawGridElements == null) return false;

    /* Checking nodes */
    Set<NodeInput> nodes = rawGridElements.getNodes();
    boolean anyNullNode = nodes.stream().map(ValidationUtils::checkNode).anyMatch(cond -> !cond);
    if (anyNullNode)
      throw new InvalidGridException("The list of nodes contains at least one NULL element.");

    /* Checking lines */
    boolean anyNullLine =
        rawGridElements.getLines().stream()
            .map(line -> checkLine(line) && checkNodeAvailability(line, nodes))
            .anyMatch(cond -> !cond);
    if (anyNullLine)
      throw new InvalidGridException("The list of lines contains at least one NULL element.");

    /* Checking two winding transformers */
    boolean anyNullTransformer2w =
        rawGridElements.getTransformer2Ws().stream()
            .map(
                transformer ->
                    checkTransformer2W(transformer) && checkNodeAvailability(transformer, nodes))
            .anyMatch(cond -> !cond);
    if (anyNullTransformer2w)
      throw new InvalidGridException(
          "The list of two winding transformers contains at least one NULL element.");

    /* Checking three winding transformers */
    boolean anyNullTransformer3w =
        rawGridElements.getTransformer3Ws().stream()
            .map(
                transformer -> {
                  if (!nodes.contains(transformer.getNodeA())
                      || !nodes.contains(transformer.getNodeB())
                      || !nodes.contains(transformer.getNodeC()))
                    throw getMissingNodeException(transformer);
                  return checkTransformer3W(transformer);
                })
            .anyMatch(cond -> !cond);
    if (anyNullTransformer3w)
      throw new InvalidGridException(
          "The list of three winding transformers contains at least one NULL element.");

    /* Checking switches */
    boolean anyNullSwitch =
        rawGridElements.getSwitches().stream()
            .map(switcher -> checkSwitch(switcher) && checkNodeAvailability(switcher, nodes))
            .anyMatch(cond -> !cond);
    if (anyNullSwitch)
      throw new InvalidGridException("The list of switches contains at least one NULL element.");

    /* Checking measurement units */
    boolean anyNullMeasurement =
        rawGridElements.getMeasurementUnits().stream()
            .map(
                measurement -> {
                  if (!nodes.contains(measurement.getNode()))
                    throw getMissingNodeException(measurement);
                  return checkMeasurementUnit(measurement);
                })
            .anyMatch(cond -> !cond);
    if (anyNullMeasurement)
      throw new InvalidGridException(
          "The list of measurement units contains at least one NULL element.");

    return true;
  }

  /**
   * Checks the validity of each and every system participant. Moreover, it checks, if the systems
   * are connected to an node that is not in the provided set
   *
   * @param systemParticipants The system participants
   * @param nodes Set of already known nodes
   * @return true
   */
  public static boolean checkSystemParticipants(
      SystemParticipants systemParticipants, Set<NodeInput> nodes) {
    if (systemParticipants == null) return false;

    systemParticipants.getBmPlants().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getChpPlants().forEach(entity -> checkNodeAvailability(entity, nodes));

    /* TODO: Electric vehicle charging systems are currently only dummy implementation */

    systemParticipants.getFixedFeedIns().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getHeatPumps().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getLoads().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getPvPlants().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getStorages().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getWecPlants().forEach(entity -> checkNodeAvailability(entity, nodes));

    return true;
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

  public static boolean checkGraphicElements(
      GraphicElements graphicElements, Set<NodeInput> nodes, Set<LineInput> lines) {
    if (graphicElements == null) return false;

    graphicElements
        .getNodeGraphics()
        .forEach(
            graphic -> {
              if (!nodes.contains(graphic.getNode()))
                throw new InvalidGridException(
                    "The node graphic "
                        + graphic
                        + " refers to a node, that is not among the provided ones.");
            });

    graphicElements
        .getLineGraphics()
        .forEach(
            graphic -> {
              if (!lines.contains(graphic.getLine()))
                throw new InvalidGridException(
                    "The line graphic "
                        + graphic
                        + " refers to a line, that is not among the provided ones.");
            });

    return true;
  }
}
