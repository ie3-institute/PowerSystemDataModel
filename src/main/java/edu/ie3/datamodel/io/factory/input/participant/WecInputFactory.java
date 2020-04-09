/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.WecInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.WecTypeInput;
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
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime) {
    WecTypeInput typeInput = data.getTypeInput();
    final boolean marketReaction = data.getBoolean(MARKET_REACTION);

    return new WecInput(
        uuid, id, operator, operationTime, node, qCharacteristics, typeInput, marketReaction);
  }
}
