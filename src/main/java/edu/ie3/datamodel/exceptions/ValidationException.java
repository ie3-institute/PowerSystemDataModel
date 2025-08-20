/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/** The type Validation exception. */
public abstract class ValidationException extends Exception {
  /**
   * Instantiates a new Validation exception.
   *
   * @param s the s
   */
  protected ValidationException(String s) {
    super(s);
  }

  /**
   * Instantiates a new Validation exception.
   *
   * @param throwable the throwable
   */
  protected ValidationException(Throwable throwable) {
    super(throwable);
  }

  /**
   * Instantiates a new Validation exception.
   *
   * @param s the s
   * @param throwable the throwable
   */
  protected ValidationException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
