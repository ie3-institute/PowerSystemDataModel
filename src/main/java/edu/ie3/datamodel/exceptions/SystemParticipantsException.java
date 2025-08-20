/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

import java.util.List;

/** The type System participants exception. */
public class SystemParticipantsException extends SourceException {
  /**
   * Instantiates a new System participants exception.
   *
   * @param message the message
   * @param exceptions the exceptions
   */
  public SystemParticipantsException(String message, List<SourceException> exceptions) {
    super(message, exceptions);
  }
}
