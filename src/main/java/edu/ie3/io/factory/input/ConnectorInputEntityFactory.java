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

/**
 * Abstract factory class that can be extended in order for creating {@link ConnectorInput} entities
 * with {@link ConnectorInputEntityData} data objects.
 *
 * @param <T> Type of entity that this factory can create. Must be a subclass of {@link
 *     ConnectorInput}
 * @param <D> Type of data class that is required for entity creation
 * @since 19.02.20
 */
abstract class ConnectorInputEntityFactory<
        T extends ConnectorInput, D extends ConnectorInputEntityData>
    extends AssetInputEntityFactory<T, D> {

  /**
   * Attribute that _can_, but does not _have to_ be present for the creation of {@link
   * ConnectorInput}s. Thus, this attribute name declaration can be used in subclasses of {@link
   * ConnectorInputEntityFactory}
   */
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
