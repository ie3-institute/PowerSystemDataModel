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

/** Describes a tuple based on active electrical power, as well as heat power */
public class HeatAndPValue extends PValue {
  /** Heat demand as power */
  private Quantity<Power> heatDemand;

  /**
   * @param p Active power
   * @param heatDemand Heat demand
   */
  public HeatAndPValue(Quantity<Power> p, Quantity<Power> heatDemand) {
    super(p);
    this.heatDemand = heatDemand.to(StandardUnits.HEAT_DEMAND_PROFILE);
  }

  public Quantity<Power> getHeatDemand() {
    return heatDemand;
  }

  public void setHeatDemand(Quantity<Power> heatDemand) {
    this.heatDemand = heatDemand.to(StandardUnits.HEAT_DEMAND_PROFILE);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    HeatAndPValue that = (HeatAndPValue) o;
    return heatDemand.equals(that.heatDemand);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), heatDemand);
  }
}
