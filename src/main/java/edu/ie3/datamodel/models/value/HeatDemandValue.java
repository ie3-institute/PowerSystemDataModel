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

/** Describes as heat demand value */
public class HeatDemandValue implements Value {
  private final ComparableQuantity<Power> heatDemand;

  public HeatDemandValue(ComparableQuantity<Power> heatDemand) {
    this.heatDemand = heatDemand.to(StandardUnits.HEAT_DEMAND_PROFILE);
  }

  public ComparableQuantity<Power> getHeatDemand() {
    return heatDemand;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HeatDemandValue that = (HeatDemandValue) o;
    return QuantityUtil.equals(heatDemand, that.heatDemand);
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
