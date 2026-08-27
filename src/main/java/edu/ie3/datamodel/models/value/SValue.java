/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import java.util.Objects;
import java.util.Optional;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes a apparent power value as a pair of active and reactive power. */
public class SValue extends PValue {
  /** Reactive power. */
  private final ComparableQuantity<Power> q;

  /**
   * Creates a new value representing apparent power.
   *
   * @param p Active power
   * @param q Reactive power
   */
  public SValue(ComparableQuantity<Power> p, ComparableQuantity<Power> q) {
    super(p);
    this.q = q;
  }

  public Optional<ComparableQuantity<Power>> getQ() {
    return Optional.ofNullable(q);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SValue that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(q, that.q);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), q);
  }

  @Override
  public String toString() {
    return "SValue{" + "p=" + getP() + ", q=" + q + "}";
  }
}
