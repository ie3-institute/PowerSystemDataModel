/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import static edu.ie3.datamodel.models.StandardUnits.VOLTAGE_ANGLE;
import static edu.ie3.util.quantities.PowerSystemUnits.DEGREE_GEOM;
import static edu.ie3.util.quantities.PowerSystemUnits.PU;
import static java.lang.Math.*;

import java.util.Objects;
import java.util.Optional;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

/** Describes a voltage value as a pair of magnitude and angle */
public class VoltageValue implements Value {

  /** Magnitude of the voltage in p.u. */
  private final ComparableQuantity<Dimensionless> magnitude;
  /** Angle of the voltage in degree */
  private final ComparableQuantity<Angle> angle;

  /**
   * @param magnitude of the voltage in p.u.
   * @param angle of the voltage in degree
   */
  public VoltageValue(
      ComparableQuantity<Dimensionless> magnitude, ComparableQuantity<Angle> angle) {
    this.magnitude = magnitude;
    this.angle = angle;
  }

  /**
   * This constructor will set the angle to 0°
   *
   * @param magnitude of the voltage in p.u.
   */
  public VoltageValue(ComparableQuantity<Dimensionless> magnitude) {
    this.magnitude = magnitude;
    this.angle = Quantities.getQuantity(0.0, VOLTAGE_ANGLE);
  }

  public Optional<ComparableQuantity<Dimensionless>> getMagnitude() {
    return Optional.ofNullable(magnitude);
  }

  public Optional<ComparableQuantity<Angle>> getAngle() {
    return Optional.ofNullable(angle);
  }

  public Optional<ComparableQuantity<Dimensionless>> getRealPart() {
    double mag = magnitude.to(PU).getValue().doubleValue();
    double ang = angle.to(DEGREE_GEOM).getValue().doubleValue();

    double eInPu = mag * cos(toRadians(ang));
    return Optional.of(Quantities.getQuantity(eInPu, PU));
  }

  public Optional<ComparableQuantity<Dimensionless>> getImagPart() {
    double mag = magnitude.to(PU).getValue().doubleValue();
    double ang = angle.to(DEGREE_GEOM).getValue().doubleValue();

    double eInPu = mag * sin(toRadians(ang));
    return Optional.of(Quantities.getQuantity(eInPu, PU));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    VoltageValue that = (VoltageValue) o;
    return Objects.equals(magnitude, that.magnitude) && Objects.equals(angle, that.angle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(magnitude, angle);
  }

  @Override
  public String toString() {
    return "VoltageValue{" + "magnitude=" + magnitude + ", angle=" + angle + '}';
  }
}
