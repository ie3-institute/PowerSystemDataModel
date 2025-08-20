/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/**
 * Exception that should be used whenever an error occurs in a instance of {@link
 * edu.ie3.datamodel.io.processor.ProcessorProvider}*
 *
 * @version 0.1
 * @since 20.03.20
 */
public class ProcessorProviderException extends Exception {

  /**
   * Instantiates a new Processor provider exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public ProcessorProviderException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new Processor provider exception.
   *
   * @param cause the cause
   */
  public ProcessorProviderException(final Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new Processor provider exception.
   *
   * @param message the message
   */
  public ProcessorProviderException(final String message) {
    super(message);
  }
}
