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

public class Transformer3WInputEntityData
    extends TypedConnectorInputEntityData<Transformer3WTypeInput> {
  private final NodeInput nodeC;

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
