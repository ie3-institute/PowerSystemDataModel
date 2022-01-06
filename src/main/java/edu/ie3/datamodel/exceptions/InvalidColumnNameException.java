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
  public InvalidColumnNameException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public InvalidColumnNameException(final Throwable cause) {
    super(cause);
  }

  public InvalidColumnNameException(final String message) {
    super(message);
  }
}
