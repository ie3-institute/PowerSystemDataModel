/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.models.OperationTime;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import java.util.Map;

public class SystemParticipantEntityData extends AssetEntityData {
  private final OperationTime operationTime;
  private final OperatorInput operatorInput;

  public SystemParticipantEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> clazz,
      NodeInput node,
      OperationTime operationTime,
      OperatorInput operatorInput) {
    super(fieldsToAttributes, clazz, node);
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
