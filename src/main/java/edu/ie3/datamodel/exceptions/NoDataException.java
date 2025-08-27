/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/**
 * Exception that should be used whenever no data is received{@link
 * edu.ie3.datamodel.io.source.DataSource}
 */
public class NoDataException extends Exception {

  public NoDataException(final String message) {
    super(message);
  }

  public NoDataException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
