/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.util.quantities.QuantityUtil;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.*;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.connector.Transformer3WInput} */
public class Transformer3WTypeInput extends AssetTypeInput {
  /** Rated apparent power of the high voltage winding (typically in MVA) */
  private final ComparableQuantity<Power> sRatedA; // Hv
  /** Rated apparent power of the medium voltage winding (typically in MVA) */
  private final ComparableQuantity<Power> sRatedB; // Mv
  /** Rated apparent power of the low voltage windings (typically in MVA) */
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
    this.rScA = rScA.to(StandardUnits.IMPEDANCE);
    this.rScB = rScB.to(StandardUnits.IMPEDANCE);
    this.rScC = rScC.to(StandardUnits.IMPEDANCE);
    this.xScA = xScA.to(StandardUnits.IMPEDANCE);
    this.xScB = xScB.to(StandardUnits.IMPEDANCE);
    this.xScC = xScC.to(StandardUnits.IMPEDANCE);
    this.gM = gM.to(StandardUnits.ADMITTANCE);
    this.bM = bM.to(StandardUnits.ADMITTANCE);
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Transformer3WTypeInput that = (Transformer3WTypeInput) o;

    if (!QuantityUtil.quantityIsEmpty(sRatedA)) {
      if (QuantityUtil.quantityIsEmpty(that.sRatedA)) return false;
      if (!sRatedA.isEquivalentTo(that.sRatedA)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.sRatedA)) return false;

    if (!QuantityUtil.quantityIsEmpty(sRatedB)) {
      if (QuantityUtil.quantityIsEmpty(that.sRatedB)) return false;
      if (!sRatedB.isEquivalentTo(that.sRatedB)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.sRatedB)) return false;

    if (!QuantityUtil.quantityIsEmpty(sRatedC)) {
      if (QuantityUtil.quantityIsEmpty(that.sRatedC)) return false;
      if (!sRatedC.isEquivalentTo(that.sRatedC)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.sRatedC)) return false;

    if (!QuantityUtil.quantityIsEmpty(vRatedA)) {
      if (QuantityUtil.quantityIsEmpty(that.vRatedA)) return false;
      if (!vRatedA.isEquivalentTo(that.vRatedA)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.vRatedA)) return false;

    if (!QuantityUtil.quantityIsEmpty(vRatedB)) {
      if (QuantityUtil.quantityIsEmpty(that.vRatedB)) return false;
      if (!vRatedB.isEquivalentTo(that.vRatedB)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.vRatedB)) return false;

    if (!QuantityUtil.quantityIsEmpty(vRatedC)) {
      if (QuantityUtil.quantityIsEmpty(that.vRatedC)) return false;
      if (!vRatedC.isEquivalentTo(that.vRatedC)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.vRatedC)) return false;

    if (!QuantityUtil.quantityIsEmpty(rScA)) {
      if (QuantityUtil.quantityIsEmpty(that.rScA)) return false;
      if (!rScA.isEquivalentTo(that.rScA)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.rScA)) return false;

    if (!QuantityUtil.quantityIsEmpty(rScB)) {
      if (QuantityUtil.quantityIsEmpty(that.rScB)) return false;
      if (!rScB.isEquivalentTo(that.rScB)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.rScB)) return false;

    if (!QuantityUtil.quantityIsEmpty(rScC)) {
      if (QuantityUtil.quantityIsEmpty(that.rScC)) return false;
      if (!rScC.isEquivalentTo(that.rScC)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.rScC)) return false;

    if (!QuantityUtil.quantityIsEmpty(xScA)) {
      if (QuantityUtil.quantityIsEmpty(that.xScA)) return false;
      if (!xScA.isEquivalentTo(that.xScA)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.xScA)) return false;

    if (!QuantityUtil.quantityIsEmpty(xScB)) {
      if (QuantityUtil.quantityIsEmpty(that.xScB)) return false;
      if (!xScB.isEquivalentTo(that.xScB)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.xScB)) return false;

    if (!QuantityUtil.quantityIsEmpty(xScC)) {
      if (QuantityUtil.quantityIsEmpty(that.xScC)) return false;
      if (!xScC.isEquivalentTo(that.xScC)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.xScC)) return false;

    if (!QuantityUtil.quantityIsEmpty(gM)) {
      if (QuantityUtil.quantityIsEmpty(that.gM)) return false;
      if (!gM.isEquivalentTo(that.gM)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.gM)) return false;

    if (!QuantityUtil.quantityIsEmpty(bM)) {
      if (QuantityUtil.quantityIsEmpty(that.bM)) return false;
      if (!bM.isEquivalentTo(that.bM)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.bM)) return false;

    if (!QuantityUtil.quantityIsEmpty(dV)) {
      if (QuantityUtil.quantityIsEmpty(that.dV)) return false;
      if (!dV.isEquivalentTo(that.dV)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.dV)) return false;

    if (!QuantityUtil.quantityIsEmpty(dPhi)) {
      if (QuantityUtil.quantityIsEmpty(that.dPhi)) return false;
      if (!dPhi.isEquivalentTo(that.dPhi)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.dPhi)) return false;

    return tapNeutr == that.tapNeutr && tapMin == that.tapMin && tapMax == that.tapMax;
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
        + "sRatedA="
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
}
