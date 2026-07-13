package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class HeatExchanger extends ThermalInput {

    private final ThermalBusInput householdBus;
    private final ThermalBusInput externalHeatGrid;


    public HeatExchanger(UUID uuid, String id, ThermalBusInput householdBus, ThermalBusInput externalHeatGrid) {
        super(uuid, id);
        this.householdBus = householdBus;
        this.externalHeatGrid = externalHeatGrid;
    }

    public HeatExchanger(UUID uuid, String id, ThermalBusInput householdBus, ThermalBusInput externalHeatGrid, Map<String, String> additionalInformation) {
        super(uuid, id);
        this.householdBus = householdBus;
        this.externalHeatGrid = externalHeatGrid;
        setAdditionalInformation(additionalInformation);
    }

    public HeatExchanger(UUID uuid, String id, OperatorInput operator, OperationTime operationTime, ThermalBusInput householdBus, ThermalBusInput externalHeatGrid) {
        super(uuid, id, operator, operationTime);
        this.householdBus = householdBus;
        this.externalHeatGrid = externalHeatGrid;
    }

    public HeatExchanger(UUID uuid, String id, OperatorInput operator, OperationTime operationTime, ThermalBusInput householdBus, ThermalBusInput externalHeatGrid, Map<String, String> additionalInformation) {
        super(uuid, id, operator, operationTime);
        this.householdBus = householdBus;
        this.externalHeatGrid = externalHeatGrid;
        setAdditionalInformation(additionalInformation);
    }


    public ThermalBusInput getHouseholdBus() {
        return householdBus;
    }

    public ThermalBusInput getExternalHeatGrid() {
        return externalHeatGrid;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HeatExchanger that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(householdBus, that.householdBus) && Objects.equals(externalHeatGrid, that.externalHeatGrid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), householdBus, externalHeatGrid);
    }

    @Override
    public String toString() {
        return "HeatExchanger{"
                + "uuid="
                + getUuid()
                + ", id="
                + getId()
                + ", operator="
                + getOperator().getUuid()
                + ", operationTime="
                + getOperationTime()
                + ", householdBus="
                + householdBus
                + ", externalHeatGrid="
                + externalHeatGrid
                + '}';
    }

    @Override
    public AssetInputCopyBuilder<?> copy() {
        return new HeatExchangerCopyBuilder(this);
    }

    public static class HeatExchangerCopyBuilder extends AssetInputCopyBuilder<HeatExchangerCopyBuilder> {

        private ThermalBusInput householdBus;
        private ThermalBusInput externalHeatGrid;

        protected HeatExchangerCopyBuilder(HeatExchanger entity) {
            super(entity);
            this.householdBus = entity.householdBus;
            this.externalHeatGrid = entity.externalHeatGrid;
        }

        public HeatExchangerCopyBuilder householdBus(ThermalBusInput householdBus) {
            this.householdBus = householdBus;
            return thisInstance();
        }

        public HeatExchangerCopyBuilder externalHeatGrid(ThermalBusInput externalHeatGrid) {
            this.externalHeatGrid = externalHeatGrid;
            return thisInstance();
        }

        @Override
        public HeatExchanger build() {
            return new HeatExchanger(getUuid(), getId(), householdBus, externalHeatGrid);
        }

        @Override
        protected HeatExchangerCopyBuilder thisInstance() {
            return null;
        }
    }
}
