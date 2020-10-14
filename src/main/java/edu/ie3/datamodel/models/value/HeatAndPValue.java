/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import java.util.Objects;
import java.util.Optional;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes a tuple based on active electrical power, as well as heat power */
public class HeatAndPValue extends PValue {
  /** Heat demand as power */
  private final ComparableQuantity<Power> heatDemand;

  /**
   * @param p Active power
   * @param heatDemand Heat demand
   */
  public HeatAndPValue(ComparableQuantity<Power> p, ComparableQuantity<Power> heatDemand) {
    super(p);
    this.heatDemand = heatDemand == null ? null : heatDemand.to(StandardUnits.HEAT_DEMAND_PROFILE);
  }

  public Optional<ComparableQuantity<Power>> getHeatDemand() {
    return Optional.ofNullable(heatDemand);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    HeatAndPValue that = (HeatAndPValue) o;
    return Objects.equals(heatDemand, that.heatDemand);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), heatDemand);
  }

  @Override
  public String toString() {
    return "HeatAndPValue{" + "p=" + getP() + ", heatDemand=" + heatDemand + '}';
  }
}
