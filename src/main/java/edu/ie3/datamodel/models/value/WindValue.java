/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import java.util.Objects;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;

/** Describes wind as a pair of direction and velocity */
public class WindValue implements Value {
  /** Wind direction as an angle from north (typically in rad) */
  private Quantity<Angle> direction;
  /** Wind velocity (typically in m/s) */
  private Quantity<Speed> velocity;

  /**
   * @param direction Wind direction as an angle from north (typically in rad)
   * @param velocity Wind velocity (typically in m/s)
   */
  public WindValue(Quantity<Angle> direction, Quantity<Speed> velocity) {
    this.direction = direction.to(StandardUnits.WIND_DIRECTION);
    this.velocity = velocity.to(StandardUnits.WIND_VELOCITY);
  }

  public Quantity<Angle> getDirection() {
    return direction;
  }

  public void setDirection(Quantity<Angle> direction) {
    this.direction = direction.to(StandardUnits.WIND_DIRECTION);
  }

  public Quantity<Speed> getVelocity() {
    return velocity;
  }

  public void setVelocity(Quantity<Speed> velocity) {
    this.velocity = velocity.to(StandardUnits.WIND_VELOCITY);
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
