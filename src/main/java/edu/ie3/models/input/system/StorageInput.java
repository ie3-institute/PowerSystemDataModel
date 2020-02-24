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
  private StorageTypeInput type;

  /** Selection of predefined behaviour of the storage */
  private StorageStrategy behaviour;
  /**
   * Constructor for an operated storage
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param cosphiRated Power factor
   * @param type of storage
   * @param behaviour Selection of predefined behaviour of the storage
   */
  public StorageInput(
      UUID uuid,
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      double cosphiRated,
      StorageTypeInput type,
      String behaviour) {
    super(uuid, operationTime, operator, id, node, qCharacteristics, cosphiRated);
    this.type = type;
    this.behaviour = StorageStrategy.get(behaviour);
  }

  /**
   * Constructor for a non-operated storage
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param cosphiRated Power factor
   * @param type of storage
   * @param behaviour Selection of predefined behaviour of the storage
   */
  public StorageInput(
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      double cosphiRated,
      StorageTypeInput type,
      String behaviour) {
    super(uuid, id, node, qCharacteristics, cosphiRated);
    this.type = type;
    this.behaviour = StorageStrategy.get(behaviour);
  }

  public StorageTypeInput getType() {
    return type;
  }

  public void setType(StorageTypeInput type) {
    this.type = type;
  }

  public StorageStrategy getBehaviour() {
    return behaviour;
  }

  public void setBehaviour(String behaviour) {
    this.behaviour = StorageStrategy.get(behaviour);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    StorageInput that = (StorageInput) o;
    return Objects.equals(type, that.type) && Objects.equals(behaviour, that.behaviour);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, behaviour);
  }
}
