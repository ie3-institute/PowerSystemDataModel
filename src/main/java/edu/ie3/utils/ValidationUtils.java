/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.utils;

import edu.ie3.exceptions.InvalidEntityException;
import edu.ie3.exceptions.UnsafeEntityException;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.MeasurementUnitInput;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.connector.ConnectorInput;
import edu.ie3.models.input.connector.LineInput;
import edu.ie3.models.input.connector.Transformer2WInput;
import edu.ie3.models.input.connector.Transformer3WInput;
import edu.ie3.models.input.connector.type.LineTypeInput;
import edu.ie3.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.models.input.connector.type.Transformer3WTypeInput;

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
    return checkLineType(line.getType()) && checkConnector(line);
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
        || (trafoType.getrSc().getValue().doubleValue() == 0d)
        || (trafoType.getxSc().getValue().doubleValue() == 0d)
        || (trafoType.getgM().getValue().doubleValue() == 0d)
        || (trafoType.getbM().getValue().doubleValue() == 0d)
        || (trafoType.getdV().getValue().doubleValue() == 0d)
        || (trafoType.getdPhi().getValue().doubleValue() == 0d))
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
    if ((trafoType.getSRatedA() == null)
        || (trafoType.getSRatedB() == null)
        || (trafoType.getSRatedC() == null)
        || (trafoType.getVRatedA() == null)
        || (trafoType.getVRatedB() == null)
        || (trafoType.getVRatedC() == null)
        || (trafoType.getRScA() == null)
        || (trafoType.getRScB() == null)
        || (trafoType.getRScC() == null)
        || (trafoType.getXScA() == null)
        || (trafoType.getXScB() == null)
        || (trafoType.getXScC() == null)
        || (trafoType.getGM() == null)
        || (trafoType.getBM() == null)
        || (trafoType.getDV() == null)
        || (trafoType.getDPhi() == null))
      throw new InvalidEntityException("at least one value of trafo3w type is null", trafoType);

    if ((trafoType.getSRatedA().getValue().doubleValue() == 0d)
        || (trafoType.getSRatedB().getValue().doubleValue() == 0d)
        || (trafoType.getSRatedC().getValue().doubleValue() == 0d)
        || (trafoType.getVRatedA().getValue().doubleValue() == 0d)
        || (trafoType.getVRatedB().getValue().doubleValue() == 0d)
        || (trafoType.getVRatedC().getValue().doubleValue() == 0d)
        || (trafoType.getRScA().getValue().doubleValue() == 0d)
        || (trafoType.getRScB().getValue().doubleValue() == 0d)
        || (trafoType.getRScC().getValue().doubleValue() == 0d)
        || (trafoType.getXScA().getValue().doubleValue() == 0d)
        || (trafoType.getXScB().getValue().doubleValue() == 0d)
        || (trafoType.getXScC().getValue().doubleValue() == 0d)
        || (trafoType.getGM().getValue().doubleValue() == 0d)
        || (trafoType.getBM().getValue().doubleValue() == 0d)
        || (trafoType.getDV().getValue().doubleValue() == 0d)
        || (trafoType.getDPhi().getValue().doubleValue() == 0d))
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
}
