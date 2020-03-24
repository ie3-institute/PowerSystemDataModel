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

/** Describes as heat demand value */
public class HeatDemandValue implements Value {
  private Quantity<Power> heatDemand;

  public HeatDemandValue(Quantity<Power> heatDemand) {
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
    HeatDemandValue that = (HeatDemandValue) o;
    return heatDemand.equals(that.heatDemand);
  }

  @Override
  public int hashCode() {
    return Objects.hash(heatDemand);
  }

  @Override
  public String toString() {
    return "HeatDemandValue{" + "heatDemand=" + heatDemand + '}';
  }
}
