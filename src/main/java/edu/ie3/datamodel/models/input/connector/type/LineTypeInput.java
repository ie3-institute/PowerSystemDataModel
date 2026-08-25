/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.util.quantities.interfaces.SpecificConductance;
import edu.ie3.util.quantities.interfaces.SpecificResistance;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of {@link edu.ie3.datamodel.models.input.connector.LineInput}. */
public class LineTypeInput extends AssetTypeInput {
  /** Specific phase-to-ground susceptance for this type of line (typically in µS/km). */
  private final ComparableQuantity<SpecificConductance> b;

  /** Specific phase-to-ground conductance for this type of line (typically in µS/km). */
  private final ComparableQuantity<SpecificConductance> g;

  /** Specific resistance for this type of line (typically in Ohm/km). */
  private final ComparableQuantity<SpecificResistance> r;

  /** Specific reactance for this type of line (typically in Ohm/km). */
  private final ComparableQuantity<SpecificResistance> x;

  /** Maximum thermal current for this type of line (typically in A). */
  private final ComparableQuantity<ElectricCurrent> iMax;

  /** Rated voltage for this type of line (typically in V). */
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
    this.b = b;
    this.g = g;
    this.r = r;
    this.x = x;
    this.iMax = iMax;
    this.vRated = vRated;
  }

  /**
   * @param uuid of the input entity
   * @param id of this type
   * @param b Specific phase-to-ground susceptance for this type of line (typically in µS/km)
   * @param g Specific phase-to-ground conductance for this type of line (typically in µS/km)
   * @param r Specific resistance for this type of line (typically in Ohm/km)
   * @param x Specific reactance for this type of line (typically in Ohm/km)
   * @param iMax Maximum thermal current for this type of line (typically in A)
   * @param vRated Rated voltage for this type of line @param additionalInformation That were
   *     provided by the source
   */
  public LineTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<SpecificConductance> b,
      ComparableQuantity<SpecificConductance> g,
      ComparableQuantity<SpecificResistance> r,
      ComparableQuantity<SpecificResistance> x,
      ComparableQuantity<ElectricCurrent> iMax,
      ComparableQuantity<ElectricPotential> vRated,
      Map<String, String> additionalInformation) {
    super(uuid, id);
    this.b = b;
    this.g = g;
    this.r = r;
    this.x = x;
    this.iMax = iMax;
    this.vRated = vRated;
    setAdditionalInformation(additionalInformation);
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
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public LineTypeInputCopyBuilder copy() {
    return new LineTypeInputCopyBuilder(this);
  }

  public static class LineTypeInputCopyBuilder
      extends AssetTypeInputCopyBuilder<LineTypeInputCopyBuilder> {
    private ComparableQuantity<SpecificConductance> b;

    private ComparableQuantity<SpecificConductance> g;

    private ComparableQuantity<SpecificResistance> r;

    private ComparableQuantity<SpecificResistance> x;

    private ComparableQuantity<ElectricCurrent> iMax;

    private ComparableQuantity<ElectricPotential> vRated;

    protected LineTypeInputCopyBuilder(LineTypeInput entity) {
      super(entity);
      this.b = entity.b;
      this.g = entity.g;
      this.r = entity.r;
      this.x = entity.x;
      this.iMax = entity.iMax;
      this.vRated = entity.vRated;
    }

    public LineTypeInputCopyBuilder b(ComparableQuantity<SpecificConductance> b) {
      this.b = b;
      return thisInstance();
    }

    protected ComparableQuantity<SpecificConductance> getB() {
      return b;
    }

    public LineTypeInputCopyBuilder g(ComparableQuantity<SpecificConductance> g) {
      this.g = g;
      return thisInstance();
    }

    protected ComparableQuantity<SpecificConductance> getG() {
      return g;
    }

    public LineTypeInputCopyBuilder r(ComparableQuantity<SpecificResistance> r) {
      this.r = r;
      return thisInstance();
    }

    protected ComparableQuantity<SpecificResistance> getR() {
      return r;
    }

    public LineTypeInputCopyBuilder x(ComparableQuantity<SpecificResistance> x) {
      this.x = x;
      return thisInstance();
    }

    protected ComparableQuantity<SpecificResistance> getX() {
      return x;
    }

    public LineTypeInputCopyBuilder iMax(ComparableQuantity<ElectricCurrent> iMax) {
      this.iMax = iMax;
      return thisInstance();
    }

    protected ComparableQuantity<ElectricCurrent> getiMax() {
      return iMax;
    }

    public LineTypeInputCopyBuilder vRated(ComparableQuantity<ElectricPotential> vRated) {
      this.vRated = vRated;
      return thisInstance();
    }

    protected ComparableQuantity<ElectricPotential> getvRated() {
      return vRated;
    }

    @Override
    public LineTypeInput build() {
      return new LineTypeInput(
          getUuid(), getId(), b, g, r, x, iMax, vRated, getAdditionalInformation());
    }

    @Override
    protected LineTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
