/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.system;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Abstract class that holds values common to result entities having heat result */
public class SystemParticipantWithHeatResult extends SystemParticipantResult {
  /** @param qDot thermal power output normally provided in MW */
  private ComparableQuantity<Power> qDot;

  /**
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   * @param qDot thermal power output normally provided in MW
   */
  public SystemParticipantWithHeatResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q,
      ComparableQuantity<Power> qDot) {
    super(time, inputModel, p, q);
    this.qDot = qDot;
  }

  /**
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   * @param qDot thermal power output normally provided in MW
   */
  public SystemParticipantWithHeatResult(
      UUID uuid,
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q,
      ComparableQuantity<Power> qDot) {
    super(uuid, time, inputModel, p, q);
    this.qDot = qDot;
  }

  /**
   * Thermal power output of the decentralised energy resource asset. Convention: Generated powers
   * are given in negative values.
   *
   * @return Thermal power output in MW.
   */
  public ComparableQuantity<Power> getQDot() {
    return qDot;
  }

  public void setQ(ComparableQuantity<Power> qDot) {
    this.qDot = qDot;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SystemParticipantWithHeatResult that = (SystemParticipantWithHeatResult) o;
    return qDot.equals(that.qDot);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), qDot);
  }

  @Override
  public String toString() {
    return "SystemParticipantWithHeatResult{"
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
        + ", qDot="
        + getQDot()
        + '}';
  }
}
