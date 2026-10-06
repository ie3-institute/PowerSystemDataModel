/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.BmInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.BmTypeInput;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import tech.units.indriya.ComparableQuantity;

import java.util.Map;
import java.util.UUID;

/** Factory to create instances of {@link BmInput}s. */
public class BmInputFactory extends SystemParticipantInputEntityFactory<BmInput> {

  private final Map<UUID, BmTypeInput> types;

  public BmInputFactory(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, BmTypeInput> types) {
    super(operators, nodes, emUnits, BmInput.class);
    this.types = types;
  }

  @Override
  protected BmInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime,
      EmInput controllingEm) {
    boolean costControlled = getBoolean(data, COST_CONTROLLED);
    ComparableQuantity<EnergyPrice> feedInTariff = getQuantity(data, FEED_IN_TARIFF, StandardUnits.ENERGY_PRICE);

    return new BmInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        qCharacteristics,
        controllingEm,
        getEntity(data, TYPE, types),
        costControlled,
        feedInTariff,
        data);
  }
}
