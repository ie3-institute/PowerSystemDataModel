/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import java.util.logging.Logger;

public class MeasurementUnitValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private MeasurementUnitValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Validates a measurement unit if: <br>
   * - it is not null <br>
   * - its node is not null <br>
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
    //Check if measurement unit measures any values
    Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    if (!measurementUnit.getP()
        && !measurementUnit.getQ()
        && !measurementUnit.getVAng()
        && !measurementUnit.getVMag())
      logger.warning("Measurement Unit does not measure any values");
      //throw new InvalidEntityException("Measurement Unit does not measure any values", measurementUnit);
    //TODO NSteffan: Work with logger warning instead of exception? Correct usage of logger?
  }

}
