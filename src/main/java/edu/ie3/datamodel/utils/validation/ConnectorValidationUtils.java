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
  /**
   * Validates a connector if: <br>
   * - it is not null <br>
   * - both of its nodes are not null
   *
   * @param connector Connector to validate
   */
  public static void check(ConnectorInput connector) {
    checkNonNull(connector, "a connector");
    if (connector.getNodeA() == null || connector.getNodeB() == null)
      throw new InvalidEntityException("at least one node of this connector is null ", connector);

    if (LineInput.class.isAssignableFrom(connector.getClass())) checkLine((LineInput) connector);
    if (Transformer2WInput.class.isAssignableFrom(connector.getClass()))
      checkTransformer2W((Transformer2WInput) connector);
    if (Transformer3WInput.class.isAssignableFrom(connector.getClass()))
      checkTransformer3W((Transformer3WInput) connector);
    if (SwitchInput.class.isAssignableFrom(connector.getClass()))
      checkSwitch((SwitchInput) connector);
  }

  /**
   * Validates a line if: <br>
   * - it is not null <br>
   * - line type is not null <br>
   * - {@link ConnectorValidationUtils#checkLineType(LineTypeInput)} confirms a valid type
   * properties
   *
   * @param line Line to validate
   */
  public static void checkLine(LineInput line) {
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
   *
   * @param lineType Line type to validate
   */
  public static void checkLineType(LineTypeInput lineType) {
    checkNonNull(lineType, "a line type");

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
   * - {@link ConnectorValidationUtils#checkTransformer2WType(Transformer2WTypeInput)} confirms a
   * valid type properties
   *
   * @param trafo Transformer to validate
   */
  public static void checkTransformer2W(Transformer2WInput trafo) {
    checkTransformer2WType(trafo.getType());
  }

  /**
   * Validates a transformer type if: <br>
   * - it is not null <br>
   * - none of its values are null or 0 <br>
   *
   * @param trafoType Transformer type to validate
   */
  public static void checkTransformer2WType(Transformer2WTypeInput trafoType) {
    checkNonNull(trafoType, "a two winding transformer type");
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
   * - {@link ConnectorValidationUtils#checkTransformer3WType(Transformer3WTypeInput)} confirm a
   * valid type
   *
   * @param trafo Transformer to validate
   */
  public static void checkTransformer3W(Transformer3WInput trafo) {
    if (trafo.getNodeC() == null)
      throw new InvalidEntityException("at least one node of this connector is null", trafo);
    checkTransformer3WType(trafo.getType());
  }

  /**
   * Validates a transformer type if: <br>
   * - it is not null <br>
   * - none of its values are null or 0 <br>
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
          trafoType.getxScA(), trafoType.getxScB(), trafoType.getxScC(),
          trafoType.getdV()
        },
        trafoType);
  }

  /**
   * Validates a measurement unit if: <br>
   * - it is not null <br>
   * - its node is not nul
   *
   * @param switchInput Switch to validate
   */
  public static void checkSwitch(SwitchInput switchInput) {
    if (switchInput.getNodeA().getVoltLvl() != switchInput.getNodeB().getVoltLvl())
      throw new InvalidEntityException(
          "the switch {} connects to different voltage levels", switchInput);
    /* Remark: Connecting two different "subnets" is fine, because as of our definition regarding a switchgear in
     * "upstream" direction of a transformer, all the nodes, that hare within the switch chain, belong to the lower
     * grid, whilst the "real" upper node is within the upper grid */
  }
}
