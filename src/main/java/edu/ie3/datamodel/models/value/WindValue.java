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
import tech.units.indriya.ComparableQuantity;

/** Describes wind as a pair of direction and velocity */
public class WindValue implements Value {
  /** Wind direction as an angle from north (typically in rad) */
  private final ComparableQuantity<Angle> direction;
  /** Wind velocity (typically in m/s) */
  private final ComparableQuantity<Speed> velocity;

  /**
   * @param direction Wind direction as an angle from north (typically in rad)
   * @param velocity Wind velocity (typically in m/s)
   */
  public WindValue(ComparableQuantity<Angle> direction, ComparableQuantity<Speed> velocity) {
    this.direction = direction.to(StandardUnits.WIND_DIRECTION);
    this.velocity = velocity.to(StandardUnits.WIND_VELOCITY);
  }

  public ComparableQuantity<Angle> getDirection() {
    return direction;
  }

  public ComparableQuantity<Speed> getVelocity() {
    return velocity;
  }

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
