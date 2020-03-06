/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input.participant;

import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.HpInput;
import edu.ie3.models.input.system.type.HpTypeInput;
import edu.ie3.models.input.thermal.ThermalBusInput;
import java.util.Map;

public class HpInputEntityData extends SystemParticipantTypedEntityData<HpTypeInput> {
  private final ThermalBusInput thermalBusInput;

  public HpInputEntityData(
      Map<String, String> fieldsToAttributes,
      NodeInput node,
      HpTypeInput typeInput,
      ThermalBusInput thermalBusInput) {
    super(fieldsToAttributes, HpInput.class, node, typeInput);
    this.thermalBusInput = thermalBusInput;
  }

  public HpInputEntityData(
      Map<String, String> fieldsToAttributes,
      OperatorInput operatorInput,
      NodeInput node,
      HpTypeInput typeInput,
      ThermalBusInput thermalBusInput) {
    super(fieldsToAttributes, HpInput.class, operatorInput, node, typeInput);
    this.thermalBusInput = thermalBusInput;
  }

  public ThermalBusInput getThermalBusInput() {
    return thermalBusInput;
  }
}
