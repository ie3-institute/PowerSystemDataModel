/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.io.extractor.HasType;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.EvTypeInput;
import java.util.Objects;
import java.util.UUID;

/** Describes an electric vehicle */
public class EvInput extends SystemParticipantInput implements HasType {
  /** Type of this EV, containing default values for EVs of this kind */
  private final EvTypeInput type;

  /**
   * Constructor for an operated electric vehicle
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param type of EV
   */
  public EvInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EvTypeInput type) {
    super(uuid, id, operator, operationTime, node, qCharacteristics);
    this.type = type;
  }

  /**
   * Constructor for an operated, always on electric vehicle
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param type of EV
   */
  public EvInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EvTypeInput type) {
    super(uuid, id, node, qCharacteristics);
    this.type = type;
  }

  @Override
  public EvTypeInput getType() {
    return type;
  }

  public EvInputCopyBuilder copy() {
    return new EvInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EvInput evInput)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(type, evInput.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type);
  }

  @Override
  public String toString() {
    return "EvInput{"
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
        + '\''
        + ", type="
        + type.getUuid()
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link EvInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link EvInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class EvInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<EvInputCopyBuilder> {

    private EvTypeInput type;

    private EvInputCopyBuilder(EvInput entity) {
      super(entity);
      this.type = entity.getType();
    }

    @Override
    public EvInput build() {
      return new EvInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNode(),
          getqCharacteristics(),
          type);
    }

    public EvInputCopyBuilder type(EvTypeInput type) {
      this.type = type;
      return this;
    }

    @Override
    protected EvInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
