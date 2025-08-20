/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import edu.ie3.datamodel.io.extractor.NestedEntity;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.time.ZonedDateTime;

/** Describes an operable Entity, with operation period interval */
public interface Operable extends NestedEntity {
  /**
   * Retrieves the operation time associated with this entity.
   *
   * @return an {@link OperationTime} object representing the operation time.
   */
  OperationTime getOperationTime();

  /**
   * Checks if this entity is in operation on a specified date.
   *
   * @param date the date to check for operation status
   * @return true if the entity is in operation on the given date; false otherwise
   */
  default boolean inOperationOn(ZonedDateTime date) {
    return getOperationTime().includes(date);
  }

  /**
   * Retrieves the operator associated with this entity.
   *
   * @return an {@link OperatorInput} object representing the operator.
   */
  OperatorInput getOperator();
}
