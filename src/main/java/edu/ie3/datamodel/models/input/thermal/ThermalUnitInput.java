/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.io.extractor.HasThermalBus;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Objects;
import java.util.UUID;

/** Abstract class for grouping all common properties to thermal models. */
public abstract class ThermalUnitInput extends AssetInput implements HasThermalBus {
  /** The thermal bus, a thermal unit is connected to. */
  private final ThermalBusInput thermalBus;

  /**
   * @param uuid Unique identifier of a certain thermal input
   * @param id Identifier of the thermal unit
   * @param thermalBus hermal bus, a thermal unit is connected to
   */
  ThermalUnitInput(UUID uuid, String id, ThermalBusInput thermalBus) {
    super(uuid, id);
    this.thermalBus = thermalBus;
  }

  /**
   * @param uuid Unique identifier of a certain thermal input
   * @param id Identifier of the thermal unit
   * @param operator operator of the asset
   * @param operationTime operation time of the asset
   * @param thermalBus thermal bus, a thermal unit is connected to
   */
  ThermalUnitInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      ThermalBusInput thermalBus) {
    super(uuid, id, operator, operationTime);
    this.thermalBus = thermalBus;
  }

  @Override
  public ThermalBusInput getThermalBus() {
    return thermalBus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ThermalUnitInput that = (ThermalUnitInput) o;
    return thermalBus.equals(that.thermalBus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), thermalBus);
  }

  @Override
  public String toString() {
    return "ThermalUnitInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", bus="
        + thermalBus.getUuid()
        + '}';
  }
}
