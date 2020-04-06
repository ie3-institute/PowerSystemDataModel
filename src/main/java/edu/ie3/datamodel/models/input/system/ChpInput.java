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
import edu.ie3.datamodel.models.input.system.type.ChpTypeInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import java.util.Objects;
import java.util.UUID;

/** Describes a combined heat and power plant */
public class ChpInput extends SystemParticipantInput implements HasType {
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
      String qCharacteristics,
      ChpTypeInput type,
      ThermalStorageInput thermalStorage,
      boolean marketReaction) {
    super(uuid, id, operator, operationTime, node, qCharacteristics);
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
   * @param type of CHP
   * @param thermalStorage Thermal storage model
   * @param marketReaction Is this asset market oriented?
   */
  public ChpInput(
      UUID uuid,
      String id,
      NodeInput node,
      ThermalBusInput thermalBus,
      String qCharacteristics,
      ChpTypeInput type,
      ThermalStorageInput thermalStorage,
      boolean marketReaction) {
    super(uuid, id, node, qCharacteristics);
    this.thermalBus = thermalBus;
    this.type = type;
    this.thermalStorage = thermalStorage;
    this.marketReaction = marketReaction;
  }

  public ThermalBusInput getThermalBus() {
    return thermalBus;
  }

  @Override
  public ChpTypeInput getType() {
    return type;
  }

  public ThermalStorageInput getThermalStorage() {
    return thermalStorage;
  }

  public boolean isMarketReaction() {
    return marketReaction;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ChpInput chpInput = (ChpInput) o;
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
        + "thermalBus="
        + thermalBus
        + ", type="
        + type
        + ", thermalStorage="
        + thermalStorage
        + ", marketReaction="
        + marketReaction
        + '}';
  }
}
