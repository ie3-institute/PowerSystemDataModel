/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.system;

import edu.ie3.datamodel.models.StandardUnits;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Represents everything, that is capable of storing electric energy */
public abstract class ElectricalEnergyStorageResult extends SystemParticipantResult {

  /** State of Charge (SoC) in % */
  private final ComparableQuantity<Dimensionless> soc;

  protected ElectricalEnergyStorageResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q,
      ComparableQuantity<Dimensionless> soc) {
    super(time, inputModel, p, q);
    this.soc = soc.to(StandardUnits.SOC);
  }

  protected ElectricalEnergyStorageResult(
      UUID uuid,
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q,
      ComparableQuantity<Dimensionless> soc) {
    super(uuid, time, inputModel, p, q);
    this.soc = soc.to(StandardUnits.SOC);
  }

  public ComparableQuantity<Dimensionless> getSoc() {
    return soc;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ElectricalEnergyStorageResult)) return false;
    if (!super.equals(o)) return false;
    ElectricalEnergyStorageResult that = (ElectricalEnergyStorageResult) o;
    return soc.equals(that.soc);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), soc);
  }

  @Override
  public String toString() {
    return "ElectricalEnergyStorageResult{"
        + "uuid="
        + getUuid()
        + ", time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", p="
        + getP()
        + ", q="
        + getQ()
        + "soc="
        + soc
        + '}';
  }
}
