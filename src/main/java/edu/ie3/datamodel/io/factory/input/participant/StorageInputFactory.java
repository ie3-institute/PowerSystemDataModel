/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.StorageInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.StorageTypeInput;
import java.util.Map;
import java.util.UUID;

/** Factory to create instances of {@link StorageInput}s. */
public class StorageInputFactory extends SystemParticipantInputEntityFactory<StorageInput> {

  private final Map<UUID, StorageTypeInput> types;

  public StorageInputFactory(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, StorageTypeInput> types) {
    super(operators, nodes, emUnits, StorageInput.class);
    this.types = types;
  }

  @Override
  protected StorageInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime,
      EmInput controllingEm) {
    return new StorageInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        qCharacteristics,
        controllingEm,
        getEntity(data, TYPE, types),
        data);
  }
}
