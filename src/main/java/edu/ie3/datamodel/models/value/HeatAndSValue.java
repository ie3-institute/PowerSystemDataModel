/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import java.util.Objects;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes a triple based on active and reactive electrical power, as well as heat power */
public class HeatAndSValue extends SValue {
  /** Heat demand as power */
  private final ComparableQuantity<Power> heatDemand;

  /**
   * @param p Active power
   * @param q Reactive power
   * @param heatDemand Heat demand
   */
  public HeatAndSValue(
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q,
      ComparableQuantity<Power> heatDemand) {
    super(p, q);
    this.heatDemand = heatDemand.to(StandardUnits.HEAT_DEMAND_PROFILE);
  }

  public ComparableQuantity<Power> getHeatDemand() {
    return heatDemand;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    HeatAndSValue that = (HeatAndSValue) o;
    return heatDemand.equals(that.heatDemand);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), heatDemand);
  }

  @Override
  public String toString() {
    return "HeatAndSValue{" + "p=" + getP() + ", q=" + getQ() + ", heatDemand=" + heatDemand + '}';
  }
}
