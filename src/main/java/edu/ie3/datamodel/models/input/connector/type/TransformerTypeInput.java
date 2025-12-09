/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.io.source.SourceValidator;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.*;
import tech.units.indriya.ComparableQuantity;

public abstract class TransformerTypeInput extends AssetTypeInput {
  /* Static fields. */
  public static final String G_M = "gM";
  public static final String B_M = "bM";
  public static final String D_V = "dV";
  public static final String D_PHI = "dPhi";
  public static final String TAP_NEUTR = "tapNeutr";
  public static final String TAP_MIN = "tapMin";
  public static final String TAP_MAX = "tapMax";

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
      int tapNeutr,
      int tapMin,
      int tapMax) {
    super(uuid, id);
    this.gM = gM;
    this.bM = bM;
    this.dV = dV;
    this.dPhi = dPhi;
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
      int tapNeutr,
      int tapMin,
      int tapMax) {
    super(assetTypeInput);
    this.gM = gM;
    this.bM = bM;
    this.dV = dV;
    this.dPhi = dPhi;
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
    this.tapNeutr = other.tapNeutr;
    this.tapMin = other.tapMin;
    this.tapMax = other.tapMax;
  }

  protected static SourceValidator.Fields transformerTypeFields() {
    return assetTypeFields().add(G_M, B_M, D_V, D_PHI, TAP_NEUTR, TAP_MIN, TAP_MAX);
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
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    TransformerTypeInput that = (TransformerTypeInput) o;
    return tapNeutr == that.tapNeutr
        && tapMin == that.tapMin
        && tapMax == that.tapMax
        && Objects.equals(gM, that.gM)
        && Objects.equals(bM, that.bM)
        && Objects.equals(dV, that.dV)
        && Objects.equals(dPhi, that.dPhi);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), gM, bM, dV, dPhi, tapNeutr, tapMin, tapMax);
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
    private int tapNeutr;
    private int tapMin;
    private int tapMax;

    protected TransformerTypeInputCopyBuilder(TransformerTypeInput entity) {
      super(entity);
      this.gM = entity.gM;
      this.bM = entity.bM;
      this.dV = entity.dV;
      this.dPhi = entity.dPhi;
      this.tapNeutr = entity.tapNeutr;
      this.tapMin = entity.tapMin;
      this.tapMax = entity.tapMax;
    }

    /** Setter */
    public TransformerTypeInputCopyBuilder<B> gM(ComparableQuantity<ElectricConductance> gM) {
      this.gM = gM;
      return thisInstance();
    }

    public TransformerTypeInputCopyBuilder<B> bM(ComparableQuantity<ElectricConductance> bM) {
      this.bM = bM;
      return thisInstance();
    }

    public TransformerTypeInputCopyBuilder<B> dV(ComparableQuantity<Dimensionless> dV) {
      this.dV = dV;
      return thisInstance();
    }

    public TransformerTypeInputCopyBuilder<B> dPhi(ComparableQuantity<Angle> dPhi) {
      this.dPhi = dPhi;
      return thisInstance();
    }

    public TransformerTypeInputCopyBuilder<B> tapNeutr(int tapNeutr) {
      this.tapNeutr = tapNeutr;
      return thisInstance();
    }

    public TransformerTypeInputCopyBuilder<B> tapMin(int tapMin) {
      this.tapMin = tapMin;
      return thisInstance();
    }

    public TransformerTypeInputCopyBuilder<B> tapMax(int tapMax) {
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
