/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.factory.input.AssetInputEntityFactory;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import java.util.Map;
import java.util.UUID;

/**
 * Abstract factory class for creating {@link SystemParticipantInput} entities.
 *
 * @param <T> Type of entity that this factory can create. Must be a subclass of {@link
 *     SystemParticipantInput}
 */
public abstract class SystemParticipantInputEntityFactory<T extends SystemParticipantInput>
    extends AssetInputEntityFactory<T> {

  private final Map<UUID, NodeInput> nodes;
  private final Map<UUID, EmInput> emUnits;

  @SafeVarargs
  protected SystemParticipantInputEntityFactory(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Class<? extends T>... allowedClasses) {
    super(operators, allowedClasses);
    this.nodes = nodes;
    this.emUnits = emUnits;
  }

  @Override
  protected T buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {
    NodeInput node = getEntity(data, NODE, nodes);
    EmInput controllingEm = getEntity(data, CONTROLLING_EM, emUnits, null);

    String qCharacteristicsValue = getField(data, Q_CHARACTERISTICS);

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

    return buildModel(
        data, uuid, id, node, qCharacteristics, operator, operationTime, controllingEm);
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
      Map<String, String> data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime,
      EmInput controllingEm);
}
