package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.UnsafeEntityException;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;

public class MeasurementUnitValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private MeasurementUnitValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Validates a measurement unit if: <br>
   * - it is not null <br>
   * - its node is not null <br>
   * - its operator is not null <br>
   * - any values are measured
   *
   * @param measurementUnit Measurement unit to validate
   */
  public static void check(MeasurementUnitInput measurementUnit) {
    //Check if null
    checkNonNull(measurementUnit, "a measurement unit");
    //Check if node is null
    if (measurementUnit.getNode() == null)
      throw new InvalidEntityException("Node is null", measurementUnit);
    //Check if operator is null
    if (measurementUnit.getOperator() == null)
      throw new InvalidEntityException("No operator assigned", measurementUnit);
    //TODO: NSteffan - necessary to check operator ("at least dummy")? vMag? vAng? p? q?
    if (measurementUnit.getP() == false
        && measurementUnit.getQ() == false
        && measurementUnit.getVAng() == false
        && measurementUnit.getVMag() == false)
      throw new UnsafeEntityException("Measurement Unit does not measure any values", measurementUnit);
  }

}
