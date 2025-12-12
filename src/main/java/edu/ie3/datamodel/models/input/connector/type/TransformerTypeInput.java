/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import static edu.ie3.datamodel.io.naming.EntityFieldNames.*;

import edu.ie3.datamodel.io.source.SourceValidator;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.*;
import tech.units.indriya.ComparableQuantity;

public abstract class TransformerTypeInput extends AssetTypeInput {

  /** Rated voltage of the high voltage winding (typically in kV) */
  private final ComparableQuantity<ElectricPotential> vRatedA;

  /** Rated voltage of the low voltage winding (typically in kV) */
  private final ComparableQuantity<ElectricPotential> vRatedB;

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

  protected TransformerTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<ElectricConductance> gM,
      ComparableQuantity<ElectricConductance> bM,
      ComparableQuantity<Dimensionless> dV,
      ComparableQuantity<Angle> dPhi,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB,
      int tapNeutr,
      int tapMin,
      int tapMax) {
    super(uuid, id);
    this.gM = gM;
    this.bM = bM;
    this.dV = dV;
    this.dPhi = dPhi;
    this.vRatedA = vRatedA.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    this.vRatedB = vRatedB.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    this.tapNeutr = tapNeutr;
    this.tapMin = tapMin;
    this.tapMax = tapMax;
  }

  protected TransformerTypeInput(
      AssetTypeInput assetTypeInput,
      ComparableQuantity<ElectricConductance> gM,
      ComparableQuantity<ElectricConductance> bM,
      ComparableQuantity<Dimensionless> dV,
      ComparableQuantity<Angle> dPhi,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB,
      int tapNeutr,
      int tapMin,
      int tapMax) {
    super(assetTypeInput);
    this.gM = gM;
    this.bM = bM;
    this.dV = dV;
    this.dPhi = dPhi;
    this.vRatedA = vRatedA.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    this.vRatedB = vRatedB.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    this.tapNeutr = tapNeutr;
    this.tapMin = tapMin;
    this.tapMax = tapMax;
  }

  protected TransformerTypeInput(TransformerTypeInput other) {
    super(other);
    this.gM = other.gM;
    this.bM = other.bM;
    this.dV = other.dV;
    this.dPhi = other.dPhi;
    this.vRatedA = other.vRatedA;
    this.vRatedB = other.vRatedB;
    this.tapNeutr = other.tapNeutr;
    this.tapMin = other.tapMin;
    this.tapMax = other.tapMax;
  }

  protected static SourceValidator.Fields transformerTypeFields() {
    return assetTypeFields()
        .add(G_M, B_M, D_V, D_PHI, V_RATED_A, V_RATED_B, TAP_NEUTR, TAP_MIN, TAP_MAX);
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

  public ComparableQuantity<ElectricPotential> getvRatedA() {
    return vRatedA;
  }

  public ComparableQuantity<ElectricPotential> getvRatedB() {
    return vRatedB;
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
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    TransformerTypeInput that = (TransformerTypeInput) o;
    return tapNeutr == that.tapNeutr
        && tapMin == that.tapMin
        && tapMax == that.tapMax
        && Objects.equals(gM, that.gM)
        && Objects.equals(bM, that.bM)
        && Objects.equals(dV, that.dV)
        && Objects.equals(dPhi, that.dPhi)
        && Objects.equals(vRatedA, that.vRatedA)
        && Objects.equals(vRatedB, that.vRatedB);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), gM, bM, dV, dPhi, vRatedA, vRatedB, tapNeutr, tapMin, tapMax);
  }

  @Override
  public String toString() {
    return "TransformerTypeInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + "gM="
        + gM
        + ", bM="
        + bM
        + ", dV="
        + dV
        + ", dPhi="
        + dPhi
        + ", vRatedA="
        + vRatedA
        + ", vRatedB="
        + vRatedB
        + ", tapNeutr="
        + tapNeutr
        + ", tapMin="
        + tapMin
        + ", tapMax="
        + tapMax
        + "}";
  }

  /**
   * Abstract class for all builder that build child entities of abstract class {@link
   * TransformerTypeInput}
   */
  protected abstract static class TransformerTypeInputCopyBuilder<
          B extends TransformerTypeInputCopyBuilder<B>>
      extends AssetTypeInputCopyBuilder<B> {
    private ComparableQuantity<ElectricConductance> gM;
    private ComparableQuantity<ElectricConductance> bM;
    private ComparableQuantity<Dimensionless> dV;
    private ComparableQuantity<Angle> dPhi;
    private ComparableQuantity<ElectricPotential> vRatedA;
    private ComparableQuantity<ElectricPotential> vRatedB;
    private int tapNeutr;
    private int tapMin;
    private int tapMax;

    protected TransformerTypeInputCopyBuilder(TransformerTypeInput entity) {
      super(entity);
      this.gM = entity.gM;
      this.bM = entity.bM;
      this.dV = entity.dV;
      this.dPhi = entity.dPhi;
      this.vRatedA = entity.vRatedA;
      this.vRatedB = entity.vRatedB;
      this.tapNeutr = entity.tapNeutr;
      this.tapMin = entity.tapMin;
      this.tapMax = entity.tapMax;
    }

    /** Setter */
    public B gM(ComparableQuantity<ElectricConductance> gM) {
      this.gM = gM;
      return thisInstance();
    }

    public B bM(ComparableQuantity<ElectricConductance> bM) {
      this.bM = bM;
      return thisInstance();
    }

    public B dV(ComparableQuantity<Dimensionless> dV) {
      this.dV = dV;
      return thisInstance();
    }

    public B dPhi(ComparableQuantity<Angle> dPhi) {
      this.dPhi = dPhi;
      return thisInstance();
    }

    public B vRatedA(ComparableQuantity<ElectricPotential> vRatedA) {
      this.vRatedA = vRatedA;
      return thisInstance();
    }

    public B vRatedB(ComparableQuantity<ElectricPotential> vRatedB) {
      this.vRatedB = vRatedB;
      return thisInstance();
    }

    public B tapNeutr(int tapNeutr) {
      this.tapNeutr = tapNeutr;
      return thisInstance();
    }

    public B tapMin(int tapMin) {
      this.tapMin = tapMin;
      return thisInstance();
    }

    public B tapMax(int tapMax) {
      this.tapMax = tapMax;
      return thisInstance();
    }

    /** Getter */
    protected ComparableQuantity<ElectricConductance> getGM() {
      return gM;
    }

    protected ComparableQuantity<ElectricConductance> getBM() {
      return bM;
    }

    protected ComparableQuantity<Dimensionless> getDV() {
      return dV;
    }

    protected ComparableQuantity<Angle> getDPhi() {
      return dPhi;
    }

    protected ComparableQuantity<ElectricPotential> getVRatedA() {
      return vRatedA;
    }

    protected ComparableQuantity<ElectricPotential> getVRatedB() {
      return vRatedB;
    }

    protected int getTapNeutr() {
      return tapNeutr;
    }

    protected int getTapMin() {
      return tapMin;
    }

    protected int getTapMax() {
      return tapMax;
    }
  }
}
