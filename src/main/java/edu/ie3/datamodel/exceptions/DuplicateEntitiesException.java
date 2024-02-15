/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

import edu.ie3.datamodel.utils.ExceptionUtils;
import java.util.List;

public class DuplicateEntitiesException extends ValidationException {

  public DuplicateEntitiesException(String s) {
    super(s);
  }

  public DuplicateEntitiesException(
      String entityName, List<? extends ValidationException> exceptions) {
    this(
        "The following exception(s) occurred while checking the uniqueness of '"
            + entityName
            + "' entities: "
            + ExceptionUtils.getMessages(exceptions));
  }
}
