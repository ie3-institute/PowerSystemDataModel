/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.util.quantities.interfaces.SpecificConductance;
import edu.ie3.util.quantities.interfaces.SpecificResistance;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.connector.LineInput} */
public class LineTypeInput extends AssetTypeInput {
  /** Specific phase-to-ground susceptance for this type of line (typically in µS/km) */
  private final ComparableQuantity<SpecificConductance> b;
  /** Specific phase-to-ground conductance for this type of line (typically in µS/km) */
  private final ComparableQuantity<SpecificConductance> g;
  /** Specific resistance for this type of line (typically in Ohm/km) */
  private final ComparableQuantity<SpecificResistance> r;
  /** Specific reactance for this type of line (typically in Ohm/km) */
  private final ComparableQuantity<SpecificResistance> x;
  /** Maximum thermal current for this type of line (typically in A) */
  private final ComparableQuantity<ElectricCurrent> iMax;
  /** Rated voltage for this type of line (typically in V) */
  private final ComparableQuantity<ElectricPotential> vRated;

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
      ComparableQuantity<SpecificConductance> b,
      ComparableQuantity<SpecificConductance> g,
      ComparableQuantity<SpecificResistance> r,
      ComparableQuantity<SpecificResistance> x,
      ComparableQuantity<ElectricCurrent> iMax,
      ComparableQuantity<ElectricPotential> vRated) {
    super(uuid, id);
    this.r = r.to(StandardUnits.IMPEDANCE_PER_LENGTH);
    this.x = x.to(StandardUnits.IMPEDANCE_PER_LENGTH);
    this.b = b.to(StandardUnits.ADMITTANCE_PER_LENGTH);
    this.g = g.to(StandardUnits.ADMITTANCE_PER_LENGTH);
    this.iMax = iMax.to(StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);
    this.vRated = vRated.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
  }

  public ComparableQuantity<SpecificConductance> getB() {
    return b;
  }

  public ComparableQuantity<SpecificConductance> getG() {
    return g;
  }

  public ComparableQuantity<SpecificResistance> getR() {
    return r;
  }

  public ComparableQuantity<SpecificResistance> getX() {
    return x;
  }

  public ComparableQuantity<ElectricCurrent> getiMax() {
    return iMax;
  }

  public ComparableQuantity<ElectricPotential> getvRated() {
    return vRated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LineTypeInput that)) return false;
    if (!super.equals(o)) return false;
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

  @Override
  public String toString() {
    return "LineTypeInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", b="
        + b
        + ", g="
        + g
        + ", r="
        + r
        + ", x="
        + x
        + ", iMax="
        + iMax
        + ", vRated="
        + vRated
        + '}';
  }
}
