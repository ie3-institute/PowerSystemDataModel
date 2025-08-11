/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

import edu.ie3.datamodel.utils.ExceptionUtils;
import java.util.List;

/** The type Failed validation exception. */
public class FailedValidationException extends ValidationException {
  /**
   * Instantiates a new Failed validation exception.
   *
   * @param message the message
   * @param throwable the throwable
   */
  public FailedValidationException(String message, Throwable throwable) {
    super(message, throwable);
  }

  /**
   * Instantiates a new Failed validation exception.
   *
   * @param throwable the throwable
   */
  public FailedValidationException(Throwable throwable) {
    super(throwable);
  }

  /**
   * Instantiates a new Failed validation exception.
   *
   * @param message the message
   */
  public FailedValidationException(String message) {
    super(message);
  }

  /**
   * Instantiates a new Failed validation exception.
   *
   * @param exceptions List of exceptions, which must not be empty
   */
  public FailedValidationException(List<? extends Exception> exceptions) {
    super("Validation failed due to:\n " + ExceptionUtils.combineExceptions(exceptions));
  }
}
