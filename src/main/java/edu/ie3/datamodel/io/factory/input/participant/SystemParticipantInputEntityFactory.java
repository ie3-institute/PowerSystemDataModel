/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.factory.FieldMap;
import edu.ie3.datamodel.io.factory.input.AssetInputEntityFactory;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import java.util.Map;
import java.util.UUID;

/**
 * Abstract factory class for creating {@link SystemParticipantInput} entities that do not
 * necessarily require a node or an EM.
 *
 * @param <T> Type of entity that this factory can create. Must be a subclass of {@link
 *     SystemParticipantInput}
 */
public abstract class SystemParticipantInputEntityFactory<T extends SystemParticipantInput>
    extends AssetInputEntityFactory<T> {

  @SafeVarargs
  protected SystemParticipantInputEntityFactory(
      Map<UUID, OperatorInput> operators, Class<? extends T>... allowedClasses) {
    super(operators, allowedClasses);
  }

  @Override
  protected T buildModel(
      FieldMap data, UUID uuid, String id, OperatorInput operator, OperationTime operationTime) {
    String qCharacteristicsValue = data.getField(Q_CHARACTERISTICS);

    ReactivePowerCharacteristic qCharacteristics;
    try {
      qCharacteristics = ReactivePowerCharacteristic.parse(qCharacteristicsValue);
    } catch (ParsingException e) {
      throw new FactoryException(
          "Cannot parse the following reactive power characteristic: '"
              + qCharacteristicsValue
              + "'",
          e);
    }

    return buildModel(data, uuid, id, qCharacteristics, operator, operationTime);
  }

  /**
   * Creates SystemParticipantInput entity with given parameters.
   *
   * @param data field data
   * @param uuid UUID of the input entity
   * @param id ID
   * @param qCharacteristics Description of a reactive power characteristic
   * @param operator Operator of the asset
   * @param operationTime time in which the entity is operated
   * @return newly created asset object
   */
  protected abstract T buildModel(
      FieldMap data,
      UUID uuid,
      String id,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime);
}
