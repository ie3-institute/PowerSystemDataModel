/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/**
 * Exception that should be used whenever an error occurs in a instance of a {@link
 * edu.ie3.datamodel.io.sink.DataSink}*
 *
 * @version 0.1
 * @since 19.03.20
 */
public class SinkException extends Exception {
  /**
   * Instantiates a new Sink exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public SinkException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new Sink exception.
   *
   * @param cause the cause
   */
  public SinkException(final Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new Sink exception.
   *
   * @param message the message
   */
  public SinkException(final String message) {
    super(message);
  }
}
