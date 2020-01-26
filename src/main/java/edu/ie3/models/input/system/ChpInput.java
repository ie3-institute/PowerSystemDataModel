/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system;

import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.type.ChpTypeInput;
import edu.ie3.models.input.thermal.ThermalBusInput;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Describes a combined heat and power plant */
public class ChpInput extends SystemParticipantInput {
  /** The thermal bus, this model is connected to */
  private ThermalBusInput thermalBus;
  /** Type of this CHP plant, containing default values for CHP plants of this kind */
  private ChpTypeInput type;
  /** Is this asset market oriented? */
  private boolean  marketReaction;

  /**
   * @param uuid of the input entity
   * @param operationInterval Empty for a non-operated asset, Interval of operation period else
   * @param operator of the asset
   * @param id of the asset
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param cosphi Power factor
   * @param type of CHP
   * @param marketReaction Is this asset market oriented?
   */
  public ChpInput(
      UUID uuid,
      Optional<ClosedInterval<ZonedDateTime>> operationInterval,
      OperatorInput operator,
      String id,
      NodeInput node,
      ThermalBusInput thermalBus,
      String qCharacteristics,
      Double cosphi,
      ChpTypeInput type,
      boolean  marketReaction) {
    super(uuid, operationInterval, operator, id, node, qCharacteristics, cosphi);
    this.thermalBus = thermalBus;
    this.type = type;
    this.marketReaction = marketReaction;
  }

  /**
   * If both operatesFrom and operatesUntil are Empty, it is assumed that the asset is non-operated.
   *
   * @param uuid of the input entity
   * @param operatesFrom start of operation period, will be replaced by LocalDateTime.MIN if Empty
   * @param operatesUntil end of operation period, will be replaced by LocalDateTime.MAX if Empty
   * @param operator of the asset
   * @param id of the asset
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param cosphi Power factor
   * @param type of CHP
   * @param marketReaction Is this asset market oriented?
   */
  public ChpInput(
      UUID uuid,
      Optional<ZonedDateTime> operatesFrom,
      Optional<ZonedDateTime> operatesUntil,
      OperatorInput operator,
      String id,
      NodeInput node,
      ThermalBusInput thermalBus,
      String qCharacteristics,
      Double cosphi,
      ChpTypeInput type,
      boolean  marketReaction) {
    super(uuid, operatesFrom, operatesUntil, operator, id, node, qCharacteristics, cosphi);
    this.thermalBus = thermalBus;
    this.type = type;
    this.marketReaction = marketReaction;
  }

  /**
   * Constructor for a non-operated asset
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param thermalBus The thermal bus, this model is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param cosphi Power factor
   * @param type of CHP
   * @param marketReaction Is this asset market oriented?
   */
  public ChpInput(
      UUID uuid,
      String id,
      NodeInput node,
      ThermalBusInput thermalBus,
      String qCharacteristics,
      Double cosphi,
      ChpTypeInput type,
      boolean  marketReaction) {
    super(uuid, id, node, qCharacteristics, cosphi);
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

  public boolean  getMarketReaction() {
    return marketReaction;
  }

  public void setMarketReaction(boolean  marketReaction) {
    this.marketReaction = marketReaction;
  }

  @Override
  public boolean equals(Object o) {
    if(this == o)
      return true;
    if(o == null || getClass() != o.getClass())
      return false;
    if(!super.equals(o))
      return false;
    ChpInput chpInput = (ChpInput) o;
    return marketReaction == chpInput.marketReaction && thermalBus.equals(chpInput.thermalBus) &&
           type.equals(chpInput.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), thermalBus, type, marketReaction);
  }
}
