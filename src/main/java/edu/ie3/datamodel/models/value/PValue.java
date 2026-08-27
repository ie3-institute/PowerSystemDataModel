/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.utils.QuantityUtils;
import java.util.Objects;
import java.util.Optional;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes a active power value as active power. */
public class PValue implements Value {
  /** Active power. */
  private final ComparableQuantity<Power> p;

  /**
   * @param p Active power
   */
  public PValue(ComparableQuantity<Power> p) {
    this.p = p;
  }

  public Optional<ComparableQuantity<Power>> getP() {
    return Optional.ofNullable(p);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PValue that)) return false;
    return QuantityUtils.equals(p, that.p);
  }

  @Override
  public int hashCode() {
    return Objects.hash(p);
  }

  @Override
  public String toString() {
    return "PValue{" + "p=" + p + "}";
  }
}
