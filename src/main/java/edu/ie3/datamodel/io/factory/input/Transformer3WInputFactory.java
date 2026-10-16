/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import java.util.Map;
import java.util.UUID;

public class Transformer3WInputFactory extends ConnectorInputEntityFactory<Transformer3WInput> {

  private final Map<UUID, Transformer3WTypeInput> types;

  public Transformer3WInputFactory(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, Transformer3WTypeInput> types) {
    super(operators, nodes, Transformer3WInput.class);
    this.types = types;
  }

  @Override
  protected Transformer3WInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      OperatorInput operator,
      OperationTime operationTime) {
    int parallelDevices = getInt(data, PARALLEL_DEVICES);
    NodeInput nodeC = getEntity(data, NODE_C, nodes);
    Transformer3WTypeInput type = getType(data, types);
    int tapPos = getInt(data, TAP_POS);
    boolean autoTap = getBoolean(data, AUTO_TAP);

    return new Transformer3WInput(
        uuid,
        id,
        operator,
        operationTime,
        nodeA,
        nodeB,
        nodeC,
        parallelDevices,
        type,
        tapPos,
        autoTap,
        data);
  }
}
