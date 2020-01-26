/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system;

import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.type.BmTypeInput;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Power;

/** Describes a biomass plant */
public class BmInput extends SystemParticipantInput {
  /** Type of this BM plant, containing default values for BM plants of this kind */
  private BmTypeInput type;
  /** Is this asset market oriented? */
  private Boolean marketReaction;
  /**
   * Does this plant increase the output power if the revenues exceed the energy generation costs?
   */
  private Boolean costControlled;
  /** Granted feed in tariff (typically in €/kWh) */
  private Quantity<edu.ie3.util.quantities.interfaces.EnergyPrice> feedInTariff;
  /** Rated apparent power (typically in kW) */
  private Quantity<Power> sRated;

  /**
   * @param uuid of the input entity
   * @param operationInterval Empty for a non-operated asset, Interval of operation period else
   * @param operator of the asset
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics
   * @param cosphi Power factor
   * @param type of BM
   * @param marketReaction Is this asset market oriented?
   * @param costControlled Does this plant increase the output power if the revenues exceed the
   *     energy generation costs?
   * @param feedInTariff Granted feed in tariff (typically in €/kWh)
   * @param sRated Rated apparent power (typically in kVA)
   */
  public BmInput(
      UUID uuid,
      Optional<ClosedInterval<ZonedDateTime>> operationInterval,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      Double cosphi,
      BmTypeInput type,
      Boolean marketReaction,
      Boolean costControlled,
      Quantity<edu.ie3.util.quantities.interfaces.EnergyPrice> feedInTariff,
      Quantity<Power> sRated) {
    super(uuid, operationInterval, operator, id, node, qCharacteristics, cosphi);
    this.type = type;
    this.marketReaction = marketReaction;
    this.costControlled = costControlled;
    this.feedInTariff = feedInTariff.to(StandardUnits.ENERGY_PRICE);
    this.sRated = sRated.to(StandardUnits.S_RATED);
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
   * @param qCharacteristics
   * @param cosphi Power factor
   * @param type of BM
   * @param marketReaction Is this asset market oriented?
   * @param costControlled Does this plant increase the output power if the revenues exceed the
   *     energy generation costs?
   * @param feedInTariff Granted feed in tariff (typically in €/kWh)
   * @param sRated Rated apparent power (typically in kVA)
   */
  public BmInput(
      UUID uuid,
      Optional<ZonedDateTime> operatesFrom,
      Optional<ZonedDateTime> operatesUntil,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      Double cosphi,
      BmTypeInput type,
      Boolean marketReaction,
      Boolean costControlled,
      Quantity<edu.ie3.util.quantities.interfaces.EnergyPrice> feedInTariff,
      Quantity<Power> sRated) {
    super(uuid, operatesFrom, operatesUntil, operator, id, node, qCharacteristics, cosphi);
    this.type = type;
    this.marketReaction = marketReaction;
    this.costControlled = costControlled;
    this.feedInTariff = feedInTariff.to(StandardUnits.ENERGY_PRICE);
    this.sRated = sRated.to(StandardUnits.S_RATED);
  }

  /**
   * Constructor for a non-operated asset
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics
   * @param cosphi Power factor
   * @param type of BM
   * @param marketReaction Is this asset market oriented?
   * @param costControlled Does this plant increase the output power if the revenues exceed the
   *     energy generation costs?
   * @param feedInTariff Granted feed in tariff (typically in €/kWh)
   * @param sRated Rated apparent power (typically in kVA)
   */
  public BmInput(
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      Double cosphi,
      BmTypeInput type,
      Boolean marketReaction,
      Boolean costControlled,
      Quantity<edu.ie3.util.quantities.interfaces.EnergyPrice> feedInTariff,
      Quantity<Power> sRated) {
    super(uuid, id, node, qCharacteristics, cosphi);
    this.type = type;
    this.marketReaction = marketReaction;
    this.costControlled = costControlled;
    this.feedInTariff = feedInTariff.to(StandardUnits.ENERGY_PRICE);
    this.sRated = sRated.to(StandardUnits.S_RATED);
  }

  public BmTypeInput getType() {
    return type;
  }

  public void setType(BmTypeInput type) {
    this.type = type;
  }

  public Boolean getMarketReaction() {
    return marketReaction;
  }

  public void setMarketReaction(Boolean marketReaction) {
    this.marketReaction = marketReaction;
  }

  public Boolean getCostControlled() {
    return costControlled;
  }

  public void setCostControlled(Boolean costControlled) {
    this.costControlled = costControlled;
  }

  public Quantity<edu.ie3.util.quantities.interfaces.EnergyPrice> getFeedInTariff() {
    return feedInTariff;
  }

  public void setFeedInTariff(
      Quantity<edu.ie3.util.quantities.interfaces.EnergyPrice> feedInTariff) {
    this.feedInTariff = feedInTariff.to(StandardUnits.ENERGY_PRICE);
  }

  public Quantity<Power> getsRated() {
    return sRated;
  }

  public void setsRated(Quantity<Power> sRated) {
    this.sRated = sRated.to(StandardUnits.S_RATED);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    BmInput bmInput = (BmInput) o;
    return type.equals(bmInput.type)
        && marketReaction.equals(bmInput.marketReaction)
        && costControlled.equals(bmInput.costControlled)
        && feedInTariff.equals(bmInput.feedInTariff)
        && sRated.equals(bmInput.sRated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), type, marketReaction, costControlled, feedInTariff, sRated);
  }
}
