/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/** The type Failure exception. */
public class FailureException extends Exception {
  /**
   * Instantiates a new Failure exception.
   *
   * @param message the message
   * @param throwable the throwable
   */
  public FailureException(String message, Throwable throwable) {
    super(message, throwable);
  }

  /**
   * Instantiates a new Failure exception.
   *
   * @param message the message
   */
  public FailureException(String message) {
    super(message);
  }

  /**
   * Instantiates a new Failure exception.
   *
   * @param throwable the throwable
   */
  public FailureException(Throwable throwable) {
    super(throwable);
  }
}
