/*
 * © 2022. TU Dortmund University,
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

/** Represents results of flexibility request */
public class FlexOptionsResult extends ResultEntity {

  /**
   * Active power (might be negative, thus feed-in) that was suggested for regular usage by the
   * system participant connected to the EmAgent
   */
  private final ComparableQuantity<Power> pRef;

  /**
   * Minimal active power to which the system participant can be reduced (might be negative, thus
   * feed-in) that was determined by the system. Therefore equates to lower bound of possible
   * flexibility provision. participant connected to the EmAgent
   */
  private final ComparableQuantity<Power> pMin;

  /**
   * Maximum active power to which the system participant can be increased (might be negative, thus
   * feed-in) that was determined by the system. Therefore equates to upper bound of possible
   * flexibility provision. participant connected to the EmAgent
   */
  private final ComparableQuantity<Power> pMax;

  /**
   * Standard constructor with automatic uuid generation.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param pRef active power that was suggested for regular usage by the system participant
   * @param pMin active minimal power that was determined by the system participant
   * @param pMax active maximum power that was determined by the system participant
   */
  public FlexOptionsResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Power> pRef,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax) {
    super(time, inputModel);
    this.pRef = pRef;
    this.pMin = pMin;
    this.pMax = pMax;
  }

  public ComparableQuantity<Power> getpRef() {
    return pRef;
  }

  public ComparableQuantity<Power> getpMin() {
    return pMin;
  }

  public ComparableQuantity<Power> getpMax() {
    return pMax;
  }

  @Override
  public String toString() {
    return "FlexOptionsResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", pRef="
        + getpRef()
        + ", pMin="
        + getpMin()
        + ", pMax="
        + getpMax()
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    FlexOptionsResult that = (FlexOptionsResult) o;

    if (!pRef.equals(that.pRef)) return false;
    if (!pMin.equals(that.pMin)) return false;
    return pMax.equals(that.pMax);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), pRef, pMin, pMax);
  }
}
