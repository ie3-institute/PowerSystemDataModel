/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.io.extractor.HasType;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.EmInput;
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

  /**
   * Constructor for an operated storage
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic for integrated inverter
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param type of storage
   */
  public StorageInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      StorageTypeInput type) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, em);
    this.type = type;
  }

  /**
   * Constructor for an operated, always on storage
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param type of storage
   */
  public StorageInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      StorageTypeInput type) {
    super(uuid, id, node, qCharacteristics, em);
    this.type = type;
  }

  @Override
  public StorageTypeInput getType() {
    return type;
  }

  public StorageInputCopyBuilder copy() {
    return new StorageInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof StorageInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type);
  }

  @Override
  public String toString() {
    return "StorageInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", node="
        + getNode().getUuid()
        + ", qCharacteristics='"
        + getqCharacteristics()
        + "', em="
        + getControllingEm()
        + ", type="
        + type.getUuid()
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link StorageInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link StorageInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class StorageInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<StorageInputCopyBuilder> {

    private StorageTypeInput type;

    private StorageInputCopyBuilder(StorageInput entity) {
      super(entity);
      this.type = entity.getType();
    }

    public StorageInputCopyBuilder type(StorageTypeInput type) {
      this.type = type;
      return thisInstance();
    }

    @Override
    public StorageInputCopyBuilder scale(Double factor) {
      type(type.copy().scale(factor).build());
      return thisInstance();
    }

    @Override
    public StorageInput build() {
      return new StorageInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNode(),
          getqCharacteristics(),
          getEm(),
          type);
    }

    @Override
    protected StorageInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
