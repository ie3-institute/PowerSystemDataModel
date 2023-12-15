/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.EmInput;
import edu.ie3.datamodel.models.input.system.HpInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
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
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime) {
    final EmInput em = data.getEm().orElse(null);

    return new HpInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        data.getThermalBusInput(),
        qCharacteristics,
        em,
        data.getTypeInput());
  }
}
