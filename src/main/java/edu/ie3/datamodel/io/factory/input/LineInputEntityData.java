/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import java.util.Map;

public class LineInputEntityData extends ConnectorInputEntityData {
  private final LineTypeInput type;

  public LineInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      NodeInput nodeA,
      NodeInput nodeB,
      LineTypeInput type) {
    super(fieldsToAttributes, entityClass, nodeA, nodeB);
    this.type = type;
  }

  public LineInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operatorInput,
      NodeInput nodeA,
      NodeInput nodeB,
      LineTypeInput type) {
    super(fieldsToAttributes, entityClass, operatorInput, nodeA, nodeB);
    this.type = type;
  }

  public LineTypeInput getType() {
    return type;
  }
}
