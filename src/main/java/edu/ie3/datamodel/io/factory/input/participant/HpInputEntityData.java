/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.HpInput;
import edu.ie3.datamodel.models.input.system.type.HpTypeInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import java.util.Objects;

public class HpInputEntityData extends SystemParticipantTypedEntityData<HpTypeInput> {
  private final ThermalBusInput thermalBusInput;

  public HpInputEntityData(
      MapWithRowIndex mapWithRowIndex,
      NodeInput node,
      HpTypeInput typeInput,
      ThermalBusInput thermalBusInput) {
    super(mapWithRowIndex, HpInput.class, node, typeInput);
    this.thermalBusInput = thermalBusInput;
  }

  public HpInputEntityData(
      MapWithRowIndex mapWithRowIndex,
      OperatorInput operator,
      NodeInput node,
      HpTypeInput typeInput,
      ThermalBusInput thermalBusInput) {
    super(mapWithRowIndex, HpInput.class, operator, node, typeInput);
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
