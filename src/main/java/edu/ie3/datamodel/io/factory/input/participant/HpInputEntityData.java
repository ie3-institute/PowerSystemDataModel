/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.HpInput;
import edu.ie3.datamodel.models.input.system.type.HpTypeInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import java.util.Map;

public class HpInputEntityData extends ThermalSystemParticipantEntityData<HpTypeInput> {

  public HpInputEntityData(
      Map<String, String> fieldsToAttributes,
      NodeInput node,
      EmInput em,
      HpTypeInput typeInput,
      ThermalBusInput thermalBusInput) {
    super(fieldsToAttributes, HpInput.class, node, em, typeInput, thermalBusInput);
  }

  public HpInputEntityData(
      Map<String, String> fieldsToAttributes,
      OperatorInput operator,
      NodeInput node,
      EmInput em,
      HpTypeInput typeInput,
      ThermalBusInput thermalBusInput) {
    super(fieldsToAttributes, HpInput.class, operator, node, em, typeInput, thermalBusInput);
  }

  /**
   * Creates a new HpInputEntityData object based on a given {@link
   * SystemParticipantTypedEntityData} object and given thermal bus input
   *
   * @param entityData The SystemParticipantTypedEntityData object to enhance
   * @param thermalBusInput The thermal bus input
   */
  public HpInputEntityData(
      SystemParticipantTypedEntityData<HpTypeInput> entityData, ThermalBusInput thermalBusInput) {
    super(entityData, thermalBusInput);
  }

  @Override
  public String toString() {
    return buildToStringContent("HpInputEntityData");
  }
}
