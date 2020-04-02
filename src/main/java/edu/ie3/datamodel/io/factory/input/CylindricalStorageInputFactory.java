/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity;
import tec.uom.se.ComparableQuantity;

import java.util.UUID;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Volume;

public class CylindricalStorageInputFactory
    extends AssetInputEntityFactory<CylindricalStorageInput, ThermalUnitInputEntityData> {
  private static final String STORAGE_VOLUME_LVL = "storagevolumelvl";
  private static final String STORAGE_VOLUME_LVL_MIN = "storagevolumelvlmin";
  private static final String INLET_TEMP = "inlettemp";
  private static final String RETURN_TEMP = "returntemp";
  private static final String C = "c";

  public CylindricalStorageInputFactory() {
    super(CylindricalStorageInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {STORAGE_VOLUME_LVL, STORAGE_VOLUME_LVL_MIN, INLET_TEMP, RETURN_TEMP, C};
  }

  @Override
  protected CylindricalStorageInput buildModel(
      ThermalUnitInputEntityData data,
      UUID uuid,
      String id,
      OperatorInput operatorInput,
      OperationTime operationTime) {
    final ThermalBusInput bus = data.getBusInput();
    final ComparableQuantity<Volume> storageVolumeLvl = // TODO doublecheck
        data.getQuantity(STORAGE_VOLUME_LVL, StandardUnits.VOLUME);
    final ComparableQuantity<Volume> storageVolumeLvlMin = // TODO doublecheck
        data.getQuantity(STORAGE_VOLUME_LVL_MIN, StandardUnits.VOLUME);
    final ComparableQuantity<Temperature> inletTemp = data.getQuantity(INLET_TEMP, StandardUnits.TEMPERATURE); // TODO doublecheck
    final ComparableQuantity<Temperature> returnTemp = // TODO doublecheck
        data.getQuantity(RETURN_TEMP, StandardUnits.TEMPERATURE);
    final ComparableQuantity<SpecificHeatCapacity> c = // TODO doublecheck
        data.getQuantity(C, StandardUnits.SPECIFIC_HEAT_CAPACITY);
    return new CylindricalStorageInput(
        uuid, id, bus, storageVolumeLvl, storageVolumeLvlMin, inletTemp, returnTemp, c);
  }
}
