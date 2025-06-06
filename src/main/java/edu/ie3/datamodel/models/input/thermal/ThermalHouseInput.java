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
  private final ComparableQuantity<Temperature> targetTemperature;
  /** Upper boundary temperature of the thermal house model (typically in °C) */
  private final ComparableQuantity<Temperature> upperTemperatureLimit;
  /** Lower boundary temperature of the thermal house model (typically in °C) */
  private final ComparableQuantity<Temperature> lowerTemperatureLimit;
  /** Type of the building, e.g. house or flat */
  private final String housingType;
  /** Number of people living in the building, double to allow proper scaling */
  private final double numberInhabitants;

  /**
   * @param uuid Unique identifier of a thermal house model
   * @param id Identifier of the model
   * @param bus Thermal bus, the model is connected to
   * @param ethLosses Thermal, transitional losses of the included thermal house model
   * @param ethCapa Thermal capacity of the included thermal house model
   * @param targetTemperature Desired target temperature of the thermal house model
   * @param upperTemperatureLimit Upper boundary temperature of the thermal house model
   * @param lowerTemperatureLimit Lower boundary temperature of the thermal house model
   * @param housingType Type of the building: either house or flat
   * @param numberInhabitants Number of inhabitants living in this house
   */
  public ThermalHouseInput(
      UUID uuid,
      String id,
      ThermalBusInput bus,
      ComparableQuantity<ThermalConductance> ethLosses,
      ComparableQuantity<HeatCapacity> ethCapa,
      ComparableQuantity<Temperature> targetTemperature,
      ComparableQuantity<Temperature> upperTemperatureLimit,
      ComparableQuantity<Temperature> lowerTemperatureLimit,
      String housingType,
      double numberInhabitants) {
    super(uuid, id, bus);
    this.ethLosses = ethLosses.to(StandardUnits.THERMAL_TRANSMISSION);
    this.ethCapa = ethCapa.to(StandardUnits.HEAT_CAPACITY);
    this.targetTemperature = targetTemperature.to(StandardUnits.TEMPERATURE);
    this.upperTemperatureLimit = upperTemperatureLimit.to(StandardUnits.TEMPERATURE);
    this.lowerTemperatureLimit = lowerTemperatureLimit.to(StandardUnits.TEMPERATURE);
    this.housingType = housingType;
    this.numberInhabitants = numberInhabitants;
  }

  /**
   * @param uuid Unique identifier of a thermal house model
   * @param id Identifier of the model
   * @param operator operator of the asset
   * @param operationTime operation time of the asset
   * @param bus Thermal bus, the model is connected to
   * @param ethLosses Thermal, transitional losses of the included thermal house model
   * @param ethCapa Thermal capacity of the included thermal house model
   * @param targetTemperature Desired target temperature of the thermal house model
   * @param upperTemperatureLimit Upper boundary temperature of the thermal house model
   * @param lowerTemperatureLimit Lower boundary temperature of the thermal house model
   * @param housingType Type of the building: either house or flat
   * @param numberInhabitants Number of inhabitants living in this house
   */
  public ThermalHouseInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      ThermalBusInput bus,
      ComparableQuantity<ThermalConductance> ethLosses,
      ComparableQuantity<HeatCapacity> ethCapa,
      ComparableQuantity<Temperature> targetTemperature,
      ComparableQuantity<Temperature> upperTemperatureLimit,
      ComparableQuantity<Temperature> lowerTemperatureLimit,
      String housingType,
      double numberInhabitants) {
    super(uuid, id, operator, operationTime, bus);
    this.ethLosses = ethLosses.to(StandardUnits.THERMAL_TRANSMISSION);
    this.ethCapa = ethCapa.to(StandardUnits.HEAT_CAPACITY);
    this.targetTemperature = targetTemperature.to(StandardUnits.TEMPERATURE);
    this.upperTemperatureLimit = upperTemperatureLimit.to(StandardUnits.TEMPERATURE);
    this.lowerTemperatureLimit = lowerTemperatureLimit.to(StandardUnits.TEMPERATURE);
    this.housingType = housingType;
    this.numberInhabitants = numberInhabitants;
  }

  public ComparableQuantity<ThermalConductance> getEthLosses() {
    return ethLosses;
  }

  public ComparableQuantity<HeatCapacity> getEthCapa() {
    return ethCapa;
  }

  public ComparableQuantity<Temperature> getTargetTemperature() {
    return targetTemperature;
  }

  public ComparableQuantity<Temperature> getUpperTemperatureLimit() {
    return upperTemperatureLimit;
  }

  public ComparableQuantity<Temperature> getLowerTemperatureLimit() {
    return lowerTemperatureLimit;
  }

  public String getHousingType() {
    return housingType;
  }

  public double getNumberOfInhabitants() {
    return numberInhabitants;
  }

  @Override
  public ThermalHouseInputCopyBuilder copy() {
    return new ThermalHouseInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ThermalHouseInput that)) return false;
    if (!super.equals(o)) return false;
    return ethLosses.equals(that.ethLosses)
        && ethCapa.equals(that.ethCapa)
        && targetTemperature.equals(that.targetTemperature)
        && upperTemperatureLimit.equals(that.upperTemperatureLimit)
        && lowerTemperatureLimit.equals(that.lowerTemperatureLimit)
        && Objects.equals(housingType, that.housingType)
        && Objects.equals(numberInhabitants, that.numberInhabitants);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        ethLosses,
        ethCapa,
        targetTemperature,
        upperTemperatureLimit,
        lowerTemperatureLimit,
        housingType,
        numberInhabitants);
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
        + ", targetTemperature="
        + targetTemperature
        + ", upperTemperatureLimit="
        + upperTemperatureLimit
        + ", lowerTemperatureLimit="
        + lowerTemperatureLimit
        + ", housingType="
        + housingType
        + ", #inhabitants="
        + numberInhabitants
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link ThermalHouseInput} entities with
   * altered field values. For detailed field descriptions refer to java docs of {@link
   * ThermalHouseInput}
   */
  public static class ThermalHouseInputCopyBuilder
      extends ThermalSinkInputCopyBuilder<ThermalHouseInputCopyBuilder> {

    private ComparableQuantity<ThermalConductance> ethLosses;
    private ComparableQuantity<HeatCapacity> ethCapa;
    private ComparableQuantity<Temperature> targetTemperature;
    private ComparableQuantity<Temperature> upperTemperatureLimit;
    private ComparableQuantity<Temperature> lowerTemperatureLimit;
    private String housingType;
    private double numberInhabitants;

    private ThermalHouseInputCopyBuilder(ThermalHouseInput entity) {
      super(entity);
      this.ethLosses = entity.getEthLosses();
      this.ethCapa = entity.getEthCapa();
      this.targetTemperature = entity.getTargetTemperature();
      this.upperTemperatureLimit = entity.getUpperTemperatureLimit();
      this.lowerTemperatureLimit = entity.getLowerTemperatureLimit();
      this.housingType = entity.getHousingType();
      this.numberInhabitants = entity.getNumberOfInhabitants();
    }

    public ThermalHouseInputCopyBuilder ethLosses(
        ComparableQuantity<ThermalConductance> ethLosses) {
      this.ethLosses = ethLosses;
      return thisInstance();
    }

    public ThermalHouseInputCopyBuilder ethCapa(ComparableQuantity<HeatCapacity> ethCapa) {
      this.ethCapa = ethCapa;
      return thisInstance();
    }

    public ThermalHouseInputCopyBuilder targetTemperature(
        ComparableQuantity<Temperature> targetTemperature) {
      this.targetTemperature = targetTemperature;
      return thisInstance();
    }

    public ThermalHouseInputCopyBuilder upperTemperatureLimit(
        ComparableQuantity<Temperature> upperTemperatureLimit) {
      this.upperTemperatureLimit = upperTemperatureLimit;
      return thisInstance();
    }

    public ThermalHouseInputCopyBuilder lowerTemperatureLimit(
        ComparableQuantity<Temperature> lowerTemperatureLimit) {
      this.lowerTemperatureLimit = lowerTemperatureLimit;
      return thisInstance();
    }

    public ThermalHouseInputCopyBuilder housingType(String housingType) {
      this.housingType = housingType;
      return thisInstance();
    }

    public ThermalHouseInputCopyBuilder numberInhabitants(double numberInhabitants) {
      this.numberInhabitants = numberInhabitants;
      return thisInstance();
    }

    @Override
    public ThermalHouseInputCopyBuilder scale(Double factor) {
      // scale losses as well as capacity and number of inhabitants to keep equal
      // the time needed to heat a scaled house
      ethLosses(ethLosses.multiply(factor));
      ethCapa(ethCapa.multiply(factor));
      numberInhabitants(numberInhabitants * factor);
      return thisInstance();
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
          targetTemperature,
          upperTemperatureLimit,
          lowerTemperatureLimit,
          housingType,
          numberInhabitants);
    }

    @Override
    protected ThermalHouseInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
