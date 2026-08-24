/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes a photovoltaic plant. */
public class PvInput extends SystemParticipantInput {
  /** Albedo value (typically a value between 0 and 1). */
  private final double albedo;

  /** Inclination in a compass direction (typically °: South 0◦; West 90◦; East -90◦). */
  private final ComparableQuantity<Angle> azimuth;

  /** Efficiency of converter (typically in %). */
  private final ComparableQuantity<Dimensionless> etaConv;

  /** Tilted inclination from horizontal (typically in °). */
  private final ComparableQuantity<Angle> elevationAngle;

  /** Generator correction factor merging different technical influences. */
  private final double kG;

  /** Temperature correction factor. */
  private final double kT;

  /** Rated apparent power (typically in kVA). */
  private final ComparableQuantity<Power> sRated;

  /** Rated power factor. */
  private final double cosPhiRated;

  /**
   * Constructor for an operated photovoltaic plant.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param albedo Albedo value (typically a value between 0 and 1)
   * @param azimuth Inclination in a compass direction (typically °: South 0◦; West 90◦; East -90◦)
   * @param etaConv Efficiency of converter (typically in %)
   * @param elevationAngle Tilted inclination from horizontal (typically in °)
   * @param kG Generator correction factor merging different technical influences
   * @param kT Generator correction factor merging different technical influences
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
      EmInput controllingEm,
      double albedo,
      ComparableQuantity<Angle> azimuth,
      ComparableQuantity<Dimensionless> etaConv,
      ComparableQuantity<Angle> elevationAngle,
      double kG,
      double kT,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.albedo = albedo;
    this.azimuth = azimuth;
    this.etaConv = etaConv;
    this.elevationAngle = elevationAngle;
    this.kG = kG;
    this.kT = kT;
    this.sRated = sRated;
    this.cosPhiRated = cosPhiRated;
  }

  /**
   * Constructor for an operated photovoltaic plant.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param albedo Albedo value (typically a value between 0 and 1)
   * @param azimuth Inclination in a compass direction (typically °: South 0◦; West 90◦; East -90◦)
   * @param etaConv Efficiency of converter (typically in %)
   * @param elevationAngle Tilted inclination from horizontal (typically in °)
   * @param kG Generator correction factor merging different technical influences
   * @param kT Generator correction factor merging different technical influences
   * @param sRated Rated apparent power (typically in kVA)
   * @param cosPhiRated Power factor
   * @param additionalInformation That were provided by the source
   */
  public PvInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      double albedo,
      ComparableQuantity<Angle> azimuth,
      ComparableQuantity<Dimensionless> etaConv,
      ComparableQuantity<Angle> elevationAngle,
      double kG,
      double kT,
      ComparableQuantity<Power> sRated,
      double cosPhiRated,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.albedo = albedo;
    this.azimuth = azimuth;
    this.etaConv = etaConv;
    this.elevationAngle = elevationAngle;
    this.kG = kG;
    this.kT = kT;
    this.sRated = sRated;
    this.cosPhiRated = cosPhiRated;
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for an operated photovoltaic plant.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param albedo Albedo value (typically a value between 0 and 1)
   * @param azimuth Inclination in a compass direction (typically °: South 0◦; West 90◦; East -90◦)
   * @param etaConv Efficiency of converter (typically in %)
   * @param elevationAngle Tilted inclination from horizontal (typically in °)
   * @param kG Generator correction factor merging different technical influences
   * @param kT Generator correction factor merging different technical influences
   * @param sRated Rated apparent power (typically in kVA)
   * @param cosPhiRated Power factor
   */
  public PvInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      double albedo,
      ComparableQuantity<Angle> azimuth,
      ComparableQuantity<Dimensionless> etaConv,
      ComparableQuantity<Angle> elevationAngle,
      double kG,
      double kT,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    super(uuid, id, node, qCharacteristics, controllingEm);
    this.albedo = albedo;
    this.azimuth = azimuth;
    this.etaConv = etaConv;
    this.elevationAngle = elevationAngle;
    this.kG = kG;
    this.kT = kT;
    this.sRated = sRated;
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

