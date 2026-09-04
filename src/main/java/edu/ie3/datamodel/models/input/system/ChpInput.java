/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.io.extractor.HasThermalBus;
import edu.ie3.datamodel.io.extractor.HasThermalStorage;
import edu.ie3.datamodel.io.extractor.HasType;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.ChpTypeInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes a combined heat and power plant. */
public class ChpInput extends SystemParticipantInput
    implements HasType, HasThermalBus, HasThermalStorage {
  /** The thermal bus, this model is connected to. */
  private final ThermalBusInput thermalBus;

  /** Type of this CHP plant, containing default values for CHP plants of this kind. */
  private final ChpTypeInput type;

  /** Thermal storage model. */
  private final ThermalStorageInput thermalStorage;

  /**
   * Constructor for an operated combined heat and power plant.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to (normally equal to the thermal
   *     bus of the provided thermal storage!)
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param type of CHP
   * @param thermalStorage Thermal storage model
   */
  public ChpInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ThermalBusInput thermalBus,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      ChpTypeInput type,
      ThermalStorageInput thermalStorage) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.thermalBus = thermalBus;
    this.type = type;
    this.thermalStorage = thermalStorage;
  }

  /**
   * Constructor for an operated combined heat and power plant.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to (normally equal to the thermal
   *     bus of the provided thermal storage!)
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param type of CHP
   * @param thermalStorage Thermal storage model
   * @param additionalInformation That were provided by the source
   */
  public ChpInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ThermalBusInput thermalBus,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      ChpTypeInput type,
      ThermalStorageInput thermalStorage,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.thermalBus = thermalBus;
    this.type = type;
    this.thermalStorage = thermalStorage;
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for an operated combined heat and power plant.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to (normally equal to the thermal
   *     bus of the provided thermal storage!)
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param type of CHP
   * @param thermalStorage Thermal storage model
   */
  public ChpInput(
      UUID uuid,
      String id,
      NodeInput node,
      ThermalBusInput thermalBus,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      ChpTypeInput type,
      ThermalStorageInput thermalStorage) {
    super(uuid, id, node, qCharacteristics, controllingEm);
    this.thermalBus = thermalBus;
    this.type = type;
    this.thermalStorage = thermalStorage;
  }

  public ThermalBusInput getThermalBus() {
    return thermalBus;
  }

  public ChpTypeInput getType() {
    return type;
  }

  public ThermalStorageInput getThermalStorage() {
    return thermalStorage;
  }

  @Override
  public ComparableQuantity<Power> sRated() {
    return this.type.getsRated();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChpInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(thermalBus, that.thermalBus)
        && Objects.equals(type, that.type)
        && Objects.equals(thermalStorage, that.thermalStorage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), thermalBus, type, thermalStorage);
  }

  @Override
  public String toString() {
    return "ChpInput{"
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
        + ", thermalBus="
        + thermalBus.getUuid()
        + ", type="
        + type.getUuid()
        + ", thermalStorage="
        + thermalStorage.getUuid()
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public ChpInputCopyBuilder copy() {
    return new ChpInputCopyBuilder(this);
  }

  public static class ChpInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<ChpInputCopyBuilder> {
    private ThermalBusInput thermalBus;

    private ChpTypeInput type;

    private ThermalStorageInput thermalStorage;

    protected ChpInputCopyBuilder(ChpInput entity) {
      super(entity);
      this.thermalBus = entity.thermalBus;
      this.type = entity.type;
      this.thermalStorage = entity.thermalStorage;
    }

    public ChpInputCopyBuilder thermalBus(ThermalBusInput thermalBus) {
      this.thermalBus = thermalBus;
      return thisInstance();
    }

    protected ThermalBusInput getThermalBus() {
      return thermalBus;
    }

    public ChpInputCopyBuilder type(ChpTypeInput type) {
      this.type = type;
      return thisInstance();
    }

    protected ChpTypeInput getType() {
      return type;
    }

    public ChpInputCopyBuilder thermalStorage(ThermalStorageInput thermalStorage) {
      this.thermalStorage = thermalStorage;
      return thisInstance();
    }

    protected ThermalStorageInput getThermalStorage() {
      return thermalStorage;
    }

    @Override
    public ChpInputCopyBuilder scale(double factor) {
      this.type = this.type.copy().scale(factor).build();
      return thisInstance();
    }

    @Override
    public ChpInput build() {
      return new ChpInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNode(),
          thermalBus,
          getqCharacteristics(),
          getControllingEm(),
          type,
          thermalStorage,
          getAdditionalInformation());
    }

    @Override
    protected ChpInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
