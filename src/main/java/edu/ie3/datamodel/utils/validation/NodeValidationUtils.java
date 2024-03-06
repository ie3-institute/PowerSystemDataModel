/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.*;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.Failure;
import java.util.ArrayList;
import java.util.List;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public class NodeValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private NodeValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Validates a node if:
   *
   * <ul>
   *   <li>it is not null
   *   <li>voltage level is not null and valid
   *   <li>target voltage is larger than zero and smaller than two
   *   <li>subnet number is larger than zero
   *   <li>geoPosition is not null
   * </ul>
   *
   * @param node Node to validate
   * @return a list of try objects either containing an {@link ValidationException} or an empty
   *     Success
   */
  protected static List<Try<Void, ? extends ValidationException>> check(NodeInput node) {
    Try<Void, InvalidEntityException> isNull = checkNonNull(node, "a node");

    if (isNull.isFailure()) {
      return List.of(isNull);
    }

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    try {
      checkVoltageLevel(node.getVoltLvl());
    } catch (VoltageLevelException e) {
      exceptions.add(
          new Failure<>(new InvalidEntityException("Node has invalid voltage level", node)));
    } catch (InvalidEntityException invalidEntityException) {
      exceptions.add(new Failure<>(invalidEntityException));
    }

    exceptions.add(
        Try.ofVoid(
            node.getvTarget()
                .isLessThanOrEqualTo(
                    Quantities.getQuantity(0, StandardUnits.TARGET_VOLTAGE_MAGNITUDE)),
            () ->
                new InvalidEntityException("Target voltage (p.u.) is not a positive value", node)));
    exceptions.add(
        Try.ofVoid(
            node.getvTarget()
                .isGreaterThan(Quantities.getQuantity(2, StandardUnits.TARGET_VOLTAGE_MAGNITUDE)),
            () -> new UnsafeEntityException("Target voltage (p.u.) might be too high", node)));
    exceptions.add(
        Try.ofVoid(
            node.getSubnet() <= 0,
            () -> new InvalidEntityException("Subnet can't be zero or negative", node)));
    exceptions.add(
        Try.ofVoid(
            node.getGeoPosition() == null,
            () -> new InvalidEntityException("GeoPosition of node is null", node)));

    return exceptions;
  }

  /**
   * Validates a voltage level
   *
   * @param voltageLevel Element to validate
   * @throws InvalidEntityException If the given voltage level is null
   * @throws VoltageLevelException If nominal voltage is not apparent or not a positive value
   */
  private static void checkVoltageLevel(VoltageLevel voltageLevel)
      throws InvalidEntityException, VoltageLevelException {
    checkNonNull(voltageLevel, "a voltage level").getOrThrow();
    if (voltageLevel.getNominalVoltage() == null)
      throw new VoltageLevelException(
          "The nominal voltage of voltage level " + voltageLevel + " is null");
    if (voltageLevel.getNominalVoltage().isLessThanOrEqualTo(Quantities.getQuantity(0, Units.VOLT)))
      throw new VoltageLevelException(
          "The nominal voltage of voltage level " + voltageLevel + " must be positive!");
  }
}
