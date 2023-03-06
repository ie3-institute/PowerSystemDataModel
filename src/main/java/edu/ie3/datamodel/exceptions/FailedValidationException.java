/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

public class FailedValidationException extends ValidationException {
  public FailedValidationException(String s) {
    super(s);
  }

  public FailedValidationException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
