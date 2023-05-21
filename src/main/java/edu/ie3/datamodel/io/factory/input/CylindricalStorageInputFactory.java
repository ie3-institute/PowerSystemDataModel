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
import edu.ie3.util.quantities.interfaces.VolumetricFlowRate;
import java.util.UUID;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Volume;
import tech.units.indriya.ComparableQuantity;

public class CylindricalStorageInputFactory
    extends AssetInputEntityFactory<CylindricalStorageInput, ThermalUnitInputEntityData> {
  private static final String STORAGE_VOLUME_LVL = "storagevolumelvl";
  private static final String STORAGE_VOLUME_LVL_MIN = "storagevolumelvlmin";
  private static final String INLET_TEMP = "inlettemp";
  private static final String RETURN_TEMP = "returntemp";
  private static final String C = "c";
  private static final String INLET_FLOW_RATE = "inletflowrate";
  private static final String OUTLET_FLOW_RATE = "outletflowrate";

  public CylindricalStorageInputFactory() {
    super(CylindricalStorageInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {
      STORAGE_VOLUME_LVL,
      STORAGE_VOLUME_LVL_MIN,
      INLET_TEMP,
      RETURN_TEMP,
      C,
      INLET_FLOW_RATE,
      OUTLET_FLOW_RATE
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
    final ComparableQuantity<VolumetricFlowRate> inletFlowRate =
        data.getQuantity(INLET_FLOW_RATE, StandardUnits.VOLUMETRIC_FLOW_RATE);
    final ComparableQuantity<VolumetricFlowRate> outletFlowRate =
        data.getQuantity(OUTLET_FLOW_RATE, StandardUnits.VOLUMETRIC_FLOW_RATE);

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
        inletFlowRate,
        outletFlowRate);
  }
}
