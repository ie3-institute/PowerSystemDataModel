/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.AssetTypeInput;
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
   * Instantiates a new Line type input.
   *
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
    this.r = r.to(StandardUnits.RESISTANCE_PER_LENGTH);
    this.x = x.to(StandardUnits.REACTANCE_PER_LENGTH);
    this.b = b.to(StandardUnits.SUSCEPTANCE_PER_LENGTH);
    this.g = g.to(StandardUnits.CONDUCTANCE_PER_LENGTH);
    this.iMax = iMax.to(StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);
    this.vRated = vRated.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
  }

  /**
   * Gets b.
   *
   * @return the b
   */
  public ComparableQuantity<SpecificConductance> getB() {
    return b;
  }

  /**
   * Gets g.
   *
   * @return the g
   */
  public ComparableQuantity<SpecificConductance> getG() {
    return g;
  }

  /**
   * Gets r.
   *
   * @return the r
   */
  public ComparableQuantity<SpecificResistance> getR() {
    return r;
  }

  /**
   * Gets x.
   *
   * @return the x
   */
  public ComparableQuantity<SpecificResistance> getX() {
    return x;
  }

  /**
   * Gets max.
   *
   * @return the max
   */
  public ComparableQuantity<ElectricCurrent> getiMax() {
    return iMax;
  }

  /**
   * Gets rated.
   *
   * @return the rated
   */
  public ComparableQuantity<ElectricPotential> getvRated() {
    return vRated;
  }

  @Override
  public LineTypeInputCopyBuilder copy() {
    return new LineTypeInputCopyBuilder(this);
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

  /**
   * Abstract class for all builder that build child entities of abstract class {@link
   * LineTypeInput}*
   */
  public static final class LineTypeInputCopyBuilder
      extends AssetTypeInput.AssetTypeInputCopyBuilder<LineTypeInputCopyBuilder> {

    private ComparableQuantity<SpecificConductance> b;
    private ComparableQuantity<SpecificConductance> g;
    private ComparableQuantity<SpecificResistance> r;
    private ComparableQuantity<SpecificResistance> x;
    private ComparableQuantity<ElectricCurrent> iMax;
    private ComparableQuantity<ElectricPotential> vRated;

    /**
     * Instantiates a new Line type input copy builder.
     *
     * @param entity the entity
     */
    protected LineTypeInputCopyBuilder(LineTypeInput entity) {
      super(entity);
      this.b = entity.b;
      this.g = entity.g;
      this.r = entity.r;
      this.x = entity.x;
      this.iMax = entity.iMax;
      this.vRated = entity.vRated;
    }

    /**
     * Setter
     *
     * @param b the b
     * @return the line type input copy builder
     */
    public LineTypeInputCopyBuilder b(ComparableQuantity<SpecificConductance> b) {
      this.b = b;
      return thisInstance();
    }

    /**
     * G line type input copy builder.
     *
     * @param g the g
     * @return the line type input copy builder
     */
    public LineTypeInputCopyBuilder g(ComparableQuantity<SpecificConductance> g) {
      this.g = g;
      return thisInstance();
    }

    /**
     * R line type input copy builder.
     *
     * @param r the r
     * @return the line type input copy builder
     */
    public LineTypeInputCopyBuilder r(ComparableQuantity<SpecificResistance> r) {
      this.r = r;
      return thisInstance();
    }

    /**
     * X line type input copy builder.
     *
     * @param x the x
     * @return the line type input copy builder
     */
    public LineTypeInputCopyBuilder x(ComparableQuantity<SpecificResistance> x) {
      this.x = x;
      return thisInstance();
    }

    /**
     * Max line type input copy builder.
     *
     * @param iMax the max
     * @return the line type input copy builder
     */
    public LineTypeInputCopyBuilder iMax(ComparableQuantity<ElectricCurrent> iMax) {
      this.iMax = iMax;
      return thisInstance();
    }

    /**
     * V rated line type input copy builder.
     *
     * @param vRated the v rated
     * @return the line type input copy builder
     */
    public LineTypeInputCopyBuilder vRated(ComparableQuantity<ElectricPotential> vRated) {
      this.vRated = vRated;
      return thisInstance();
    }

    @Override
    public LineTypeInput build() {
      return new LineTypeInput(getUuid(), getId(), b, g, r, x, iMax, vRated);
    }

    @Override
    protected LineTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
