/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system;

import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.type.StorageTypeInput;
import java.util.Objects;
import java.util.UUID;

/** Describes a battery storage */
public class StorageInput extends SystemParticipantInput {
  /** Type of this storage, containing default values for storages of this kind */
  private final StorageTypeInput type;

  /**
   * Constructor for an operated storage
   *
   * @param uuid of the input entity
   * @param operationTime time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic for integrated inverter
   * @param type of storage
   */
  public StorageInput(
      UUID uuid,
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      StorageTypeInput type) {
    super(uuid, operationTime, operator, id, node, qCharacteristics);
    this.type = type;
  }

  /**
   * Constructor for a non-operated storage
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param type of storage
   */
  public StorageInput(
      UUID uuid, String id, NodeInput node, String qCharacteristics, StorageTypeInput type) {
    super(uuid, id, node, qCharacteristics);
    this.type = type;
  }

  public StorageTypeInput getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    StorageInput that = (StorageInput) o;
    return Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type);
  }
}
