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
import edu.ie3.datamodel.models.input.system.type.AcTypeInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes an air condition. */
public class AcInput extends SystemParticipantInput implements HasType, HasThermalBus {
  /** Type of this air condition, containing default values for air condition of this kind. */
  private final AcTypeInput type;

  /** The thermal bus, this model is connected to. */
  private final ThermalBusInput thermalBus;

  /**
   * Constructor for an operated air condition.
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
      EmInput controllingEm,
      AcTypeInput type) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.thermalBus = thermalBus;
    this.type = type;
  }

  /**
   * Constructor for an operated air condition.
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
   * @param type of AC
   * @param additionalInformation That were provided by the source
   */
  public AcInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ThermalBusInput thermalBus,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      AcTypeInput type,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.thermalBus = thermalBus;
    this.type = type;
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for an operated air condition.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param type of AC
   */
  public AcInput(
      UUID uuid,
      String id,
      NodeInput node,
      ThermalBusInput thermalBus,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      AcTypeInput type) {
    super(uuid, id, node, qCharacteristics, controllingEm);
    this.thermalBus = thermalBus;
    this.type = type;
  }

  public AcTypeInput getType() {
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
    if (!(o instanceof AcInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(type, that.type) && Objects.equals(thermalBus, that.thermalBus);
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
        + ", qCharacteristics="
        + getQCharacteristics()
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
  public AcInputCopyBuilder copy() {
    return new AcInputCopyBuilder(this);
  }

  public static class AcInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<AcInputCopyBuilder> {
    private AcTypeInput type;

    private ThermalBusInput thermalBus;

    protected AcInputCopyBuilder(AcInput entity) {
      super(entity);
      this.type = entity.type;
      this.thermalBus = entity.thermalBus;
    }

    public AcInputCopyBuilder type(AcTypeInput type) {
      this.type = type;
      return thisInstance();
    }

    protected AcTypeInput getType() {
      return type;
    }

    public AcInputCopyBuilder thermalBus(ThermalBusInput thermalBus) {
      this.thermalBus = thermalBus;
      return thisInstance();
    }

    protected ThermalBusInput getThermalBus() {
      return thermalBus;
    }

    @Override
    public AcInputCopyBuilder scale(double factor) {
      return type(type.copy().scale(factor).build());
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
          getQCharacteristics(),
          getControllingEm(),
          type,
          getAdditionalInformation());
    }

    @Override
    protected AcInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
