/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.connector.type;

import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.AssetTypeInput;
import edu.ie3.util.quantities.interfaces.SpecificConductance;
import edu.ie3.util.quantities.interfaces.SpecificResistance;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;

/** Describes the type of a {@link edu.ie3.models.input.connector.LineInput} */
public class LineTypeInput extends AssetTypeInput {
  /** Specific phase-to-ground susceptance for this type of line (typically in µS/km) */
  private Quantity<SpecificConductance> b;
  /** Specific phase-to-ground conductance for this type of line (typically in µS/km) */
  private Quantity<SpecificConductance> g;
  /** Specific resistance for this type of line (typically in Ohm/km) */
  private Quantity<SpecificResistance> r;
  /** Specific reactance for this type of line (typically in Ohm/km) */
  private Quantity<SpecificResistance> x;
  /** Maximum thermal current for this type of line (typically in A) */
  private Quantity<ElectricCurrent> iMax;
  /** Rated voltage for this type of line (typically in V) */
  private Quantity<ElectricPotential> vRated;

  /**
   * @param uuid of the input entity
   * @param id of this type
   * @param b Specific phase-to-ground susceptance for this type of line (typically in µS/km)
   * @param g Specific phase-to-ground conductance for this type of line (typically in µS/km)
   * @param r Specific resistance for this type of line (typically in Ohm/km)
   * @param x Specific reactance for this type of line (typically in Ohm/km)
   * @param iMax Maximum thermal current for this type of line (typically in A)
   * @param vRated Rated voltage for this type of line
   */
  public LineTypeInput(
      UUID uuid,
      String id,
      Quantity<SpecificConductance> b,
      Quantity<SpecificConductance> g,
      Quantity<SpecificResistance> r,
      Quantity<SpecificResistance> x,
      Quantity<ElectricCurrent> iMax,
      Quantity<ElectricPotential> vRated) {
    super(uuid, id);
    this.r = r.to(StandardUnits.SPECIFIC_IMPEDANCE);
    this.x = x.to(StandardUnits.SPECIFIC_IMPEDANCE);
    this.b = b.to(StandardUnits.SPECIFIC_ADMITTANCE);
    this.g = g.to(StandardUnits.SPECIFIC_ADMITTANCE);
    this.iMax = iMax.to(StandardUnits.CURRENT);
    this.vRated = vRated.to(StandardUnits.V_RATED);
  }

  public Quantity<SpecificConductance> getB() {
    return b;
  }

  public void setB(Quantity<SpecificConductance> b) {
    this.b = b.to(StandardUnits.SPECIFIC_ADMITTANCE);
  }

  public Quantity<SpecificConductance> getG() {
    return g;
  }

  public void setG(Quantity<SpecificConductance> g) {
    this.g = g.to(StandardUnits.SPECIFIC_ADMITTANCE);
  }

  public Quantity<SpecificResistance> getR() {
    return r;
  }

  public void setR(Quantity<SpecificResistance> r) {
    this.r = r.to(StandardUnits.SPECIFIC_IMPEDANCE);
  }

  public Quantity<SpecificResistance> getX() {
    return x;
  }

  public void setX(Quantity<SpecificResistance> x) {
    this.x = x.to(StandardUnits.SPECIFIC_IMPEDANCE);
  }

  public Quantity<ElectricCurrent> getIMax() {
    return iMax;
  }

  public void setIMax(Quantity<ElectricCurrent> iMax) {
    this.iMax = iMax.to(StandardUnits.CURRENT);
  }

  public Quantity<ElectricPotential> getVRated() {
    return vRated;
  }

  public void setVRated(Quantity<ElectricPotential> vRated) {
    this.vRated = vRated.to(StandardUnits.V_RATED);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LineTypeInput that = (LineTypeInput) o;
    return b.equals(that.b)
        && g.equals(that.g)
        && r.equals(that.r)
        && x.equals(that.x)
        && iMax.equals(that.iMax)
        && vRated.equals(that.vRated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), b, g, r, x, iMax, vRated);
  }
}
