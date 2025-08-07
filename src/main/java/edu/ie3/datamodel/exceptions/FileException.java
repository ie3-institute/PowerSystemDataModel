/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/** The type File exception. */
public class FileException extends Exception {
  /**
   * Instantiates a new File exception.
   *
   * @param message the message
   */
  public FileException(String message) {
    super(message);
  }

  /**
   * Instantiates a new File exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public FileException(String message, Throwable cause) {
    super(message, cause);
  }
}
