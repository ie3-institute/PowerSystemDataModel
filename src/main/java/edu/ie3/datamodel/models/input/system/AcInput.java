/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.io.extractor.HasThermalBus;
import edu.ie3.datamodel.io.extractor.HasType;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.AcTypeInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import java.util.Objects;
import java.util.UUID;

/** Describes an air condition */
public class AcInput extends SystemParticipantInput implements HasType, HasThermalBus {
  /** Type of this air condition, containing default values for air condition of this kind */
  private final AcTypeInput type;
  /** The thermal bus, this model is connected to */
  private final ThermalBusInput thermalBus;

  /**
   * Constructor for an operated air condition
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param type of AC
   */
  public AcInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ThermalBusInput thermalBus,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      AcTypeInput type) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, em);
    this.thermalBus = thermalBus;
    this.type = type;
  }

  /**
   * Constructor for an operated, always on air condition
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param type of AC
   */
  public AcInput(
      UUID uuid,
      String id,
      NodeInput node,
      ThermalBusInput thermalBus,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      AcTypeInput type) {
    super(uuid, id, node, qCharacteristics, em);
    this.thermalBus = thermalBus;
    this.type = type;
  }

  @Override
  public AcTypeInput getType() {
    return type;
  }

  @Override
  public ThermalBusInput getThermalBus() {
    return thermalBus;
  }

  public AcInputCopyBuilder copy() {
    return new AcInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AcInput acInput)) return false;
    if (!super.equals(o)) return false;
    return type.equals(acInput.type) && thermalBus.equals(acInput.thermalBus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, thermalBus);
  }

  @Override
  public String toString() {
    return "AcInput{"
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
        + ", thermalBus="
        + thermalBus.getUuid()
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link AcInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link AcInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class AcInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<AcInputCopyBuilder> {

    private AcTypeInput type;
    private ThermalBusInput thermalBus;

    private AcInputCopyBuilder(AcInput entity) {
      super(entity);
      this.type = entity.getType();
      this.thermalBus = entity.getThermalBus();
    }

    public AcInputCopyBuilder type(AcTypeInput type) {
      this.type = type;
      return thisInstance();
    }

    public AcInputCopyBuilder thermalBus(ThermalBusInput thermalBus) {
      this.thermalBus = thermalBus;
      return thisInstance();
    }

    @Override
    public AcInputCopyBuilder scale(Double factor) {
      type(type.copy().scale(factor).build());
      return thisInstance();
    }

    @Override
    public AcInput build() {
      return new AcInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNode(),
          thermalBus,
          getqCharacteristics(),
          getEm(),
          type);
    }

    @Override
    protected AcInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
