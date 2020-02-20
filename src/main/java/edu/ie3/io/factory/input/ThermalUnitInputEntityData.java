/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.thermal.ThermalBusInput;
import java.util.Map;

public class ThermalUnitInputEntityData extends AssetInputEntityData {
  private final ThermalBusInput busInput;

  public ThermalUnitInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      ThermalBusInput busInput) {
    super(fieldsToAttributes, entityClass);
    this.busInput = busInput;
  }

  public ThermalUnitInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operatorInput,
      ThermalBusInput busInput) {
    super(fieldsToAttributes, entityClass, operatorInput);
    this.busInput = busInput;
  }

  public ThermalBusInput getBusInput() {
    return busInput;
  }
}
