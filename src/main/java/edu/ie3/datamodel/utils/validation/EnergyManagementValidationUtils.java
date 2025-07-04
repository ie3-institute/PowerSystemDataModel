/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.utils.Try;
import java.util.ArrayList;
import java.util.List;

public class EnergyManagementValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private EnergyManagementValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Validates a energy management unit if:
   *
   * <ul>
   *   <li>its control strategy is not null
   * </ul>
   *
   * A "distribution" method, that forwards the check request to specific implementations to fulfill
   * the checking task, based on the class of the given object.
   *
   * @param energyManagement EmInput to validate
   * @return a list of try objects either containing an {@link ValidationException} or an empty
   *     Success
   */
  protected static List<Try<Void, ? extends ValidationException>> check(EmInput energyManagement) {
    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    exceptions.add(
        Try.ofVoid(
            energyManagement.getControlStrategy() == null,
            () ->
                new InvalidEntityException(
                    "No control strategy of energy management defined for", energyManagement)));

    return exceptions;
  }
}
