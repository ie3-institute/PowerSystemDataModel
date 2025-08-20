/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.ChpInput;
import edu.ie3.datamodel.models.input.system.type.ChpTypeInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import java.util.Map;
import java.util.Objects;

/** The type Chp input entity data. */
public class ChpInputEntityData extends SystemParticipantTypedEntityData<ChpTypeInput> {
  private final ThermalBusInput thermalBusInput;
  private final ThermalStorageInput thermalStorageInput;

  /**
   * Instantiates a new Chp input entity data.
   *
   * @param fieldsToAttributes the fields to attributes
   * @param node the node
   * @param em the em
   * @param typeInput the type input
   * @param thermalBusInput the thermal bus input
   * @param thermalStorageInput the thermal storage input
   */
  public ChpInputEntityData(
      Map<String, String> fieldsToAttributes,
      NodeInput node,
      EmInput em,
      ChpTypeInput typeInput,
      ThermalBusInput thermalBusInput,
      ThermalStorageInput thermalStorageInput) {
    super(fieldsToAttributes, ChpInput.class, node, em, typeInput);
    this.thermalBusInput = thermalBusInput;
    this.thermalStorageInput = thermalStorageInput;
  }

  /**
   * Instantiates a new Chp input entity data.
   *
   * @param fieldsToAttributes the fields to attributes
   * @param operator the operator
   * @param node the node
   * @param em the em
   * @param typeInput the type input
   * @param thermalBusInput the thermal bus input
   * @param thermalStorageInput the thermal storage input
   */
  public ChpInputEntityData(
      Map<String, String> fieldsToAttributes,
      OperatorInput operator,
      NodeInput node,
      EmInput em,
      ChpTypeInput typeInput,
      ThermalBusInput thermalBusInput,
      ThermalStorageInput thermalStorageInput) {
    super(fieldsToAttributes, ChpInput.class, operator, node, em, typeInput);
    this.thermalBusInput = thermalBusInput;
    this.thermalStorageInput = thermalStorageInput;
  }

  /**
   * Creates a new ChpInputEntityData object based on a given {@link
   * SystemParticipantTypedEntityData}* object and a thermal bus and storage input
   *
   * @param entityData The SystemParticipantTypedEntityData object to enhance
   * @param thermalBusInput The thermal bus input
   * @param thermalStorageInput The thermal storage input
   */
  public ChpInputEntityData(
      SystemParticipantTypedEntityData<ChpTypeInput> entityData,
      ThermalBusInput thermalBusInput,
      ThermalStorageInput thermalStorageInput) {
    super(entityData, entityData.getTypeInput());
    this.thermalBusInput = thermalBusInput;
    this.thermalStorageInput = thermalStorageInput;
  }

  /**
   * Gets thermal bus input.
   *
   * @return the thermal bus input
   */
  public ThermalBusInput getThermalBusInput() {
    return thermalBusInput;
  }

  /**
   * Gets thermal storage input.
   *
   * @return the thermal storage input
   */
  public ThermalStorageInput getThermalStorageInput() {
    return thermalStorageInput;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChpInputEntityData that)) return false;
    if (!super.equals(o)) return false;
    return thermalBusInput.equals(that.thermalBusInput)
        && thermalStorageInput.equals(that.thermalStorageInput);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), thermalBusInput, thermalStorageInput);
  }

  @Override
  public String toString() {
    return "ChpInputEntityData{"
        + "thermalBusInput="
        + thermalBusInput.getUuid()
        + ", thermalStorageInput="
        + thermalStorageInput.getUuid()
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
