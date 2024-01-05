/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.factory.input.AssetInputEntityFactory;
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import java.util.*;

/**
 * Abstract factory class for creating {@link SystemParticipantInput} entities with {@link
 * NodeAssetInputEntityData} data objects.
 *
 * @param <T> Type of entity that this factory can create. Must be a subclass of {@link
 *     SystemParticipantInput}
 * @param <D> Type of data class that is required for entity creation
 * @version 0.1
 * @since 28.01.20
 */
abstract class SystemParticipantInputEntityFactory<
        T extends SystemParticipantInput, D extends NodeAssetInputEntityData>
    extends AssetInputEntityFactory<T, D> {

  private static final String Q_CHARACTERISTICS = "qCharacteristics";

  protected SystemParticipantInputEntityFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    List<Set<String>> fields = super.getFields(entityClass);
    for (Set<String> set : fields) set.add(Q_CHARACTERISTICS);

    return fields;
  }

  @Override
  protected T buildModel(
      D data, UUID uuid, String id, OperatorInput operator, OperationTime operationTime) {
    NodeInput node = data.getNode();
    ReactivePowerCharacteristic qCharacteristics;
    try {
      qCharacteristics = ReactivePowerCharacteristic.parse(data.getField(Q_CHARACTERISTICS));
    } catch (ParsingException e) {
      throw new FactoryException(
          "Cannot parse the following reactive power characteristic: '"
              + data.getField(Q_CHARACTERISTICS)
              + "'",
          e);
    }

    return buildModel(data, uuid, id, node, qCharacteristics, operator, operationTime);
  }

  /**
   * Creates SystemParticipantInput entity with given parameters
   *
   * @param data entity data
   * @param uuid UUID of the input entity
   * @param id ID
   * @param node Node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param operator Operator of the asset
   * @param operationTime time in which the entity is operated
   * @return newly created asset object
   */
  protected abstract T buildModel(
      D data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime);
}
