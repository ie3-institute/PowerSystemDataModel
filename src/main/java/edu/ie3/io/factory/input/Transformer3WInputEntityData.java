/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.connector.type.Transformer3WTypeInput;
import java.util.Map;

public class Transformer3WInputEntityData extends ConnectorInputEntityData {
  private final NodeInput nodeC;
  private final Transformer3WTypeInput type;

  public Transformer3WInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      NodeInput nodeA,
      NodeInput nodeB,
      NodeInput nodeC,
      Transformer3WTypeInput type) {
    super(fieldsToAttributes, entityClass, nodeA, nodeB);
    this.nodeC = nodeC;
    this.type = type;
  }

  public Transformer3WInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operatorInput,
      NodeInput nodeA,
      NodeInput nodeB,
      NodeInput nodeC,
      Transformer3WTypeInput type) {
    super(fieldsToAttributes, entityClass, operatorInput, nodeA, nodeB);
    this.nodeC = nodeC;
    this.type = type;
  }

  public NodeInput getNodeC() {
    return nodeC;
  }

  public Transformer3WTypeInput getType() {
    return type;
  }
}
