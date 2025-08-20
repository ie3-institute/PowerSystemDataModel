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
import edu.ie3.datamodel.models.input.system.ChpInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import java.util.UUID;

public class ChpInputFactory
    extends SystemParticipantInputEntityFactory<ChpInput, ChpInputEntityData> {
  private static final String THERMAL_BUS = "thermalBus";
  private static final String TYPE = "type";
  private static final String THERMAL_STORAGE = "thermalStorage";
  private static final String MARKET_REACTION = "marketReaction";

  public ChpInputFactory() {
    super(ChpInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {THERMAL_BUS, TYPE, THERMAL_STORAGE, MARKET_REACTION};
  }

  @Override
  protected ChpInput buildModel(
      ChpInputEntityData data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime) {
    final EmInput em = data.getControllingEm().orElse(null);
    final boolean marketReaction = data.getBoolean(MARKET_REACTION);

    return new ChpInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        data.getThermalBusInput(),
        qCharacteristics,
        em,
        data.getTypeInput(),
        data.getThermalStorageInput(),
        marketReaction);
  }
}
