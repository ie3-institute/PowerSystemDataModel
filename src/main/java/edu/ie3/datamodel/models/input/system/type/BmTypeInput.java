/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.DimensionlessRate;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.BmInput} */
public class BmTypeInput extends SystemParticipantTypeInput {

  /** Permissible load gradient (typically in %/h) */
  private final ComparableQuantity<DimensionlessRate> activePowerGradient;
  /** Efficiency of converter for this type of BM (typically in %) */
  private final ComparableQuantity<Dimensionless> etaConv;

  /**
   * @param uuid of the input entity
   * @param id of this type of BM
   * @param capex Capital expense for this type of BM (typically in €)
   * @param opex Operating expense for this type of BM (typically in €)
   * @param cosphiRated Power factor for this type of BM
   * @param activePowerGradient Maximum permissible gradient of active power change
   * @param sRated Rated apparent power for this type of BM (typically in kVA)
   * @param etaConv Efficiency of converter for this type of BM (typically in %)
   */
  public BmTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex,
      ComparableQuantity<EnergyPrice> opex,
      ComparableQuantity<DimensionlessRate> activePowerGradient,
      ComparableQuantity<Power> sRated,
      double cosphiRated,
      ComparableQuantity<Dimensionless> etaConv) {
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosphiRated);
    this.activePowerGradient = activePowerGradient.to(StandardUnits.ACTIVE_POWER_GRADIENT);
    this.etaConv = etaConv.to(StandardUnits.EFFICIENCY);
  }

  public ComparableQuantity<DimensionlessRate> getActivePowerGradient() {
    return activePowerGradient;
  }

  public ComparableQuantity<Dimensionless> getEtaConv() {
    return etaConv;
  }

  @Override
  public BmTypeInputCopyBuilder copy() {
    return new BmTypeInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BmTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return activePowerGradient.equals(that.activePowerGradient) && etaConv.equals(that.etaConv);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), activePowerGradient, etaConv);
  }

  @Override
  public String toString() {
    return "BmTypeInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", capex="
        + getCapex()
        + ", opex="
        + getOpex()
        + ", sRated="
        + getsRated()
        + ", cosphiRated="
        + getCosPhiRated()
        + "loadGradient="
        + activePowerGradient
        + ", etaConv="
        + etaConv
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link BmTypeInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link BmTypeInput}
   */
  public static class BmTypeInputCopyBuilder
      extends SystemParticipantTypeInputCopyBuilder<BmTypeInputCopyBuilder> {

    private ComparableQuantity<DimensionlessRate> activePowerGradient;
    private ComparableQuantity<Dimensionless> etaConv;

    private BmTypeInputCopyBuilder(BmTypeInput entity) {
      super(entity);
      this.activePowerGradient = entity.getActivePowerGradient();
      this.etaConv = entity.getEtaConv();
    }

    public BmTypeInputCopyBuilder setActivePowerGradient(
        ComparableQuantity<DimensionlessRate> activePowerGradient) {
      this.activePowerGradient = activePowerGradient;
      return thisInstance();
    }

    public BmTypeInputCopyBuilder setEtaConv(ComparableQuantity<Dimensionless> etaConv) {
      this.etaConv = etaConv;
      return thisInstance();
    }

    public ComparableQuantity<DimensionlessRate> getActivePowerGradient() {
      return activePowerGradient;
    }

    public ComparableQuantity<Dimensionless> getEtaConv() {
      return etaConv;
    }

    @Override
    public BmTypeInputCopyBuilder scale(Double factor) {
      capex(getCapex().multiply(factor));
      sRated(getsRated().multiply(factor));
      return thisInstance();
    }

    @Override
    public BmTypeInput build() {
      return new BmTypeInput(
          getUuid(),
          getId(),
          getCapex(),
          getOpex(),
          activePowerGradient,
          getsRated(),
          getCosPhiRated(),
          etaConv);
    }

    @Override
    protected BmTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
