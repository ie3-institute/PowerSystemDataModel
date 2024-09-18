/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.thermal;

import edu.ie3.datamodel.models.StandardUnits;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Abstract class representing the common results of different types of thermal storages */
public abstract class AbstractThermalStorageResult extends ThermalStorageResult {
  /** Fill level of the storage */
  private ComparableQuantity<Dimensionless> fillLevel;

  /**
   * Constructs the result with
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param energy Currently stored energy
   * @param qDot Heat power flowing into (&gt; 0) or coming from (&lt; 0) the storage
   * @param fillLevel Fill level of the storage
   */
  public AbstractThermalStorageResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Energy> energy,
      ComparableQuantity<Power> qDot,
      ComparableQuantity<Dimensionless> fillLevel) {
    super(time, inputModel, energy, qDot);
    this.fillLevel = fillLevel.to(StandardUnits.FILL_LEVEL);
  }

  public ComparableQuantity<Dimensionless> getFillLevel() {
    return fillLevel;
  }

  public void setFillLevel(ComparableQuantity<Dimensionless> fillLevel) {
    this.fillLevel = fillLevel.to(StandardUnits.FILL_LEVEL);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    AbstractThermalStorageResult that = (AbstractThermalStorageResult) o;
    return fillLevel.equals(that.fillLevel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fillLevel);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName()
        + "{"
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
        + '}';
  }
}
