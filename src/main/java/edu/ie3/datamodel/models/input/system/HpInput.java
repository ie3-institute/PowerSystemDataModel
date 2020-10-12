/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.io.extractor.HasThermalBus;
import edu.ie3.datamodel.io.extractor.HasType;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.HpTypeInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import java.util.Objects;
import java.util.UUID;

/** Describes a heat pump */
public class HpInput extends SystemParticipantInput implements HasType, HasThermalBus {
  /** Type of this heat pump, containing default values for heat pump of this kind */
  private final HpTypeInput type;
  /** The thermal bus, this model is connected to */
  private final ThermalBusInput thermalBus;

  /**
   * Constructor for an operated heat pump
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param type of HP
   */
  public HpInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ThermalBusInput thermalBus,
      ReactivePowerCharacteristic qCharacteristics,
      HpTypeInput type) {
    super(uuid, id, operator, operationTime, node, qCharacteristics);
    this.thermalBus = thermalBus;
    this.type = type;
  }

  /**
   * Constructor for an operated, always on heat pump
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param type of HP
   */
  public HpInput(
      UUID uuid,
      String id,
      NodeInput node,
      ThermalBusInput thermalBus,
      ReactivePowerCharacteristic qCharacteristics,
      HpTypeInput type) {
    super(uuid, id, node, qCharacteristics);
    this.thermalBus = thermalBus;
    this.type = type;
  }

  @Override
  public HpTypeInput getType() {
    return type;
  }

  @Override
  public ThermalBusInput getThermalBus() {
    return thermalBus;
  }

  public HpInputCopyBuilder copy() {
    return new HpInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    HpInput hpInput = (HpInput) o;
    return Objects.equals(type, hpInput.type) && Objects.equals(thermalBus, hpInput.thermalBus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, thermalBus);
  }

  @Override
  public String toString() {
    return "HpInput{"
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
        + ", thermalBus="
        + thermalBus.getUuid()
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link HpInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link HpInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class HpInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<HpInputCopyBuilder> {

    private HpTypeInput type;
    private ThermalBusInput thermalBus;

    private HpInputCopyBuilder(HpInput entity) {
      super(entity);
      this.type = entity.getType();
      this.thermalBus = entity.getThermalBus();
    }

    @Override
    public HpInput build() {
      return new HpInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNode(),
          thermalBus,
          getqCharacteristics(),
          type);
    }

    public HpInputCopyBuilder type(HpTypeInput type) {
      this.type = type;
      return this;
    }

    public HpInputCopyBuilder thermalBus(ThermalBusInput thermalBus) {
      this.thermalBus = thermalBus;
      return this;
    }

    @Override
    protected HpInputCopyBuilder childInstance() {
      return this;
    }
  }
}
