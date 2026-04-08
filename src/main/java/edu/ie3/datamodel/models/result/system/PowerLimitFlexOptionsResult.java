/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.system;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** A power limit flexibility options result of a model (system participant or EM agent). */
public class PowerLimitFlexOptionsResult extends FlexOptionsResult {

  /**
   * Active power (might be negative, thus feed-in) that was suggested for regular usage by the
   * model.
   */
  private final ComparableQuantity<Power> pRef;

  /**
   * Standard constructor for a power limit flex options result entity.
   *
   * @param time Date and time when the result is produced.
   * @param inputModel The UUID of the input model that produces the result.
   * @param pRef The active power that was suggested for regular usage by the model.
   */
  public PowerLimitFlexOptionsResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Power> pRef,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax) {
    super(time, inputModel, pMin, pMax);
    this.pRef = pRef;
  }

  public ComparableQuantity<Power> getpRef() {
    return pRef;
  }

  @Override
  public String toString() {
    return "PowerLimitFlexOptionsResult{"
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

    PowerLimitFlexOptionsResult that = (PowerLimitFlexOptionsResult) o;

    return pRef.equals(that.pRef);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), pRef);
  }
}
