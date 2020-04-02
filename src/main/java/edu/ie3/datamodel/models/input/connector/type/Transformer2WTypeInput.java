/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import tec.uom.se.ComparableQuantity;

import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.*;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.connector.Transformer2WInput} */
public class Transformer2WTypeInput extends AssetTypeInput {
  /** Short circuit resistance (typically in Ohm) */
  private final ComparableQuantity<ElectricResistance> rSc; // TODO doublecheck
  /** Short circuit reactance (typically in Ohm) */
  private final ComparableQuantity<ElectricResistance> xSc; // TODO doublecheck
  /** Rated apparent power (typically in MVA) */
  private final ComparableQuantity<Power> sRated; // TODO doublecheck
  /** Rated voltage of the high voltage winding (typically in kV) */
  private final ComparableQuantity<ElectricPotential> vRatedA; // TODO doublecheck
  /** Rated voltage of the low voltage winding (typically in kV) */
  private final ComparableQuantity<ElectricPotential> vRatedB; // TODO doublecheck
  /** Phase-to-ground conductance (typically in nS) */
  private final ComparableQuantity<ElectricConductance> gM; // TODO doublecheck
  /** Phase-to-ground susceptance (typically in nS) */
  private final ComparableQuantity<ElectricConductance> bM; // TODO doublecheck
  /** Voltage magnitude deviation per tap position (typically in %) */
  private final ComparableQuantity<Dimensionless> dV; // TODO doublecheck
  /** Voltage angle deviation per tap position (typically in °) */
  private final ComparableQuantity<Angle> dPhi; // TODO doublecheck
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
   * @param sRated Rated apparent power (typically in MVA)
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
      ComparableQuantity<ElectricResistance> rSc, // TODO doublecheck
      ComparableQuantity<ElectricResistance> xSc, // TODO doublecheck
      ComparableQuantity<Power> sRated, // TODO doublecheck
      ComparableQuantity<ElectricPotential> vRatedA, // TODO doublecheck
      ComparableQuantity<ElectricPotential> vRatedB, // TODO doublecheck
      ComparableQuantity<ElectricConductance> gM, // TODO doublecheck
      ComparableQuantity<ElectricConductance> bM, // TODO doublecheck
      ComparableQuantity<Dimensionless> dV, // TODO doublecheck
      ComparableQuantity<Angle> dPhi, // TODO doublecheck
      boolean tapSide,
      int tapNeutr,
      int tapMin,
      int tapMax) {
    super(uuid, id);
    this.rSc = rSc.to(StandardUnits.IMPEDANCE);
    this.xSc = xSc.to(StandardUnits.IMPEDANCE);
    this.sRated = sRated.to(StandardUnits.S_RATED);
    this.vRatedA = vRatedA.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    this.vRatedB = vRatedB.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    this.gM = gM.to(StandardUnits.ADMITTANCE);
    this.bM = bM.to(StandardUnits.ADMITTANCE);
    this.dV = dV.to(StandardUnits.DV_TAP);
    this.dPhi = dPhi.to(StandardUnits.DPHI_TAP);
    this.tapSide = tapSide;
    this.tapNeutr = tapNeutr;
    this.tapMin = tapMin;
    this.tapMax = tapMax;
  }

  public ComparableQuantity<ElectricResistance> getrSc() {
    return rSc;
  } // TODO doublecheck

  public ComparableQuantity<ElectricResistance> getxSc() {
    return xSc;
  } // TODO doublecheck

  public ComparableQuantity<Power> getsRated() {
    return sRated;
  } // TODO doublecheck

  public ComparableQuantity<ElectricPotential> getvRatedA() {
    return vRatedA;
  } // TODO doublecheck

  public ComparableQuantity<ElectricPotential> getvRatedB() {
    return vRatedB;
  } // TODO doublecheck

  public ComparableQuantity<ElectricConductance> getgM() {
    return gM;
  } // TODO doublecheck

  public ComparableQuantity<ElectricConductance> getbM() {
    return bM;
  } // TODO doublecheck

  public ComparableQuantity<Dimensionless> getdV() {
    return dV;
  } // TODO doublecheck

  public ComparableQuantity<Angle> getdPhi() {
    return dPhi;
  } // TODO doublecheck

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
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Transformer2WTypeInput that = (Transformer2WTypeInput) o;
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
        + "rSc="
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
}
