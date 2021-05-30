/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.util.quantities.interfaces.HeatCapacity;
import edu.ie3.util.quantities.interfaces.ThermalConductance;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;

/** Quite simple thermal model of a house to serve as a heat sink */
public class ThermalHouseInput extends ThermalSinkInput {
  /** Thermal, transitional losses of the included thermal house model (typically in kW/K) */
  private final ComparableQuantity<ThermalConductance> ethLosses;
  /** Thermal capacity of the included thermal house model (typically in kWh/K) */
  private final ComparableQuantity<HeatCapacity> ethCapa;
  /** Desired target temperature of the thermal house model (typically in °C) */
  private final ComparableQuantity<Temperature> desiredTemperature;
  /** Upper boundary temperature of the thermal house model (typically in °C) */
  private final ComparableQuantity<Temperature> upperTemperatureLimit;
  /** Lower boundary temperature of the thermal house model (typically in °C) */
  private final ComparableQuantity<Temperature> lowerTemperatureLimit;

  /**
   * @param uuid Unique identifier of a thermal house model
   * @param id Identifier of the model
   * @param bus Thermal bus, the model is connected to
   * @param ethLosses Thermal, transitional losses of the included thermal house model
   * @param ethCapa Thermal capacity of the included thermal house model
   * @param desiredTemperature Desired target temperature of the thermal house model
   * @param upperTemperatureLimit Upper boundary temperature of the thermal house model
   * @param lowerTemperatureLimit Lower boundary temperature of the thermal house model
   */
  public ThermalHouseInput(
      UUID uuid,
      String id,
      ThermalBusInput bus,
      ComparableQuantity<ThermalConductance> ethLosses,
      ComparableQuantity<HeatCapacity> ethCapa,
      ComparableQuantity<Temperature> desiredTemperature,
      ComparableQuantity<Temperature> upperTemperatureLimit,
      ComparableQuantity<Temperature> lowerTemperatureLimit) {
    super(uuid, id, bus);
    this.ethLosses = ethLosses.to(StandardUnits.THERMAL_TRANSMISSION);
    this.ethCapa = ethCapa.to(StandardUnits.HEAT_CAPACITY);
    this.desiredTemperature = desiredTemperature.to(StandardUnits.TEMPERATURE);
    this.upperTemperatureLimit = upperTemperatureLimit.to(StandardUnits.TEMPERATURE);
    this.lowerTemperatureLimit = lowerTemperatureLimit.to(StandardUnits.TEMPERATURE);
  }

