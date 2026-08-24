/*
 * © 2026. TU Dortmund University,
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
import edu.ie3.datamodel.models.input.system.type.HpTypeInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes a heat pump. */
public class HpInput extends SystemParticipantInput implements HasType, HasThermalBus {
  /** Type of this heat pump, containing default values for heat pump of this kind. */
  private final HpTypeInput type;

  /** The thermal bus, this model is connected to. */
  private final ThermalBusInput thermalBus;

  /**
   * Constructor for an operated, always on heat pump.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
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
      EmInput controllingEm,
      HpTypeInput type) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.thermalBus = thermalBus;
    this.type = type;
  }

  /**
   * Constructor for an operated, always on heat pump.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param type of HP
   * @param additionalInformation That were provided by the source
   */
  public HpInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ThermalBusInput thermalBus,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      HpTypeInput type,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.thermalBus = thermalBus;
    this.type = type;
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for an operated, always on heat pump.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param type of HP
   */
  public HpInput(
      UUID uuid,
      String id,
      NodeInput node,
      ThermalBusInput thermalBus,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      HpTypeInput type) {
    super(uuid, id, node, qCharacteristics, controllingEm);
    this.thermalBus = thermalBus;
    this.type = type;
  }

  public HpTypeInput getType() {
    return type;
  }

  public ThermalBusInput getThermalBus() {
    return thermalBus;
  }

  @Override
  public ComparableQuantity<Power> sRated() {
    return this.type.getsRated();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof HpInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(type, that.type) && Objects.equals(thermalBus, that.thermalBus);
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
        + ", qCharacteristics="
        + getqCharacteristics()
        + ", controllingEm="
        + getControllingEm().map(e -> e.getUuid().toString()).orElse("")
        + ", type="
        + type.getUuid()
        + ", thermalBus="
        + thermalBus.getUuid()
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public HpInputCopyBuilder copy() {
    return new HpInputCopyBuilder(this);
  }

  public static class HpInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<HpInputCopyBuilder> {
    private HpTypeInput type;

    private ThermalBusInput thermalBus;

    protected HpInputCopyBuilder(HpInput entity) {
      super(entity);
      this.type = entity.type;
      this.thermalBus = entity.thermalBus;
    }

    public HpInputCopyBuilder type(HpTypeInput type) {
      this.type = type;
      return thisInstance();
    }

    protected HpTypeInput getType() {
      return type;
    }

    public HpInputCopyBuilder thermalBus(ThermalBusInput thermalBus) {
      this.thermalBus = thermalBus;
      return thisInstance();
    }

    protected ThermalBusInput getThermalBus() {
      return thermalBus;
    }

    @Override
    public HpInputCopyBuilder scale(double factor) {
      type(type.copy().scale(factor).build());
      return thisInstance();
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
          getControllingEm(),
          type,
          getAdditionalInformation());
    }

    @Override
    protected HpInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
