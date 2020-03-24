/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import edu.ie3.datamodel.models.input.OperatorInput;
import java.time.ZonedDateTime;

/** Describes an operable Entity, with operation period interval */
public interface Operable {

  OperationTime getOperationTime();

  default boolean inOperationOn(ZonedDateTime date) {
    return getOperationTime().includes(date);
  }

  OperatorInput getOperator();
}
