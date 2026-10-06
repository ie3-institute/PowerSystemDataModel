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
import edu.ie3.datamodel.models.input.system.EvInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.EvTypeInput;
import java.util.Map;
import java.util.UUID;

/** Factory to create instances of {@link EvInput}s. */
public class EvInputFactory extends SystemParticipantInputEntityFactory<EvInput> {

  private final Map<UUID, EvTypeInput> types;

  public EvInputFactory(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, EvTypeInput> types) {
    super(operators, nodes, emUnits, EvInput.class);
    this.types = types;
  }

  @Override
  protected EvInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime,
      EmInput controllingEm) {
    return new EvInput(
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
