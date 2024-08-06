/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/**
 * Is thrown, when something went wrong during entity field mapping creation in a {@link
 * edu.ie3.datamodel.io.processor.EntityProcessor}
 */
public class EntityProcessorException extends Exception {
  public EntityProcessorException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public EntityProcessorException(final Throwable cause) {
    super(cause);
  }

  public EntityProcessorException(final String message) {
    super(message);
  }
}
