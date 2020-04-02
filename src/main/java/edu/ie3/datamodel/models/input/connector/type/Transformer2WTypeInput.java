/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.*;
import tec.uom.se.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.connector.Transformer2WInput} */
public class Transformer2WTypeInput extends AssetTypeInput {
  /** Short circuit resistance (typically in Ohm) */
  private final ComparableQuantity<ElectricResistance> rSc; // TODO #65 Quantity replaced
  /** Short circuit reactance (typically in Ohm) */
  private final ComparableQuantity<ElectricResistance> xSc; // TODO #65 Quantity replaced
  /** Rated apparent power (typically in MVA) */
  private final ComparableQuantity<Power> sRated; // TODO #65 Quantity replaced
  /** Rated voltage of the high voltage winding (typically in kV) */
  private final ComparableQuantity<ElectricPotential> vRatedA; // TODO #65 Quantity replaced
  /** Rated voltage of the low voltage winding (typically in kV) */
  private final ComparableQuantity<ElectricPotential> vRatedB; // TODO #65 Quantity replaced
  /** Phase-to-ground conductance (typically in nS) */
  private final ComparableQuantity<ElectricConductance> gM; // TODO #65 Quantity replaced
  /** Phase-to-ground susceptance (typically in nS) */
  private final ComparableQuantity<ElectricConductance> bM; // TODO #65 Quantity replaced
  /** Voltage magnitude deviation per tap position (typically in %) */
  private final ComparableQuantity<Dimensionless> dV; // TODO #65 Quantity replaced
  /** Voltage angle deviation per tap position (typically in °) */
  private final ComparableQuantity<Angle> dPhi; // TODO #65 Quantity replaced
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
      ComparableQuantity<ElectricResistance> rSc, // TODO #65 Quantity replaced
      ComparableQuantity<ElectricResistance> xSc, // TODO #65 Quantity replaced
      ComparableQuantity<Power> sRated, // TODO #65 Quantity replaced
      ComparableQuantity<ElectricPotential> vRatedA, // TODO #65 Quantity replaced
      ComparableQuantity<ElectricPotential> vRatedB, // TODO #65 Quantity replaced
      ComparableQuantity<ElectricConductance> gM, // TODO #65 Quantity replaced
      ComparableQuantity<ElectricConductance> bM, // TODO #65 Quantity replaced
      ComparableQuantity<Dimensionless> dV, // TODO #65 Quantity replaced
      ComparableQuantity<Angle> dPhi, // TODO #65 Quantity replaced
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
  } // TODO #65 Quantity replaced

  public ComparableQuantity<ElectricResistance> getxSc() {
    return xSc;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<Power> getsRated() {
    return sRated;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<ElectricPotential> getvRatedA() {
    return vRatedA;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<ElectricPotential> getvRatedB() {
    return vRatedB;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<ElectricConductance> getgM() {
    return gM;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<ElectricConductance> getbM() {
    return bM;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<Dimensionless> getdV() {
    return dV;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<Angle> getdPhi() {
    return dPhi;
  } // TODO #65 Quantity replaced

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
