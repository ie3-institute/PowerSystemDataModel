/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.StorageInput;
import edu.ie3.models.input.system.type.StorageTypeInput;
import java.util.UUID;

public class StorageInputFactory
    extends SystemParticipantInputEntityFactory<
        StorageInput, SystemParticipantTypedEntityData<StorageTypeInput>> {
  private static final String BEHAVIOUR = "behaviour";

  public StorageInputFactory() {
    super(StorageInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {BEHAVIOUR};
  }

  @Override
  protected StorageInput buildModel(
      SystemParticipantTypedEntityData<StorageTypeInput> data,
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      OperatorInput operatorInput,
      OperationTime operationTime) {
    final StorageTypeInput typeInput = data.getTypeInput();
    final String behaviour = data.getField(BEHAVIOUR);

    return new StorageInput(
        uuid, operationTime, operatorInput, id, node, qCharacteristics, typeInput, behaviour);
  }

  @Override
  protected StorageInput buildModel(
      SystemParticipantTypedEntityData<StorageTypeInput> data,
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics) {
    final StorageTypeInput typeInput = data.getTypeInput();
    final String behaviour = data.getField(BEHAVIOUR);

    return new StorageInput(uuid, id, node, qCharacteristics, typeInput, behaviour);
  }
}
