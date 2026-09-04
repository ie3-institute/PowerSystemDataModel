/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.thermal;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.utils.QuantityUtils;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Representation of a result with regard to a thermal unit. */
public abstract class ThermalUnitResult extends ResultEntity {
  /**
   * Average thermal power flowing into the thermal unit (+: Power flowing into unit, -: Power
   * flowing from unit)
   */
  private final ComparableQuantity<Power> qDot;

  /**
   * Constructor for the thermal result with
   *
   * @param time The time, the result is related to
   * @param inputModel The input model's UUID, the result is related to
   * @param qDot Average thermal power exchanged with the unit
   */
  protected ThermalUnitResult(ZonedDateTime time, UUID inputModel, ComparableQuantity<Power> qDot) {
    super(time, inputModel);
    this.qDot = qDot;
  }

  public ComparableQuantity<Power> getqDot() {
    return qDot;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ThermalUnitResult that)) return false;
    if (!super.equals(o)) return false;
    return QuantityUtils.equals(qDot, that.qDot);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), qDot);
  }

  @Override
  public String toString() {
    return "ThermalUnitResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", qDot="
        + qDot
        + "}";
  }
}
