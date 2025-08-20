/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

import java.util.List;

/** The type Raw grid exception. */
public class RawGridException extends SourceException {
  /**
   * Instantiates a new Raw grid exception.
   *
   * @param message the message
   * @param exceptions the exceptions
   */
  public RawGridException(String message, List<SourceException> exceptions) {
    super(message, exceptions);
  }
}
