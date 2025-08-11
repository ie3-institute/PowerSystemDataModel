/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

import edu.ie3.datamodel.utils.ExceptionUtils;
import java.util.List;

/** The type Duplicate entities exception. */
public class DuplicateEntitiesException extends ValidationException {

  /**
   * Instantiates a new Duplicate entities exception.
   *
   * @param s the s
   */
  public DuplicateEntitiesException(String s) {
    super(s);
  }

  /**
   * Instantiates a new Duplicate entities exception.
   *
   * @param entityName the entity name
   * @param exceptions the exceptions
   */
  public DuplicateEntitiesException(
      String entityName, List<? extends ValidationException> exceptions) {
    this(
        "The following exception(s) occurred while checking the uniqueness of '"
            + entityName
            + "' entities: \n"
            + ExceptionUtils.combineExceptions(exceptions));
  }
}
