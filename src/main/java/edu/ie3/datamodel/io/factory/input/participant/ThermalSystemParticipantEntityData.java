/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.type.SystemParticipantTypeInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract base class for system participants that connect to thermal buses
 *
 * @param <T> The specific type input for the thermal system participant
 */
public abstract class ThermalSystemParticipantEntityData<T extends SystemParticipantTypeInput>
    extends SystemParticipantTypedEntityData<T> {

  private final ThermalBusInput thermalBusInput;

  protected ThermalSystemParticipantEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> targetClass,
      NodeInput node,
      EmInput em,
      T typeInput,
      ThermalBusInput thermalBusInput) {
    super(fieldsToAttributes, targetClass, node, em, typeInput);
    this.thermalBusInput = thermalBusInput;
  }

  protected ThermalSystemParticipantEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> targetClass,
      OperatorInput operator,
      NodeInput node,
      EmInput em,
      T typeInput,
      ThermalBusInput thermalBusInput) {
    super(fieldsToAttributes, targetClass, operator, node, em, typeInput);
    this.thermalBusInput = thermalBusInput;
  }

  /**
   * Creates a new ThermalSystemParticipantEntityData object based on a given {@link
   * SystemParticipantTypedEntityData} object and given thermal bus input
   *
   * @param entityData The SystemParticipantTypedEntityData object to enhance
   * @param thermalBusInput The thermal bus input
   */
  protected ThermalSystemParticipantEntityData(
      SystemParticipantTypedEntityData<T> entityData, ThermalBusInput thermalBusInput) {
    super(entityData, entityData.getTypeInput());
    this.thermalBusInput = thermalBusInput;
  }

  public ThermalBusInput getThermalBusInput() {
    return thermalBusInput;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ThermalSystemParticipantEntityData<?> that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(thermalBusInput, that.thermalBusInput);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), thermalBusInput);
  }

  protected String buildToStringContent(String className) {
    return className
        + "{"
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
