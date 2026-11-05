/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.io.factory.FieldMap;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.ExtendedSystemParticipantInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import java.util.Map;
import java.util.UUID;

/**
 * Factory base class for system participants that require a node and may have a controlling EM.
 *
 * @param <T> Type of entity that this factory can create
 */
public abstract class ExtendedSystemParticipantInputEntityFactory<
        T extends ExtendedSystemParticipantInput>
    extends SystemParticipantInputEntityFactory<T> {

  private final Map<UUID, NodeInput> nodes;
  private final Map<UUID, EmInput> emUnits;

  @SafeVarargs
  protected ExtendedSystemParticipantInputEntityFactory(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Class<? extends T>... allowedClasses) {
    super(operators, allowedClasses);
    this.nodes = nodes;
    this.emUnits = emUnits;
  }

  @Override
  protected final T buildModel(
      FieldMap data,
      UUID uuid,
      String id,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime) {
    NodeInput node = data.getEntity(NODE, nodes);
    EmInput controllingEm = data.getEntity(CONTROLLING_EM, emUnits, null);

    return buildModel(
        data, uuid, id, node, qCharacteristics, operator, operationTime, controllingEm);
  }

  /** Creates an extended system participant with its node and controlling EM. */
  protected abstract T buildModel(
      FieldMap data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime,
      EmInput controllingEm);
}
