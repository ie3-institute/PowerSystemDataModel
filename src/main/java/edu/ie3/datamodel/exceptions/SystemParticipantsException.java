/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

import edu.ie3.datamodel.utils.ExceptionUtils;
import java.util.List;

public class SystemParticipantsException extends RuntimeException {
  public SystemParticipantsException(String message, List<SourceException> exceptions) {
    super(message + " " + ExceptionUtils.getMessages(exceptions), exceptions.get(0));
  }
}
