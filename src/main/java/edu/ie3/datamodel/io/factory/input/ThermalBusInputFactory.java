/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import java.util.Map;
import java.util.UUID;

public class ThermalBusInputFactory extends AssetInputEntityFactory<ThermalBusInput> {
  public ThermalBusInputFactory(Map<UUID, OperatorInput> operators) {
    super(operators, ThermalBusInput.class);
  }

  @Override
  protected ThermalBusInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {
    return new ThermalBusInput(uuid, id, operator, operationTime, data);
  }
}
