/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.system;

import edu.ie3.datamodel.models.result.ResultEntity;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Abstract class that holds values common to all other result entities. */
public abstract class SystemParticipantResult extends ResultEntity {
  /** Active power output normally provided in MW. */
  private ComparableQuantity<Power> p;

  /** Reactive power output normally provided in MVAr. */
  private ComparableQuantity<Power> q;

  /**
   * Standard constructor for a system participant result.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   */
  protected SystemParticipantResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q) {
    super(time, inputModel);
    this.p = p;
    this.q = q;
  }

  public ComparableQuantity<Power> getP() {
    return p;
  }

  public ComparableQuantity<Power> getQ() {
    return q;
  }

  public void setP(ComparableQuantity<Power> p) {
    this.p = p;
  }

  public void setQ(ComparableQuantity<Power> q) {
    this.q = q;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SystemParticipantResult that)) return false;
    if (!super.equals(o)) return false;
    return p.equals(that.p) && q.equals(that.q);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), p, q);
  }

  @Override
  public String toString() {
    return "SystemParticipantResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", p="
        + p
        + ", q="
        + q
        + "}";
  }
}
