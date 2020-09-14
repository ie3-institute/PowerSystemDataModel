/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

public class FileException extends Exception {
  public FileException(String message) {
    super(message);
  }

  public FileException(String message, Throwable cause) {
    super(message, cause);
  }
}
