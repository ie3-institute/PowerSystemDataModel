/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.value;

import edu.ie3.models.StandardUnits;
import java.util.Objects;
import javax.measure.Quantity;
import javax.measure.quantity.Power;

/** Describes a power value as a pair of active and reactive power */
public class PowerValue implements Value {

  /** Active power */
  private Quantity<Power> p;
  /** Reactive power */
  private Quantity<Power> q;

  /**
   * @param p Active power
   * @param q Reactive power
   */
  public PowerValue(Quantity<Power> p, Quantity<Power> q) {
    this.p = p.to(StandardUnits.ACTIVE_POWER_IN);
    this.q = q.to(StandardUnits.REACTIVE_POWER_IN);
  }

  public Quantity<Power> getP() {
    return p;
  }

  public void setP(Quantity<Power> p) {
    this.p = p.to(StandardUnits.ACTIVE_POWER_IN);
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
    PowerValue that = (PowerValue) o;
    return p.equals(that.p) && q.equals(that.q);
  }

  @Override
  public int hashCode() {
    return Objects.hash(p, q);
  }
}
