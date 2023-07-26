/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

public class FailureException extends Exception {
  public FailureException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public FailureException(String message) {
    super(message);
  }

  public FailureException(Throwable throwable) {
    super(throwable);
  }
}
