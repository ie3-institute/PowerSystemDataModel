/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import edu.ie3.util.quantities.interfaces.HeatCapacity;
import edu.ie3.util.quantities.interfaces.ThermalConductance;
import tech.units.indriya.ComparableQuantity;

import javax.measure.quantity.Temperature;
import java.util.Map;
import java.util.UUID;

import static edu.ie3.datamodel.models.StandardUnits.*;

public class ThermalHouseInputFactory extends AssetInputEntityFactory<ThermalHouseInput> {

  private final Map<UUID, ThermalBusInput> thermalBuses;

  public ThermalHouseInputFactory(
      Map<UUID, OperatorInput> operators, Map<UUID, ThermalBusInput> thermalBuses) {
    super(operators, ThermalHouseInput.class);
    this.thermalBuses = thermalBuses;
  }

  @Override
  protected ThermalHouseInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {
    ComparableQuantity<ThermalConductance> ethLosses = getQuantity(data, ETH_LOSSES, THERMAL_TRANSMISSION);
    ComparableQuantity<HeatCapacity> ethCapa = getQuantity(data, ETH_CAPA, HEAT_CAPACITY);
    ComparableQuantity<Temperature> targetTemperature = getQuantity(data, TARGET_TEMPERATURE, TEMPERATURE);
    ComparableQuantity<Temperature> upperTemperatureLimit = getQuantity(data, UPPER_TEMPERATURE_LIMIT, TEMPERATURE);
    ComparableQuantity<Temperature> lowerTemperatureLimit = getQuantity(data, LOWER_TEMPERATURE_LIMIT, TEMPERATURE);
    String housingType = getField(data, HOUSING_TYPE);
    double numberInhabitants = getDouble(data, NUMBER_INHABITANTS);
    return new ThermalHouseInput(
        uuid,
        id,
        operator,
        operationTime,
        getEntity(data, THERMAL_BUS, thermalBuses),
        ethLosses,
        ethCapa,
        targetTemperature,
        upperTemperatureLimit,
        lowerTemperatureLimit,
        housingType,
        numberInhabitants,
        data);
  }
}
