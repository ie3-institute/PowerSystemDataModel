/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.connector.ConnectorInput;
import java.util.UUID;

abstract class ConnectorInputEntityFactory<
        T extends ConnectorInput, D extends ConnectorInputEntityData>
    extends AssetInputEntityFactory<T, D> {
  protected static final String PARALLEL_DEVICES = "paralleldevices";

  public ConnectorInputEntityFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
  }

  @Override
  protected T buildModel(
      D data, UUID uuid, String id, OperatorInput operatorInput, OperationTime operationTime) {
    final NodeInput nodeA = data.getNodeA();
    final NodeInput nodeB = data.getNodeB();

    return buildModel(data, uuid, id, nodeA, nodeB, operatorInput, operationTime);
  }

  @Override
  protected T buildModel(D data, UUID uuid, String id) {
    final NodeInput nodeA = data.getNodeA();
    final NodeInput nodeB = data.getNodeB();

    return buildModel(data, uuid, id, nodeA, nodeB);
  }

  protected abstract T buildModel(
      D data,
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      OperatorInput operatorInput,
      OperationTime operationTime);

  protected abstract T buildModel(D data, UUID uuid, String id, NodeInput nodeA, NodeInput nodeB);
}
