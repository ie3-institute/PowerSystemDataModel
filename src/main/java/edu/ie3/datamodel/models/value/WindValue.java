/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import java.util.Objects;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;
import tec.uom.se.ComparableQuantity;

/** Describes wind as a pair of direction and velocity */
public class WindValue implements Value {
  /** Wind direction as an angle from north (typically in rad) */
  private ComparableQuantity<Angle> direction; // TODO #65 Quantity replaced
  /** Wind velocity (typically in m/s) */
  private ComparableQuantity<Speed> velocity; // TODO #65 Quantity replaced

  /**
   * @param direction Wind direction as an angle from north (typically in rad)
   * @param velocity Wind velocity (typically in m/s)
   */
  public WindValue(
      ComparableQuantity<Angle> direction,
      ComparableQuantity<Speed> velocity) { // TODO #65 Quantity replaced
    this.direction = direction.to(StandardUnits.WIND_DIRECTION);
    this.velocity = velocity.to(StandardUnits.WIND_VELOCITY);
  }

  public ComparableQuantity<Angle> getDirection() {
    return direction;
  } // TODO #65 Quantity replaced

  public void setDirection(ComparableQuantity<Angle> direction) {
    this.direction = direction.to(StandardUnits.WIND_DIRECTION);
  } // TODO #65 Quantity replaced

  public ComparableQuantity<Speed> getVelocity() {
    return velocity;
  } // TODO #65 Quantity replaced

  public void setVelocity(ComparableQuantity<Speed> velocity) {
    this.velocity = velocity.to(StandardUnits.WIND_VELOCITY);
  } // TODO #65 Quantity replaced

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WindValue windValue = (WindValue) o;
    return Objects.equals(direction, windValue.direction)
        && Objects.equals(velocity, windValue.velocity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(direction, velocity);
  }

  @Override
  public String toString() {
    return "WindValue{" + "direction=" + direction + ", velocity=" + velocity + '}';
  }
}
