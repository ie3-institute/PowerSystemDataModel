/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.BmInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.BmTypeInput;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.UUID;
import tech.units.indriya.ComparableQuantity;

public class BmInputFactory
    extends SystemParticipantInputEntityFactory<
        BmInput, SystemParticipantTypedEntityData<BmTypeInput>> {
  private static final String MARKET_REACTION = "marketreaction";
  private static final String COST_CONTROLLED = "costcontrolled";
  private static final String FEED_IN_TARIFF = "feedintariff";

  public BmInputFactory() {
    super(BmInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {MARKET_REACTION, COST_CONTROLLED, FEED_IN_TARIFF};
  }

  @Override
  protected BmInput buildModel(
      SystemParticipantTypedEntityData<BmTypeInput> data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      UUID em,
      OperatorInput operator,
      OperationTime operationTime) {
    final BmTypeInput typeInput = data.getTypeInput();
    final boolean marketReaction = data.getBoolean(MARKET_REACTION);
    final boolean costControlled = data.getBoolean(COST_CONTROLLED);
    final ComparableQuantity<EnergyPrice> feedInTariff =
        data.getQuantity(FEED_IN_TARIFF, StandardUnits.ENERGY_PRICE);

    return new BmInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        qCharacteristics,
        em,
        typeInput,
        marketReaction,
        costControlled,
        feedInTariff);
  }
}
