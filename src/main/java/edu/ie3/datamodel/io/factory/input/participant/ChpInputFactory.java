/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.ChpInput;
import java.util.UUID;

public class ChpInputFactory
    extends SystemParticipantInputEntityFactory<ChpInput, ChpInputEntityData> {
  private static final String MARKET_REACTION = "marketreaction";

  public ChpInputFactory() {
    super(ChpInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {MARKET_REACTION};
  }

  @Override
  protected ChpInput buildModel(
      ChpInputEntityData data,
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      OperatorInput operatorInput,
      OperationTime operationTime) {
    final boolean marketReaction = data.getBoolean(MARKET_REACTION);

    return new ChpInput(
        uuid,
        operationTime,
        operatorInput,
        id,
        node,
        data.getThermalBusInput(),
        qCharacteristics,
        data.getTypeInput(),
        data.getThermalStorageInput(),
        marketReaction);
  }
}
