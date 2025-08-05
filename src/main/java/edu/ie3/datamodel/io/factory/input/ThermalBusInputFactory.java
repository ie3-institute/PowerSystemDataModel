/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import java.util.UUID;

/** The type Thermal bus input factory. */
public class ThermalBusInputFactory
    extends AssetInputEntityFactory<ThermalBusInput, AssetInputEntityData> {
  /** Instantiates a new Thermal bus input factory. */
  public ThermalBusInputFactory() {
    super(ThermalBusInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[0];
  }

  @Override
  protected ThermalBusInput buildModel(
      AssetInputEntityData data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {
    return new ThermalBusInput(uuid, id, operator, operationTime);
  }
}
