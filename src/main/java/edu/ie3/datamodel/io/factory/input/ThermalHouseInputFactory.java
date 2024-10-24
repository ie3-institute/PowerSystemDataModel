/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import edu.ie3.util.quantities.interfaces.HeatCapacity;
import edu.ie3.util.quantities.interfaces.ThermalConductance;
import java.util.UUID;
import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;

public class ThermalHouseInputFactory
    extends AssetInputEntityFactory<ThermalHouseInput, ThermalUnitInputEntityData> {
  private static final String ETH_LOSSES = "ethLosses";
  private static final String ETH_CAPA = "ethCapa";
  private static final String TARGET_TEMPERATURE = "targetTemperature";
  private static final String UPPER_TEMPERATURE_LIMIT = "upperTemperatureLimit";
  private static final String LOWER_TEMPERATURE_LIMIT = "lowerTemperatureLimit";
  private static final String HOUSING_TYPE = "housingType";
  private static final String NUMBER_INHABITANTS = "numberInhabitants";

  public ThermalHouseInputFactory() {
    super(ThermalHouseInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {
      ETH_LOSSES,
      ETH_CAPA,
      TARGET_TEMPERATURE,
      UPPER_TEMPERATURE_LIMIT,
      LOWER_TEMPERATURE_LIMIT,
      HOUSING_TYPE,
      NUMBER_INHABITANTS
    };
  }

  @Override
  protected ThermalHouseInput buildModel(
      ThermalUnitInputEntityData data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {
    final ThermalBusInput busInput = data.getBusInput();
    final ComparableQuantity<ThermalConductance> ethLosses =
        data.getQuantity(ETH_LOSSES, StandardUnits.THERMAL_TRANSMISSION);
    final ComparableQuantity<HeatCapacity> ethCapa =
        data.getQuantity(ETH_CAPA, StandardUnits.HEAT_CAPACITY);
    final ComparableQuantity<Temperature> targetTemperature =
        data.getQuantity(TARGET_TEMPERATURE, StandardUnits.TEMPERATURE);
    final ComparableQuantity<Temperature> upperTemperatureLimit =
        data.getQuantity(UPPER_TEMPERATURE_LIMIT, StandardUnits.TEMPERATURE);
    final ComparableQuantity<Temperature> lowerTemperatureLimit =
        data.getQuantity(LOWER_TEMPERATURE_LIMIT, StandardUnits.TEMPERATURE);
    final String housingType = data.getField(HOUSING_TYPE);
    final Integer numberInhabitants = data.getInt(NUMBER_INHABITANTS);
    return new ThermalHouseInput(
        uuid,
        id,
        operator,
        operationTime,
        busInput,
        ethLosses,
        ethCapa,
        targetTemperature,
        upperTemperatureLimit,
        lowerTemperatureLimit,
        housingType,
        numberInhabitants);
  }
}
