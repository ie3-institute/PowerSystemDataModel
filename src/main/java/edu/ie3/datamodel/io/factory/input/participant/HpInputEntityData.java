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
import java.util.Objects;

public class HpInputEntityData extends SystemParticipantTypedEntityData<HpTypeInput> {
  private final ThermalBusInput thermalBusInput;

  public HpInputEntityData(
      Map<String, String> fieldsToAttributes,
      NodeInput node,
      EmInput em,
      HpTypeInput typeInput,
      ThermalBusInput thermalBusInput) {
    super(fieldsToAttributes, HpInput.class, node, em, typeInput);
    this.thermalBusInput = thermalBusInput;
  }

  public HpInputEntityData(
      Map<String, String> fieldsToAttributes,
      OperatorInput operator,
      NodeInput node,
      EmInput em,
      HpTypeInput typeInput,
      ThermalBusInput thermalBusInput) {
    super(fieldsToAttributes, HpInput.class, operator, node, em, typeInput);
    this.thermalBusInput = thermalBusInput;
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
    super(entityData, entityData.getTypeInput());
    this.thermalBusInput = thermalBusInput;
  }

  public ThermalBusInput getThermalBusInput() {
    return thermalBusInput;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof HpInputEntityData that)) return false;
    if (!super.equals(o)) return false;
    return thermalBusInput.equals(that.thermalBusInput);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), thermalBusInput);
  }

  @Override
  public String toString() {
    return "HpInputEntityData{"
        + "thermalBusInput="
        + thermalBusInput.getUuid()
        + ", typeInput="
        + getTypeInput().getUuid()
        + ", node="
        + getNode().getUuid()
        + ", operatorInput="
        + getOperatorInput().getUuid()
        + ", fieldsToValues="
        + getFieldsToValues()
        + ", targetClass="
        + getTargetClass()
        + '}';
  }
}
