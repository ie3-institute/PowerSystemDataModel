/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.system;

import edu.ie3.datamodel.models.result.ResultEntity;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Power;

/** Abstract class that holds values common to all other result entities */
public abstract class SystemParticipantResult extends ResultEntity {

  /**
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   */
  private Quantity<Power> p;

  private Quantity<Power> q;

  /**
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   */
  public SystemParticipantResult(
      ZonedDateTime timestamp, UUID inputModel, Quantity<Power> p, Quantity<Power> q) {
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
      UUID uuid, ZonedDateTime timestamp, UUID inputModel, Quantity<Power> p, Quantity<Power> q) {
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
  public Quantity<Power> getP() {
    return p;
  }

  public void setP(Quantity<Power> p) {
    this.p = p;
  }

  /**
   * Reactive power output of the decentralised energy resource asset. Convention: Generated powers
   * are given in negative values.
   *
   * @return Reactive power output in MVAr.
   */
  public Quantity<Power> getQ() {
    return q;
  }

  public void setQ(Quantity<Power> q) {
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
}
