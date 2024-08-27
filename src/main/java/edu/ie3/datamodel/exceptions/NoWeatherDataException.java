/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.exceptions;

/**
 * Exception that should be used whenever no weather data is available in a weather data source.
 *
 * @version 0.1
 * @since 27.08.24
 */
public class NoWeatherDataException extends SourceException {
  private static final long serialVersionUID = 123456789L;

  public NoWeatherDataException(final String message) {
      super(message);
  }

  public NoWeatherDataException(final String message, final Throwable cause) {
      super(message, cause);
  }
}
