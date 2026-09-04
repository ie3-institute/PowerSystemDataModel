/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.utils.QuantityUtils;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricConductance;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.connector.Transformer3WInput}. */
public class Transformer3WTypeInput extends AssetTypeInput {
  /** Rated apparent power of the high voltage winding (typically in kVA). */
  private final ComparableQuantity<Power> sRatedA;

  /** Rated apparent power of the medium voltage winding (typically in kVA). */
  private final ComparableQuantity<Power> sRatedB;

  /** Rated apparent power of the low voltage windings (typically in kVA). */
  private final ComparableQuantity<Power> sRatedC;

  /** Rated voltage of the high voltage winding (typically in kV). */
  private final ComparableQuantity<ElectricPotential> vRatedA;

  /** Rated voltage of the medium voltage winding (typically in kV). */
  private final ComparableQuantity<ElectricPotential> vRatedB;

  /** Rated voltage of the low voltage winding (typically in kV). */
  private final ComparableQuantity<ElectricPotential> vRatedC;

  /** Short-circuit resistance of the high voltage winding (typically in Ohm). */
  private final ComparableQuantity<ElectricResistance> rScA;

  /** Short-circuit resistance of the medium voltage winding (typically in Ohm). */
  private final ComparableQuantity<ElectricResistance> rScB;

  /** Short-circuit resistance of the low voltage winding (typically in Ohm). */
  private final ComparableQuantity<ElectricResistance> rScC;

  /** Short-circuit reactance of the high voltage winding (typically in Ohm). */
  private final ComparableQuantity<ElectricResistance> xScA;

  /** Short-circuit reactance of the medium voltage winding (typically in Ohm). */
  private final ComparableQuantity<ElectricResistance> xScB;

  /** Short-circuit reactance of the low voltage winding (typically in Ohm). */
  private final ComparableQuantity<ElectricResistance> xScC;

  /** Phase-to-ground conductance (typically in nS). */
  private final ComparableQuantity<ElectricConductance> gM;

  /** Phase-to-ground susceptance (typically in nS). */
  private final ComparableQuantity<ElectricConductance> bM;

  /** Voltage magnitude deviation per tap position (typically in %). */
  private final ComparableQuantity<Dimensionless> dV;

  /** Voltage angle deviation per tap position (typically in °). */
  private final ComparableQuantity<Angle> dPhi;

  /** Neutral tap position. */
  private final int tapNeutr;

  /** Minimum available tap position. */
  private final int tapMin;

  /** Maximum available tap position. */
  private final int tapMax;

