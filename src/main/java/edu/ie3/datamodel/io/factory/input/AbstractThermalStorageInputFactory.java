/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.thermal.AbstractStorageInput;
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity;
import javax.measure.quantity.Power;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Volume;
import tech.units.indriya.ComparableQuantity;

public abstract class AbstractThermalStorageInputFactory<T extends AbstractStorageInput>
    extends AssetInputEntityFactory<T, ThermalUnitInputEntityData> {

  protected AbstractThermalStorageInputFactory(Class<T> clazz) {
    super(clazz);
  }

  protected ComparableQuantity<Volume> getStorageVolumeLvl(ThermalUnitInputEntityData data) {
    return data.getQuantity(STORAGE_VOLUME_LVL, StandardUnits.VOLUME);
  }

  protected ComparableQuantity<Temperature> getInletTemp(ThermalUnitInputEntityData data) {
    return data.getQuantity(INLET_TEMP, StandardUnits.TEMPERATURE);
  }

  protected ComparableQuantity<Temperature> getReturnTemp(ThermalUnitInputEntityData data) {
    return data.getQuantity(RETURN_TEMP, StandardUnits.TEMPERATURE);
  }

  protected ComparableQuantity<SpecificHeatCapacity> getSpecificHeatCapacity(
      ThermalUnitInputEntityData data) {
    return data.getQuantity(C, StandardUnits.SPECIFIC_HEAT_CAPACITY);
  }

  protected ComparableQuantity<Power> getMaxThermalPower(ThermalUnitInputEntityData data) {
    return data.getQuantity(P_THERMAL_MAX, StandardUnits.ACTIVE_POWER_IN);
  }
}
