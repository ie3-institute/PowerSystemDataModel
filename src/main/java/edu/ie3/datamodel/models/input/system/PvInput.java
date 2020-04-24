/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import tec.uom.se.ComparableQuantity;

/** Describes a photovoltaic plant */
public class PvInput extends SystemParticipantInput {

  /** Albedo value (typically a value between 0 and 1) */
  private final double albedo;
  /** Inclination in a compass direction (typically °: South 0◦; West 90◦; East -90◦) */
  private final ComparableQuantity<Angle> azimuth;
  /** Efficiency of converter (typically in %) */
  private final ComparableQuantity<Dimensionless> etaConv;
  /** Tilted inclination from horizontal (typically in °) */
  private final ComparableQuantity<Angle> height;
  /** Generator correction factor merging different technical influences */
  private final double kG;
  /** Temperature correction factor */
  private final double kT;
  /** Is this asset market oriented? */
  private final boolean marketReaction;
  /** Rated apparent power (typically in kVA) */
  private final ComparableQuantity<Power> sRated;
  /** Rated power factor */
  private final double cosPhiRated;
  /**
   * Constructor for an operated photovoltaic plant
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param albedo Albedo value (typically a value between 0 and 1)
   * @param azimuth Inclination in a compass direction (typically °: South 0◦; West 90◦; East -90◦)
   * @param etaConv Efficiency of converter (typically in %)
   * @param height Tilted inclination from horizontal (typically in °)
   * @param kG Generator correction factor merging different technical influences
   * @param kT Generator correction factor merging different technical influences
   * @param marketReaction Is this asset market oriented?
   * @param sRated Rated apparent power (typically in kVA)
   * @param cosPhiRated Power factor
   */
  public PvInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      double albedo,
      ComparableQuantity<Angle> azimuth,
      ComparableQuantity<Dimensionless> etaConv,
      ComparableQuantity<Angle> height,
      double kG,
      double kT,
      boolean marketReaction,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    super(uuid, id, operator, operationTime, node, qCharacteristics);
    this.albedo = albedo;
    this.azimuth = azimuth.to(StandardUnits.AZIMUTH);
    this.etaConv = etaConv.to(StandardUnits.EFFICIENCY);
    this.height = height.to(StandardUnits.SOLAR_HEIGHT);
    this.kG = kG;
    this.kT = kT;
    this.marketReaction = marketReaction;
    this.sRated = sRated.to(StandardUnits.S_RATED);
    this.cosPhiRated = cosPhiRated;
  }

  /**
   * Constructor for an operated, always on photovoltaic plant
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param albedo Albedo value (typically a value between 0 and 1)
   * @param azimuth Inclination in a compass direction (typically °: South 0◦; West 90◦; East -90◦)
   * @param etaConv Efficiency of converter (typically in %)
   * @param height Tilted inclination from horizontal (typically in °)
   * @param kG Generator correction factor merging different technical influences
   * @param kT Generator correction factor merging different technical influences
   * @param marketReaction Is this asset market oriented?
   * @param sRated Rated apparent power (typically in kVA)
   * @param cosPhiRated Power factor
   */
  public PvInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      double albedo,
      ComparableQuantity<Angle> azimuth,
      ComparableQuantity<Dimensionless> etaConv,
      ComparableQuantity<Angle> height,
      double kG,
      double kT,
      boolean marketReaction,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    super(uuid, id, node, qCharacteristics);
    this.albedo = albedo;
    this.azimuth = azimuth.to(StandardUnits.AZIMUTH);
    this.etaConv = etaConv.to(StandardUnits.EFFICIENCY);
    this.height = height.to(StandardUnits.SOLAR_HEIGHT);
    this.kG = kG;
    this.kT = kT;
    this.marketReaction = marketReaction;
    this.sRated = sRated.to(StandardUnits.S_RATED);
    this.cosPhiRated = cosPhiRated;
  }

  public double getAlbedo() {
    return albedo;
  }

  public ComparableQuantity<Angle> getAzimuth() {
    return azimuth;
  }

  public ComparableQuantity<Dimensionless> getEtaConv() {
    return etaConv;
  }

  public ComparableQuantity<Angle> getHeight() {
    return height;
  }

  public boolean isMarketReaction() {
    return marketReaction;
  }

  public double getCosPhiRated() {
    return cosPhiRated;
  }

  public double getkG() {
    return kG;
  }

  public double getkT() {
    return kT;
  }

  public ComparableQuantity<Power> getsRated() {
    return sRated;
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
        && Double.compare(pvInput.cosPhiRated, cosPhiRated) == 0
        && azimuth.equals(pvInput.azimuth)
        && etaConv.equals(pvInput.etaConv)
        && height.equals(pvInput.height)
        && sRated.equals(pvInput.sRated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        albedo,
        azimuth,
        etaConv,
        height,
        kG,
        kT,
        marketReaction,
        sRated,
        cosPhiRated);
  }

  @Override
  public String toString() {
    return "PvInput{"
        + "albedo="
        + albedo
        + ", azimuth="
        + azimuth
        + ", etaConv="
        + etaConv
        + ", height="
        + height
        + ", kG="
        + kG
        + ", kT="
        + kT
        + ", marketReaction="
        + marketReaction
        + ", sRated="
        + sRated
        + ", cosphiRated="
        + cosPhiRated
        + '}';
  }
}
