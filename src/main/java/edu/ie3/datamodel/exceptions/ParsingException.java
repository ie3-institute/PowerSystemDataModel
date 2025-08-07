/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/** The type Parsing exception. */
public class ParsingException extends Exception {
  /**
   * Instantiates a new Parsing exception.
   *
   * @param message the message
   */
  public ParsingException(String message) {
    super(message);
  }

  /**
   * Instantiates a new Parsing exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public ParsingException(String message, Throwable cause) {
    super(message, cause);
  }
}
