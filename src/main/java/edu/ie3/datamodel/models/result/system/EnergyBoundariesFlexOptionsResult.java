/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.system;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** An energy boundaries flexibility options result of a model (system participant or EM agent). */
public class EnergyBoundariesFlexOptionsResult extends FlexOptionsResult {
  /** State of energy of the model at the given point in simulation time. */
  private final ComparableQuantity<Energy> eState;

  /** Minimum state of energy of the model at the given point in simulation time. */
  private final ComparableQuantity<Energy> eMin;

  /** Maximum state of energy of the model at the given point in simulation time. */
  private final ComparableQuantity<Energy> eMax;

  /**
   * Standard constructor for a power limit flex options result entity.
   *
   * @param time Date and time when the result is produced.
   * @param inputModel The UUID of the input model that produces the result.
   * @param eState State of energy of the model at the given point in simulation time.
   * @param eMin Minimum state of energy of the model at the given point in simulation time.
   * @param eMax Maximum state of energy of the model at the given point in simulation time.
   * @param pMin The minimum active power of the model at the given point in simulation time.
   * @param pMax The maximum active power of the model at the given point in simulation time.
   */
  public EnergyBoundariesFlexOptionsResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Energy> eState,
      ComparableQuantity<Energy> eMin,
      ComparableQuantity<Energy> eMax,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax) {
    super(time, inputModel, pMin, pMax);
    this.eState = eState;
    this.eMin = eMin;
    this.eMax = eMax;
  }

  public ComparableQuantity<Energy> geteState() {
    return eState;
  }

  public ComparableQuantity<Energy> geteMin() {
    return eMin;
  }

  public ComparableQuantity<Energy> geteMax() {
    return eMax;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EnergyBoundariesFlexOptionsResult that)) return false;
    if (!super.equals(o)) return false;
    return eState.equals(that.eState) && eMin.equals(that.eMin) && eMax.equals(that.eMax);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), eState, eMin, eMax);
  }

  @Override
  public String toString() {
    return "EnergyBoundariesFlexOptionsResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", pMin="
        + getpMin()
        + ", pMax="
        + getpMax()
        + ", eState="
        + eState
        + ", eMin="
        + eMin
        + ", eMax="
        + eMax
        + "}";
  }
}
