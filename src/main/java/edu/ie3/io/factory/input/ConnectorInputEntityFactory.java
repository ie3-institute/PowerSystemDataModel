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
import java.util.List;
import java.util.Set;
import java.util.UUID;

abstract class ConnectorInputEntityFactory<
        T extends ConnectorInput, D extends ConnectorInputEntityData>
    extends AssetInputEntityFactory<T, D> {

  private static final String PARALLEL_DEVICES = "paralleldevices";

  @Override
  protected List<Set<String>> getFields(D data) {
    List<Set<String>> fields = super.getFields(data);
    for (Set<String> set : fields) set.add(PARALLEL_DEVICES);

    return fields;
  }

  @Override
  protected T buildModel(
      D data, UUID uuid, String id, OperatorInput operatorInput, OperationTime operationTime) {
    final NodeInput nodeA = data.getNodeA();
    final NodeInput nodeB = data.getNodeB();
    final int parallelDevices = data.getInt(PARALLEL_DEVICES);

    return buildModel(data, uuid, id, nodeA, nodeB, parallelDevices, operatorInput, operationTime);
  }

  @Override
  protected T buildModel(D data, UUID uuid, String id) {
    final NodeInput nodeA = data.getNodeA();
    final NodeInput nodeB = data.getNodeB();
    final int parallelDevices = data.getInt(PARALLEL_DEVICES);

    return buildModel(data, uuid, id, nodeA, nodeB, parallelDevices);
  }

  protected abstract T buildModel(
      D data,
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      OperatorInput operatorInput,
      OperationTime operationTime);

  protected abstract T buildModel(
      D data,
      java.util.UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices);
}
