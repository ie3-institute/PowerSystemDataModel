/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.thermal;

import edu.ie3.datamodel.utils.QuantityUtils;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Represents calculation results of a {@link
 * edu.ie3.datamodel.models.input.thermal.ThermalStorageInput}.
 */
public abstract class ThermalStorageResult extends ThermalSinkResult {
  /** Currently stored energy. */
  private final ComparableQuantity<Energy> energy;

  /**
   * Constructs the result with
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param energy Currently stored energy
   * @param qDot Heat power flowing into (&gt; 0) or coming from (&lt; 0) the storage
   */
  protected ThermalStorageResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Energy> energy,
      ComparableQuantity<Power> qDot) {
    super(time, inputModel, qDot);
    this.energy = energy;
  }

  public ComparableQuantity<Energy> getEnergy() {
    return energy;
  }

  public void setEnergy(ComparableQuantity<Energy> energy) {
    this.energy = energy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ThermalStorageResult that)) return false;
    if (!super.equals(o)) return false;
    return QuantityUtils.equals(energy, that.energy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), energy);
  }

  @Override
  public String toString() {
    return "ThermalStorageResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", qDot="
        + getqDot()
        + ", energy="
        + energy
        + "}";
  }
}
