/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Objects;
import java.util.UUID;

/** Common properties to all thermal storage devices. */
public abstract class ThermalStorageInput extends ThermalUnitInput {
  /**
   * @param uuid Unique identifier of a certain thermal storage input model
   * @param id Identifier of the thermal unit
   * @param thermalBus Thermal bus, a thermal unit is connected to
   */
  protected ThermalStorageInput(UUID uuid, String id, ThermalBusInput thermalBus) {
    super(uuid, id, thermalBus);
  }

  /**
   * @param uuid Unique identifier of a certain thermal storage input model
   * @param id Identifier of the thermal unit
   * @param operator operator of the asset
   * @param operationTime operation time of the asset
   * @param thermalBus Thermal bus, a thermal unit is connected to
   */
  protected ThermalStorageInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      ThermalBusInput thermalBus) {
    super(uuid, id, operator, operationTime, thermalBus);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ThermalStorageInput that)) return false;
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String toString() {
    return "ThermalStorageInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", thermalBus="
        + getThermalBus().getUuid()
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public abstract ThermalStorageInputCopyBuilder<?> copy();

  public abstract static class ThermalStorageInputCopyBuilder<
          B extends ThermalStorageInputCopyBuilder<B>>
      extends ThermalUnitInputCopyBuilder<B> {
    protected ThermalStorageInputCopyBuilder(ThermalStorageInput entity) {
      super(entity);
    }

    @Override
    public abstract ThermalStorageInput build();

    @Override
    protected abstract B thisInstance();
  }
}
