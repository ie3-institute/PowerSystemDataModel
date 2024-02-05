/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.UnsafeEntityException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.utils.Try;

public class MeasurementUnitValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private MeasurementUnitValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Validates a measurement unit if:
   *
   * <ul>
   *   <li>it is not null
   *   <li>any values are measured
   * </ul>
   *
   * @param measurementUnit Measurement unit to validate
   * @return a try object either containing an {@link ValidationException} or an empty Success
   */
  protected static Try<Void, ? extends ValidationException> check(
      MeasurementUnitInput measurementUnit) {
    Try<Void, InvalidEntityException> isNull = checkNonNull(measurementUnit, "a measurement unit");

    if (isNull.isFailure()) {
      return isNull;
    }

    return Try.ofVoid(
        !measurementUnit.getP()
            && !measurementUnit.getQ()
            && !measurementUnit.getVAng()
            && !measurementUnit.getVMag(),
        () ->
            new UnsafeEntityException(
                "Measurement Unit does not measure any values", measurementUnit));
  }
}
