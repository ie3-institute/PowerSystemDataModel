/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.FailedValidationException;
import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.models.input.thermal.*;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.Failure;
import java.util.ArrayList;
import java.util.List;
import javax.measure.Quantity;

public class ThermalUnitValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private ThermalUnitValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Validates a thermal unit if: <br>
   * - it is not null <br>
   * A "distribution" method, that forwards the check request to specific implementations to fulfill
   * the checking task, based on the class of the given object.
   *
   * @param thermalUnitInput ThermalUnitInput to validate
   * @return a list of try objects either containing an {@link ValidationException} or an empty
   *     Success
   */
  protected static List<Try<Void, ? extends ValidationException>> check(
      ThermalUnitInput thermalUnitInput) {
    try {
      checkNonNull(thermalUnitInput, "a thermal unit");
    } catch (InvalidEntityException e) {
      return List.of(
          new Failure<>(
              new InvalidEntityException(
                  "Validation not possible because received object {"
                      + thermalUnitInput
                      + "} was null",
                  e)));
    }

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    // Further checks for subclasses
    if (ThermalSinkInput.class.isAssignableFrom(thermalUnitInput.getClass())) {
      exceptions.addAll(checkThermalSink((ThermalSinkInput) thermalUnitInput));
    } else if (ThermalStorageInput.class.isAssignableFrom(thermalUnitInput.getClass())) {
      exceptions.addAll(checkThermalStorage((ThermalStorageInput) thermalUnitInput));
    } else {
      exceptions.add(
          new Failure<>(
              new FailedValidationException(
                  checkNotImplementedException(thermalUnitInput).getMessage())));
    }

    return exceptions;
  }

  /**
   * Validates a thermalSinkInput if: <br>
   * - it is not null <br>
   * A "distribution" method, that forwards the check request to specific implementations to fulfill
   * the checking task, based on the class of the given object.
   *
   * @param thermalSinkInput ThermalSinkInput to validate
   * @return a list of try objects either containing an {@link ValidationException} or an empty
   *     Success
   */
  private static List<Try<Void, ? extends ValidationException>> checkThermalSink(
      ThermalSinkInput thermalSinkInput) {
    try {
      checkNonNull(thermalSinkInput, "a thermal sink");
    } catch (InvalidEntityException e) {
      return List.of(
          new Failure<>(
              new InvalidEntityException(
                  "Validation not possible because received object {"
                      + thermalSinkInput
                      + "} was null",
                  e)));
    }

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    // Further checks for subclasses
    if (ThermalHouseInput.class.isAssignableFrom(thermalSinkInput.getClass())) {
      exceptions.addAll(checkThermalHouse((ThermalHouseInput) thermalSinkInput));
    } else {
      exceptions.add(
          new Failure<>(
              new FailedValidationException(
                  checkNotImplementedException(thermalSinkInput).getMessage())));
    }

    return exceptions;
  }

  /**
   * Validates a thermalStorageInput if: <br>
   * - it is not null <br>
   * A "distribution" method, that forwards the check request to specific implementations to fulfill
   * the checking task, based on the class of the given object.
   *
   * @param thermalStorageInput ThermalStorageInput to validate
   * @return a list of try objects either containing an {@link ValidationException} or an empty
   *     Success
   */
  private static List<Try<Void, ? extends ValidationException>> checkThermalStorage(
      ThermalStorageInput thermalStorageInput) {
    try {
      checkNonNull(thermalStorageInput, "a thermal storage");
    } catch (InvalidEntityException e) {
      return List.of(
          new Failure<>(
              new InvalidEntityException(
                  "Validation not possible because received object {"
                      + thermalStorageInput
                      + "} was null",
                  e)));
    }

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    // Further checks for subclasses
    if (CylindricalStorageInput.class.isAssignableFrom(thermalStorageInput.getClass())) {
      exceptions.addAll(checkCylindricalStorage((CylindricalStorageInput) thermalStorageInput));
    } else {
      exceptions.add(
          new Failure<>(
              new FailedValidationException(
                  checkNotImplementedException(thermalStorageInput).getMessage())));
    }

    return exceptions;
  }

  /**
   * Validates a thermalHouseInput if: <br>
   * - it is not null <br>
   * - its thermal losses are not negative <br>
   * - its thermal capacity is positive <br>
   * - its upper temperature limit is higher than the lower temperature limit <br>
   * - its target temperature lies between the upper und lower limit temperatures
   *
   * @param thermalHouseInput ThermalHouseInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkThermalHouse(
      ThermalHouseInput thermalHouseInput) {
    try {
      checkNonNull(thermalHouseInput, "a thermal house");
    } catch (InvalidEntityException e) {
      return List.of(
          new Failure<>(
              new InvalidEntityException(
                  "Validation not possible because received object {"
                      + thermalHouseInput
                      + "} was null",
                  e)));
    }

    List<Try<Void, InvalidEntityException>> exceptions =
        new ArrayList<>(
            Try.ofVoid(
                InvalidEntityException.class,
                () ->
                    detectNegativeQuantities(
                        new Quantity<?>[] {thermalHouseInput.getEthLosses()}, thermalHouseInput),
                () ->
                    detectZeroOrNegativeQuantities(
                        new Quantity<?>[] {thermalHouseInput.getEthCapa()}, thermalHouseInput)));

    if (thermalHouseInput
            .getLowerTemperatureLimit()
            .isGreaterThan(thermalHouseInput.getTargetTemperature())
        || thermalHouseInput
            .getUpperTemperatureLimit()
            .isLessThan(thermalHouseInput.getTargetTemperature())) {
      exceptions.add(
          new Failure<>(
              new InvalidEntityException(
                  "Target temperature must be higher than lower temperature limit and lower than upper temperature limit",
                  thermalHouseInput)));
    }

    return exceptions;
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
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkCylindricalStorage(
      CylindricalStorageInput cylindricalStorageInput) {
    try {
      checkNonNull(cylindricalStorageInput, "a cylindrical storage");
    } catch (InvalidEntityException e) {
      return List.of(
          new Failure<>(
              new InvalidEntityException(
                  "Validation not possible because received object {"
                      + cylindricalStorageInput
                      + "} was null",
                  e)));
    }

    List<Try<Void, InvalidEntityException>> exceptions = new ArrayList<>();

    // Check if inlet temperature is higher/equal to outlet temperature
    if (cylindricalStorageInput.getInletTemp().isLessThan(cylindricalStorageInput.getReturnTemp()))
      exceptions.add(
          new Failure<>(
              new InvalidEntityException(
                  "Inlet temperature of the cylindrical storage cannot be lower than outlet temperature",
                  cylindricalStorageInput)));
    // Check if minimum permissible storage volume is lower than overall available storage volume
    if (cylindricalStorageInput
        .getStorageVolumeLvlMin()
        .isGreaterThan(cylindricalStorageInput.getStorageVolumeLvl()))
      exceptions.add(
          new Failure<>(
              new InvalidEntityException(
                  "Minimum permissible storage volume of the cylindrical storage cannot be higher than overall available storage volume",
                  cylindricalStorageInput)));

    exceptions.add(
        Try.ofVoid(
            () ->
                detectZeroOrNegativeQuantities(
                    new Quantity<?>[] {
                      cylindricalStorageInput.getStorageVolumeLvl(),
                      cylindricalStorageInput.getStorageVolumeLvlMin(),
                      cylindricalStorageInput.getC()
                    },
                    cylindricalStorageInput),
            InvalidEntityException.class));

    return exceptions;
  }
}
