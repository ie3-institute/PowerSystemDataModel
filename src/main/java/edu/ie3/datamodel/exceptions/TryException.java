/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/** The type Try exception. */
public class TryException extends RuntimeException {
  /**
   * Instantiates a new Try exception.
   *
   * @param message the message
   * @param throwable the throwable
   */
  public TryException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
