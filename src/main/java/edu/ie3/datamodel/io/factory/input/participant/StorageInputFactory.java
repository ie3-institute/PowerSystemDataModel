/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.StorageInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.StorageTypeInput;
import java.util.UUID;

public class StorageInputFactory
    extends SystemParticipantInputEntityFactory<
        StorageInput, SystemParticipantTypedEntityData<StorageTypeInput>> {

  public StorageInputFactory() {
    super(StorageInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[0];
  }

  @Override
  protected StorageInput buildModel(
      SystemParticipantTypedEntityData<StorageTypeInput> data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime) {
    final StorageTypeInput typeInput = data.getTypeInput();
    return new StorageInput(uuid, id, operator, operationTime, node, qCharacteristics, typeInput);
  }
}
