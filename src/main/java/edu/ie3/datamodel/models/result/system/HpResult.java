/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.system;

import edu.ie3.util.quantities.QuantityUtil;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Represents calculation results of a {@link edu.ie3.datamodel.models.input.system.HpInput} */
public class HpResult extends SystemParticipantResult {

  private final ComparableQuantity<Power> qDot;

  /**
   * Standard constructor with automatic uuid generation.
   *
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   * @param qDot provided head energy
   */
  public HpResult(
      ZonedDateTime timestamp,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q,
      ComparableQuantity<Power> qDot) {
    super(timestamp, inputModel, p, q);
    this.qDot = qDot;
  }

  /**
   * Standard constructor which allows uuid provision
   *
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   * @param qDot provided head energy
   */
  public HpResult(
      UUID uuid,
      ZonedDateTime timestamp,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q,
      ComparableQuantity<Power> qDot) {
    super(uuid, timestamp, inputModel, p, q);
    this.qDot = qDot;
  }

  public Quantity<Power> getqDot() {
    return qDot;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    HpResult hpResult = (HpResult) o;
    if (!QuantityUtil.quantityIsEmpty(qDot)) {
      if (QuantityUtil.quantityIsEmpty(hpResult.qDot)) return false;
      return qDot.isEquivalentTo(hpResult.qDot);
    } else return QuantityUtil.quantityIsEmpty(hpResult.qDot);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), qDot);
  }

  @Override
  public String toString() {
    return "HpResult{"
        + "uuid="
        + getUuid()
        + ", timestamp="
        + getTimestamp()
        + ", inputModel="
        + getInputModel()
        + ", p="
        + getP()
        + ", q="
        + getQ()
        + ", qDot="
        + qDot
        + '}';
  }
}
