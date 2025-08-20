/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

import java.util.List;

/** The type Graphic source exception. */
public class GraphicSourceException extends SourceException {
  /**
   * Instantiates a new Graphic source exception.
   *
   * @param message the message
   * @param exceptions the exceptions
   */
  public GraphicSourceException(String message, List<SourceException> exceptions) {
    super(message, exceptions);
  }
}
