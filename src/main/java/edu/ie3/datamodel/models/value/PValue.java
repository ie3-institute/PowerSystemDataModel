/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.QuantityUtil;
import java.util.Objects;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes a actove power value as active power */
public class PValue implements Value {

  /** Active power */
  private final ComparableQuantity<Power> p;

  /** @param p Active power */
  public PValue(ComparableQuantity<Power> p) {
    this.p = p.to(StandardUnits.ACTIVE_POWER_IN);
  }

  public ComparableQuantity<Power> getP() {
    return p;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PValue that = (PValue) o;
    if (!QuantityUtil.quantityIsEmpty(p)) {
      if (QuantityUtil.quantityIsEmpty(that.p)) return false;
      return p.isEquivalentTo(that.p);
    } else return QuantityUtil.quantityIsEmpty(that.p);
  }

  @Override
  public int hashCode() {
    return Objects.hash(p);
  }

  @Override
  public String toString() {
    return "PValue{" + "p=" + p + '}';
  }
}
