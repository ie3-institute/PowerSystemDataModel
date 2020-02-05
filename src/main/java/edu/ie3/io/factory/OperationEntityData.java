/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.models.OperationTime;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.OperatorInput;
import java.util.Map;

public class OperationEntityData extends EntityData {

  private final OperationTime operationTime;
  private final OperatorInput operatorInput;

  public OperationEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> clazz,
      OperationTime operationTime,
      OperatorInput operatorInput) {
    super(fieldsToAttributes, clazz);
    this.operationTime = operationTime;
    this.operatorInput = operatorInput;
  }

  public OperationTime getOperationTime() {
    return operationTime;
  }

  public OperatorInput getOperatorInput() {
    return operatorInput;
  }
}
