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
import edu.ie3.datamodel.models.input.system.AcInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.AcTypeInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import java.util.Map;
import java.util.UUID;

/** Factory to create instances of {@link AcInput}s. */
public class AcInputFactory extends ThermalSystemParticipantInputFactory<AcInput, AcTypeInput> {

  public AcInputFactory(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, AcTypeInput> types,
      Map<UUID, ThermalBusInput> thermalBuses) {
    super(operators, nodes, emUnits, types, thermalBuses, AcInput.class);
  }

  @Override
  protected AcInput createThermalSystemModel(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ThermalBusInput thermalBusInput,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      AcTypeInput typeInput,
      Map<String, String> additionalInformation) {

    return new AcInput(
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
