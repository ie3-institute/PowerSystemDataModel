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
import javax.measure.Quantity;
import javax.measure.quantity.*;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.connector.Transformer3WInput} */
public class Transformer3WTypeInput extends AssetTypeInput {
  /** Rated apparent power of the high voltage winding (typically in MVA) */
  private final Quantity<Power> sRatedA; // Hv
  /** Rated apparent power of the medium voltage winding (typically in MVA) */
  private final Quantity<Power> sRatedB; // Mv
  /** Rated apparent power of the low voltage windings (typically in MVA) */
  private final Quantity<Power> sRatedC; // Lv
  /** Rated voltage magnitude of the high voltage winding (typically in kV) */
  private final Quantity<ElectricPotential> vRatedA; // Hv
  /** Rated voltage magnitude of the medium voltage winding (typically in kV) */
  private final Quantity<ElectricPotential> vRatedB; // Mv
  /** Rated voltage magnitude of the low voltage winding (typically in kV) */
  private final Quantity<ElectricPotential> vRatedC; // Lv
  /** Short-circuit resistance of the high voltage winding (typically in Ohm) */
  private final Quantity<ElectricResistance> rScA; // Hv
  /** Short-circuit resistance of the medium voltage winding (typically in Ohm) */
  private final Quantity<ElectricResistance> rScB; // Mv
  /** Short-circuit resistance of the low voltage winding (typically in Ohm) */
  private final Quantity<ElectricResistance> rScC; // Lv
  /** Short-circuit reactance of the high voltage winding (typically in Ohm) */
  private final Quantity<ElectricResistance> xScA; // Hv
  /** Short-circuit reactance of the medium voltage winding (typically in Ohm) */
  private final Quantity<ElectricResistance> xScB; // Mv
  /** Short-circuit reactance of the low voltage winding (typically in Ohm) */
  private final Quantity<ElectricResistance> xScC; // Lv
  /** Phase-to-ground conductance (typically in nS) */
  private final Quantity<ElectricConductance> gM;
  /** Phase-to-ground susceptance (typically in nS) */
  private final Quantity<ElectricConductance> bM;
  /** Voltage magnitude deviation per tap position (typically in %) */
  private final Quantity<Dimensionless> dV;
  /** Voltage angle deviation per tap position (typically in °) */
  private final Quantity<Angle> dPhi;
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
      Quantity<Power> sRatedA,
      Quantity<Power> sRatedB,
      Quantity<Power> sRatedC,
      Quantity<ElectricPotential> vRatedA,
      Quantity<ElectricPotential> vRatedB,
      Quantity<ElectricPotential> vRatedC,
      Quantity<ElectricResistance> rScA,
      Quantity<ElectricResistance> rScB,
      Quantity<ElectricResistance> rScC,
      Quantity<ElectricResistance> xScA,
      Quantity<ElectricResistance> xScB,
      Quantity<ElectricResistance> xScC,
      Quantity<ElectricConductance> gM,
      Quantity<ElectricConductance> bM,
      Quantity<Dimensionless> dV,
      Quantity<Angle> dPhi,
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

  public Quantity<Power> getSRatedA() {
    return sRatedA;
  }

  public Quantity<Power> getSRatedB() {
    return sRatedB;
  }

  public Quantity<Power> getSRatedC() {
    return sRatedC;
  }

  public Quantity<ElectricPotential> getVRatedA() {
    return vRatedA;
  }

  public Quantity<ElectricPotential> getVRatedB() {
    return vRatedB;
  }

  public Quantity<ElectricPotential> getVRatedC() {
    return vRatedC;
  }

  public Quantity<ElectricResistance> getRScA() {
    return rScA;
  }

  public Quantity<ElectricResistance> getRScB() {
    return rScB;
  }

  public Quantity<ElectricResistance> getRScC() {
    return rScC;
  }

  public Quantity<ElectricResistance> getXScA() {
    return xScA;
  }

  public Quantity<ElectricResistance> getXScB() {
    return xScB;
  }

  public Quantity<ElectricResistance> getXScC() {
    return xScC;
  }

  public Quantity<ElectricConductance> getGM() {
    return gM;
  }

  public Quantity<ElectricConductance> getBM() {
    return bM;
  }

  public Quantity<Dimensionless> getDV() {
    return dV;
  }

  public Quantity<Angle> getDPhi() {
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
}
