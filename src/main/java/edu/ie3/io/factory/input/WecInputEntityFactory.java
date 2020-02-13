/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.io.factory.SystemParticipantInputFactory;
import edu.ie3.io.factory.SystemParticipantTypedEntityData;
import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.WecInput;
import edu.ie3.models.input.system.type.WecTypeInput;

public class WecInputEntityFactory
    extends SystemParticipantInputFactory<
        WecInput, SystemParticipantTypedEntityData<WecTypeInput>> {
  private static final String MARKET_REACTION = "marketreaction";

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {MARKET_REACTION};
  }

  @Override
  protected WecInput buildModel(
      SystemParticipantTypedEntityData<WecTypeInput> data,
      java.util.UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      OperatorInput operatorInput,
      OperationTime operationTime) {
    WecTypeInput typeInput = data.getTypeInput();
    final boolean marketReaction =
        data.getField(MARKET_REACTION).trim().equals("1")
            || data.getField(MARKET_REACTION).trim().equals("true");

    return new WecInput(
        uuid, operationTime, operatorInput, id, node, qCharacteristics, typeInput, marketReaction);
  }

  @Override
  protected WecInput buildModel(
      SystemParticipantTypedEntityData<WecTypeInput> data,
      java.util.UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics) {
    WecTypeInput typeInput = data.getTypeInput();
    final boolean marketReaction =
        data.getField(MARKET_REACTION).trim().equals("1")
            || data.getField(MARKET_REACTION).trim().equals("true");

    return new WecInput(uuid, id, node, qCharacteristics, typeInput, marketReaction);
  }
}
