/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import java.util.Map;

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
}
