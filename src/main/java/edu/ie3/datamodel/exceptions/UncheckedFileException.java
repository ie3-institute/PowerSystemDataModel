/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/**
 * Unchecked counterpart of {@link FileException}, thrown when file handling fails within a context
 * that cannot propagate a checked exception (e.g. inside a stream operation).
 */
public class UncheckedFileException extends RuntimeException {
  public UncheckedFileException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