  /**
   * @param uuid of the input entity
   * @param id of this type
   * @param sRatedA Rated apparent power of the high voltage winding
   * @param sRatedB Rated apparent power of the medium voltage winding
   * @param sRatedC Rated apparent power of the low voltage winding
   * @param vRatedA Rated voltage magnitude of the high voltage winding
   * @param vRatedB Rated voltage magnitude of the medium voltage winding
   * @param vRatedC Rated voltage magnitude of the low voltage winding
   * @param rScA Short-circuit resistance of the high voltage winding
   * @param rScB Short-circuit resistance of the medium voltage winding
   * @param rScC Short-circuit resistance of the low voltage winding
   * @param xScA Short-circuit reactance of the high voltage winding
   * @param xScB Short-circuit reactance of the medium voltage winding
   * @param xScC Short-circuit reactance of the low voltage winding
   * @param gM Phase-to-ground conductance
   * @param bM Phase-to-ground susceptance
   * @param dV Voltage magnitude deviation per tap position
   * @param dPhi Voltage angle deviation per tap position
   * @param tapNeutr Neutral tap position
   * @param tapMin Minimum available tap position
   * @param tapMax Maximum available tap position
   */
  public Transformer3WTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Power> sRatedA,
      ComparableQuantity<Power> sRatedB,
      ComparableQuantity<Power> sRatedC,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB,
      ComparableQuantity<ElectricPotential> vRatedC,
      ComparableQuantity<ElectricResistance> rScA,
      ComparableQuantity<ElectricResistance> rScB,
      ComparableQuantity<ElectricResistance> rScC,
      ComparableQuantity<ElectricResistance> xScA,
      ComparableQuantity<ElectricResistance> xScB,
      ComparableQuantity<ElectricResistance> xScC,
      ComparableQuantity<ElectricConductance> gM,
      ComparableQuantity<ElectricConductance> bM,
      ComparableQuantity<Dimensionless> dV,
      ComparableQuantity<Angle> dPhi,
      int tapNeutr,
      int tapMin,
      int tapMax) {
    super(uuid, id);
    this.sRatedA = sRatedA;
    this.sRatedB = sRatedB;
    this.sRatedC = sRatedC;
    this.vRatedA = vRatedA;
    this.vRatedB = vRatedB;
    this.vRatedC = vRatedC;
    this.rScA = rScA;
    this.rScB = rScB;
    this.rScC = rScC;
    this.xScA = xScA;
    this.xScB = xScB;
    this.xScC = xScC;
    this.gM = gM;
    this.bM = bM;
    this.dV = dV;
    this.dPhi = dPhi;
    this.tapNeutr = tapNeutr;
    this.tapMin = tapMin;
    this.tapMax = tapMax;
  }

  /**
   * @param uuid of the input entity
   * @param id of this type
   * @param sRatedA Rated apparent power of the high voltage winding
   * @param sRatedB Rated apparent power of the medium voltage winding
   * @param sRatedC Rated apparent power of the low voltage winding
   * @param vRatedA Rated voltage magnitude of the high voltage winding
   * @param vRatedB Rated voltage magnitude of the medium voltage winding
   * @param vRatedC Rated voltage magnitude of the low voltage winding
   * @param rScA Short-circuit resistance of the high voltage winding
   * @param rScB Short-circuit resistance of the medium voltage winding
   * @param rScC Short-circuit resistance of the low voltage winding
   * @param xScA Short-circuit reactance of the high voltage winding
   * @param xScB Short-circuit reactance of the medium voltage winding
   * @param xScC Short-circuit reactance of the low voltage winding
   * @param gM Phase-to-ground conductance
   * @param bM Phase-to-ground susceptance
   * @param dV Voltage magnitude deviation per tap position
   * @param dPhi Voltage angle deviation per tap position
   * @param tapNeutr Neutral tap position
   * @param tapMin Minimum available tap position
   * @param tapMax Maximum available tap position
   * @param additionalInformation That were provided by the source
   */
  public Transformer3WTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Power> sRatedA,
      ComparableQuantity<Power> sRatedB,
      ComparableQuantity<Power> sRatedC,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB,
      ComparableQuantity<ElectricPotential> vRatedC,
      ComparableQuantity<ElectricResistance> rScA,
      ComparableQuantity<ElectricResistance> rScB,
      ComparableQuantity<ElectricResistance> rScC,
      ComparableQuantity<ElectricResistance> xScA,
      ComparableQuantity<ElectricResistance> xScB,
      ComparableQuantity<ElectricResistance> xScC,
      ComparableQuantity<ElectricConductance> gM,
      ComparableQuantity<ElectricConductance> bM,
      ComparableQuantity<Dimensionless> dV,
      ComparableQuantity<Angle> dPhi,
      int tapNeutr,
      int tapMin,
      int tapMax,
      Map<String, String> additionalInformation) {
    super(uuid, id);
    this.sRatedA = sRatedA;
    this.sRatedB = sRatedB;
    this.sRatedC = sRatedC;
    this.vRatedA = vRatedA;
    this.vRatedB = vRatedB;
    this.vRatedC = vRatedC;
    this.rScA = rScA;
    this.rScB = rScB;
    this.rScC = rScC;
    this.xScA = xScA;
    this.xScB = xScB;
    this.xScC = xScC;
    this.gM = gM;
    this.bM = bM;
    this.dV = dV;
    this.dPhi = dPhi;
    this.tapNeutr = tapNeutr;
    this.tapMin = tapMin;
    this.tapMax = tapMax;
    setAdditionalInformation(additionalInformation);
  }

  public ComparableQuantity<Power> getSRatedA() {
    return sRatedA;
  }

  public ComparableQuantity<Power> getSRatedB() {
    return sRatedB;
  }

  public ComparableQuantity<Power> getSRatedC() {
    return sRatedC;
  }

  public ComparableQuantity<ElectricPotential> getVRatedA() {
    return vRatedA;
  }

  public ComparableQuantity<ElectricPotential> getVRatedB() {
    return vRatedB;
  }

  public ComparableQuantity<ElectricPotential> getVRatedC() {
    return vRatedC;
  }

  public ComparableQuantity<ElectricResistance> getRScA() {
    return rScA;
  }

  public ComparableQuantity<ElectricResistance> getRScB() {
    return rScB;
  }

  public ComparableQuantity<ElectricResistance> getRScC() {
    return rScC;
  }

  public ComparableQuantity<ElectricResistance> getXScA() {
    return xScA;
  }

  public ComparableQuantity<ElectricResistance> getXScB() {
    return xScB;
  }

  public ComparableQuantity<ElectricResistance> getXScC() {
    return xScC;
  }

  public ComparableQuantity<ElectricConductance> getGM() {
    return gM;
  }

  public ComparableQuantity<ElectricConductance> getBM() {
    return bM;
  }

  public ComparableQuantity<Dimensionless> getDV() {
    return dV;
  }

  public ComparableQuantity<Angle> getDPhi() {
    return dPhi;
  }

  public int getTapNeutr() {
    return tapNeutr;
  }

  public int getTapMin() {
    return tapMin;
  }

  public int getTapMax() {
    return tapMax;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Transformer3WTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return QuantityUtils.equals(sRatedA, that.sRatedA)
        && QuantityUtils.equals(sRatedB, that.sRatedB)
        && QuantityUtils.equals(sRatedC, that.sRatedC)
        && QuantityUtils.equals(vRatedA, that.vRatedA)
        && QuantityUtils.equals(vRatedB, that.vRatedB)
        && QuantityUtils.equals(vRatedC, that.vRatedC)
        && QuantityUtils.equals(rScA, that.rScA)
        && QuantityUtils.equals(rScB, that.rScB)
        && QuantityUtils.equals(rScC, that.rScC)
        && QuantityUtils.equals(xScA, that.xScA)
        && QuantityUtils.equals(xScB, that.xScB)
        && QuantityUtils.equals(xScC, that.xScC)
        && QuantityUtils.equals(gM, that.gM)
        && QuantityUtils.equals(bM, that.bM)
        && QuantityUtils.equals(dV, that.dV)
        && QuantityUtils.equals(dPhi, that.dPhi)
        && tapNeutr == that.tapNeutr
        && tapMin == that.tapMin
        && tapMax == that.tapMax;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        sRatedA,
        sRatedB,
        sRatedC,
        vRatedA,
        vRatedB,
        vRatedC,
        rScA,
        rScB,
        rScC,
        xScA,
        xScB,
        xScC,
        gM,
        bM,
        dV,
        dPhi,
        tapNeutr,
        tapMin,
        tapMax);
  }

  @Override
  public String toString() {
    return "Transformer3WTypeInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", sRatedA="
        + sRatedA
        + ", sRatedB="
        + sRatedB
        + ", sRatedC="
        + sRatedC
        + ", vRatedA="
        + vRatedA
        + ", vRatedB="
        + vRatedB
        + ", vRatedC="
        + vRatedC
        + ", rScA="
        + rScA
        + ", rScB="
        + rScB
        + ", rScC="
        + rScC
        + ", xScA="
        + xScA
        + ", xScB="
        + xScB
        + ", xScC="
        + xScC
        + ", gM="
        + gM
        + ", bM="
        + bM
        + ", dV="
        + dV
        + ", dPhi="
        + dPhi
        + ", tapNeutr="
        + tapNeutr
        + ", tapMin="
        + tapMin
        + ", tapMax="
        + tapMax
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public Transformer3WTypeInputCopyBuilder copy() {
    return new Transformer3WTypeInputCopyBuilder(this);
  }

  public static class Transformer3WTypeInputCopyBuilder
      extends AssetTypeInputCopyBuilder<Transformer3WTypeInputCopyBuilder> {
    private ComparableQuantity<Power> sRatedA;

    private ComparableQuantity<Power> sRatedB;

    private ComparableQuantity<Power> sRatedC;

    private ComparableQuantity<ElectricPotential> vRatedA;

    private ComparableQuantity<ElectricPotential> vRatedB;

    private ComparableQuantity<ElectricPotential> vRatedC;

    private ComparableQuantity<ElectricResistance> rScA;

    private ComparableQuantity<ElectricResistance> rScB;

    private ComparableQuantity<ElectricResistance> rScC;

    private ComparableQuantity<ElectricResistance> xScA;

    private ComparableQuantity<ElectricResistance> xScB;

    private ComparableQuantity<ElectricResistance> xScC;

    private ComparableQuantity<ElectricConductance> gM;

    private ComparableQuantity<ElectricConductance> bM;

    private ComparableQuantity<Dimensionless> dV;

    private ComparableQuantity<Angle> dPhi;

    private int tapNeutr;

    private int tapMin;

    private int tapMax;

    protected Transformer3WTypeInputCopyBuilder(Transformer3WTypeInput entity) {
      super(entity);
      this.sRatedA = entity.sRatedA;
      this.sRatedB = entity.sRatedB;
      this.sRatedC = entity.sRatedC;
      this.vRatedA = entity.vRatedA;
      this.vRatedB = entity.vRatedB;
      this.vRatedC = entity.vRatedC;
      this.rScA = entity.rScA;
      this.rScB = entity.rScB;
      this.rScC = entity.rScC;
      this.xScA = entity.xScA;
      this.xScB = entity.xScB;
      this.xScC = entity.xScC;
      this.gM = entity.gM;
      this.bM = entity.bM;
      this.dV = entity.dV;
      this.dPhi = entity.dPhi;
      this.tapNeutr = entity.tapNeutr;
      this.tapMin = entity.tapMin;
      this.tapMax = entity.tapMax;
    }

    public Transformer3WTypeInputCopyBuilder sRatedA(ComparableQuantity<Power> sRatedA) {
      this.sRatedA = sRatedA;
      return thisInstance();
    }

    protected ComparableQuantity<Power> getSRatedA() {
      return sRatedA;
    }

    public Transformer3WTypeInputCopyBuilder sRatedB(ComparableQuantity<Power> sRatedB) {
      this.sRatedB = sRatedB;
      return thisInstance();
    }

    protected ComparableQuantity<Power> getSRatedB() {
      return sRatedB;
    }

    public Transformer3WTypeInputCopyBuilder sRatedC(ComparableQuantity<Power> sRatedC) {
      this.sRatedC = sRatedC;
      return thisInstance();
    }

    protected ComparableQuantity<Power> getSRatedC() {
      return sRatedC;
    }

    public Transformer3WTypeInputCopyBuilder vRatedA(
        ComparableQuantity<ElectricPotential> vRatedA) {
      this.vRatedA = vRatedA;
      return thisInstance();
    }

    protected ComparableQuantity<ElectricPotential> getVRatedA() {
      return vRatedA;
    }

    public Transformer3WTypeInputCopyBuilder vRatedB(
        ComparableQuantity<ElectricPotential> vRatedB) {
      this.vRatedB = vRatedB;
      return thisInstance();
    }

    protected ComparableQuantity<ElectricPotential> getVRatedB() {
      return vRatedB;
    }

    public Transformer3WTypeInputCopyBuilder vRatedC(
        ComparableQuantity<ElectricPotential> vRatedC) {
      this.vRatedC = vRatedC;
      return thisInstance();
    }

    protected ComparableQuantity<ElectricPotential> getVRatedC() {
      return vRatedC;
    }

    public Transformer3WTypeInputCopyBuilder rScA(ComparableQuantity<ElectricResistance> rScA) {
      this.rScA = rScA;
      return thisInstance();
    }

    protected ComparableQuantity<ElectricResistance> getRScA() {
      return rScA;
    }

    public Transformer3WTypeInputCopyBuilder rScB(ComparableQuantity<ElectricResistance> rScB) {
      this.rScB = rScB;
      return thisInstance();
    }

    protected ComparableQuantity<ElectricResistance> getRScB() {
      return rScB;
    }

    public Transformer3WTypeInputCopyBuilder rScC(ComparableQuantity<ElectricResistance> rScC) {
      this.rScC = rScC;
      return thisInstance();
    }

    protected ComparableQuantity<ElectricResistance> getRScC() {
      return rScC;
    }

    public Transformer3WTypeInputCopyBuilder xScA(ComparableQuantity<ElectricResistance> xScA) {
      this.xScA = xScA;
      return thisInstance();
    }

    protected ComparableQuantity<ElectricResistance> getXScA() {
      return xScA;
    }

    public Transformer3WTypeInputCopyBuilder xScB(ComparableQuantity<ElectricResistance> xScB) {
      this.xScB = xScB;
      return thisInstance();
    }

    protected ComparableQuantity<ElectricResistance> getXScB() {
      return xScB;
    }

    public Transformer3WTypeInputCopyBuilder xScC(ComparableQuantity<ElectricResistance> xScC) {
      this.xScC = xScC;
      return thisInstance();
    }

    protected ComparableQuantity<ElectricResistance> getXScC() {
      return xScC;
    }

    public Transformer3WTypeInputCopyBuilder gM(ComparableQuantity<ElectricConductance> gM) {
      this.gM = gM;
      return thisInstance();
    }

    protected ComparableQuantity<ElectricConductance> getGM() {
      return gM;
    }

    public Transformer3WTypeInputCopyBuilder bM(ComparableQuantity<ElectricConductance> bM) {
      this.bM = bM;
      return thisInstance();
    }

    protected ComparableQuantity<ElectricConductance> getBM() {
      return bM;
    }

    public Transformer3WTypeInputCopyBuilder dV(ComparableQuantity<Dimensionless> dV) {
      this.dV = dV;
      return thisInstance();
    }

    protected ComparableQuantity<Dimensionless> getDV() {
      return dV;
    }

    public Transformer3WTypeInputCopyBuilder dPhi(ComparableQuantity<Angle> dPhi) {
      this.dPhi = dPhi;
      return thisInstance();
    }

    protected ComparableQuantity<Angle> getDPhi() {
      return dPhi;
    }

    public Transformer3WTypeInputCopyBuilder tapNeutr(int tapNeutr) {
      this.tapNeutr = tapNeutr;
      return thisInstance();
    }

    protected int getTapNeutr() {
      return tapNeutr;
    }

    public Transformer3WTypeInputCopyBuilder tapMin(int tapMin) {
      this.tapMin = tapMin;
      return thisInstance();
    }

    protected int getTapMin() {
      return tapMin;
    }

    public Transformer3WTypeInputCopyBuilder tapMax(int tapMax) {
      this.tapMax = tapMax;
      return thisInstance();
    }

    protected int getTapMax() {
      return tapMax;
    }

    @Override
    public Transformer3WTypeInput build() {
      return new Transformer3WTypeInput(
          getUuid(),
          getId(),
          sRatedA,
          sRatedB,
          sRatedC,
          vRatedA,
          vRatedB,
          vRatedC,
          rScA,
          rScB,
          rScC,
          xScA,
          xScB,
          xScC,
          gM,
          bM,
          dV,
          dPhi,
          tapNeutr,
          tapMin,
          tapMax,
          getAdditionalInformation());
    }

    @Override
    protected Transformer3WTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
