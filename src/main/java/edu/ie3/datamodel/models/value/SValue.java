/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.utils.QuantityUtil;
import java.util.Objects;
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
    this.q = q.to(StandardUnits.REACTIVE_POWER_IN);
  }

  public ComparableQuantity<Power> getQ() {
    return q;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SValue that = (SValue) o;
    return QuantityUtil.equals(q, that.q);
  }

  @Override
  public int hashCode() {
    return Objects.hash(q);
  }

  @Override
  public String toString() {
    return "SValue{" + "q=" + q + '}';
  }
}
