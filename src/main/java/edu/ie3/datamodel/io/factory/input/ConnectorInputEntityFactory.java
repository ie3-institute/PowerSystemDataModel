/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.ConnectorInput;
import java.util.Map;
import java.util.UUID;

/**
 * Abstract factory class that can be extended in order for creating {@link ConnectorInput}
 * entities.
 *
 * @param <T> Type of entity that this factory can create. Must be a subclass of {@link
 *     ConnectorInput}
 */
public abstract class ConnectorInputEntityFactory<T extends ConnectorInput>
    extends AssetInputEntityFactory<T> {

  protected final Map<UUID, NodeInput> nodes;

  @SafeVarargs
  protected ConnectorInputEntityFactory(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Class<? extends T>... allowedClasses) {
    super(operators, allowedClasses);
    this.nodes = nodes;
  }

  @Override
  protected T buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {
    final NodeInput nodeA = getEntity(data, NODE_A, nodes);
    final NodeInput nodeB = getEntity(data, NODE_B, nodes);

    return buildModel(data, uuid, id, nodeA, nodeB, operator, operationTime);
  }

  protected abstract T buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      OperatorInput operator,
      OperationTime operationTime);
}
