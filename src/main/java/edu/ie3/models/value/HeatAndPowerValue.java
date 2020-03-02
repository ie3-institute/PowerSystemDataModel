package edu.ie3.models.value;

import edu.ie3.models.StandardUnits;

import javax.measure.Quantity;
import javax.measure.quantity.Power;
import java.util.Objects;

/** Describes a triple based on active and reactive electrical power, as well as heat power */
public class HeatAndPowerValue extends PowerValue {
    /**
     * Heat demand as power
     */
    private Quantity<Power> heatDemand;

    /**
     * @param p Active power
     * @param q Reactive power
     */
    public HeatAndPowerValue(Quantity<Power> p, Quantity<Power> q, Quantity<Power> heatDemand) {
        super(p, q);
        this.heatDemand = heatDemand.to(StandardUnits.HEAT_DEMAND_PROFILE);
    }

    public Quantity<Power> getHeatDemand() {
        return heatDemand;
    }

    public void setHeatDemand(Quantity<Power> heatDemand) {
        this.heatDemand = heatDemand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HeatAndPowerValue that = (HeatAndPowerValue) o;
        return heatDemand.equals(that.heatDemand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), heatDemand);
    }
}
