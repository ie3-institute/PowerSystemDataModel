/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/**
 * Exception that is thrown whenever data columns are not as expected.
 *
 * @version 0.1
 * @since 10.12.20
 */
public class InvalidColumnNameException extends RuntimeException {
  /**
   * Instantiates a new Invalid column name exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public InvalidColumnNameException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new Invalid column name exception.
   *
   * @param cause the cause
   */
  public InvalidColumnNameException(final Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new Invalid column name exception.
   *
   * @param message the message
   */
  public InvalidColumnNameException(final String message) {
    super(message);
  }
}
