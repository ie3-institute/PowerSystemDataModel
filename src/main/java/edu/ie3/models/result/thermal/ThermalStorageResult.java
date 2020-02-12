/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.result.thermal;

import edu.ie3.models.StandardUnits;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

/** Represents calculation results of {@link edu.ie3.models.input.thermal.ThermalStorageInput} */
public abstract class ThermalStorageResult extends ThermalUnitResult {
  /** Currently stored energy */
  private Quantity<Energy> energy;

  /**
   * Constructs the result with
   *
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param energy Currently stored energy
   * @param qDot Heat power flowing into (> 0) or coming from (< 0) the storage
   */
  public ThermalStorageResult(
      ZonedDateTime timestamp, UUID inputModel, Quantity<Energy> energy, Quantity<Power> qDot) {
    super(timestamp, inputModel, qDot);
    this.energy = energy.to(StandardUnits.ENERGY_RESULT);
  }

  /**
   * Constructs the result with
   *
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param energy Currently stored energy
   * @param qDot Heat power flowing into (> 0) or coming from (< 0) the storage
   */
  public ThermalStorageResult(
      UUID uuid,
      ZonedDateTime timestamp,
      UUID inputModel,
      Quantity<Energy> energy,
      Quantity<Power> qDot) {
    super(uuid, timestamp, inputModel, qDot);
    this.energy = energy.to(StandardUnits.ENERGY_RESULT);
  }

  public Quantity<Energy> getEnergy() {
    return energy;
  }

  public void setEnergy(Quantity<Energy> energy) {
    this.energy = energy.to(StandardUnits.ENERGY_RESULT);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ThermalStorageResult that = (ThermalStorageResult) o;
    return energy.equals(that.energy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), energy);
  }
}
