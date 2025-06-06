/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.*;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.connector.Transformer3WInput} */
public class Transformer3WTypeInput extends AssetTypeInput {
  /** Rated apparent power of the high voltage winding (typically in kVA) */
  private final ComparableQuantity<Power> sRatedA; // Hv
  /** Rated apparent power of the medium voltage winding (typically in kVA) */
  private final ComparableQuantity<Power> sRatedB; // Mv
  /** Rated apparent power of the low voltage windings (typically in kVA) */
  private final ComparableQuantity<Power> sRatedC; // Lv
  /** Rated voltage magnitude of the high voltage winding (typically in kV) */
  private final ComparableQuantity<ElectricPotential> vRatedA; // Hv
  /** Rated voltage magnitude of the medium voltage winding (typically in kV) */
  private final ComparableQuantity<ElectricPotential> vRatedB; // Mv
  /** Rated voltage magnitude of the low voltage winding (typically in kV) */
  private final ComparableQuantity<ElectricPotential> vRatedC; // Lv
  /** Short-circuit resistance of the high voltage winding (typically in Ohm) */
  private final ComparableQuantity<ElectricResistance> rScA; // Hv
  /** Short-circuit resistance of the medium voltage winding (typically in Ohm) */
  private final ComparableQuantity<ElectricResistance> rScB; // Mv
  /** Short-circuit resistance of the low voltage winding (typically in Ohm) */
  private final ComparableQuantity<ElectricResistance> rScC; // Lv
  /** Short-circuit reactance of the high voltage winding (typically in Ohm) */
  private final ComparableQuantity<ElectricResistance> xScA; // Hv
  /** Short-circuit reactance of the medium voltage winding (typically in Ohm) */
  private final ComparableQuantity<ElectricResistance> xScB; // Mv
  /** Short-circuit reactance of the low voltage winding (typically in Ohm) */
  private final ComparableQuantity<ElectricResistance> xScC; // Lv
  /** Phase-to-ground conductance (typically in nS) */
  private final ComparableQuantity<ElectricConductance> gM;
  /** Phase-to-ground susceptance (typically in nS) */
  private final ComparableQuantity<ElectricConductance> bM;
  /** Voltage magnitude deviation per tap position (typically in %) */
  private final ComparableQuantity<Dimensionless> dV;
  /** Voltage angle deviation per tap position (typically in °) */
  private final ComparableQuantity<Angle> dPhi;
  /** Neutral tap position */
  private final int tapNeutr;
  /** Minimum available tap position */
  private final int tapMin;
  /** Maximum available tap position */
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
    this.sRatedA = sRatedA.to(StandardUnits.S_RATED);
    this.sRatedB = sRatedB.to(StandardUnits.S_RATED);
    this.sRatedC = sRatedC.to(StandardUnits.S_RATED);
    this.vRatedA = vRatedA.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    this.vRatedB = vRatedB.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    this.vRatedC = vRatedC.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    this.rScA = rScA.to(StandardUnits.RESISTANCE);
    this.rScB = rScB.to(StandardUnits.RESISTANCE);
    this.rScC = rScC.to(StandardUnits.RESISTANCE);
    this.xScA = xScA.to(StandardUnits.REACTANCE);
    this.xScB = xScB.to(StandardUnits.REACTANCE);
    this.xScC = xScC.to(StandardUnits.REACTANCE);
    this.gM = gM.to(StandardUnits.CONDUCTANCE);
    this.bM = bM.to(StandardUnits.SUSCEPTANCE);
    this.dV = dV.to(StandardUnits.DV_TAP);
    this.dPhi = dPhi.to(StandardUnits.DPHI_TAP);
    this.tapNeutr = tapNeutr;
    this.tapMin = tapMin;
    this.tapMax = tapMax;
  }

  public ComparableQuantity<Power> getsRatedA() {
    return sRatedA;
  }

  public ComparableQuantity<Power> getsRatedB() {
    return sRatedB;
  }

  public ComparableQuantity<Power> getsRatedC() {
    return sRatedC;
  }

  public ComparableQuantity<ElectricPotential> getvRatedA() {
    return vRatedA;
  }

  public ComparableQuantity<ElectricPotential> getvRatedB() {
    return vRatedB;
  }

  public ComparableQuantity<ElectricPotential> getvRatedC() {
    return vRatedC;
  }

  public ComparableQuantity<ElectricResistance> getrScA() {
    return rScA;
  }

  public ComparableQuantity<ElectricResistance> getrScB() {
    return rScB;
  }

  public ComparableQuantity<ElectricResistance> getrScC() {
    return rScC;
  }

  public ComparableQuantity<ElectricResistance> getxScA() {
    return xScA;
  }

  public ComparableQuantity<ElectricResistance> getxScB() {
    return xScB;
  }

  public ComparableQuantity<ElectricResistance> getxScC() {
    return xScC;
  }

  public ComparableQuantity<ElectricConductance> getgM() {
    return gM;
  }

  public ComparableQuantity<ElectricConductance> getbM() {
    return bM;
  }

  public ComparableQuantity<Dimensionless> getdV() {
    return dV;
  }

  public ComparableQuantity<Angle> getdPhi() {
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
  public Transformer3WTypeInputCopyBuilder copy() {
    return new Transformer3WTypeInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Transformer3WTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return tapNeutr == that.tapNeutr
        && tapMin == that.tapMin
        && tapMax == that.tapMax
        && sRatedA.equals(that.sRatedA)
        && sRatedB.equals(that.sRatedB)
        && sRatedC.equals(that.sRatedC)
        && vRatedA.equals(that.vRatedA)
        && vRatedB.equals(that.vRatedB)
        && vRatedC.equals(that.vRatedC)
        && rScA.equals(that.rScA)
        && rScB.equals(that.rScB)
        && rScC.equals(that.rScC)
        && xScA.equals(that.xScA)
        && xScB.equals(that.xScB)
        && xScC.equals(that.xScC)
        && gM.equals(that.gM)
        && bM.equals(that.bM)
        && dV.equals(that.dV)
        && dPhi.equals(that.dPhi);
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
        + '}';
  }

  /**
   * Abstract class for all builder that build child entities of abstract class {@link
   * Transformer3WTypeInput}
   */
  public static final class Transformer3WTypeInputCopyBuilder
      extends AssetTypeInput.AssetTypeInputCopyBuilder<Transformer3WTypeInputCopyBuilder> {

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

    private Transformer3WTypeInputCopyBuilder(Transformer3WTypeInput entity) {
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

    /** Setter */
    public Transformer3WTypeInputCopyBuilder sRatedA(ComparableQuantity<Power> sRatedA) {
      this.sRatedA = sRatedA;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder sRatedB(ComparableQuantity<Power> sRatedB) {
      this.sRatedB = sRatedB;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder sRatedC(ComparableQuantity<Power> sRatedC) {
      this.sRatedC = sRatedC;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder vRatedA(
        ComparableQuantity<ElectricPotential> vRatedA) {
      this.vRatedA = vRatedA;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder vRatedB(
        ComparableQuantity<ElectricPotential> vRatedB) {
      this.vRatedB = vRatedB;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder vRatedC(
        ComparableQuantity<ElectricPotential> vRatedC) {
      this.vRatedC = vRatedC;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder rScA(ComparableQuantity<ElectricResistance> rScA) {
      this.rScA = rScA;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder rScB(ComparableQuantity<ElectricResistance> rScB) {
      this.rScB = rScB;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder rScC(ComparableQuantity<ElectricResistance> rScC) {
      this.rScC = rScC;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder xScA(ComparableQuantity<ElectricResistance> xScA) {
      this.xScA = xScA;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder xScB(ComparableQuantity<ElectricResistance> xScB) {
      this.xScB = xScB;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder xScC(ComparableQuantity<ElectricResistance> xScC) {
      this.xScC = xScC;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder gM(ComparableQuantity<ElectricConductance> gM) {
      this.gM = gM;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder bM(ComparableQuantity<ElectricConductance> bM) {
      this.bM = bM;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder dV(ComparableQuantity<Dimensionless> dV) {
      this.dV = dV;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder dPhi(ComparableQuantity<Angle> dPhi) {
      this.dPhi = dPhi;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder tapNeutr(int tapNeutr) {
      this.tapNeutr = tapNeutr;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder tapMin(int tapMin) {
      this.tapMin = tapMin;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder tapMax(int tapMax) {
      this.tapMax = tapMax;
      return thisInstance();
    }

    @Override
    public Transformer3WTypeInput build() {
      return new Transformer3WTypeInput(
          getUuid(), getId(), sRatedA, sRatedB, sRatedC, vRatedA, vRatedB, vRatedC, rScA, rScB,
          rScC, xScA, xScB, xScC, gM, bM, dV, dPhi, tapNeutr, tapMin, tapMax);
    }

    @Override
    protected Transformer3WTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
