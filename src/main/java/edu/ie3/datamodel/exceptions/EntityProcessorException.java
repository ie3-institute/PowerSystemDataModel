/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/**
 * Is thrown, when something went wrong during entity field mapping creation in a {@link
 * edu.ie3.datamodel.io.processor.EntityProcessor}*
 */
public class EntityProcessorException extends Exception {
  /**
   * Instantiates a new Entity processor exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public EntityProcessorException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new Entity processor exception.
   *
   * @param cause the cause
   */
  public EntityProcessorException(final Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new Entity processor exception.
   *
   * @param message the message
   */
  public EntityProcessorException(final String message) {
    super(message);
  }
}
