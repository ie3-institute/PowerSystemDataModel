/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.utils.GridAndGeoUtils;
import javax.measure.Quantity;

public class ConnectorValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private ConnectorValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Validates a connector if: <br>
   * - it is not null <br>
   * A "distribution" method, that forwards the check request to specific implementations to fulfill
   * the checking task, based on the class of the given object. If an unknown class is handed in, a
   * {@link ValidationException} is thrown.
   *
   * @param connector Connector to validate
   */
  public static void check(ConnectorInput connector) {
    // Check if null
    checkNonNull(connector, "a connector");

    // Further checks for subclasses
    if (LineInput.class.isAssignableFrom(connector.getClass())) checkLine((LineInput) connector);
    else if (Transformer2WInput.class.isAssignableFrom(connector.getClass()))
      checkTransformer2W((Transformer2WInput) connector);
    else if (Transformer3WInput.class.isAssignableFrom(connector.getClass()))
      checkTransformer3W((Transformer3WInput) connector);
    else if (SwitchInput.class.isAssignableFrom(connector.getClass()))
      checkSwitch((SwitchInput) connector);
    else
      throw new ValidationException(
          "Cannot validate object of class '"
              + connector.getClass().getSimpleName()
              + "', as no routine is implemented.");
  }

  /**
   * Validates a line if: <br>
   * - {@link ConnectorValidationUtils#checkLineType(LineTypeInput)} confirms valid type properties
   * <br>
   * - it does not connect the same node <br>
   * - it connects nodes in the same subnet <br>
   * - it connects nodes in the same voltage level <br>
   * - its line length has a positive value <br>
   * - its length equals the sum of calculated distances between points of LineString <br>
   * - its characteristic for overhead line monitoring is not null <br>
   * - its coordinates of start and end point equal coordinates of nodes
   *
   * @param line Line to validate
   */
  public static void checkLine(LineInput line) {
    // check LineType
    checkLineType(line.getType());
    // Check if line connects same node
    if (line.getNodeA() == line.getNodeB())
      throw new InvalidEntityException("Line connects the same node", line);
    // Check if line connects same subnet
    if (line.getNodeA().getSubnet() != line.getNodeB().getSubnet())
      throw new InvalidEntityException("Line connects different subnets", line);
    // Check if line connects same voltage level
    if (!line.getNodeA().getVoltLvl().equals(line.getNodeB().getVoltLvl()))
      throw new InvalidEntityException("Line connects different voltage levels", line);
    // Check if line length is positive value
    if (line.getLength().getValue().doubleValue() <= 0d)
      throw new InvalidEntityException("Line has a zero or negative length", line);
    detectZeroOrNegativeQuantities(new Quantity<?>[] {line.getLength()}, line);
    // Coordinates of start and end point of line equal coordinates of nodes
    if (!(line.getGeoPosition()
            .getStartPoint()
            .isWithinDistance(line.getNodeA().getGeoPosition(), 0.000001)
        || line.getGeoPosition()
            .getEndPoint()
            .isWithinDistance(line.getNodeA().getGeoPosition(), 0.000001)))
      throw new InvalidEntityException(
          "Coordinates of start and end point do not match coordinates of connected nodes", line);
    if (!(line.getGeoPosition()
            .getStartPoint()
            .isWithinDistance(line.getNodeB().getGeoPosition(), 0.000001)
        || line.getGeoPosition()
            .getEndPoint()
            .isWithinDistance(line.getNodeB().getGeoPosition(), 0.000001)))
      throw new InvalidEntityException(
          "Coordinates of start and end point do not match coordinates of connected nodes", line);
    // Check if lineLength equals sum of calculated distances between points of LineString
    // (only if not geo positions ob both nodes are dummy values)
    if (line.getNodeA().getGeoPosition() != NodeInput.DEFAULT_GEO_POSITION
        || line.getNodeB().getGeoPosition() != NodeInput.DEFAULT_GEO_POSITION) {
      if (!line.getLength()
          .isEquivalentTo(GridAndGeoUtils.TotalLengthOfLineString(line.getGeoPosition())))
        throw new InvalidEntityException(
            "Line length does not equal calculated distances between points building the line",
            line);
    }
    // Check if olmCharacteristics is null
    if (line.getOlmCharacteristic() == null)
      throw new InvalidEntityException(
          "Characteristic for overhead line monitoring of the line is null", line);
  }

  /**
   * Validates a line type if: <br>
   * - it is not null <br>
   * - B >= 0 (Phase-to-ground susceptance per length) <br>
   * - G >= 0 (Phase-to-ground conductance per length) <br>
   * - R > 0 (Phase resistance per length) <br>
   * - X > 0 (Phase reactance per length) <br>
   * - iMax > 0 (Maximum permissible current) <br>
   * - vRated > 0 (Rated voltage)
   *
   * @param lineType Line type to validate
   */
  public static void checkLineType(LineTypeInput lineType) {
    // Check if null
    checkNonNull(lineType, "a line type");
    // Check for negative quantities
    detectNegativeQuantities(new Quantity<?>[] {lineType.getB(), lineType.getG()}, lineType);
    // Check for zero or negative quantities
    detectZeroOrNegativeQuantities(
        new Quantity<?>[] {
          lineType.getvRated(), lineType.getiMax(), lineType.getX(), lineType.getR()
        },
        lineType);
  }

  /**
   * Validates a transformer2W if: <br>
   * - {@link ConnectorValidationUtils#checkTransformer2WType(Transformer2WTypeInput)} confirms a
   * valid type properties <br>
   * - its tap position is within bounds <br>
   * - it connects different subnets <br>
   * - it connects different voltage levels <br>
   * - its rated voltages match the voltages at the nodes
   *
   * @param transformer2W Transformer2W to validate
   */
  public static void checkTransformer2W(Transformer2WInput transformer2W) {
    // Check Transformer2WType
    checkTransformer2WType(transformer2W.getType());
    // Check if tap position is within bounds
    if (transformer2W.getTapPos() < transformer2W.getType().getTapMin()
        || transformer2W.getTapPos() > transformer2W.getType().getTapMax())
      throw new InvalidEntityException(
          "Tap position of transformer is outside of bounds", transformer2W);
    // Check if transformer connects different voltage levels
    if (transformer2W.getNodeA().getVoltLvl() == transformer2W.getNodeB().getVoltLvl())
      throw new InvalidEntityException(
          "Transformer connects nodes of the same voltage level", transformer2W);
    // Check if transformer connects different subnets
    if (transformer2W.getNodeA().getSubnet() == transformer2W.getNodeB().getSubnet())
      throw new InvalidEntityException(
          "Transformer connects nodes in the same subnet", transformer2W);
    // Check if vRated of transformer match voltLvl of nodes
    if (!transformer2W
            .getType()
            .getvRatedA()
            .equals(transformer2W.getNodeA().getVoltLvl().getNominalVoltage())
        || !transformer2W
            .getType()
            .getvRatedB()
            .equals(transformer2W.getNodeB().getVoltLvl().getNominalVoltage()))
      throw new InvalidEntityException(
          "Rated voltages of transformer do not equal voltage levels at the nodes", transformer2W);
  }

  /**
   * Validates a transformer2W type if: <br>
   * - it is not null <br>
   * - rSc > 0 (short circuit resistance) <br>
   * - xSc > 0 (short circuit impedance) <br>
   * - gM >= 0 (no load conductance) <br>
   * - bM >= 0 (no load susceptance) <br>
   * - sRated > 0 (rated apparent power) <br>
   * - vRatedA > 0 (rated voltage at higher voltage terminal) <br>
   * - vRatedB > 0 (rated voltage at lower voltage terminal) <br>
   * - dV > 0% and dV <= 100% (voltage magnitude increase per tap position <br>
   * - dPhi >= 0 (voltage angle increase per tap position) <br>
   * - neutral tap position is between min and max tap position <br>
   * - minimum tap position is smaller than maximum tap position
   *
   * @param transformer2WType Transformer2W type to validate
   */
  public static void checkTransformer2WType(Transformer2WTypeInput transformer2WType) {
    // check if null
    checkNonNull(transformer2WType, "a two winding transformer type");
    // Check for negative quantities
    detectNegativeQuantities(
        new Quantity<?>[] {
          transformer2WType.getgM(), transformer2WType.getbM(), transformer2WType.getdPhi()
        },
        transformer2WType);
    // Check for zero or negative quantities
    detectZeroOrNegativeQuantities(
        new Quantity<?>[] {
          transformer2WType.getsRated(),
          transformer2WType.getvRatedA(),
          transformer2WType.getvRatedB(),
          transformer2WType.getrSc(),
          transformer2WType.getxSc(),
        },
        transformer2WType);
    // Check if voltage magnitude increase per tap position is between 0% and 100%
    if (transformer2WType.getdV().getValue().doubleValue() <= 0d
        || transformer2WType.getdV().getValue().doubleValue() > 100d)
      throw new InvalidEntityException(
          "Voltage magnitude increase per tap position must be between 0% and 100%",
          transformer2WType);
    // Check minimum tap position is lower than maximum tap position
    if (transformer2WType.getTapMax() < transformer2WType.getTapMin())
      throw new InvalidEntityException(
          "Minimum tap position must be lower than maximum tap position", transformer2WType);
    // Check if neutral tap position lies between minimum and maximum tap position
    if (transformer2WType.getTapNeutr() < transformer2WType.getTapMin()
        || transformer2WType.getTapNeutr() > transformer2WType.getTapMax())
      throw new InvalidEntityException(
          "Neutral tap position must be between minimum and maximum tap position",
          transformer2WType);
  }

  /**
   * Validates a transformer3W if: <br>
   * - {@link ConnectorValidationUtils#checkTransformer3WType(Transformer3WTypeInput)} confirm a
   * valid type <br>
   * - its tap position is within bounds <br>
   * - it connects different subnets <br>
   * - it connects different voltage levels <br>
   * - its rated voltages match the voltages at the nodes
   *
   * @param transformer3W Transformer3W to validate
   */
  public static void checkTransformer3W(Transformer3WInput transformer3W) {
    // Check Transformer3WType
    checkTransformer3WType(transformer3W.getType());
    // Check if tap position is within bounds
    if (transformer3W.getTapPos() < transformer3W.getType().getTapMin()
        || transformer3W.getTapPos() > transformer3W.getType().getTapMax())
      throw new InvalidEntityException(
          "Tap position of transformer is outside of bounds", transformer3W);
    // Check if transformer connects different voltage levels
    if (transformer3W.getNodeA().getVoltLvl() == transformer3W.getNodeB().getVoltLvl()
        || transformer3W.getNodeA().getVoltLvl() == transformer3W.getNodeC().getVoltLvl()
        || transformer3W.getNodeB().getVoltLvl() == transformer3W.getNodeC().getVoltLvl())
      throw new InvalidEntityException(
          "Transformer connects nodes of the same voltage level", transformer3W);
    // Check if transformer connects different subnets
    if (transformer3W.getNodeA().getSubnet() == transformer3W.getNodeB().getSubnet()
        || transformer3W.getNodeA().getSubnet() == transformer3W.getNodeC().getSubnet()
        || transformer3W.getNodeB().getSubnet() == transformer3W.getNodeC().getSubnet())
      throw new InvalidEntityException(
          "Transformer connects nodes in the same subnet", transformer3W);
    // Check if vRated of transformer match voltLvl of nodes
    if (!transformer3W
            .getType()
            .getvRatedA()
            .equals(transformer3W.getNodeA().getVoltLvl().getNominalVoltage())
        || !transformer3W
            .getType()
            .getvRatedB()
            .equals(transformer3W.getNodeB().getVoltLvl().getNominalVoltage())
        || !transformer3W
            .getType()
            .getvRatedC()
            .equals(transformer3W.getNodeC().getVoltLvl().getNominalVoltage()))
      throw new InvalidEntityException(
          "Rated voltages of transformer do not equal voltage levels at the nodes", transformer3W);
  }

  /**
   * Validates a transformer3W type if: <br>
   * - it is not null <br>
   * - rScA, rScB, rScC > 0 (short circuit resistance in branches A,B,C) <br>
   * - xScA, xScB, xScC > 0 (short circuit impedance in branches A,B,C) <br>
   * - gM >= 0 (no load conductance) <br>
   * - bM >= 0 (no load susceptance) <br>
   * - sRatedA, sRatedB, sRatedC > 0 (rated apparent power in branches A,B,C) <br>
   * - vRatedA, vRatedB, vRatedC > 0 (rated voltage at higher node A,B,C) <br>
   * - dV > 0% and dV <= 100% (voltage magnitude increase per tap position <br>
   * - dPhi >= 0 (voltage angle increase per tap position) <br>
   * - neutral tap position is between min and max tap position <br>
   * - minimum tap position is smaller than maximum tap position <br>
   *
   * @param transformer3WType Transformer type to validate
   */
  public static void checkTransformer3WType(Transformer3WTypeInput transformer3WType) {
    // check if null
    checkNonNull(transformer3WType, "a three winding transformer type");
    // Check for negative quantities
    detectNegativeQuantities(
        new Quantity<?>[] {
          transformer3WType.getgM(), transformer3WType.getbM(), transformer3WType.getdPhi()
        },
        transformer3WType);
    // Check for zero or negative quantities
    detectZeroOrNegativeQuantities(
        new Quantity<?>[] {
          transformer3WType.getsRatedA(), transformer3WType.getsRatedB(),
              transformer3WType.getsRatedC(),
          transformer3WType.getvRatedA(), transformer3WType.getvRatedB(),
              transformer3WType.getvRatedC(),
          transformer3WType.getrScA(), transformer3WType.getrScB(), transformer3WType.getrScC(),
          transformer3WType.getxScA(), transformer3WType.getxScB(), transformer3WType.getxScC()
        },
        transformer3WType);
    // Check if voltage magnitude increase per tap position is between 0% and 100%
    if (transformer3WType.getdV().getValue().doubleValue() <= 0d
        || transformer3WType.getdV().getValue().doubleValue() > 100d)
      throw new InvalidEntityException(
          "Voltage magnitude increase per tap position must be between 0% and 100%",
          transformer3WType);
    // Check if minimum tap position is lower than maximum tap position
    if (transformer3WType.getTapMax() < transformer3WType.getTapMin())
      throw new InvalidEntityException(
          "Minimum tap position must be lower than maximum tap position", transformer3WType);
    // Check if neutral tap position lies between minimum and maximum tap position
    if (transformer3WType.getTapNeutr() < transformer3WType.getTapMin()
        || transformer3WType.getTapNeutr() > transformer3WType.getTapMax())
      throw new InvalidEntityException(
          "Neutral tap position must be between minimum and maximum tap position",
          transformer3WType);
  }

  /**
   * Validates a switch if: <br>
   * - its connected nodes are in the same voltage level
   *
   * @param switchInput Switch to validate
   */
  public static void checkSwitch(SwitchInput switchInput) {
    // Check if switch connects nodes of same voltage level
    if (!switchInput.getNodeA().getVoltLvl().equals(switchInput.getNodeB().getVoltLvl()))
      throw new InvalidEntityException("Switch connects two different voltage levels", switchInput);
    /* Remark: Connecting two different "subnets" is fine, because as of our definition regarding a switchgear in
     * "upstream" direction of a transformer, all the nodes, that hare within the switch chain, belong to the lower
     * grid, whilst the "real" upper node is within the upper grid */
  }
}
