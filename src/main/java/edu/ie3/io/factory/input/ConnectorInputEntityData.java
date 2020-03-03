/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import java.util.Map;

/**
 * Data used by {@link ConnectorInputEntityFactory} to create an instance of {@link
 * edu.ie3.models.input.connector.ConnectorInput}, thus needing additional information about the
 * {@link edu.ie3.models.input.NodeInput}, which cannot be provided through the attribute map.
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
      OperatorInput operatorInput,
      NodeInput nodeA,
      NodeInput nodeB) {
    super(fieldsToAttributes, entityClass, operatorInput);
    this.nodeA = nodeA;
    this.nodeB = nodeB;
  }

  public NodeInput getNodeA() {
    return nodeA;
  }

  public NodeInput getNodeB() {
    return nodeB;
  }
}
