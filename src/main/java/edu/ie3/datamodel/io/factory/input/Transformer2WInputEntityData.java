/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import java.util.Map;

public class Transformer2WInputEntityData extends ConnectorInputEntityData {
  private final Transformer2WTypeInput type;

  public Transformer2WInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      NodeInput nodeA,
      NodeInput nodeB,
      Transformer2WTypeInput type) {
    super(fieldsToAttributes, entityClass, nodeA, nodeB);
    this.type = type;
  }

  public Transformer2WInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operatorInput,
      NodeInput nodeA,
      NodeInput nodeB,
      Transformer2WTypeInput type) {
    super(fieldsToAttributes, entityClass, operatorInput, nodeA, nodeB);
    this.type = type;
  }

  public Transformer2WTypeInput getType() {
    return type;
  }
}
