/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.utils.ExceptionUtils;
import java.util.Collection;

public class DuplicateEntitiesException extends ValidationException {

  protected DuplicateEntitiesException(String s) {
    super(s);
  }

  protected DuplicateEntitiesException(String s, String entities) {
    super(s + entities);
  }

  public DuplicateEntitiesException(String fieldName, Collection<? extends UniqueEntity> entities) {
    this(
        "The following entities have duplicate '" + fieldName + "':",
        ExceptionUtils.combine(entities));
  }
}
