/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 19.03.20
 */
public class SinkException extends RuntimeException {
  public SinkException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public SinkException(final Throwable cause) {
    super(cause);
  }

  public SinkException(final String message) {
    super(message);
  }
}
