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

/** Describes the type of a {@link edu.ie3.datamodel.models.input.connector.Transformer2WInput} */
public class Transformer2WTypeInput extends AssetTypeInput {
  /** Short circuit resistance (typically in Ohm) */
  private final ComparableQuantity<ElectricResistance> rSc;
  /** Short circuit reactance (typically in Ohm) */
  private final ComparableQuantity<ElectricResistance> xSc;
  /** Rated apparent power (typically in kVA) */
  private final ComparableQuantity<Power> sRated;
  /** Rated voltage of the high voltage winding (typically in kV) */
  private final ComparableQuantity<ElectricPotential> vRatedA;
  /** Rated voltage of the low voltage winding (typically in kV) */
  private final ComparableQuantity<ElectricPotential> vRatedB;
  /** Phase-to-ground conductance (typically in nS) */
  private final ComparableQuantity<ElectricConductance> gM;
  /** Phase-to-ground susceptance (typically in nS) */
  private final ComparableQuantity<ElectricConductance> bM;
  /** Voltage magnitude deviation per tap position (typically in %) */
  private final ComparableQuantity<Dimensionless> dV;
  /** Voltage angle deviation per tap position (typically in °) */
  private final ComparableQuantity<Angle> dPhi;
  /** Selection of winding, where the tap changer is installed. Low voltage, if true */
  private final boolean tapSide;
  /** Neutral tap position */
  private final int tapNeutr;
  /** Minimum available tap position */
  private final int tapMin;
  /** Maximum available tap position */
  private final int tapMax;

