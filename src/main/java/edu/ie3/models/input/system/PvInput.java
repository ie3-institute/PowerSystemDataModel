/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system;

import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;

/** Describes a photovoltaic plant */
public class PvInput extends SystemParticipantInput {

  /** Albedo value (typically a value between 0 and 1) */
  Double albedo;
  /** Inclination in a compass direction (typically °: South 0◦; West 90◦; East -90◦) */
  Quantity<Angle> azimuth;
  /** Efficiency of converter (typically in %) */
  Quantity<Dimensionless> etaConv;
  /** Tilted inclination from horizontal (typically in °) */
  Quantity<Angle> height;
  /** Generator correction factor merging different technical influences */
  Double kG;
  /** Temperature correction factor */
  Double kT;
  /** Is this asset market oriented? */
  Boolean marketReaction;
  /** Rated apparent power (typically in kVA) */
  Quantity<Power> sRated;
  /**
   * @param uuid of the input entity
   * @param operationInterval Empty for a non-operated asset, Interval of operation period else
   * @param operator of the asset
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param cosphiRated Power factor
   * @param albedo Albedo value (typically a value between 0 and 1)
   * @param azimuth Inclination in a compass direction (typically °: South 0◦; West 90◦; East -90◦)
   * @param etaConv Efficiency of converter (typically in %)
   * @param height Tilted inclination from horizontal (typically in °)
   * @param kG Generator correction factor merging different technical influences
   * @param kT Generator correction factor merging different technical influences
   * @param marketReaction Is this asset market oriented?
   * @param sRated Rated apparent power (typically in kVA)
   */
  public PvInput(
      UUID uuid,
      Optional<ClosedInterval<ZonedDateTime>> operationInterval,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      Double cosphiRated,
      Double albedo,
      Quantity<Angle> azimuth,
      Quantity<Dimensionless> etaConv,
      Quantity<Angle> height,
      Double kG,
      Double kT,
      Boolean marketReaction,
      Quantity<Power> sRated) {
    super(uuid, operationInterval, operator, id, node, qCharacteristics, cosphiRated);
    this.albedo = albedo;
    this.azimuth = azimuth.to(StandardUnits.AZIMUTH);
    this.etaConv = etaConv.to(StandardUnits.EFFICIENCY);
    this.height = height.to(StandardUnits.SOLAR_HEIGHT);
    this.kG = kG;
    this.kT = kT;
    this.marketReaction = marketReaction;
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
   * @param qCharacteristics Description of a reactive power characteristic
   * @param cosphiRated Power factor
   * @param albedo Albedo value (typically a value between 0 and 1)
   * @param azimuth Inclination in a compass direction (typically °: South 0◦; West 90◦; East -90◦)
   * @param etaConv Efficiency of converter (typically in %)
   * @param height Tilted inclination from horizontal (typically in °)
   * @param kG Generator correction factor merging different technical influences
   * @param kT Generator correction factor merging different technical influences
   * @param marketReaction Is this asset market oriented?
   * @param sRated Rated apparent power (typically in kVA)
   */
  public PvInput(
      UUID uuid,
      Optional<ZonedDateTime> operatesFrom,
      Optional<ZonedDateTime> operatesUntil,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      Double cosphiRated,
      Double albedo,
      Quantity<Angle> azimuth,
      Quantity<Dimensionless> etaConv,
      Quantity<Angle> height,
      Double kG,
      Double kT,
      Boolean marketReaction,
      Quantity<Power> sRated) {
    super(uuid, operatesFrom, operatesUntil, operator, id, node, qCharacteristics, cosphiRated);
    this.albedo = albedo;
    this.azimuth = azimuth.to(StandardUnits.AZIMUTH);
    this.etaConv = etaConv.to(StandardUnits.EFFICIENCY);
    this.height = height.to(StandardUnits.SOLAR_HEIGHT);
    this.kG = kG;
    this.kT = kT;
    this.marketReaction = marketReaction;
    this.sRated = sRated.to(StandardUnits.S_RATED);
  }
  /**
   * Constructor for a non-operated asset
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param cosphiRated Power factor
   * @param albedo Albedo value (typically a value between 0 and 1)
   * @param azimuth Inclination in a compass direction (typically °: South 0◦; West 90◦; East -90◦)
   * @param etaConv Efficiency of converter (typically in %)
   * @param height Tilted inclination from horizontal (typically in °)
   * @param kG Generator correction factor merging different technical influences
   * @param kT Generator correction factor merging different technical influences
   * @param marketReaction Is this asset market oriented?
   * @param sRated Rated apparent power (typically in kVA)
   */
  public PvInput(
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      Double cosphiRated,
      Double albedo,
      Quantity<Angle> azimuth,
      Quantity<Dimensionless> etaConv,
      Quantity<Angle> height,
      Double kG,
      Double kT,
      Boolean marketReaction,
      Quantity<Power> sRated) {
    super(uuid, id, node, qCharacteristics, cosphiRated);
    this.albedo = albedo;
    this.azimuth = azimuth.to(StandardUnits.AZIMUTH);
    this.etaConv = etaConv.to(StandardUnits.EFFICIENCY);
    this.height = height.to(StandardUnits.SOLAR_HEIGHT);
    this.kG = kG;
    this.kT = kT;
    this.marketReaction = marketReaction;
    this.sRated = sRated.to(StandardUnits.S_RATED);
  }

  public Double getAlbedo() {
    return albedo;
  }

  public void setAlbedo(Double albedo) {
    this.albedo = albedo;
  }

  public Quantity<Angle> getAzimuth() {
    return azimuth;
  }

  public void setAzimuth(Quantity<Angle> azimuth) {
    this.azimuth = azimuth.to(StandardUnits.AZIMUTH);
  }

  public Quantity<Dimensionless> getEtaConv() {
    return etaConv;
  }

  public void setEtaConv(Quantity<Dimensionless> etaConv) {
    this.etaConv = etaConv.to(StandardUnits.EFFICIENCY);
  }

  public Quantity<Angle> getHeight() {
    return height;
  }

  public void setHeight(Quantity<Angle> height) {
    this.height = height.to(StandardUnits.SOLAR_HEIGHT);
  }

  public Double getkG() {
    return kG;
  }

  public void setkG(Double kG) {
    this.kG = kG;
  }

  public Double getkT() {
    return kT;
  }

  public void setkT(Double kT) {
    this.kT = kT;
  }

  public Boolean getMarketReaction() {
    return marketReaction;
  }

  public void setMarketReaction(Boolean marketReaction) {
    this.marketReaction = marketReaction;
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
    PvInput pvInput = (PvInput) o;
    return albedo.equals(pvInput.albedo)
        && azimuth.equals(pvInput.azimuth)
        && etaConv.equals(pvInput.etaConv)
        && height.equals(pvInput.height)
        && kG.equals(pvInput.kG)
        && kT.equals(pvInput.kT)
        && marketReaction.equals(pvInput.marketReaction)
        && sRated.equals(pvInput.sRated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), albedo, azimuth, etaConv, height, kG, kT, marketReaction, sRated);
  }
}
