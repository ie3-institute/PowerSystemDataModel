/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.type.SystemParticipantTypeInput;
import java.util.Map;

public class SystemParticipantTypedEntityData<T extends SystemParticipantTypeInput>
    extends SystemParticipantEntityData {

  private final T typeInput;

  public SystemParticipantTypedEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      NodeInput node,
      T typeInput) {
    super(fieldsToAttributes, entityClass, node);
    this.typeInput = typeInput;
  }

  public SystemParticipantTypedEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operatorInput,
      NodeInput node,
      T typeInput) {
    super(fieldsToAttributes, entityClass, operatorInput, node);
    this.typeInput = typeInput;
  }

  public T getTypeInput() {
    return typeInput;
  }
}
