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
public class ConnectorException extends Exception {

  public ConnectorException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public ConnectorException(final Throwable cause) {
    super(cause);
  }

  public ConnectorException(final String message) {
    super(message);
  }
}
