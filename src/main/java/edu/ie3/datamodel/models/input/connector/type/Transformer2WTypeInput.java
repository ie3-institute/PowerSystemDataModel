/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import static edu.ie3.datamodel.io.naming.EntityFieldNames.*;

import edu.ie3.datamodel.io.source.SourceValidator;
import edu.ie3.datamodel.models.StandardUnits;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.*;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.connector.Transformer2WInput} */
public class Transformer2WTypeInput extends TransformerTypeInput {

  /** Short circuit resistance (typically in Ohm) */
  private final ComparableQuantity<ElectricResistance> rSc;

  /** Short circuit reactance (typically in Ohm) */
  private final ComparableQuantity<ElectricResistance> xSc;

  /** Rated apparent power (typically in kVA) */
  private final ComparableQuantity<Power> sRated;

  /** Selection of winding, where the tap changer is installed. Low voltage, if true */
  private final boolean tapSide;

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
    super(uuid, id, gM, bM, dV, dPhi, vRatedA, vRatedB, tapNeutr, tapMin, tapMax);
    this.rSc = rSc.to(StandardUnits.RESISTANCE);
    this.xSc = xSc.to(StandardUnits.REACTANCE);
    this.sRated = sRated.to(StandardUnits.S_RATED);
    this.tapSide = tapSide;
  }

  public Transformer2WTypeInput(
      TransformerTypeInput transformerTypeInput,
      ComparableQuantity<ElectricResistance> rSc,
      ComparableQuantity<ElectricResistance> xSc,
      ComparableQuantity<Power> sRated,
      boolean tapSide) {
    super(transformerTypeInput);
    this.rSc = rSc.to(StandardUnits.RESISTANCE);
    this.xSc = xSc.to(StandardUnits.REACTANCE);
    this.sRated = sRated.to(StandardUnits.S_RATED);
    this.tapSide = tapSide;
  }

  public static SourceValidator.Fields getFields() {
    return transformerTypeFields().add(R_SC, X_SC, S_RATED, TAP_SIDE);
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

  public boolean isTapSide() {
    return tapSide;
  }

  @Override
  public Transformer2WTypeInputCopyBuilder copy() {
    return new Transformer2WTypeInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Transformer2WTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return tapSide == that.tapSide
        && rSc.equals(that.rSc)
        && xSc.equals(that.xSc)
        && sRated.equals(that.sRated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), rSc, xSc, sRated, tapSide);
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
        + getvRatedA()
        + ", vRatedB="
        + getvRatedB()
        + ", gM="
        + getbM()
        + ", bM="
        + getbM()
        + ", dV="
        + getdV()
        + ", dPhi="
        + getdPhi()
        + ", tapSide="
        + tapSide
        + ", tapNeutr="
        + getTapNeutr()
        + ", tapMin="
        + getTapMin()
        + ", tapMax="
        + getTapMax()
        + '}';
  }

  /**
   * Abstract class for all builder that build child entities of abstract class {@link
   * Transformer2WTypeInput}
   */
  public static final class Transformer2WTypeInputCopyBuilder
      extends TransformerTypeInputCopyBuilder<Transformer2WTypeInputCopyBuilder> {

    private ComparableQuantity<ElectricResistance> rSc;
    private ComparableQuantity<ElectricResistance> xSc;
    private ComparableQuantity<Power> sRated;
    private boolean tapSide;

    private Transformer2WTypeInputCopyBuilder(Transformer2WTypeInput entity) {
      super(entity);
      this.rSc = entity.rSc;
      this.xSc = entity.xSc;
      this.sRated = entity.sRated;
    }

    /** Setter */
    public Transformer2WTypeInputCopyBuilder rSc(ComparableQuantity<ElectricResistance> rSc) {
      this.rSc = rSc;
      return thisInstance();
    }

    public Transformer2WTypeInputCopyBuilder xSc(ComparableQuantity<ElectricResistance> xSc) {
      this.xSc = xSc;
      return thisInstance();
    }

    public Transformer2WTypeInputCopyBuilder sRated(ComparableQuantity<Power> sRated) {
      this.sRated = sRated;
      return thisInstance();
    }

    public Transformer2WTypeInputCopyBuilder tapSide(boolean tapSide) {
      this.tapSide = tapSide;
      return thisInstance();
    }

    @Override
    public Transformer2WTypeInput build() {
      return new Transformer2WTypeInput(
          getUuid(),
          getId(),
          rSc,
          xSc,
          sRated,
          getVRatedA(),
          getVRatedB(),
          getGM(),
          getBM(),
          getDV(),
          getDPhi(),
          tapSide,
          getTapNeutr(),
          getTapMin(),
          getTapMax());
    }

    @Override
    protected Transformer2WTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
