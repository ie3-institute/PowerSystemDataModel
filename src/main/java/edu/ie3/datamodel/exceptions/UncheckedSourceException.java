/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/**
 * Unchecked counterpart of {@link SourceException}, thrown when reading from a data source fails
 * within a context that cannot propagate a checked exception (e.g. behind a {@link
 * java.util.function.Supplier}).
 */
public class UncheckedSourceException extends RuntimeException {
  public UncheckedSourceException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
