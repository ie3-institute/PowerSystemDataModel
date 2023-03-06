/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.UnsafeEntityException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.utils.options.Failure;
import edu.ie3.datamodel.utils.options.Success;
import edu.ie3.datamodel.utils.options.Try;

public class MeasurementUnitValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private MeasurementUnitValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Validates a measurement unit if: <br>
   * - it is not null <br>
   * - any values are measured
   *
   * @param measurementUnit Measurement unit to validate
   */
  protected static Try<Void, ValidationException> check(MeasurementUnitInput measurementUnit) {
    try {
      checkNonNull(measurementUnit, "a measurement unit");
    } catch (ValidationException e) {
      return new Failure<>(e);
    }

    if (!measurementUnit.getP()
        && !measurementUnit.getQ()
        && !measurementUnit.getVAng()
        && !measurementUnit.getVMag()) {
      return new Failure<>(
          new UnsafeEntityException(
              "Measurement Unit does not measure any values", measurementUnit));
    } else {
      return Success.empty();
    }
  }
}
