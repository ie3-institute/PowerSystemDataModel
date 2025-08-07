/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import java.util.Map;
import java.util.Objects;

/** The type Transformer 3 w input entity data. */
public class Transformer3WInputEntityData
    extends TypedConnectorInputEntityData<Transformer3WTypeInput> {
  private final NodeInput nodeC;

  /**
   * Instantiates a new Transformer 3 w input entity data.
   *
   * @param fieldsToAttributes the fields to attributes
   * @param entityClass the entity class
   * @param nodeA the node a
   * @param nodeB the node b
   * @param nodeC the node c
   * @param type the type
   */
  public Transformer3WInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      NodeInput nodeA,
      NodeInput nodeB,
      NodeInput nodeC,
      Transformer3WTypeInput type) {
    super(fieldsToAttributes, entityClass, nodeA, nodeB, type);
    this.nodeC = nodeC;
  }

  /**
   * Instantiates a new Transformer 3 w input entity data.
   *
   * @param fieldsToAttributes the fields to attributes
   * @param entityClass the entity class
   * @param operator the operator
   * @param nodeA the node a
   * @param nodeB the node b
   * @param nodeC the node c
   * @param type the type
   */
  public Transformer3WInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operator,
      NodeInput nodeA,
      NodeInput nodeB,
      NodeInput nodeC,
      Transformer3WTypeInput type) {
    super(fieldsToAttributes, entityClass, operator, nodeA, nodeB, type);
    this.nodeC = nodeC;
  }

  /**
   * Creates a new Transformer3WInputEntityData object based on a given {@link
   * ConnectorInputEntityData}* object, a given third node as well as a given {@link
   * Transformer3WTypeInput}*.
   *
   * @param entityData The TypedConnectorInputEntityData object to enhance
   * @param nodeC The third node
   * @param type of the transformer
   */
  public Transformer3WInputEntityData(
      ConnectorInputEntityData entityData, NodeInput nodeC, Transformer3WTypeInput type) {
    super(entityData, type);
    this.nodeC = nodeC;
  }

  /**
   * Creates a new Transformer3WInputEntityData object based on a given {@link
   * TypedConnectorInputEntityData}* object and given third node
   *
   * @param entityData The TypedConnectorInputEntityData object to enhance
   * @param nodeC The third node
   */
  public Transformer3WInputEntityData(
      TypedConnectorInputEntityData<Transformer3WTypeInput> entityData, NodeInput nodeC) {
    super(entityData, entityData.getType());
    this.nodeC = nodeC;
  }

  /**
   * Gets node c.
   *
   * @return the node c
   */
  public NodeInput getNodeC() {
    return nodeC;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Transformer3WInputEntityData that = (Transformer3WInputEntityData) o;
    return Objects.equals(nodeC, that.nodeC);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), nodeC);
  }

  @Override
  public String toString() {
    return "Transformer3WInputEntityData{"
        + "fieldsToValues="
        + getFieldsToValues()
        + ", targetClass="
        + getTargetClass()
        + ", operatorInput="
        + getOperatorInput()
        + ", nodeA="
        + getNodeA()
        + ", nodeB="
        + getNodeB()
        + ", nodeC="
        + nodeC
        + ", type="
        + getType()
        + '}';
  }
}
