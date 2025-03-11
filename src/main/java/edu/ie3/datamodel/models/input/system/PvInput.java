/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes a photovoltaic plant */
public class PvInput extends SystemParticipantInput {

  /** Albedo value (typically a value between 0 and 1) */
  private final double albedo;
  /** Inclination in a compass direction (typically °: South 0◦; West 90◦; East -90◦) */
  private final ComparableQuantity<Angle> azimuth;
  /** Efficiency of converter (typically in %) */
  private final ComparableQuantity<Dimensionless> etaConv;
  /** Tilted inclination from horizontal (typically in °) */
  private final ComparableQuantity<Angle> elevationAngle;
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
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param albedo Albedo value (typically a value between 0 and 1)
   * @param azimuth Inclination in a compass direction (typically °: South 0◦; West 90◦; East -90◦)
   * @param etaConv Efficiency of converter (typically in %)
   * @param elevationAngle Tilted inclination from horizontal (typically in °)
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
      EmInput em,
      double albedo,
      ComparableQuantity<Angle> azimuth,
      ComparableQuantity<Dimensionless> etaConv,
      ComparableQuantity<Angle> elevationAngle,
      double kG,
      double kT,
      boolean marketReaction,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, em);
    this.albedo = albedo;
    this.azimuth = azimuth.to(StandardUnits.AZIMUTH);
    this.etaConv = etaConv.to(StandardUnits.EFFICIENCY);
    this.elevationAngle = elevationAngle.to(StandardUnits.SOLAR_ELEVATION_ANGLE);
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
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param albedo Albedo value (typically a value between 0 and 1)
   * @param azimuth Inclination in a compass direction (typically °: South 0◦; West 90◦; East -90◦)
   * @param etaConv Efficiency of converter (typically in %)
   * @param elevationAngle Tilted inclination from horizontal (typically in °)
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
      EmInput em,
      double albedo,
      ComparableQuantity<Angle> azimuth,
      ComparableQuantity<Dimensionless> etaConv,
      ComparableQuantity<Angle> elevationAngle,
      double kG,
      double kT,
      boolean marketReaction,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    super(uuid, id, node, qCharacteristics, em);
    this.albedo = albedo;
    this.azimuth = azimuth.to(StandardUnits.AZIMUTH);
    this.etaConv = etaConv.to(StandardUnits.EFFICIENCY);
    this.elevationAngle = elevationAngle.to(StandardUnits.SOLAR_ELEVATION_ANGLE);
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

  public ComparableQuantity<Angle> getElevationAngle() {
    return elevationAngle;
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

  public PvInputCopyBuilder copy() {
    return new PvInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PvInput pvInput)) return false;
    if (!super.equals(o)) return false;
    return Double.compare(pvInput.albedo, albedo) == 0
        && Double.compare(pvInput.kG, kG) == 0
        && Double.compare(pvInput.kT, kT) == 0
        && marketReaction == pvInput.marketReaction
        && Double.compare(pvInput.cosPhiRated, cosPhiRated) == 0
        && azimuth.equals(pvInput.azimuth)
        && etaConv.equals(pvInput.etaConv)
        && elevationAngle.equals(pvInput.elevationAngle)
        && sRated.equals(pvInput.sRated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        albedo,
        azimuth,
        etaConv,
        elevationAngle,
        kG,
        kT,
        marketReaction,
        sRated,
        cosPhiRated);
  }

  @Override
  public String toString() {
    return "PvInput{"
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
        + ", albedo="
        + albedo
        + ", azimuth="
        + azimuth
        + ", etaConv="
        + etaConv
        + ", elevationAngle="
        + elevationAngle
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

  /**
   * A builder pattern based approach to create copies of {@link PvInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link PvInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class PvInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<PvInputCopyBuilder> {

    private double albedo;
    private ComparableQuantity<Angle> azimuth;
    private ComparableQuantity<Dimensionless> etaConv;
    private ComparableQuantity<Angle> elevationAngle;
    private double kG;
    private double kT;
    private boolean marketReaction;
    private ComparableQuantity<Power> sRated;
    private double cosPhiRated;

    public PvInputCopyBuilder(PvInput entity) {
      super(entity);
      this.albedo = entity.getAlbedo();
      this.azimuth = entity.getAzimuth();
      this.etaConv = entity.getEtaConv();
      this.elevationAngle = entity.getElevationAngle();
      this.kG = entity.getkG();
      this.kT = entity.getkT();
      this.marketReaction = entity.isMarketReaction();
      this.sRated = entity.getsRated();
      this.cosPhiRated = entity.getCosPhiRated();
    }

    public PvInputCopyBuilder albedo(double albedo) {
      this.albedo = albedo;
      return thisInstance();
    }

    public PvInputCopyBuilder azimuth(ComparableQuantity<Angle> azimuth) {
      this.azimuth = azimuth;
      return thisInstance();
    }

    public PvInputCopyBuilder etaConv(ComparableQuantity<Dimensionless> etaConv) {
      this.etaConv = etaConv;
      return thisInstance();
    }

    public PvInputCopyBuilder elevationAngle(ComparableQuantity<Angle> elevationAngle) {
      this.elevationAngle = elevationAngle;
      return thisInstance();
    }

    public PvInputCopyBuilder kG(double kG) {
      this.kG = kG;
      return thisInstance();
    }

    public PvInputCopyBuilder kT(double kT) {
      this.kT = kT;
      return thisInstance();
    }

    public PvInputCopyBuilder marketReaction(boolean marketReaction) {
      this.marketReaction = marketReaction;
      return thisInstance();
    }

    public PvInputCopyBuilder sRated(ComparableQuantity<Power> sRated) {
      this.sRated = sRated;
      return thisInstance();
    }

    public PvInputCopyBuilder cosPhiRated(double cosPhiRated) {
      this.cosPhiRated = cosPhiRated;
      return thisInstance();
    }

    @Override
    public PvInputCopyBuilder scale(Double factor) {
      this.sRated = this.sRated.multiply(factor);
      return thisInstance();
    }

    @Override
    public PvInput build() {
      return new PvInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNode(),
          getqCharacteristics(),
          getEm(),
          albedo,
          azimuth,
          etaConv,
          elevationAngle,
          kG,
          kT,
          marketReaction,
          sRated,
          cosPhiRated);
    }

    @Override
    protected PvInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
