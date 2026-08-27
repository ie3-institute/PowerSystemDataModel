/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import java.util.Objects;
import java.util.Optional;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import tech.units.indriya.ComparableQuantity;

/** Describes a voltage value as a pair of magnitude and angle. */
public class VoltageValue implements Value {
  /** Magnitude of the voltage in p.u. */
  private final ComparableQuantity<Dimensionless> magnitude;

  /** Angle of the voltage in degree. */
  private final ComparableQuantity<Angle> angle;

  /**
   * @param magnitude of the voltage in p.u.
   * @param angle of this voltage in degree
   */
  public VoltageValue(
      ComparableQuantity<Dimensionless> magnitude, ComparableQuantity<Angle> angle) {
    this.magnitude = magnitude;
    this.angle = angle;
  }

  /**
   * @param magnitude of the voltage in p.u.
   */
  public VoltageValue(ComparableQuantity<Dimensionless> magnitude) {
    this.magnitude = magnitude;
    this.angle = null;
  }

  public Optional<ComparableQuantity<Dimensionless>> getMagnitude() {
    return Optional.ofNullable(magnitude);
  }

  public Optional<ComparableQuantity<Angle>> getAngle() {
    return Optional.ofNullable(angle);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof VoltageValue that)) return false;
    return Objects.equals(magnitude, that.magnitude) && Objects.equals(angle, that.angle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(magnitude, angle);
  }

  @Override
  public String toString() {
    return "VoltageValue{" + "magnitude=" + magnitude + ", angle=" + angle + "}";
  }
}
