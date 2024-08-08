/*
 * © 2021. TU Dortmund University,
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
import java.util.UUID;
import javax.measure.quantity.Power;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Volume;
import tech.units.indriya.ComparableQuantity;

public class CylindricalStorageInputFactory
    extends AssetInputEntityFactory<CylindricalStorageInput, ThermalUnitInputEntityData> {
  private static final String STORAGE_VOLUME_LVL = "storageVolumeLvl";
  private static final String STORAGE_VOLUME_LVL_MIN = "storageVolumeLvlMin";
  private static final String INLET_TEMP = "inletTemp";
  private static final String RETURN_TEMP = "returnTemp";
  private static final String C = "c";
  private static final String P_THERMAL_MAX = "pThermalMax";

  public CylindricalStorageInputFactory() {
    super(CylindricalStorageInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {
      STORAGE_VOLUME_LVL, STORAGE_VOLUME_LVL_MIN, INLET_TEMP, RETURN_TEMP, C, P_THERMAL_MAX
    };
  }

  @Override
  protected CylindricalStorageInput buildModel(
      ThermalUnitInputEntityData data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {
    final ThermalBusInput bus = data.getBusInput();
    final ComparableQuantity<Volume> storageVolumeLvl =
        data.getQuantity(STORAGE_VOLUME_LVL, StandardUnits.VOLUME);
    final ComparableQuantity<Volume> storageVolumeLvlMin =
        data.getQuantity(STORAGE_VOLUME_LVL_MIN, StandardUnits.VOLUME);
    final ComparableQuantity<Temperature> inletTemp =
        data.getQuantity(INLET_TEMP, StandardUnits.TEMPERATURE);
    final ComparableQuantity<Temperature> returnTemp =
        data.getQuantity(RETURN_TEMP, StandardUnits.TEMPERATURE);
    final ComparableQuantity<SpecificHeatCapacity> c =
        data.getQuantity(C, StandardUnits.SPECIFIC_HEAT_CAPACITY);
    final ComparableQuantity<Power> pThermalMax =
        data.getQuantity(P_THERMAL_MAX, StandardUnits.ACTIVE_POWER_IN);
    return new CylindricalStorageInput(
        uuid,
        id,
        operator,
        operationTime,
        bus,
        storageVolumeLvl,
        storageVolumeLvlMin,
        inletTemp,
        returnTemp,
        c,
        pThermalMax);
  }
}
