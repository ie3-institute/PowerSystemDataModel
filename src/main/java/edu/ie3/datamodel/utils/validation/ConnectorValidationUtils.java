/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import javax.measure.Quantity;

public class ConnectorValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private ConnectorValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Validates a connector if: <br>
   * - it is not null <br>
   * - both of its nodes are not null <br>
   * - its operator is not null
   *
   * @param connector Connector to validate
   */
  public static void check(ConnectorInput connector) {
    //Check if null
    checkNonNull(connector, "a connector");
    //Check if nodes of connector are null
    if (connector.getNodeA() == null || connector.getNodeB() == null)
      throw new InvalidEntityException("at least one node of this connector is null ", connector);
    //Check if operator is null
    if (connector.getOperator() == null)
      throw new InvalidEntityException("no operator assigned", connector);
    //TODO: NSteffan - necessary to check operator ("at least dummy")? operationTime? parallelDevices?

    //Further checks for subclasses
    if (LineInput.class.isAssignableFrom(connector.getClass()))
      checkLine((LineInput) connector);
    if (Transformer2WInput.class.isAssignableFrom(connector.getClass()))
      checkTransformer2W((Transformer2WInput) connector);
    if (Transformer3WInput.class.isAssignableFrom(connector.getClass()))
      checkTransformer3W((Transformer3WInput) connector);
    if (SwitchInput.class.isAssignableFrom(connector.getClass()))
      checkSwitch((SwitchInput) connector);
  }

  /**
   * Validates a line if: <br>
   * - line type is not null <br>
   * - {@link ConnectorValidationUtils#checkLineType(LineTypeInput)} confirms a valid type
   * properties <br>
   * - line length is not null and positive value <br>
   * - geoPosition is not null <br>
   *
   * @param line Line to validate
   */
  public static void checkLine(LineInput line) {
    //check LineType
    checkLineType(line.getType());
    //Check if line connects same subnet
    if (line.getNodeA().getSubnet() != line.getNodeB().getSubnet())
      throw new InvalidEntityException("the line {} connects to different subnets", line);
    //Check if line connects same voltage level
    if (line.getNodeA().getVoltLvl() != line.getNodeB().getVoltLvl())
      throw new InvalidEntityException("the line {} connects to different voltage levels", line);
    //Check if line length is not null
    if (line.getLength() == null)
      throw new InvalidEntityException("line length of line {} is null", line);
    //Check if line length is positive value
    if (line.getLength().getValue().doubleValue() <= 0d)
      throw new InvalidEntityException("the line {} has a negative length", line);
          //TODO: NSteffan - alternatively work with detectZeroOrNegativeQuantities?
          // detectZeroOrNegativeQuantities(new Quantity<?>[] {line.getLength()}, line);
          // bei sowas auch checkNonNull nötig?
    //Check if geoPosition is null
    if (line.getGeoPosition() == null)
      throw new InvalidEntityException("found no geoPosition for line {}", line);
  }

  /**
   * Validates a line type if: <br>
   * - it is not null <br>
   * - none of its values are null or <= 0 or < 0 <br>
   *
   * @param lineType Line type to validate
   */
  public static void checkLineType(LineTypeInput lineType) {
    //Check if null
    checkNonNull(lineType, "a line type");
    //Check if any values are null
    if ((lineType.getB() == null)
        || (lineType.getG() == null)
        || (lineType.getvRated() == null)
        || (lineType.getiMax() == null)
        || (lineType.getX() == null)
        || (lineType.getR() == null))
      throw new InvalidEntityException("at least one value of lineType is null", lineType);
    //Check for negative quantities
    detectNegativeQuantities(
            new Quantity<?>[] {lineType.getB(), lineType.getG()}, lineType);
    //Check for zero or negative quantities
    detectZeroOrNegativeQuantities(
            new Quantity<?>[] {
                    lineType.getvRated(), lineType.getiMax(), lineType.getX(), lineType.getR()
            },
            lineType);
  }

  /**
   * Validates a transformer2w if: <br>
   * - transformer type is not null <br>
   * - {@link ConnectorValidationUtils#checkTransformer2WType(Transformer2WTypeInput)} confirms a
   * valid type properties <br>
   * - tap position is within bounds <br>
   * - connects different subnets <br>
   * - connects different voltage levels <br>
   * @param trafo Transformer to validate
   */
  public static void checkTransformer2W(Transformer2WInput trafo) {
    //Check Transformer2WType
    checkTransformer2WType(trafo.getType());
    //Check if tap position is within bounds
    if (trafo.getTapPos() < trafo.getType().getTapMin() || trafo.getTapPos() > trafo.getType().getTapMax())
      throw new InvalidEntityException("tap position of trafo {} is outside of bounds", trafo);
    //TODO: NSteffan - Check if (trafo.isAutoTap() is null) -> necessary? how is syntax?
    // necessary to check tap position (int) is "not null"?
    //Check if trafo connects different voltage levels
    if (trafo.getNodeA().getVoltLvl() == trafo.getNodeB().getVoltLvl())
      throw new InvalidEntityException("trafo {} connects nodes with the same voltage level", trafo);
    //Check if trafo connects different subnets
    if (trafo.getNodeA().getSubnet() == trafo.getNodeB().getSubnet())
      throw new InvalidEntityException("trafo {} connects nodes in the same subnet", trafo);
  }

  /**
   * Validates a transformer2w type if: <br>
   * - it is not null <br>
   * - none of its values are null or <= 0 or < 0 <br>
   *
   * @param trafoType Transformer type to validate
   */
  public static void checkTransformer2WType(Transformer2WTypeInput trafoType) {
    checkNonNull(trafoType, "a two winding transformer type");
    //Check if any value are null
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
          trafoType.getrSc(),
          trafoType.getxSc(),
          trafoType.getdV()
        },
        trafoType);
  }

  /**
   * Validates a transformer3w if: <br>
   * - transformer type is not null <br>
   * - {@link ConnectorValidationUtils#checkTransformer3WType(Transformer3WTypeInput)} confirm a
   * valid type <br>
   * - node C is not null <br>
   * - tap position is within bounds <br>
   * - connects different subnets <br>
   * - connects different voltage levels <br>
   * @param trafo Transformer to validate
   */
  public static void checkTransformer3W(Transformer3WInput trafo) {
    //Check if node C is null
    if (trafo.getNodeC() == null)
      throw new InvalidEntityException("at least one node of this connector is null", trafo);
    //Check Transformer3WType
    checkTransformer3WType(trafo.getType());
    //Check if tap position is within bounds
    if (trafo.getTapPos() < trafo.getType().getTapMin() || trafo.getTapPos() > trafo.getType().getTapMax())
      throw new InvalidEntityException("tap position of trafo {} is outside of bounds", trafo);
    //Check if trafo connects different voltage levels
    if (trafo.getNodeA().getVoltLvl() == trafo.getNodeB().getVoltLvl()
        || trafo.getNodeA().getVoltLvl() == trafo.getNodeC().getVoltLvl()
        || trafo.getNodeB().getVoltLvl() == trafo.getNodeC().getVoltLvl())
      throw new InvalidEntityException("trafo {} connects nodes with the same voltage level", trafo);
    //Check if trafo connects different subnets
    if (trafo.getNodeA().getSubnet() == trafo.getNodeB().getSubnet()
        || trafo.getNodeA().getSubnet() == trafo.getNodeC().getSubnet()
        || trafo.getNodeB().getSubnet() == trafo.getNodeC().getSubnet())
      throw new InvalidEntityException("trafo {} connects nodes in the same subnet", trafo);
    //TODO NSteffan: check with Chris if those checks is correct (for 2W and 3W)
  }

  /**
   * Validates a transformer3w type if: <br>
   * - it is not null <br>
   * - none of its values are null or <= 0 or < 0 <br>
   *
   * @param trafoType Transformer type to validate
   */
  public static void checkTransformer3WType(Transformer3WTypeInput trafoType) {
    checkNonNull(trafoType, "a three winding transformer type");
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
          trafoType.getsRatedA(), trafoType.getsRatedB(), trafoType.getsRatedC(),
          trafoType.getvRatedA(), trafoType.getvRatedB(), trafoType.getvRatedC(),
          trafoType.getrScA(), trafoType.getrScB(), trafoType.getrScC(),
          trafoType.getxScA(), trafoType.getxScB(), trafoType.getxScC(),
          trafoType.getdV()
        },
        trafoType);
  }

  /**
   * Validates a switch if: <br>
   * - connects same voltage level
   *
   * @param switchInput Switch to validate
   */
  public static void checkSwitch(SwitchInput switchInput) {
    //Check if switch connects same voltage level
    if (switchInput.getNodeA().getVoltLvl() != switchInput.getNodeB().getVoltLvl())
      throw new InvalidEntityException("the switch {} connects to different voltage levels", switchInput);
    /* Remark: Connecting two different "subnets" is fine, because as of our definition regarding a switchgear in
     * "upstream" direction of a transformer, all the nodes, that hare within the switch chain, belong to the lower
     * grid, whilst the "real" upper node is within the upper grid */
  }
}
