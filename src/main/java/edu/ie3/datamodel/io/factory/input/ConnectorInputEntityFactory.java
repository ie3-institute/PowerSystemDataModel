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
public abstract class ConnectorInputEntityFactory<
        T extends ConnectorInput, D extends ConnectorInputEntityData>
    extends AssetInputEntityFactory<T, D> {

  /**
   * Attribute that _can_, but does not _have to_ be present for the creation of {@link
   * ConnectorInput}*s. Thus, this attribute name declaration can be used in subclasses of {@link
   * ConnectorInputEntityFactory}*
   */
  protected static final String PARALLEL_DEVICES = "parallelDevices";

  /**
   * Instantiates a new Connector input entity factory.
   *
   * @param allowedClasses the allowed classes
   */
  @SafeVarargs
  protected ConnectorInputEntityFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
  }

  @Override
  protected T buildModel(
      D data, UUID uuid, String id, OperatorInput operator, OperationTime operationTime) {
    final NodeInput nodeA = data.getNodeA();
    final NodeInput nodeB = data.getNodeB();

    return buildModel(data, uuid, id, nodeA, nodeB, operator, operationTime);
  }

  /**
   * Build model t.
   *
   * @param data the data
   * @param uuid the uuid
   * @param id the id
   * @param nodeA the node a
   * @param nodeB the node b
   * @param operator the operator
   * @param operationTime the operation time
   * @return the t
   */
  protected abstract T buildModel(
      D data,
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      OperatorInput operator,
      OperationTime operationTime);
}
