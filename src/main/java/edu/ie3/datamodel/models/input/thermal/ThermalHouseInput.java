/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.utils.QuantityUtils;
import edu.ie3.util.quantities.interfaces.HeatCapacity;
import edu.ie3.util.quantities.interfaces.ThermalConductance;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;

/** Quite simple thermal model of a house to serve as a heat sink. */
public class ThermalHouseInput extends ThermalSinkInput {
  /** Thermal, transitional losses of the included thermal house model (typically in kW/K). */
  private final ComparableQuantity<ThermalConductance> ethLosses;

  /** Thermal capacity of the included thermal house model (typically in kWh/K). */
  private final ComparableQuantity<HeatCapacity> ethCapa;

  /** Desired target temperature of the thermal house model (typically in °C). */
  private final ComparableQuantity<Temperature> targetTemperature;

  /** Upper boundary temperature of the thermal house model (typically in °C). */
  private final ComparableQuantity<Temperature> upperTemperatureLimit;

  /** Lower boundary temperature of the thermal house model (typically in °C). */
  private final ComparableQuantity<Temperature> lowerTemperatureLimit;

  /** Type of the building, e.g. house or flat. */
  private final String housingType;

  /** Number of people living in the building, double to allow proper scaling. */
  private final double numberOfInhabitants;

  /**
   * @param uuid Unique identifier of a thermal house model
   * @param id Identifier of the model
   * @param thermalBus Thermal bus, the model is connected to
   * @param ethLosses Thermal, transitional losses of the included thermal house model
   * @param ethCapa Thermal capacity of the included thermal house model
   * @param targetTemperature Desired target temperature of the thermal house model
   * @param upperTemperatureLimit Upper boundary temperature of the thermal house model
   * @param lowerTemperatureLimit Lower boundary temperature of the thermal house model
   * @param housingType Type of the building: either house or flat
   * @param numberOfInhabitants Number of inhabitants living in this house
   */
  public ThermalHouseInput(
      UUID uuid,
      String id,
      ThermalBusInput thermalBus,
      ComparableQuantity<ThermalConductance> ethLosses,
      ComparableQuantity<HeatCapacity> ethCapa,
      ComparableQuantity<Temperature> targetTemperature,
      ComparableQuantity<Temperature> upperTemperatureLimit,
      ComparableQuantity<Temperature> lowerTemperatureLimit,
      String housingType,
      double numberOfInhabitants) {
    super(uuid, id, thermalBus);
    this.ethLosses = ethLosses;
    this.ethCapa = ethCapa;
    this.targetTemperature = targetTemperature;
    this.upperTemperatureLimit = upperTemperatureLimit;
    this.lowerTemperatureLimit = lowerTemperatureLimit;
    this.housingType = housingType;
    this.numberOfInhabitants = numberOfInhabitants;
  }

