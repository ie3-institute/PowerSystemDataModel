/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/** Is thrown, when something went wrong during entity creation process in a EntityFactory */
public class FactoryException extends RuntimeException {
  /**
   * Instantiates a new Factory exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public FactoryException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new Factory exception.
   *
   * @param cause the cause
   */
  public FactoryException(final Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new Factory exception.
   *
   * @param message the message
   */
  public FactoryException(final String message) {
    super(message);
  }
}
