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
   * Gets operation time.
   *
   * @return the operation time
   */
  OperationTime getOperationTime();

  /**
   * In operation on boolean.
   *
   * @param date the date
   * @return the boolean
   */
  default boolean inOperationOn(ZonedDateTime date) {
    return getOperationTime().includes(date);
  }

  /**
   * Gets operator.
   *
   * @return the operator
   */
  OperatorInput getOperator();
}
