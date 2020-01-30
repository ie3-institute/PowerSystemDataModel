/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system;

import edu.ie3.models.OperationTime;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;

/** Describes a photovoltaic plant */
public class PvInput extends SystemParticipantInput {

  /** Albedo value (typically a value between 0 and 1) */
  private double albedo;
  /** Inclination in a compass direction (typically °: South 0◦; West 90◦; East -90◦) */
  private Quantity<Angle> azimuth;
  /** Efficiency of converter (typically in %) */
  private Quantity<Dimensionless> etaConv;
  /** Tilted inclination from horizontal (typically in °) */
  private Quantity<Angle> height;
  /** Generator correction factor merging different technical influences */
  private double kG;
  /** Temperature correction factor */
  private double kT;
  /** Is this asset market oriented? */
  private boolean marketReaction;
  /** Rated apparent power (typically in kVA) */
  private Quantity<Power> sRated;
  /**
   * Constructor for an operated photovoltaic plant
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
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
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      double cosphiRated,
      double albedo,
      Quantity<Angle> azimuth,
      Quantity<Dimensionless> etaConv,
      Quantity<Angle> height,
      double kG,
      double kT,
      boolean marketReaction,
      Quantity<Power> sRated) {
    super(uuid, operationTime, operator, id, node, qCharacteristics, cosphiRated);
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
   * Constructor for a non-operated photovoltaic plant
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
      double cosphiRated,
      double albedo,
      Quantity<Angle> azimuth,
      Quantity<Dimensionless> etaConv,
      Quantity<Angle> height,
      double kG,
      double kT,
      boolean marketReaction,
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

  public double getAlbedo() {
    return albedo;
  }

  public void setAlbedo(double albedo) {
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

  public double getkG() {
    return kG;
  }

  public void setkG(double kG) {
    this.kG = kG;
  }

  public double getkT() {
    return kT;
  }

  public void setkT(double kT) {
    this.kT = kT;
  }

  public boolean getMarketReaction() {
    return marketReaction;
  }

  public void setMarketReaction(boolean marketReaction) {
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
    return Double.compare(pvInput.albedo, albedo) == 0
        && Double.compare(pvInput.kG, kG) == 0
        && Double.compare(pvInput.kT, kT) == 0
        && marketReaction == pvInput.marketReaction
        && azimuth.equals(pvInput.azimuth)
        && etaConv.equals(pvInput.etaConv)
        && height.equals(pvInput.height)
        && sRated.equals(pvInput.sRated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), albedo, azimuth, etaConv, height, kG, kT, marketReaction, sRated);
  }
}
