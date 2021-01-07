/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import java.util.Map;
import java.util.Objects;

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
      OperatorInput operator,
      ThermalBusInput busInput) {
    super(fieldsToAttributes, entityClass, operator);
    this.busInput = busInput;
  }

  public ThermalBusInput getBusInput() {
    return busInput;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ThermalUnitInputEntityData)) return false;
    if (!super.equals(o)) return false;
    ThermalUnitInputEntityData that = (ThermalUnitInputEntityData) o;
    return busInput.equals(that.busInput);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), busInput);
  }
}
