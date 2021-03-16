/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

import edu.ie3.datamodel.models.UniqueEntity;

/** Is thrown, when a checked entity may be unsafe to use, but is not necessarily unsafe */
public class UnsafeEntityException extends ValidationException {

  public UnsafeEntityException(String faultDescription, UniqueEntity unsafeEntity) {
    super("Entity may be unsafe because of: " + faultDescription + " [" + unsafeEntity + "]");
  }
}
