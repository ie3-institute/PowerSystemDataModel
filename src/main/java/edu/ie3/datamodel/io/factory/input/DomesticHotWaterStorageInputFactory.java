/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.thermal.DomesticHotWaterStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import java.util.UUID;

/** The type Domestic hot water storage input factory. */
public class DomesticHotWaterStorageInputFactory
    extends AbstractThermalStorageInputFactory<DomesticHotWaterStorageInput> {

  /** Instantiates a new Domestic hot water storage input factory. */
  public DomesticHotWaterStorageInputFactory() {
    super(DomesticHotWaterStorageInput.class);
  }

  @Override
  protected DomesticHotWaterStorageInput buildModel(
      ThermalUnitInputEntityData data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {

    final ThermalBusInput bus = data.getBusInput();
    return new DomesticHotWaterStorageInput(
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
