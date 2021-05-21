/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.thermal;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.ResultEntity;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Representation of a result with regard to a thermal unit */
public abstract class ThermalUnitResult extends ResultEntity {

  /**
   * Average thermal power flowing into the thermal unit (+: Power flowing into unit, -: Power
   * flowing from unit)
   */
  private ComparableQuantity<Power> qDot;

  /**
   * Constructor for the thermal result with
   *
   * @param time The time, the result is related to
   * @param inputModel The input model's UUID, the result is related to
   * @param qDot Average thermal power exchanged with the unit
   */
  public ThermalUnitResult(ZonedDateTime time, UUID inputModel, ComparableQuantity<Power> qDot) {
    super(time, inputModel);
    this.qDot = qDot;
  }

  /**
   * Constructor for the thermal result with
   *
   * @param uuid The uuid of this result
   * @param time The time, the result is related to
   * @param inputModel The input model's UUID, the result is related to
   * @param qDot Average thermal power exchanged with the unit
   */
  public ThermalUnitResult(
      UUID uuid, ZonedDateTime time, UUID inputModel, ComparableQuantity<Power> qDot) {
    super(uuid, time, inputModel);
    this.qDot = qDot;
  }

  public ComparableQuantity<Power> getqDot() {
    return qDot;
  }

  public void setqDot(ComparableQuantity<Power> qDot) {
    this.qDot = qDot.to(StandardUnits.HEAT_DEMAND);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ThermalUnitResult that = (ThermalUnitResult) o;
    return qDot.equals(that.qDot);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), qDot);
  }

  @Override
  public String toString() {
    return "ThermalUnitResult{"
        + "uuid="
        + getUuid()
        + ", time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", qDot="
        + qDot
        + '}';
  }
}
