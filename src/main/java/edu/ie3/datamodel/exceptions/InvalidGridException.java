/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

public class InvalidGridException extends IllegalArgumentException {
  public InvalidGridException(String message) {
    super(message);
  }

  public InvalidGridException(String message, Throwable cause) {
    super(message, cause);
  }
}
