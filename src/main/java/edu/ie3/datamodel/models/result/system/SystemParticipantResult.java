/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.system;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.util.quantities.QuantityUtil;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Abstract class that holds values common to all other result entities */
public abstract class SystemParticipantResult extends ResultEntity {

  /**
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   */
  private ComparableQuantity<Power> p;

  private ComparableQuantity<Power> q;

  /**
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   */
  public SystemParticipantResult(
      ZonedDateTime timestamp,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q) {
    super(timestamp, inputModel);
    this.p = p;
    this.q = q;
  }

  /**
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   */
  public SystemParticipantResult(
      UUID uuid,
      ZonedDateTime timestamp,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q) {
    super(uuid, timestamp, inputModel);
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

    if (!QuantityUtil.quantityIsEmpty(p)) {
      if (QuantityUtil.quantityIsEmpty(that.p)) return false;
      if (!p.isEquivalentTo(that.p)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.p)) return false;

    if (!QuantityUtil.quantityIsEmpty(q)) {
      if (QuantityUtil.quantityIsEmpty(that.q)) return false;
      return q.isEquivalentTo(that.q);
    } else return QuantityUtil.quantityIsEmpty(that.q);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), p, q);
  }

  @Override
  public String toString() {
    return "SystemParticipantResult{" + "p=" + p + ", q=" + q + '}';
  }
}
