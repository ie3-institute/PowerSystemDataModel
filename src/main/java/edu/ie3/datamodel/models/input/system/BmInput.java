/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.io.extractor.HasType;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.type.BmTypeInput;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Objects;
import java.util.UUID;
import tec.uom.se.ComparableQuantity;

/** Describes a biomass plant */
public class BmInput extends SystemParticipantInput implements HasType {
  /** Type of this BM plant, containing default values for BM plants of this kind */
  private final BmTypeInput type;
  /** Is this asset market oriented? */
  private final boolean marketReaction;
  /**
   * Does this plant increase the output power if the revenues exceed the energy generation costs?
   */
  private final boolean costControlled;
  /** Granted feed in tariff (typically in €/kWh) */
  private final ComparableQuantity<EnergyPrice> feedInTariff;

  /**
   * Constructor for an operated biomass plant
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics
   * @param type of BM
   * @param marketReaction Is this asset market oriented?
   * @param costControlled Does this plant increase the output power if the revenues exceed the
   *     energy generation costs?
   * @param feedInTariff Granted feed in tariff (typically in €/MWh)
   */
  public BmInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      String qCharacteristics,
      BmTypeInput type,
      boolean marketReaction,
      boolean costControlled,
      ComparableQuantity<EnergyPrice> feedInTariff) {
    super(uuid, id, operator, operationTime, node, qCharacteristics);
    this.type = type;
    this.marketReaction = marketReaction;
    this.costControlled = costControlled;
    this.feedInTariff = feedInTariff.to(StandardUnits.ENERGY_PRICE);
  }

  /**
   * Constructor for an operated, always on biomass plant
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics
   * @param type of BM
   * @param marketReaction Is this asset market oriented?
   * @param costControlled Does this plant increase the output power if the revenues exceed the
   *     energy generation costs?
   * @param feedInTariff Granted feed in tariff (typically in €/MWh)
   */
  public BmInput(
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      BmTypeInput type,
      boolean marketReaction,
      boolean costControlled,
      ComparableQuantity<EnergyPrice> feedInTariff) {
    super(uuid, id, node, qCharacteristics);
    this.type = type;
    this.marketReaction = marketReaction;
    this.costControlled = costControlled;
    this.feedInTariff = feedInTariff.to(StandardUnits.ENERGY_PRICE);
  }

  @Override
  public BmTypeInput getType() {
    return type;
  }

  public boolean isMarketReaction() {
    return marketReaction;
  }

  public boolean isCostControlled() {
    return costControlled;
  }

  public ComparableQuantity<EnergyPrice> getFeedInTariff() {
    return feedInTariff;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    BmInput bmInput = (BmInput) o;
    return marketReaction == bmInput.marketReaction
        && costControlled == bmInput.costControlled
        && type.equals(bmInput.type)
        && feedInTariff.equals(bmInput.feedInTariff);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, marketReaction, costControlled, feedInTariff);
  }

  @Override
  public String toString() {
    return "BmInput{"
        + "type="
        + type
        + ", marketReaction="
        + marketReaction
        + ", costControlled="
        + costControlled
        + ", feedInTariff="
        + feedInTariff
        + '}';
  }
}
