/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.UnsafeEntityException;
import edu.ie3.datamodel.exceptions.VoltageLevelException;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;

public class NodeValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private NodeValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Validates a node if: <br>
   * - it is not null <br>
   * - voltage level is not null and valid <br>
   * - target voltage is larger than zero and smaller than two <br>
   * - subnet number is larger than zero <br>
   * - geoPosition is not null
   *
   * @param node Node to validate
   */
  public static void check(NodeInput node) {
    checkNonNull(node, "a node");
    try {
      checkVoltageLevel(node.getVoltLvl());
    } catch (VoltageLevelException e) {
      throw new InvalidEntityException("Node has invalid voltage level", node);
    }
    if (node.getvTarget().getValue().doubleValue() <= 0d)
      throw new InvalidEntityException("Target voltage (p.u.) is not a positive value", node);
    else if (node.getvTarget().getValue().doubleValue() > 2d)
      throw new UnsafeEntityException("Target voltage (p.u.) might be too high", node);
    if (node.getSubnet() <= 0)
      throw new InvalidEntityException("Subnet can't be zero or negative", node);
    if (node.getGeoPosition() == null)
      throw new InvalidEntityException("GeoPosition of node is null", node);
  }

  /**
   * Validates a voltage level
   *
   * @param voltageLevel Element to validate
   * @throws VoltageLevelException If nominal voltage is not apparent or not a positive value
   */
  private static void checkVoltageLevel(VoltageLevel voltageLevel) throws VoltageLevelException {
    checkNonNull(voltageLevel, "a voltage level");
    if (voltageLevel.getNominalVoltage() == null)
      throw new VoltageLevelException(
          "The nominal voltage of voltage level " + voltageLevel + " is null");
    if (voltageLevel.getNominalVoltage().getValue().doubleValue() <= 0d)
      throw new VoltageLevelException(
          "The nominal voltage of voltage level " + voltageLevel + " must be positive!");
  }
}
