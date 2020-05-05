/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Map;
import java.util.Objects;

/**
 * Data used for those classes of {@link edu.ie3.datamodel.models.input.connector.ConnectorInput}
 * that need an instance of some type T of {@link
 * edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput} as well.
 *
 * @param <T> Subclass of {@link AssetTypeInput} that is required for the construction of the
 *     ConnectorInput
 */
public class TypedConnectorInputEntityData<T extends AssetTypeInput>
    extends ConnectorInputEntityData {

  private final T type;

  /**
   * Creates a new TypedConnectorInputEntityData object for a connector input that needs a type
   * input as well. It sets the operator to default.
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param entityClass class of the entity to be created with this data
   * @param nodeA input nodeA
   * @param nodeB input nodeB
   * @param type type input
   */
  public TypedConnectorInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      NodeInput nodeA,
      NodeInput nodeB,
      T type) {
    super(fieldsToAttributes, entityClass, nodeA, nodeB);
    this.type = type;
  }

  /**
   * Creates a new TypedConnectorInputEntityData object for an operable connector input input that
   * input that needs a type input as well
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param entityClass class of the entity to be created with this data
   * @param operator specific operator to use
   * @param nodeA input nodeA
   * @param nodeB input nodeB
   * @param type type input
   */
  public TypedConnectorInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operator,
      NodeInput nodeA,
      NodeInput nodeB,
      T type) {
    super(fieldsToAttributes, entityClass, operator, nodeA, nodeB);
    this.type = type;
  }

  public T getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    TypedConnectorInputEntityData<?> that = (TypedConnectorInputEntityData<?>) o;
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type);
  }

  @Override
  public String toString() {
    return "TypedConnectorInputEntityData{"
        + "fieldsToValues="
        + getFieldsToValues()
        + ", entityClass="
        + getEntityClass()
        + ", operatorInput="
        + getOperatorInput()
        + ", nodeA="
        + getNodeA()
        + ", nodeB="
        + getNodeB()
        + ", type="
        + type
        + '}';
  }
}