  /**
   * @param uuid Unique identifier of a thermal house model
   * @param id Identifier of the model
   * @param operator operator of the asset
   * @param operationTime operation time of the asset
   * @param bus Thermal bus, the model is connected to
   * @param ethLosses Thermal, transitional losses of the included thermal house model
   * @param ethCapa Thermal capacity of the included thermal house model
   * @param desiredTemperature Desired target temperature of the thermal house model
   * @param upperTemperatureLimit Upper boundary temperature of the thermal house model
   * @param lowerTemperatureLimit Lower boundary temperature of the thermal house model
   */
  public ThermalHouseInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      ThermalBusInput bus,
      ComparableQuantity<ThermalConductance> ethLosses,
      ComparableQuantity<HeatCapacity> ethCapa,
      ComparableQuantity<Temperature> desiredTemperature,
      ComparableQuantity<Temperature> upperTemperatureLimit,
      ComparableQuantity<Temperature> lowerTemperatureLimit) {
    super(uuid, id, operator, operationTime, bus);
    this.ethLosses = ethLosses.to(StandardUnits.THERMAL_TRANSMISSION);
    this.ethCapa = ethCapa.to(StandardUnits.HEAT_CAPACITY);
    this.desiredTemperature = desiredTemperature.to(StandardUnits.TEMPERATURE);
    this.upperTemperatureLimit = upperTemperatureLimit.to(StandardUnits.TEMPERATURE);
    this.lowerTemperatureLimit = lowerTemperatureLimit.to(StandardUnits.TEMPERATURE);
  }

  public ComparableQuantity<ThermalConductance> getEthLosses() {
    return ethLosses;
  }

  public ComparableQuantity<HeatCapacity> getEthCapa() {
    return ethCapa;
  }

  public ComparableQuantity<Temperature> getDesiredTemperature() {
    return desiredTemperature;
  }

  public ComparableQuantity<Temperature> getUpperTemperatureLimit() {
    return upperTemperatureLimit;
  }

  public ComparableQuantity<Temperature> getLowerTemperatureLimit() {
    return lowerTemperatureLimit;
  }

  @Override
  public ThermalHouseInputCopyBuilder copy() {
    return new ThermalHouseInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ThermalHouseInput that = (ThermalHouseInput) o;
    return ethLosses.equals(that.ethLosses)
        && ethCapa.equals(that.ethCapa)
        && desiredTemperature.equals(that.desiredTemperature)
        && upperTemperatureLimit.equals(that.upperTemperatureLimit)
        && lowerTemperatureLimit.equals(that.lowerTemperatureLimit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), ethLosses, ethCapa);
  }

  @Override
  public String toString() {
    return "ThermalHouseInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", bus="
        + getThermalBus().getUuid()
        + ", ethLosses="
        + ethLosses
        + ", ethCapa="
        + ethCapa
        + ", desiredTemperature="
        + desiredTemperature
        + ", upperTemperatureLimit="
        + upperTemperatureLimit
        + ", lowerTemperatureLimit="
        + lowerTemperatureLimit
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link ThermalHouseInput} entities with
   * altered field values. For detailed field descriptions refer to java docs of {@link
   * ThermalHouseInput}
   */
  public static class ThermalHouseInputCopyBuilder
      extends ThermalUnitInput.ThermalUnitInputCopyBuilder<ThermalHouseInputCopyBuilder> {

    private ComparableQuantity<ThermalConductance> ethLosses;
    private ComparableQuantity<HeatCapacity> ethCapa;
    private ComparableQuantity<Temperature> desiredTemperature;
    private ComparableQuantity<Temperature> upperTemperatureLimit;
    private ComparableQuantity<Temperature> lowerTemperatureLimit;

    private ThermalHouseInputCopyBuilder(ThermalHouseInput entity) {
      super(entity);
      this.ethLosses = entity.getEthLosses();
      this.ethCapa = entity.getEthCapa();
      this.desiredTemperature = entity.getDesiredTemperature();
      this.upperTemperatureLimit = entity.getUpperTemperatureLimit();
      this.lowerTemperatureLimit = entity.getLowerTemperatureLimit();
    }

    @Override
    public ThermalHouseInput build() {
      return new ThermalHouseInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getThermalBus(),
          ethLosses,
          ethCapa,
          desiredTemperature,
          upperTemperatureLimit,
          lowerTemperatureLimit);
    }

    public ThermalHouseInputCopyBuilder ethLosses(
        ComparableQuantity<ThermalConductance> ethLosses) {
      this.ethLosses = ethLosses;
      return this;
    }

    public ThermalHouseInputCopyBuilder ethCapa(ComparableQuantity<HeatCapacity> ethCapa) {
      this.ethCapa = ethCapa;
      return this;
    }

    public ThermalHouseInputCopyBuilder desiredTemperature(
        ComparableQuantity<Temperature> desiredTemperature) {
      this.desiredTemperature = desiredTemperature;
      return this;
    }

    public ThermalHouseInputCopyBuilder upperTemperatureLimit(
        ComparableQuantity<Temperature> upperTemperatureLimit) {
      this.upperTemperatureLimit = upperTemperatureLimit;
      return this;
    }

    public ThermalHouseInputCopyBuilder lowerTemperatureLimit(
        ComparableQuantity<Temperature> lowerTemperatureLimit) {
      this.lowerTemperatureLimit = lowerTemperatureLimit;
      return this;
    }

    @Override
    protected ThermalHouseInputCopyBuilder childInstance() {
      return this;
    }
  }
}
