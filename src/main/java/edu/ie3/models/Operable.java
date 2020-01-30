/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models;

import edu.ie3.models.input.OperatorInput;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.Optional;

/** Describes an operable Entity, with operation period interval */
public interface Operable {

  OperationTime getOperationTime();

  void setOperationTime(OperationTime operationTime);

  default boolean inOperationOn(ZonedDateTime date){
    return getOperationTime().includes(date);
  }

  void setOperator(OperatorInput operator);

  OperatorInput getOperator();
}