  /**
   * @param uuid of the input entity
   * @param id of the type
   * @param rSc Short circuit resistance
   * @param xSc Short circuit reactance
   * @param sRated Rated apparent power (typically in kVA)
   * @param vRatedA Rated voltage of the high voltage winding
   * @param vRatedB Rated voltage of the low voltage winding
   * @param gM Phase-to-ground conductance
   * @param bM Phase-to-ground susceptance
   * @param dV Voltage magnitude deviation per tap position
   * @param dPhi Voltage angle deviation per tap position
   * @param tapSide Selection of winding, where the tap changer is installed. Low voltage, if true
   * @param tapNeutr Neutral tap position
   * @param tapMin Minimum available tap position
   * @param tapMax Maximum available tap position
   */
  public Transformer2WTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<ElectricResistance> rSc,
      ComparableQuantity<ElectricResistance> xSc,
      ComparableQuantity<Power> sRated,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB,
      ComparableQuantity<ElectricConductance> gM,
      ComparableQuantity<ElectricConductance> bM,
      ComparableQuantity<Dimensionless> dV,
      ComparableQuantity<Angle> dPhi,
      boolean tapSide,
      int tapNeutr,
      int tapMin,
      int tapMax) {
    super(uuid, id);
    this.rSc = rSc.to(StandardUnits.RESISTANCE);
    this.xSc = xSc.to(StandardUnits.REACTANCE);
    this.sRated = sRated.to(StandardUnits.S_RATED);
    this.vRatedA = vRatedA.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    this.vRatedB = vRatedB.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    this.gM = gM.to(StandardUnits.CONDUCTANCE);
    this.bM = bM.to(StandardUnits.SUSCEPTANCE);
    this.dV = dV.to(StandardUnits.DV_TAP);
    this.dPhi = dPhi.to(StandardUnits.DPHI_TAP);
    this.tapSide = tapSide;
    this.tapNeutr = tapNeutr;
    this.tapMin = tapMin;
    this.tapMax = tapMax;
  }

  public ComparableQuantity<ElectricResistance> getrSc() {
    return rSc;
  }

  public ComparableQuantity<ElectricResistance> getxSc() {
    return xSc;
  }

  public ComparableQuantity<Power> getsRated() {
    return sRated;
  }

  public ComparableQuantity<ElectricPotential> getvRatedA() {
    return vRatedA;
  }

  public ComparableQuantity<ElectricPotential> getvRatedB() {
    return vRatedB;
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

  public boolean isTapSide() {
    return tapSide;
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
    if (!(o instanceof Transformer2WTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return tapSide == that.tapSide
        && tapNeutr == that.tapNeutr
        && tapMin == that.tapMin
        && tapMax == that.tapMax
        && rSc.equals(that.rSc)
        && xSc.equals(that.xSc)
        && sRated.equals(that.sRated)
        && vRatedA.equals(that.vRatedA)
        && vRatedB.equals(that.vRatedB)
        && gM.equals(that.gM)
        && bM.equals(that.bM)
        && dV.equals(that.dV)
        && dPhi.equals(that.dPhi);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        rSc,
        xSc,
        sRated,
        vRatedA,
        vRatedB,
        gM,
        bM,
        dV,
        dPhi,
        tapSide,
        tapNeutr,
        tapMin,
        tapMax);
  }

  @Override
  public String toString() {
    return "Transformer2WTypeInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", rSc="
        + rSc
        + ", xSc="
        + xSc
        + ", sRated="
        + sRated
        + ", vRatedA="
        + vRatedA
        + ", vRatedB="
        + vRatedB
        + ", gM="
        + gM
        + ", bM="
        + bM
        + ", dV="
        + dV
        + ", dPhi="
        + dPhi
        + ", tapSide="
        + tapSide
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
   * Transformer2WTypeInput}
   */
  public abstract static class Transformer2WTypeInputCopyBuilder<
          B extends Transformer2WTypeInputCopyBuilder<B>>
      extends AssetTypeInput.AssetTypeInputCopyBuilder<B> {

    private ComparableQuantity<ElectricResistance> rSc;
    private ComparableQuantity<ElectricResistance> xSc;
    private ComparableQuantity<Power> sRated;
    private ComparableQuantity<ElectricPotential> vRatedA;
    private ComparableQuantity<ElectricPotential> vRatedB;
    private ComparableQuantity<ElectricConductance> gM;
    private ComparableQuantity<ElectricConductance> bM;
    private ComparableQuantity<Dimensionless> dV;
    private ComparableQuantity<Angle> dPhi;
    private boolean tapSide;
    private int tapNeutr;
    private int tapMin;
    private int tapMax;

    protected Transformer2WTypeInputCopyBuilder(Transformer2WTypeInput entity) {
      super(entity);
      this.rSc = entity.rSc;
      this.xSc = entity.xSc;
      this.sRated = entity.sRated;
      this.vRatedA = entity.vRatedA;
      this.vRatedB = entity.vRatedB;
      this.gM = entity.gM;
      this.bM = entity.bM;
      this.dV = entity.dV;
      this.dPhi = entity.dPhi;
      this.tapSide = entity.tapSide;
      this.tapNeutr = entity.tapNeutr;
      this.tapMin = entity.tapMin;
      this.tapMax = entity.tapMax;
    }

    /** Setter */
    public B setRSc(ComparableQuantity<ElectricResistance> rSc) {
      this.rSc = rSc;
      return thisInstance();
    }

    public B setXSc(ComparableQuantity<ElectricResistance> xSc) {
      this.xSc = xSc;
      return thisInstance();
    }

    public B setsRated(ComparableQuantity<Power> sRated) {
      this.sRated = sRated;
      return thisInstance();
    }

    public B setvRatedA(ComparableQuantity<ElectricPotential> vRatedA) {
      this.vRatedA = vRatedA;
      return thisInstance();
    }

    public B setvRatedB(ComparableQuantity<ElectricPotential> vRatedB) {
      this.vRatedB = vRatedB;
      return thisInstance();
    }

    public B setgM(ComparableQuantity<ElectricConductance> gM) {
      this.gM = gM;
      return thisInstance();
    }

    public B setbM(ComparableQuantity<ElectricConductance> bM) {
      this.bM = bM;
      return thisInstance();
    }

    public B setdV(ComparableQuantity<Dimensionless> dV) {
      this.dV = dV;
      return thisInstance();
    }

    public B setdPhi(ComparableQuantity<Angle> dPhi) {
      this.dPhi = dPhi;
      return thisInstance();
    }

    public B settapSide(boolean tapSide) {
      this.tapSide = tapSide;
      return thisInstance();
    }

    public B settapNeutr(int tapNeutr) {
      this.tapNeutr = tapNeutr;
      return thisInstance();
    }

    public B settapMin(int tapMin) {
      this.tapMin = tapMin;
      return thisInstance();
    }

    public B settapMax(int tapMax) {
      this.tapMax = tapMax;
      return thisInstance();
    }

    /** Getter */
    public ComparableQuantity<ElectricResistance> getrSc() {
      return rSc;
    }

    public ComparableQuantity<ElectricResistance> getxSc() {
      return xSc;
    }

    public ComparableQuantity<Power> getsRated() {
      return sRated;
    }

    public ComparableQuantity<ElectricPotential> getvRatedA() {
      return vRatedA;
    }

    public ComparableQuantity<ElectricPotential> getvRatedB() {
      return vRatedB;
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

    public boolean isTapSide() {
      return tapSide;
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
    public abstract Transformer2WTypeInput build();

    @Override
    protected abstract B thisInstance();
  }
}
