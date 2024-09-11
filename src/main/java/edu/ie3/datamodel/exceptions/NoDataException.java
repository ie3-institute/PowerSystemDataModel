/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/**
 * Exception that should be used whenever no weather data is received{@link
 * edu.ie3.datamodel.io.source.DataSource}
 *
 * @version 0.1
 * @since 04.09.24
 */
public class NoDataException extends Exception {

  public NoDataException(final String message) {
    super(message);
  }
}
