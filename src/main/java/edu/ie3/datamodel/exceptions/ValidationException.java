/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

public class ValidationException extends IllegalArgumentException {

  public ValidationException(String s) {
    super(s);
  }

  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
