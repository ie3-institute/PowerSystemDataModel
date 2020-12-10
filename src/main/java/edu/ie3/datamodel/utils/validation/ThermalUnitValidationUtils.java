/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.models.input.thermal.*;
import javax.measure.Quantity;

public class ThermalUnitValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private ThermalUnitValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  private static String notImplementedString(Object obj) {
    return "Cannot validate object of class '"
        + obj.getClass().getSimpleName()
        + "', as no routine is implemented.";
  }

  /**
   * Validates a thermal unit if: <br>
   * - it is not null <br>
   * A "distribution" method, that forwards the check request to specific implementations to fulfill
   * the checking task, based on the class of the given object. If an unknown class is handed in, a
   * {@link ValidationException} is thrown.
   *
   * @param thermalUnitInput ThermalUnitInput to validate
   */
  public static void check(ThermalUnitInput thermalUnitInput) {
    // Check if null
    checkNonNull(thermalUnitInput, "a thermal unit");

    // Further checks for subclasses
    if (ThermalSinkInput.class.isAssignableFrom(thermalUnitInput.getClass()))
      checkThermalSink((ThermalSinkInput) thermalUnitInput);
    else if (ThermalStorageInput.class.isAssignableFrom(thermalUnitInput.getClass()))
      checkThermalStorage((ThermalStorageInput) thermalUnitInput);
    else throw new ValidationException(notImplementedString(thermalUnitInput));
  }

  /**
   * Validates a thermalSinkInput if: <br>
   * - it is not null <br>
   * A "distribution" method, that forwards the check request to specific implementations to fulfill
   * the checking task, based on the class of the given object. If an unknown class is handed in, a
   * {@link ValidationException} is thrown.
   *
   * @param thermalSinkInput ThermalSinkInput to validate
   */
  public static void checkThermalSink(ThermalSinkInput thermalSinkInput) {
    // Check if null
    checkNonNull(thermalSinkInput, "a thermal sink");

    // Further checks for subclasses
    if (ThermalHouseInput.class.isAssignableFrom(thermalSinkInput.getClass()))
      checkThermalHouse((ThermalHouseInput) thermalSinkInput);
    else throw new ValidationException(notImplementedString(thermalSinkInput));
  }

  /**
   * Validates a thermalStorageInput if: <br>
   * - it is not null <br>
   * A "distribution" method, that forwards the check request to specific implementations to fulfill
   * the checking task, based on the class of the given object. If an unknown class is handed in, a
   * {@link ValidationException} is thrown.
   *
   * @param thermalStorageInput ThermalStorageInput to validate
   */
  public static void checkThermalStorage(ThermalStorageInput thermalStorageInput) {
    // Check if null
    checkNonNull(thermalStorageInput, "a thermal storage");

    // Further checks for subclasses
    if (CylindricalStorageInput.class.isAssignableFrom(thermalStorageInput.getClass()))
      checkCylindricalStorage((CylindricalStorageInput) thermalStorageInput);
    else throw new ValidationException(notImplementedString(thermalStorageInput));
  }

  /**
   * Validates a thermalHouseInput if: <br>
   * - it is not null <br>
   * - its thermal losses are not negative <br>
   * - its thermal capacity is positive
   *
   * @param thermalHouseInput ThermalHouseInput to validate
   */
  public static void checkThermalHouse(ThermalHouseInput thermalHouseInput) {
    // Check if null
    checkNonNull(thermalHouseInput, "a thermal house");
    // Check for negative quantities
    detectNegativeQuantities(
        new Quantity<?>[] {thermalHouseInput.getEthLosses()}, thermalHouseInput);
    // Check for zero or negative quantities
    detectZeroOrNegativeQuantities(
        new Quantity<?>[] {thermalHouseInput.getEthCapa()}, thermalHouseInput);
  }

  /**
   * Validates a cylindricalStorageInput if: <br>
   * - it is not null <br>
   * - its available storage volume is positive <br>
   * - its minimum permissible storage volume is positive and not greater than the available storage
   * volume <br>
   * - its inlet temperature is equal/greater than the outlet temperature <br>
   * - its specific heat capacity is positive
   *
   * @param cylindricalStorageInput CylindricalStorageInput to validate
   */
  public static void checkCylindricalStorage(CylindricalStorageInput cylindricalStorageInput) {
    // Check if null
    checkNonNull(cylindricalStorageInput, "a cylindrical storage");
    // Check if inlet temperature is higher/equal to outlet temperature
    if (cylindricalStorageInput.getInletTemp().isLessThan(cylindricalStorageInput.getReturnTemp()))
      throw new InvalidEntityException(
          "Inlet temperature of the cylindrical storage cannot be lower than outlet temperature",
          cylindricalStorageInput);
    // Check if minimum permissible storage volume is lower than overall available storage volume
    if (cylindricalStorageInput
        .getStorageVolumeLvlMin()
        .isGreaterThan(cylindricalStorageInput.getStorageVolumeLvl()))
      throw new InvalidEntityException(
          "Minimum permissible storage volume of the cylindrical storage cannot be higher than overall available storage volume",
          cylindricalStorageInput);
    // Check for zero or negative quantities
    detectZeroOrNegativeQuantities(
        new Quantity<?>[] {
          cylindricalStorageInput.getStorageVolumeLvl(),
          cylindricalStorageInput.getStorageVolumeLvlMin(),
          cylindricalStorageInput.getC()
        },
        cylindricalStorageInput);
  }
}
