/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/** The type Invalid grid exception. */
public class InvalidGridException extends ValidationException {
  /**
   * Instantiates a new Invalid grid exception.
   *
   * @param message the message
   */
  public InvalidGridException(String message) {
    super(message);
  }

  /**
   * Instantiates a new Invalid grid exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public InvalidGridException(String message, Throwable cause) {
    super(message, cause);
  }
}
