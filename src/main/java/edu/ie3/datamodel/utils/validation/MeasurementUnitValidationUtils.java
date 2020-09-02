package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;

public class MeasurementUnitValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private MeasurementUnitValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Validates a measurement unit if: <br>
   * - it is not null <br>
   * - its node is not nul
   *
   * @param measurementUnit Measurement unit to validate
   */
  public static void check(MeasurementUnitInput measurementUnit) {
    checkNonNull(measurementUnit, "a measurement unit");
    if (measurementUnit.getNode() == null)
      throw new InvalidEntityException("node is null", measurementUnit);
  }


}
