/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import java.util.Map;
import java.util.UUID;

public class Transformer2WInputFactory extends ConnectorInputEntityFactory<Transformer2WInput> {

  private final Map<UUID, Transformer2WTypeInput> types;

  public Transformer2WInputFactory(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, Transformer2WTypeInput> types) {
    super(operators, nodes, Transformer2WInput.class);
    this.types = types;
  }

  @Override
  protected Transformer2WInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      OperatorInput operator,
      OperationTime operationTime) {
    int parallelDevices = getInt(data, PARALLEL_DEVICES);
    Transformer2WTypeInput type = getType(data, types);
    int tapPos = getInt(data, TAP_POS);
    boolean autoTap = getBoolean(data, AUTO_TAP);

    return new Transformer2WInput(
        uuid,
        id,
        operator,
        operationTime,
        nodeA,
        nodeB,
        parallelDevices,
        type,
        tapPos,
        autoTap,
        data);
  }
}
