package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;

public class SystemParticipantValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private SystemParticipantValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  public static void check(SystemParticipantInput systemParticipantInput) {
    checkNonNull(systemParticipantInput, "a system participant");
    if (systemParticipantInput.getNode() == null)
      throw new InvalidEntityException("system participant is null", systemParticipantInput);
  }

}
