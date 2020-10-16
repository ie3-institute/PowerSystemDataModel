/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Map;
import java.util.Objects;

/**
 * Data used by {@link ConnectorInputEntityFactory} to create an instance of {@link
 * edu.ie3.datamodel.models.input.connector.ConnectorInput}, thus needing additional information
 * about the {@link edu.ie3.datamodel.models.input.NodeInput}, which cannot be provided through the
 * attribute map.
 */
public class ConnectorInputEntityData extends AssetInputEntityData {
  private final NodeInput nodeA;
  private final NodeInput nodeB;

  public ConnectorInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      NodeInput nodeA,
      NodeInput nodeB) {
    super(fieldsToAttributes, entityClass);
    this.nodeA = nodeA;
    this.nodeB = nodeB;
  }

  public ConnectorInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operator,
      NodeInput nodeA,
      NodeInput nodeB) {
    super(fieldsToAttributes, entityClass, operator);
    this.nodeA = nodeA;
    this.nodeB = nodeB;
  }

  public NodeInput getNodeA() {
    return nodeA;
  }

  public NodeInput getNodeB() {
    return nodeB;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ConnectorInputEntityData that = (ConnectorInputEntityData) o;
    return nodeA.equals(that.nodeA) && nodeB.equals(that.nodeB);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), nodeA, nodeB);
  }

  @Override
  public String toString() {
    return "ConnectorInputEntityData{"
        + "fieldsToValues="
        + getFieldsToValues()
        + ", targetClass="
        + getTargetClass()
        + ", operatorInput="
        + getOperatorInput().getUuid()
        + ", nodeA="
        + nodeA.getUuid()
        + ", nodeB="
        + nodeB.getUuid()
        + '}';
  }
}
