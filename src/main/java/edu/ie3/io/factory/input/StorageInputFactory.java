/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.io.factory.SystemParticipantInputEntityFactory;
import edu.ie3.io.factory.SystemParticipantTypedEntityData;
import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.StorageInput;
import edu.ie3.models.input.system.type.StorageTypeInput;
import java.util.UUID;

public class StorageInputFactory
    extends SystemParticipantInputEntityFactory<
        StorageInput, SystemParticipantTypedEntityData<StorageTypeInput>> {
  private static final String BEHAVIOR = "behavior";

  public StorageInputFactory() {
    super(StorageInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {BEHAVIOR};
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
    final String behavior = data.getField(BEHAVIOR);

    return new StorageInput(
        uuid, operationTime, operatorInput, id, node, qCharacteristics, typeInput, behavior);
  }

  @Override
  protected StorageInput buildModel(
      SystemParticipantTypedEntityData<StorageTypeInput> data,
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics) {
    final StorageTypeInput typeInput = data.getTypeInput();
    final String behavior = data.getField(BEHAVIOR);

    return new StorageInput(uuid, id, node, qCharacteristics, typeInput, behavior);
  }
}
