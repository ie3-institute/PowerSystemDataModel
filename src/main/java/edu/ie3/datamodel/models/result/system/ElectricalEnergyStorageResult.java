/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.system;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Abstract class that holds values common to result entities having heat result. */
public abstract class ElectricalEnergyStorageResult extends SystemParticipantResult {
  /** State of Charge (SoC) in %. */
  private ComparableQuantity<Dimensionless> soc;

  /**
   * Standard constructor for a system participant result.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   * @param soc state of Charge (SoC) in %
   */
  protected ElectricalEnergyStorageResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q,
      ComparableQuantity<Dimensionless> soc) {
    super(time, inputModel, p, q);
    this.soc = soc;
  }

  public ComparableQuantity<Dimensionless> getSoc() {
    return soc;
  }

  public void setSoc(ComparableQuantity<Dimensionless> soc) {
    this.soc = soc;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ElectricalEnergyStorageResult that)) return false;
    if (!super.equals(o)) return false;
    return soc.equals(that.soc);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), soc);
  }

  @Override
  public String toString() {
    return "ElectricalEnergyStorageResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", p="
        + getP()
        + ", q="
        + getQ()
        + ", soc="
        + soc
        + "}";
  }
}
