/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.WecInput;
import edu.ie3.models.input.system.type.WecTypeInput;
import java.util.UUID;

public class WecInputFactory
    extends SystemParticipantInputEntityFactory<
        WecInput, SystemParticipantTypedEntityData<WecTypeInput>> {
  private static final String MARKET_REACTION = "marketreaction";

  public WecInputFactory() {
    super(WecInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {MARKET_REACTION};
  }

  @Override
  protected WecInput buildModel(
      SystemParticipantTypedEntityData<WecTypeInput> data,
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      OperatorInput operatorInput,
      OperationTime operationTime) {
    WecTypeInput typeInput = data.getTypeInput();
    final boolean marketReaction = data.getBoolean(MARKET_REACTION);

    return new WecInput(
        uuid, operationTime, operatorInput, id, node, qCharacteristics, typeInput, marketReaction);
  }

  @Override
  protected WecInput buildModel(
      SystemParticipantTypedEntityData<WecTypeInput> data,
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics) {
    WecTypeInput typeInput = data.getTypeInput();
    final boolean marketReaction = data.getBoolean(MARKET_REACTION);

    return new WecInput(uuid, id, node, qCharacteristics, typeInput, marketReaction);
  }
}
