/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

public class MissingTypeException extends Exception {

  public MissingTypeException(String message) {
    super(message);
  }

  public MissingTypeException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
