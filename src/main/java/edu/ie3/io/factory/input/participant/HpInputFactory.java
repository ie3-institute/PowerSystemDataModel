/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input.participant;

import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.HpInput;
import java.util.UUID;

public class HpInputFactory
    extends SystemParticipantInputEntityFactory<HpInput, HpInputEntityData> {

  public HpInputFactory() {
    super(HpInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[0];
  }

  @Override
  protected HpInput buildModel(
      HpInputEntityData data,
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      OperatorInput operatorInput,
      OperationTime operationTime) {
    return new HpInput(
        uuid,
        operationTime,
        operatorInput,
        id,
        node,
        data.getThermalBusInput(),
        qCharacteristics,
        data.getTypeInput());
  }

  @Override
  protected HpInput buildModel(
      HpInputEntityData data, UUID uuid, String id, NodeInput node, String qCharacteristics) {
    return new HpInput(
        uuid, id, node, data.getThermalBusInput(), qCharacteristics, data.getTypeInput());
  }
}
