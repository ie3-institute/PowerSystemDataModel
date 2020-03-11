/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import java.util.Objects;
import javax.measure.Quantity;
import javax.measure.quantity.Power;

/** Describes a apparent power value as a pair of active and reactive power */
public class SValue extends PValue {

  /** Reactive power */
  private Quantity<Power> q;

  /** @param q Reactive power */
  public SValue(Quantity<Power> p, Quantity<Power> q) {
    super(p);
    this.q = q.to(StandardUnits.REACTIVE_POWER_IN);
  }

  public Quantity<Power> getQ() {
    return q;
  }

  public void setQ(Quantity<Power> q) {
    this.q = q.to(StandardUnits.REACTIVE_POWER_IN);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SValue that = (SValue) o;
    return q.equals(that.q);
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
