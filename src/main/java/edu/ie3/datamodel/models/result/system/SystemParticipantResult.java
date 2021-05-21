/*
 * © 2021. TU Dortmund University,
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

/** Abstract class that holds values common to all other result entities */
public abstract class SystemParticipantResult extends ResultEntity {

  /** active power output normally provided in MW */
  private ComparableQuantity<Power> p;

  /** reactive power output normally provided in MVAr */
  private ComparableQuantity<Power> q;

  /**
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   */
  public SystemParticipantResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q) {
    super(time, inputModel);
    this.p = p;
    this.q = q;
  }

  /**
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   */
  public SystemParticipantResult(
      UUID uuid,
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q) {
    super(uuid, time, inputModel);
    this.p = p;
    this.q = q;
  }

  /**
   * Active power output of the decentralised energy resource asset. Convention: Generated powers
   * are given in negative values.
   *
   * @return Active power output in MW.
   */
  public ComparableQuantity<Power> getP() {
    return p;
  }

  public void setP(ComparableQuantity<Power> p) {
    this.p = p;
  }

  /**
   * Reactive power output of the decentralised energy resource asset. Convention: Generated powers
   * are given in negative values.
   *
   * @return Reactive power output in MVAr.
   */
  public ComparableQuantity<Power> getQ() {
    return q;
  }

  public void setQ(ComparableQuantity<Power> q) {
    this.q = q;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SystemParticipantResult that = (SystemParticipantResult) o;
    return p.equals(that.p) && q.equals(that.q);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), p, q);
  }

  @Override
  public String toString() {
    return "SystemParticipantResult{"
        + "uuid="
        + getUuid()
        + ", time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", p="
        + p
        + ", q="
        + q
        + '}';
  }
}
