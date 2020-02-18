/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system;

import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.type.ChpTypeInput;
import edu.ie3.models.input.thermal.ThermalBusInput;
import edu.ie3.models.input.thermal.ThermalStorageInput;
import java.util.Objects;
import java.util.UUID;

/** Describes a combined heat and power plant */
public class ChpInput extends SystemParticipantInput {
  /** The thermal bus, this model is connected to */
  private ThermalBusInput thermalBus;
  /** Type of this CHP plant, containing default values for CHP plants of this kind */
  private ChpTypeInput type;
  /** Thermal storage model */
  private ThermalStorageInput thermalStorage;
  /** Is this asset market oriented? */
  private boolean marketReaction;

  /**
   * Constructor for an operated combined heat and power plant
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param type of CHP
   * @param thermalStorage Thermal storage model
   * @param marketReaction Is this asset market oriented?
   */
  public ChpInput(
      UUID uuid,
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput node,
      ThermalBusInput thermalBus,
      String qCharacteristics,
      ChpTypeInput type,
      ThermalStorageInput thermalStorage,
      boolean marketReaction) {
    super(uuid, operationTime, operator, id, node, qCharacteristics);
    this.thermalBus = thermalBus;
    this.type = type;
    this.thermalStorage = thermalStorage;
    this.marketReaction = marketReaction;
  }

  /**
   * Constructor for a non-operated combined heat and power plant
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param type of CHP
   * @param marketReaction Is this asset market oriented?
   */
  public ChpInput(
      UUID uuid,
      String id,
      NodeInput node,
      ThermalBusInput thermalBus,
      String qCharacteristics,
      ChpTypeInput type,
      boolean marketReaction) {
    super(uuid, id, node, qCharacteristics);
    this.thermalBus = thermalBus;
    this.type = type;
    this.marketReaction = marketReaction;
  }

  public ThermalBusInput getThermalBus() {
    return thermalBus;
  }

  public void setThermalBus(ThermalBusInput thermalBus) {
    this.thermalBus = thermalBus;
  }

  public ChpTypeInput getType() {
    return type;
  }

  public void setType(ChpTypeInput type) {
    this.type = type;
  }

  public boolean getMarketReaction() {
    return marketReaction;
  }

  public void setMarketReaction(boolean marketReaction) {
    this.marketReaction = marketReaction;
  }

  public ThermalStorageInput getThermalStorage() {
    return thermalStorage;
  }

  public void setThermalStorage(ThermalStorageInput thermalStorage) {
    this.thermalStorage = thermalStorage;
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
}
