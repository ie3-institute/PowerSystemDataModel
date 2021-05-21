/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import java.util.Objects;
import java.util.Optional;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes a apparent power value as a pair of active and reactive power */
public class SValue extends PValue {

  /** Reactive power */
  private final ComparableQuantity<Power> q;

  /**
   * Creates a new value representing apparent power
   *
   * @param p Active power
   * @param q Reactive power
   */
  public SValue(ComparableQuantity<Power> p, ComparableQuantity<Power> q) {
    super(p);
    this.q = q == null ? null : q.to(StandardUnits.REACTIVE_POWER_IN);
  }

  public Optional<ComparableQuantity<Power>> getQ() {
    return Optional.ofNullable(q);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SValue sValue = (SValue) o;
    return Objects.equals(q, sValue.q);
  }

  @Override
  public int hashCode() {
    return Objects.hash(q);
  }

  @Override
  public String toString() {
    return "SValue{" + "p=" + getP() + ", q=" + q + '}';
  }
}
