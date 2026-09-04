/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.thermal;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Represents the results of Cylindrical Storage. */
public class CylindricalStorageResult extends AbstractThermalStorageResult {
  public CylindricalStorageResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Energy> energy,
      ComparableQuantity<Power> qDot,
      ComparableQuantity<Dimensionless> fillLevel) {
    super(time, inputModel, energy, qDot, fillLevel);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CylindricalStorageResult that)) return false;
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String toString() {
    return "CylindricalStorageResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", qDot="
        + getqDot()
        + ", energy="
        + getEnergy()
        + ", fillLevel="
        + getFillLevel()
        + "}";
  }
}
