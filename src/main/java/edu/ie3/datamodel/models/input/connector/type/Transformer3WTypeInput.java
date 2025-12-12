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

/** Describes the type of a {@link edu.ie3.datamodel.models.input.connector.Transformer3WInput} */
public class Transformer3WTypeInput extends TransformerTypeInput {

  /** Rated apparent power of the high voltage winding (typically in kVA) */
  private final ComparableQuantity<Power> sRatedA; // Hv

  /** Rated apparent power of the medium voltage winding (typically in kVA) */
  private final ComparableQuantity<Power> sRatedB; // Mv

  /** Rated apparent power of the low voltage windings (typically in kVA) */
  private final ComparableQuantity<Power> sRatedC; // Lv

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
    super(uuid, id, gM, bM, dV, dPhi, vRatedA, vRatedB, tapNeutr, tapMin, tapMax);
    this.sRatedA = sRatedA.to(StandardUnits.S_RATED);
    this.sRatedB = sRatedB.to(StandardUnits.S_RATED);
    this.sRatedC = sRatedC.to(StandardUnits.S_RATED);
    this.vRatedC = vRatedC.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    this.rScA = rScA.to(StandardUnits.RESISTANCE);
    this.rScB = rScB.to(StandardUnits.RESISTANCE);
    this.rScC = rScC.to(StandardUnits.RESISTANCE);
    this.xScA = xScA.to(StandardUnits.REACTANCE);
    this.xScB = xScB.to(StandardUnits.REACTANCE);
    this.xScC = xScC.to(StandardUnits.REACTANCE);
  }

  public Transformer3WTypeInput(
      TransformerTypeInput transformerTypeInput,
      ComparableQuantity<Power> sRatedA,
      ComparableQuantity<Power> sRatedB,
      ComparableQuantity<Power> sRatedC,
      ComparableQuantity<ElectricPotential> vRatedC,
      ComparableQuantity<ElectricResistance> rScA,
      ComparableQuantity<ElectricResistance> rScB,
      ComparableQuantity<ElectricResistance> rScC,
      ComparableQuantity<ElectricResistance> xScA,
      ComparableQuantity<ElectricResistance> xScB,
      ComparableQuantity<ElectricResistance> xScC) {
    super(transformerTypeInput);
    this.sRatedA = sRatedA.to(StandardUnits.S_RATED);
    this.sRatedB = sRatedB.to(StandardUnits.S_RATED);
    this.sRatedC = sRatedC.to(StandardUnits.S_RATED);
    this.vRatedC = vRatedC.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    this.rScA = rScA.to(StandardUnits.RESISTANCE);
    this.rScB = rScB.to(StandardUnits.RESISTANCE);
    this.rScC = rScC.to(StandardUnits.RESISTANCE);
    this.xScA = xScA.to(StandardUnits.REACTANCE);
    this.xScB = xScB.to(StandardUnits.REACTANCE);
    this.xScC = xScC.to(StandardUnits.REACTANCE);
  }

  public static SourceValidator.Fields getFields() {
    return transformerTypeFields()
        .add(
            S_RATED_A, S_RATED_B, S_RATED_C, V_RATED_C, R_SC_A, R_SC_B, R_SC_C, X_SC_A, X_SC_B,
            X_SC_C);
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

  @Override
  public Transformer3WTypeInputCopyBuilder copy() {
    return new Transformer3WTypeInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Transformer3WTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return sRatedA.equals(that.sRatedA)
        && sRatedB.equals(that.sRatedB)
        && sRatedC.equals(that.sRatedC)
        && vRatedC.equals(that.vRatedC)
        && rScA.equals(that.rScA)
        && rScB.equals(that.rScB)
        && rScC.equals(that.rScC)
        && xScA.equals(that.xScA)
        && xScB.equals(that.xScB)
        && xScC.equals(that.xScC);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), sRatedA, sRatedB, sRatedC, vRatedC, rScA, rScB, rScC, xScA, xScB, xScC);
  }

  @Override
  public String toString() {
    return "Transformer3WTypeInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", sRatedA="
        + sRatedA
        + ", sRatedB="
        + sRatedB
        + ", sRatedC="
        + sRatedC
        + ", vRatedA="
        + getvRatedA()
        + ", vRatedB="
        + getvRatedB()
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
        + getbM()
        + ", bM="
        + getbM()
        + ", dV="
        + getdV()
        + ", dPhi="
        + getdPhi()
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
   * Transformer3WTypeInput}
   */
  public static final class Transformer3WTypeInputCopyBuilder
      extends TransformerTypeInputCopyBuilder<Transformer3WTypeInputCopyBuilder> {

    private ComparableQuantity<Power> sRatedA;
    private ComparableQuantity<Power> sRatedB;
    private ComparableQuantity<Power> sRatedC;
    private ComparableQuantity<ElectricPotential> vRatedC;
    private ComparableQuantity<ElectricResistance> rScA;
    private ComparableQuantity<ElectricResistance> rScB;
    private ComparableQuantity<ElectricResistance> rScC;
    private ComparableQuantity<ElectricResistance> xScA;
    private ComparableQuantity<ElectricResistance> xScB;
    private ComparableQuantity<ElectricResistance> xScC;

    private Transformer3WTypeInputCopyBuilder(Transformer3WTypeInput entity) {
      super(entity);
      this.sRatedA = entity.sRatedA;
      this.sRatedB = entity.sRatedB;
      this.sRatedC = entity.sRatedC;
      this.vRatedC = entity.vRatedC;
      this.rScA = entity.rScA;
      this.rScB = entity.rScB;
      this.rScC = entity.rScC;
      this.xScA = entity.xScA;
      this.xScB = entity.xScB;
      this.xScC = entity.xScC;
    }

    /** Setter */
    public Transformer3WTypeInputCopyBuilder sRatedA(ComparableQuantity<Power> sRatedA) {
      this.sRatedA = sRatedA;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder sRatedB(ComparableQuantity<Power> sRatedB) {
      this.sRatedB = sRatedB;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder sRatedC(ComparableQuantity<Power> sRatedC) {
      this.sRatedC = sRatedC;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder vRatedC(
        ComparableQuantity<ElectricPotential> vRatedC) {
      this.vRatedC = vRatedC;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder rScA(ComparableQuantity<ElectricResistance> rScA) {
      this.rScA = rScA;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder rScB(ComparableQuantity<ElectricResistance> rScB) {
      this.rScB = rScB;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder rScC(ComparableQuantity<ElectricResistance> rScC) {
      this.rScC = rScC;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder xScA(ComparableQuantity<ElectricResistance> xScA) {
      this.xScA = xScA;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder xScB(ComparableQuantity<ElectricResistance> xScB) {
      this.xScB = xScB;
      return thisInstance();
    }

    public Transformer3WTypeInputCopyBuilder xScC(ComparableQuantity<ElectricResistance> xScC) {
      this.xScC = xScC;
      return thisInstance();
    }

    @Override
    public Transformer3WTypeInput build() {
      return new Transformer3WTypeInput(
          getUuid(),
          getId(),
          sRatedA,
          sRatedB,
          sRatedC,
          getVRatedA(),
          getVRatedB(),
          vRatedC,
          rScA,
          rScB,
          rScC,
          xScA,
          xScB,
          xScC,
          getGM(),
          getBM(),
          getDV(),
          getDPhi(),
          getTapNeutr(),
          getTapMin(),
          getTapMax());
    }

    @Override
    protected Transformer3WTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