  public double getKG() {
    return kG;
  }

  public double getKT() {
    return kT;
  }

  public ComparableQuantity<Power> getsRated() {
    return sRated;
  }

  public double getCosPhiRated() {
    return cosPhiRated;
  }

  @Override
  public ComparableQuantity<Power> sRated() {
    return sRated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PvInput that)) return false;
    if (!super.equals(o)) return false;
    return albedo == that.albedo
        && azimuth.equals(that.azimuth)
        && etaConv.equals(that.etaConv)
        && elevationAngle.equals(that.elevationAngle)
        && kG == that.kG
        && kT == that.kT
        && sRated.equals(that.sRated)
        && cosPhiRated == that.cosPhiRated;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), albedo, azimuth, etaConv, elevationAngle, kG, kT, sRated, cosPhiRated);
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
        + ", qCharacteristics="
        + getqCharacteristics()
        + ", controllingEm="
        + getControllingEm().map(e -> e.getUuid().toString()).orElse("")
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
        + ", sRated="
        + sRated
        + ", cosPhiRated="
        + cosPhiRated
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public PvInputCopyBuilder copy() {
    return new PvInputCopyBuilder(this);
  }

  public static class PvInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<PvInputCopyBuilder> {
    private double albedo;

    private ComparableQuantity<Angle> azimuth;

    private ComparableQuantity<Dimensionless> etaConv;

    private ComparableQuantity<Angle> elevationAngle;

    private double kG;

    private double kT;

    private ComparableQuantity<Power> sRated;

    private double cosPhiRated;

    protected PvInputCopyBuilder(PvInput entity) {
      super(entity);
      this.albedo = entity.albedo;
      this.azimuth = entity.azimuth;
      this.etaConv = entity.etaConv;
      this.elevationAngle = entity.elevationAngle;
      this.kG = entity.kG;
      this.kT = entity.kT;
      this.sRated = entity.sRated;
      this.cosPhiRated = entity.cosPhiRated;
    }

    public PvInputCopyBuilder albedo(double albedo) {
      this.albedo = albedo;
      return thisInstance();
    }

    protected double getAlbedo() {
      return albedo;
    }

    public PvInputCopyBuilder azimuth(ComparableQuantity<Angle> azimuth) {
      this.azimuth = azimuth;
      return thisInstance();
    }

    protected ComparableQuantity<Angle> getAzimuth() {
      return azimuth;
    }

    public PvInputCopyBuilder etaConv(ComparableQuantity<Dimensionless> etaConv) {
      this.etaConv = etaConv;
      return thisInstance();
    }

    protected ComparableQuantity<Dimensionless> getEtaConv() {
      return etaConv;
    }

    public PvInputCopyBuilder elevationAngle(ComparableQuantity<Angle> elevationAngle) {
      this.elevationAngle = elevationAngle;
      return thisInstance();
    }

    protected ComparableQuantity<Angle> getElevationAngle() {
      return elevationAngle;
    }

    public PvInputCopyBuilder kG(double kG) {
      this.kG = kG;
      return thisInstance();
    }

    protected double getKG() {
      return kG;
    }

    public PvInputCopyBuilder kT(double kT) {
      this.kT = kT;
      return thisInstance();
    }

    protected double getKT() {
      return kT;
    }

    public PvInputCopyBuilder sRated(ComparableQuantity<Power> sRated) {
      this.sRated = sRated;
      return thisInstance();
    }

    protected ComparableQuantity<Power> getsRated() {
      return sRated;
    }

    public PvInputCopyBuilder cosPhiRated(double cosPhiRated) {
      this.cosPhiRated = cosPhiRated;
      return thisInstance();
    }

    protected double getCosPhiRated() {
      return cosPhiRated;
    }

    @Override
    public PvInputCopyBuilder scale(double factor) {
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
          getControllingEm(),
          albedo,
          azimuth,
          etaConv,
          elevationAngle,
          kG,
          kT,
          sRated,
          cosPhiRated,
          getAdditionalInformation());
    }

    @Override
    protected PvInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
