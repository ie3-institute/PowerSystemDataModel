/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.QuantityUtil;
import java.util.Objects;
import java.util.Optional;
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
    this.direction = direction == null ? null : direction.to(StandardUnits.WIND_DIRECTION);
    this.velocity = velocity == null ? null : velocity.to(StandardUnits.WIND_VELOCITY);
  }

  public Optional<ComparableQuantity<Angle>> getDirection() {
    return Optional.ofNullable(direction);
  }

  public Optional<ComparableQuantity<Speed>> getVelocity() {
    return Optional.ofNullable(velocity);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WindValue windValue = (WindValue) o;
    if (!QuantityUtil.quantityIsEmpty(direction)) {
      if (QuantityUtil.quantityIsEmpty(windValue.direction)) return false;
      if (!direction.isEquivalentTo(windValue.direction)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(windValue.direction)) return false;
    if (!QuantityUtil.quantityIsEmpty(velocity)) {
      if (QuantityUtil.quantityIsEmpty(windValue.velocity)) return false;
      return velocity.isEquivalentTo(windValue.velocity);
    } else return QuantityUtil.quantityIsEmpty(windValue.velocity);
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
