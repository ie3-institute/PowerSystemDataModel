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

/** A result depicting flexibility options of a model (system participant or EM agent). */
public abstract class FlexOptionsResult extends ResultEntity {
  /**
   * Minimum active power to which the operating point of the model can be reduced (might be
   * negative, thus feed-in) at the given point in simulation time. This equates to the lower bound
   * of possible power flexibility provision.
   */
  private final ComparableQuantity<Power> pMin;

  /**
   * Maximum active power to which the operating point of the model can be increased (might be
   * negative, thus feed-in) at the given point in simulation time. This equates to upper bound of
   * possible power flexibility provision.
   */
  private final ComparableQuantity<Power> pMax;

  /**
   * Standard constructor for a flex options result entity.
   *
   * @param time Date and time when the result is produced.
   * @param inputModel The UUID of the input model that produces the result.
   * @param pMin The minimum active power of the model at the given point in simulation time.
   * @param pMax The maximum active power of the model at the given point in simulation time.
   */
  protected FlexOptionsResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax) {
    super(time, inputModel);
    this.pMin = pMin;
    this.pMax = pMax;
  }

  public ComparableQuantity<Power> getpMin() {
    return pMin;
  }

  public ComparableQuantity<Power> getpMax() {
    return pMax;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FlexOptionsResult that)) return false;
    if (!super.equals(o)) return false;
    return pMin.equals(that.pMin) && pMax.equals(that.pMax);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), pMin, pMax);
  }

  @Override
  public String toString() {
    return "FlexOptionsResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", pMin="
        + pMin
        + ", pMax="
        + pMax
        + "}";
  }
}
