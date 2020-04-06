/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.io.extractor.HasType;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.StorageTypeInput;
import java.util.Objects;
import java.util.UUID;

/** Describes a battery storage */
public class StorageInput extends SystemParticipantInput implements HasType {
  /** Type of this storage, containing default values for storages of this kind */
  private final StorageTypeInput type;

  /** Selection of predefined behaviour of the storage */
  private final StorageStrategy behaviour;

  /**
   * Constructor for an operated storage
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic for integrated inverter
   * @param type of storage
   * @param behaviour Selection of predefined behaviour of the storage
   */
  public StorageInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic<?> qCharacteristics,
      StorageTypeInput type,
      String behaviour) {
    super(uuid, id, operator, operationTime, node, qCharacteristics);
    this.type = type;
    this.behaviour = StorageStrategy.get(behaviour);
  }

  /**
   * Constructor for an operated, always on storage
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param type of storage
   * @param behaviour Selection of predefined behaviour of the storage
   */
  public StorageInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic<?> qCharacteristics,
      StorageTypeInput type,
      String behaviour) {
    super(uuid, id, node, qCharacteristics);
    this.type = type;
    this.behaviour = StorageStrategy.get(behaviour);
  }

  @Override
  public StorageTypeInput getType() {
    return type;
  }

  public StorageStrategy getBehaviour() {
    return behaviour;
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

  @Override
  public String toString() {
    return "StorageInput{" + "type=" + type + ", behaviour=" + behaviour + '}';
  }
}
