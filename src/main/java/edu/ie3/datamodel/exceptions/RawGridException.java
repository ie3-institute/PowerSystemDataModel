/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

import java.util.List;

public class RawGridException extends SourceException {
  public RawGridException(String message, List<SourceException> exceptions) {
    super(message, exceptions);
  }
}
