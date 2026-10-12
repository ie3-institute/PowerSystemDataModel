/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.io.factory.input.AssetInputEntityData;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.type.EvTypeInput;
import java.util.Map;
import java.util.Objects;

/**
 * Data used for the construction of {@link edu.ie3.datamodel.models.input.system.EvInput} entities.
 * Extends {@link AssetInputEntityData} by the required {@link EvTypeInput}.
 */
public class EvInputEntityData extends AssetInputEntityData {

  private final EvTypeInput typeInput;

  /**
   * Creates a new EvInputEntityData object without operator.
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param entityClass class of the entity to be created with this data
   * @param typeInput type input
   */
  public EvInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      EvTypeInput typeInput) {
    super(fieldsToAttributes, entityClass);
    this.typeInput = typeInput;
  }

  /**
   * Creates a new EvInputEntityData object with operator.
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param entityClass class of the entity to be created with this data
   * @param operator operator input
   * @param typeInput type input
   */
  public EvInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operator,
      EvTypeInput typeInput) {
    super(fieldsToAttributes, entityClass, operator);
    this.typeInput = typeInput;
  }

  public EvTypeInput getTypeInput() {
    return typeInput;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    EvInputEntityData that = (EvInputEntityData) o;
    return Objects.equals(typeInput, that.typeInput);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), typeInput);
  }

  @Override
  public String toString() {
    return "EvInputEntityData{"
        + "typeInput="
        + typeInput.getUuid()
        + ", operatorInput="
        + getOperatorInput().getUuid()
        + ", fieldsToValues="
        + getFieldsToValues()
        + ", targetClass="
        + getTargetClass()
        + '}';
  }
}
