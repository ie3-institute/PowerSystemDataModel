/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/**
 * Exception that should be used whenever something invalid happens in a implementation of a {@link
 * edu.ie3.datamodel.io.connectors.DataConnector}
 *
 * @version 0.1
 * @since 20.03.20
 */
public class ExtractorException extends Exception {

  public ExtractorException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public ExtractorException(final Throwable cause) {
    super(cause);
  }

  public ExtractorException(final String message) {
    super(message);
  }
}
