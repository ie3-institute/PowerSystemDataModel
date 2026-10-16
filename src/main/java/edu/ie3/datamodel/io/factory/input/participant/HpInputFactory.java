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
import edu.ie3.datamodel.models.input.system.HpInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.HpTypeInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import java.util.Map;
import java.util.UUID;

/** Factory to create instances of {@link HpInput}s. */
public class HpInputFactory extends ThermalSystemParticipantInputFactory<HpInput, HpTypeInput> {

  public HpInputFactory(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, HpTypeInput> types,
      Map<UUID, ThermalBusInput> thermalBuses) {
    super(operators, nodes, emUnits, types, thermalBuses, HpInput.class);
  }

  @Override
  protected HpInput createThermalSystemModel(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ThermalBusInput thermalBusInput,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      HpTypeInput typeInput,
      Map<String, String> additionalInformation) {

    return new HpInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        thermalBusInput,
        qCharacteristics,
        em,
        typeInput,
        additionalInformation);
  }
}
