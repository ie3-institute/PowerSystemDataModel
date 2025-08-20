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

/** The type Thermal unit input entity data. */
public class ThermalUnitInputEntityData extends AssetInputEntityData {
  private final ThermalBusInput busInput;

  /**
   * Instantiates a new Thermal unit input entity data.
   *
   * @param fieldsToAttributes the fields to attributes
   * @param entityClass the entity class
   * @param busInput the bus input
   */
  public ThermalUnitInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      ThermalBusInput busInput) {
    super(fieldsToAttributes, entityClass);
    this.busInput = busInput;
  }

  /**
   * Instantiates a new Thermal unit input entity data.
   *
   * @param fieldsToAttributes the fields to attributes
   * @param entityClass the entity class
   * @param operator the operator
   * @param busInput the bus input
   */
  public ThermalUnitInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operator,
      ThermalBusInput busInput) {
    super(fieldsToAttributes, entityClass, operator);
    this.busInput = busInput;
  }

  /**
   * Creates a new ThermalUnitInputEntityData object based on a given {@link AssetInputEntityData}
   * object and a bus input
   *
   * @param entityData The AssetInputEntityData object to enhance
   * @param busInput The thermal bus input
   */
  public ThermalUnitInputEntityData(AssetInputEntityData entityData, ThermalBusInput busInput) {
    super(entityData, entityData.getOperatorInput());
    this.busInput = busInput;
  }

  /**
   * Gets bus input.
   *
   * @return the bus input
   */
  public ThermalBusInput getBusInput() {
    return busInput;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ThermalUnitInputEntityData that)) return false;
    if (!super.equals(o)) return false;
    return busInput.equals(that.busInput);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), busInput);
  }
}
