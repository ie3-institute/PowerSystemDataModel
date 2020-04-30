/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Map;
import java.util.Objects;

/**
 * Data used for the construction of {@link edu.ie3.datamodel.models.input.AssetInput} entities.
 * This data object can include additional information about the {@link OperatorInput}, which cannot
 * be provided through the attribute map.
 */
public class AssetInputEntityData extends EntityData {
  private final OperatorInput operator;

  /**
   * Creates a new AssetInputEntityData object without operator.
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param entityClass class of the entity to be created with this data
   */
  public AssetInputEntityData(
      Map<String, String> fieldsToAttributes, Class<? extends UniqueEntity> entityClass) {
    this(fieldsToAttributes, entityClass, OperatorInput.NO_OPERATOR_ASSIGNED);
  }

  /**
   * Creates a new AssetInputEntityData object with operator.
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param entityClass class of the entity to be created with this data
   * @param operator operator input
   */
  public AssetInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operator) {
    super(fieldsToAttributes, entityClass);
    this.operator = operator;
  }

  public OperatorInput getOperatorInput() {
    return operator;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    AssetInputEntityData that = (AssetInputEntityData) o;
    return operator.equals(that.operator);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), operator);
  }

  @Override
  public String toString() {
    return "AssetInputEntityData{"
        + "fieldsToValues="
        + getFieldsToValues()
        + ", entityClass="
        + getEntityClass()
        + ", operatorInput="
        + operator
        + "} ";
  }
}
