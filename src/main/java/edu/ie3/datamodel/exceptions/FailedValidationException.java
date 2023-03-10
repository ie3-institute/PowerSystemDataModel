/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

import edu.ie3.datamodel.utils.ExceptionUtils;
import java.util.List;

public class FailedValidationException extends ValidationException {
  public FailedValidationException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public FailedValidationException(String message) {
    super(message);
  }

  public FailedValidationException(List<? extends Exception> exceptions) {
    super("Validation failed due to: " + ExceptionUtils.getMessages(exceptions));
  }
}
