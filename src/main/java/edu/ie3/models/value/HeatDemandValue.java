package edu.ie3.models.value;

import edu.ie3.models.StandardUnits;

import javax.measure.Quantity;
import javax.measure.quantity.Power;
import java.util.Objects;

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
}
