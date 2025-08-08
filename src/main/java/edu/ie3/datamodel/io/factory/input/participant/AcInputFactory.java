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
import java.util.UUID;

public class AcInputFactory
    extends ThermalSystemParticipantInputFactory<AcInput, AcInputEntityData> {

  public AcInputFactory() {
    super(AcInput.class);
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
      Object typeInput) {

    return new AcInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        thermalBusInput,
        qCharacteristics,
        em,
        (AcTypeInput) typeInput);
  }
}
