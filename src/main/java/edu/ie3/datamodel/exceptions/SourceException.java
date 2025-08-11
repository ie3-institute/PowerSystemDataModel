/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

import edu.ie3.datamodel.utils.ExceptionUtils;
import java.util.List;

/**
 * Exception that should be used whenever an error occurs in a instance of a {@link
 * edu.ie3.datamodel.io.source.DataSource}*
 *
 * @version 0.1
 * @since 19.03.20
 */
public class SourceException extends Exception {

  private static final long serialVersionUID = -1861732230033172395L;

  /**
   * Instantiates a new Source exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public SourceException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new Source exception.
   *
   * @param cause the cause
   */
  public SourceException(final Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new Source exception.
   *
   * @param message the message
   */
  public SourceException(final String message) {
    super(message);
  }

  /**
   * Instantiates a new Source exception.
   *
   * @param message the message
   * @param exceptions the exceptions
   */
  public SourceException(String message, List<? extends Exception> exceptions) {
    super(message + "\n " + ExceptionUtils.combineExceptions(exceptions));
  }
}
