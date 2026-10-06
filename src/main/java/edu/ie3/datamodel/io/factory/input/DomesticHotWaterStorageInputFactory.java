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
import java.util.Map;
import java.util.UUID;

public class DomesticHotWaterStorageInputFactory
    extends AbstractThermalStorageInputFactory<DomesticHotWaterStorageInput> {

  public DomesticHotWaterStorageInputFactory(
      Map<UUID, OperatorInput> operators, Map<UUID, ThermalBusInput> thermalBuses) {
    super(operators, thermalBuses, DomesticHotWaterStorageInput.class);
  }

  @Override
  protected DomesticHotWaterStorageInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {

    return new DomesticHotWaterStorageInput(
        uuid,
        id,
        operator,
        operationTime,
        getBus(data),
        getStorageVolumeLvl(data),
        getInletTemp(data),
        getReturnTemp(data),
        getSpecificHeatCapacity(data),
        getMaxThermalPower(data),
        data);
  }
}
