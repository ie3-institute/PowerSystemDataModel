/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.models.input.AssetInput;
import java.util.Objects;
import java.util.UUID;

/** Abstract class for grouping all common properties to thermal models. */
public abstract class ThermalUnitInput extends AssetInput {
  /** The thermal bus, a thermal unit is connected to. */
  private final ThermalBusInput bus;

  /**
   * @param uuid Unique identifier of a certain thermal input
   * @param id Identifier of the thermal unit
   * @param bus hermal bus, a thermal unit is connected to
   */
  ThermalUnitInput(UUID uuid, String id, ThermalBusInput bus) {
    super(uuid, id);
    this.bus = bus;
  }

  public ThermalBusInput getBus() {
    return bus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ThermalUnitInput that = (ThermalUnitInput) o;
    return bus.equals(that.bus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), bus);
  }

  @Override
  public String toString() {
    return "ThermalUnitInput{" + "bus=" + bus + '}';
  }
}
