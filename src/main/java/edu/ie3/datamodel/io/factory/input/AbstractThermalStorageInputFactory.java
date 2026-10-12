/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.thermal.AbstractStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity;
import java.util.Map;
import java.util.UUID;
import javax.measure.quantity.Power;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Volume;
import tech.units.indriya.ComparableQuantity;

public abstract class AbstractThermalStorageInputFactory<T extends AbstractStorageInput>
    extends AssetInputEntityFactory<T> {

  private final Map<UUID, ThermalBusInput> thermalBuses;

  protected AbstractThermalStorageInputFactory(
      Map<UUID, OperatorInput> operators, Map<UUID, ThermalBusInput> thermalBuses, Class<T> clazz) {
    super(operators, clazz);
    this.thermalBuses = thermalBuses;
  }

  protected ThermalBusInput getBus(Map<String, String> data) {
    return getEntity(data, THERMAL_BUS, thermalBuses);
  }

  protected ComparableQuantity<Volume> getStorageVolumeLvl(Map<String, String> data) {
    return getQuantity(data, STORAGE_VOLUME_LVL, StandardUnits.VOLUME);
  }

  protected ComparableQuantity<Temperature> getInletTemp(Map<String, String> data) {
    return getQuantity(data, INLET_TEMP, StandardUnits.TEMPERATURE);
  }

  protected ComparableQuantity<Temperature> getReturnTemp(Map<String, String> data) {
    return getQuantity(data, RETURN_TEMP, StandardUnits.TEMPERATURE);
  }

  protected ComparableQuantity<SpecificHeatCapacity> getSpecificHeatCapacity(
      Map<String, String> data) {
    return getQuantity(data, C, StandardUnits.SPECIFIC_HEAT_CAPACITY);
  }

  protected ComparableQuantity<Power> getMaxThermalPower(Map<String, String> data) {
    return getQuantity(data, P_THERMAL_MAX, StandardUnits.ACTIVE_POWER_IN);
  }
}
