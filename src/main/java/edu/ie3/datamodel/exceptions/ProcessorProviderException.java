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
 * @since 20.03.20
 */
public class ProcessorProviderException extends Exception {

  public ProcessorProviderException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public ProcessorProviderException(final Throwable cause) {
    super(cause);
  }

  public ProcessorProviderException(final String message) {
    super(message);
  }
}
