/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import java.util.UUID;

public class CylindricalStorageInputFactory
    extends AbstractThermalStorageInputFactory<CylindricalStorageInput> {

  public CylindricalStorageInputFactory() {
    super(CylindricalStorageInput.class);
  }

  @Override
  protected CylindricalStorageInput buildModel(
      ThermalUnitInputEntityData data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {

    final ThermalBusInput bus = data.getBusInput();
    return new CylindricalStorageInput(
        uuid,
        id,
        operator,
        operationTime,
        bus,
        getStorageVolumeLvl(data),
        getInletTemp(data),
        getReturnTemp(data),
        getSpecificHeatCapacity(data),
        getMaxThermalPower(data));
  }
}
