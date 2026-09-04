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
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Abstract class representing the common results of different types of thermal storages. */
public abstract class AbstractThermalStorageResult extends ThermalStorageResult {
  /** Fill level of the storage. */
  private final ComparableQuantity<Dimensionless> fillLevel;

  /**
   * Constructs the result with
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param energy Currently stored energy
   * @param qDot Heat power flowing into (&gt; 0) or coming from (&lt; 0) the storage
   * @param fillLevel Fill level of the storage
   */
  protected AbstractThermalStorageResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Energy> energy,
      ComparableQuantity<Power> qDot,
      ComparableQuantity<Dimensionless> fillLevel) {
    super(time, inputModel, energy, qDot);
    this.fillLevel = fillLevel;
  }

  public ComparableQuantity<Dimensionless> getFillLevel() {
    return fillLevel;
  }

  public void setFillLevel(ComparableQuantity<Dimensionless> fillLevel) {
    this.fillLevel = fillLevel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AbstractThermalStorageResult that)) return false;
    if (!super.equals(o)) return false;
    return QuantityUtils.equals(fillLevel, that.fillLevel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fillLevel);
  }

  @Override
  public String toString() {
    return "AbstractThermalStorageResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", qDot="
        + getqDot()
        + ", energy="
        + getEnergy()
        + ", fillLevel="
        + fillLevel
        + "}";
  }
}
