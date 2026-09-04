/*
 * © 2026. TU Dortmund University,
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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes a battery storage. */
public class StorageInput extends SystemParticipantInput implements HasType {
  /** Type of this storage, containing default values for storages of this kind. */
  private final StorageTypeInput type;

  /**
   * Constructor for an operated photovoltaic plant.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param type of storage
   */
  public StorageInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      StorageTypeInput type) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.type = type;
  }

  /**
   * Constructor for an operated photovoltaic plant.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param type of storage
   * @param additionalInformation That were provided by the source
   */
  public StorageInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      StorageTypeInput type,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.type = type;
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for an operated photovoltaic plant.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param type of storage
   */
  public StorageInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      StorageTypeInput type) {
    super(uuid, id, node, qCharacteristics, controllingEm);
    this.type = type;
  }

  public StorageTypeInput getType() {
    return type;
  }

  @Override
  public ComparableQuantity<Power> sRated() {
    return this.type.getsRated();
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
        + ", qCharacteristics="
        + getQCharacteristics()
        + ", controllingEm="
        + getControllingEm().map(e -> e.getUuid().toString()).orElse("")
        + ", type="
        + type.getUuid()
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public StorageInputCopyBuilder copy() {
    return new StorageInputCopyBuilder(this);
  }

  public static class StorageInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<StorageInputCopyBuilder> {
    private StorageTypeInput type;

    protected StorageInputCopyBuilder(StorageInput entity) {
      super(entity);
      this.type = entity.type;
    }

    public StorageInputCopyBuilder type(StorageTypeInput type) {
      this.type = type;
      return thisInstance();
    }

    protected StorageTypeInput getType() {
      return type;
    }

    @Override
    public StorageInputCopyBuilder scale(double factor) {
      return type(type.copy().scale(factor).build());
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
          getQCharacteristics(),
          getControllingEm(),
          type,
          getAdditionalInformation());
    }

    @Override
    protected StorageInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
