/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

public class RawInputDataException extends RuntimeException {

  public RawInputDataException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public RawInputDataException(final Throwable cause) {
    super(cause);
  }

  public RawInputDataException(final String message) {
    super(message);
  }
}
