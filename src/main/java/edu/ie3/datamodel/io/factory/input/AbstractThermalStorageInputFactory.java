/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity;
import javax.measure.quantity.Power;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Volume;
import tech.units.indriya.ComparableQuantity;

public abstract class AbstractThermalStorageInputFactory<T extends AssetInput>
extends AssetInputEntityFactory<T, ThermalUnitInputEntityData> {

  private static final String STORAGE_VOLUME_LVL = "storageVolumeLvl";
  private static final String INLET_TEMP = "inletTemp";
  private static final String RETURN_TEMP = "returnTemp";
  private static final String C = "c";
  private static final String P_THERMAL_MAX = "pThermalMax";

  public AbstractThermalStorageInputFactory(Class<T> clazz) {
    super(clazz);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {STORAGE_VOLUME_LVL, INLET_TEMP, RETURN_TEMP, C, P_THERMAL_MAX};
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