  /**
   * @param uuid Unique identifier of a thermal house model
   * @param id Identifier of the model
   * @param operator operator of the asset
   * @param operationTime operation time of the asset
   * @param thermalBus Thermal bus, the model is connected to
   * @param ethLosses Thermal, transitional losses of the included thermal house model
   * @param ethCapa Thermal capacity of the included thermal house model
   * @param targetTemperature Desired target temperature of the thermal house model
   * @param upperTemperatureLimit Upper boundary temperature of the thermal house model
   * @param lowerTemperatureLimit Lower boundary temperature of the thermal house model
   * @param housingType Type of the building: either house or flat
   * @param numberOfInhabitants Number of inhabitants living in this house
   */
  public ThermalHouseInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      ThermalBusInput thermalBus,
      ComparableQuantity<ThermalConductance> ethLosses,
      ComparableQuantity<HeatCapacity> ethCapa,
      ComparableQuantity<Temperature> targetTemperature,
      ComparableQuantity<Temperature> upperTemperatureLimit,
      ComparableQuantity<Temperature> lowerTemperatureLimit,
      String housingType,
      double numberOfInhabitants) {
    super(uuid, id, operator, operationTime, thermalBus);
    this.ethLosses = ethLosses;
    this.ethCapa = ethCapa;
    this.targetTemperature = targetTemperature;
    this.upperTemperatureLimit = upperTemperatureLimit;
    this.lowerTemperatureLimit = lowerTemperatureLimit;
    this.housingType = housingType;
    this.numberOfInhabitants = numberOfInhabitants;
  }

  /**
   * @param uuid Unique identifier of a thermal house model
   * @param id Identifier of the model
   * @param operator operator of the asset
   * @param operationTime operation time of the asset
   * @param thermalBus Thermal bus, the model is connected to
   * @param ethLosses Thermal, transitional losses of the included thermal house model
   * @param ethCapa Thermal capacity of the included thermal house model
   * @param targetTemperature Desired target temperature of the thermal house model
   * @param upperTemperatureLimit Upper boundary temperature of the thermal house model
   * @param lowerTemperatureLimit Lower boundary temperature of the thermal house model
   * @param housingType Type of the building: either house or flat
   * @param numberOfInhabitants Number of inhabitants living in this house
   * @param additionalInformation Of the input
   */
  public ThermalHouseInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      ThermalBusInput thermalBus,
      ComparableQuantity<ThermalConductance> ethLosses,
      ComparableQuantity<HeatCapacity> ethCapa,
      ComparableQuantity<Temperature> targetTemperature,
      ComparableQuantity<Temperature> upperTemperatureLimit,
      ComparableQuantity<Temperature> lowerTemperatureLimit,
      String housingType,
      double numberOfInhabitants,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime, thermalBus);
    this.ethLosses = ethLosses;
    this.ethCapa = ethCapa;
    this.targetTemperature = targetTemperature;
    this.upperTemperatureLimit = upperTemperatureLimit;
    this.lowerTemperatureLimit = lowerTemperatureLimit;
    this.housingType = housingType;
    this.numberOfInhabitants = numberOfInhabitants;
    setAdditionalInformation(additionalInformation);
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
    return numberOfInhabitants;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ThermalHouseInput that)) return false;
    if (!super.equals(o)) return false;
    return QuantityUtils.equals(ethLosses, that.ethLosses)
        && QuantityUtils.equals(ethCapa, that.ethCapa)
        && QuantityUtils.equals(targetTemperature, that.targetTemperature)
        && QuantityUtils.equals(upperTemperatureLimit, that.upperTemperatureLimit)
        && QuantityUtils.equals(lowerTemperatureLimit, that.lowerTemperatureLimit)
        && Objects.equals(housingType, that.housingType)
        && numberOfInhabitants == that.numberOfInhabitants;
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
        numberOfInhabitants);
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
        + ", thermalBus="
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
        + ", numberOfInhabitants="
        + numberOfInhabitants
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public ThermalHouseInputCopyBuilder copy() {
    return new ThermalHouseInputCopyBuilder(this);
  }

  public static class ThermalHouseInputCopyBuilder
      extends ThermalSinkInputCopyBuilder<ThermalHouseInputCopyBuilder> {
    private ComparableQuantity<ThermalConductance> ethLosses;

    private ComparableQuantity<HeatCapacity> ethCapa;

    private ComparableQuantity<Temperature> targetTemperature;

    private ComparableQuantity<Temperature> upperTemperatureLimit;

    private ComparableQuantity<Temperature> lowerTemperatureLimit;

    private String housingType;

    private double numberOfInhabitants;

    protected ThermalHouseInputCopyBuilder(ThermalHouseInput entity) {
      super(entity);
      this.ethLosses = entity.ethLosses;
      this.ethCapa = entity.ethCapa;
      this.targetTemperature = entity.targetTemperature;
      this.upperTemperatureLimit = entity.upperTemperatureLimit;
      this.lowerTemperatureLimit = entity.lowerTemperatureLimit;
      this.housingType = entity.housingType;
      this.numberOfInhabitants = entity.numberOfInhabitants;
    }

    public ThermalHouseInputCopyBuilder ethLosses(
        ComparableQuantity<ThermalConductance> ethLosses) {
      this.ethLosses = ethLosses;
      return thisInstance();
    }

    protected ComparableQuantity<ThermalConductance> getEthLosses() {
      return ethLosses;
    }

    public ThermalHouseInputCopyBuilder ethCapa(ComparableQuantity<HeatCapacity> ethCapa) {
      this.ethCapa = ethCapa;
      return thisInstance();
    }

    protected ComparableQuantity<HeatCapacity> getEthCapa() {
      return ethCapa;
    }

    public ThermalHouseInputCopyBuilder targetTemperature(
        ComparableQuantity<Temperature> targetTemperature) {
      this.targetTemperature = targetTemperature;
      return thisInstance();
    }

    protected ComparableQuantity<Temperature> getTargetTemperature() {
      return targetTemperature;
    }

    public ThermalHouseInputCopyBuilder upperTemperatureLimit(
        ComparableQuantity<Temperature> upperTemperatureLimit) {
      this.upperTemperatureLimit = upperTemperatureLimit;
      return thisInstance();
    }

    protected ComparableQuantity<Temperature> getUpperTemperatureLimit() {
      return upperTemperatureLimit;
    }

    public ThermalHouseInputCopyBuilder lowerTemperatureLimit(
        ComparableQuantity<Temperature> lowerTemperatureLimit) {
      this.lowerTemperatureLimit = lowerTemperatureLimit;
      return thisInstance();
    }

    protected ComparableQuantity<Temperature> getLowerTemperatureLimit() {
      return lowerTemperatureLimit;
    }

    public ThermalHouseInputCopyBuilder housingType(String housingType) {
      this.housingType = housingType;
      return thisInstance();
    }

    protected String getHousingType() {
      return housingType;
    }

    public ThermalHouseInputCopyBuilder numberOfInhabitants(double numberOfInhabitants) {
      this.numberOfInhabitants = numberOfInhabitants;
      return thisInstance();
    }

    protected double getNumberOfInhabitants() {
      return numberOfInhabitants;
    }

    @Override
    public ThermalHouseInputCopyBuilder scale(double factor) {
      // scale losses as well as capacity and number of inhabitants to keep equal
      // the time needed to heat a scaled house
      ethLosses(ethLosses.multiply(factor));
      ethCapa(ethCapa.multiply(factor));
      numberOfInhabitants(numberOfInhabitants * factor);
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
          numberOfInhabitants,
          getAdditionalInformation());
    }

    @Override
    protected ThermalHouseInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
