/*
 * © 2021. TU Dortmund University,
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
import java.util.Objects;
import java.util.UUID;

/** Describes a combined heat and power plant */
public class ChpInput extends SystemParticipantInput
    implements HasType, HasThermalBus, HasThermalStorage {
  /** The thermal bus, this model is connected to */
  private final ThermalBusInput thermalBus;
  /** Type of this CHP plant, containing default values for CHP plants of this kind */
  private final ChpTypeInput type;
  /** Thermal storage model */
  private final ThermalStorageInput thermalStorage;
  /** Is this asset market oriented? */
  private final boolean marketReaction;

  /**
   * Constructor for an operated combined heat and power plant
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to (normally equal to the thermal
   *     bus of the provided thermal storage!)
   * @param qCharacteristics Description of a reactive power characteristic
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param type of CHP
   * @param thermalStorage Thermal storage model
   * @param marketReaction Is this asset market oriented?
   */
  public ChpInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ThermalBusInput thermalBus,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      ChpTypeInput type,
      ThermalStorageInput thermalStorage,
      boolean marketReaction) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, em);
    this.thermalBus = thermalBus;
    this.type = type;
    this.thermalStorage = thermalStorage;
    this.marketReaction = marketReaction;
  }

  /**
   * Constructor for an operated, always on combined heat and power plant
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to (normally equal to the thermal
   *     bus of the provided thermal storage!)
   * @param qCharacteristics Description of a reactive power characteristic
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param type of CHP
   * @param thermalStorage Thermal storage model
   * @param marketReaction Is this asset market oriented?
   */
  public ChpInput(
      UUID uuid,
      String id,
      NodeInput node,
      ThermalBusInput thermalBus,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      ChpTypeInput type,
      ThermalStorageInput thermalStorage,
      boolean marketReaction) {
    super(uuid, id, node, qCharacteristics, em);
    this.thermalBus = thermalBus;
    this.type = type;
    this.thermalStorage = thermalStorage;
    this.marketReaction = marketReaction;
  }

  @Override
  public ThermalBusInput getThermalBus() {
    return thermalBus;
  }

  @Override
  public ChpTypeInput getType() {
    return type;
  }

  @Override
  public ThermalStorageInput getThermalStorage() {
    return thermalStorage;
  }

  public boolean isMarketReaction() {
    return marketReaction;
  }

  public ChpInputCopyBuilder copy() {
    return new ChpInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChpInput chpInput)) return false;
    if (!super.equals(o)) return false;
    return marketReaction == chpInput.marketReaction
        && thermalBus.equals(chpInput.thermalBus)
        && type.equals(chpInput.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), thermalBus, type, marketReaction);
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
        + ", qCharacteristics='"
        + getqCharacteristics()
        + "', em="
        + getControllingEm()
        + ", thermalBus="
        + thermalBus.getUuid()
        + ", type="
        + type.getUuid()
        + ", thermalStorage="
        + thermalStorage.getUuid()
        + ", marketReaction="
        + marketReaction
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link ChpInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link ChpInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class ChpInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<ChpInputCopyBuilder> {

    private ChpTypeInput type;
    private ThermalBusInput thermalBus;
    private ThermalStorageInput thermalStorage;
    private boolean marketReaction;

    private ChpInputCopyBuilder(ChpInput entity) {
      super(entity);
      this.type = entity.getType();
      this.thermalBus = entity.getThermalBus();
      this.marketReaction = entity.isMarketReaction();
      this.thermalStorage = entity.getThermalStorage();
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
          getEm(),
          type,
          thermalStorage,
          marketReaction);
    }

    public ChpInputCopyBuilder type(ChpTypeInput type) {
      this.type = type;
      return thisInstance();
    }

    public ChpInputCopyBuilder thermalBus(ThermalBusInput thermalBus) {
      this.thermalBus = thermalBus;
      return thisInstance();
    }

    public ChpInputCopyBuilder thermalStorage(ThermalStorageInput thermalStorage) {
      this.thermalStorage = thermalStorage;
      return thisInstance();
    }

    public ChpInputCopyBuilder marketReaction(boolean marketReaction) {
      this.marketReaction = marketReaction;
      return thisInstance();
    }

    @Override
    public ChpInputCopyBuilder scale(Double factor) {
      this.type = this.type.copy().scale(factor).build();
      return thisInstance();
    }

    @Override
    protected ChpInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
