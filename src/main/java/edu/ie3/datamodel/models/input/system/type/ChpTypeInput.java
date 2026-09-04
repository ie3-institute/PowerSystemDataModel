/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

import edu.ie3.datamodel.utils.QuantityUtils;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.ChpInput}. */
public class ChpTypeInput extends SystemParticipantTypeInput {
  /** Electrical efficiency (typically in %). */
  private final ComparableQuantity<Dimensionless> etaEl;

  /** Thermal efficiency (typically in %). */
  private final ComparableQuantity<Dimensionless> etaThermal;

  /** Rated thermal power (typically in kW). */
  private final ComparableQuantity<Power> pThermal;

  /** Internal consumption (typically in kW). */
  private final ComparableQuantity<Power> pOwn;

  /**
   * @param uuid of the input entity
   * @param id of this type of CHP
   * @param capex Capital expense for this type of CHP (typically in €)
   * @param opex Operating expense for this type of CHP (typically in €)
   * @param etaEl Electrical efficiency
   * @param etaThermal Thermal efficiency
   * @param sRated Rated electrical apparent power
   * @param cosPhiRated Power factor for this type of CHP
   * @param pThermal Rated thermal power
   * @param pOwn Internal consumption
   */
  public ChpTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex,
      ComparableQuantity<EnergyPrice> opex,
      ComparableQuantity<Dimensionless> etaEl,
      ComparableQuantity<Dimensionless> etaThermal,
      ComparableQuantity<Power> sRated,
      double cosPhiRated,
      ComparableQuantity<Power> pThermal,
      ComparableQuantity<Power> pOwn) {
    super(uuid, id, capex, opex, sRated, cosPhiRated);
    this.etaEl = etaEl;
    this.etaThermal = etaThermal;
    this.pThermal = pThermal;
    this.pOwn = pOwn;
  }

  /**
   * @param uuid of the input entity
   * @param id of this type of CHP
   * @param capex Capital expense for this type of CHP (typically in €)
   * @param opex Operating expense for this type of CHP (typically in €)
   * @param etaEl Electrical efficiency
   * @param etaThermal Thermal efficiency
   * @param sRated Rated electrical apparent power
   * @param cosPhiRated Power factor for this type of CHP
   * @param pThermal Rated thermal power
   * @param pOwn Internal consumption
   * @param additionalInformation That were provided by the source
   */
  public ChpTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex,
      ComparableQuantity<EnergyPrice> opex,
      ComparableQuantity<Dimensionless> etaEl,
      ComparableQuantity<Dimensionless> etaThermal,
      ComparableQuantity<Power> sRated,
      double cosPhiRated,
      ComparableQuantity<Power> pThermal,
      ComparableQuantity<Power> pOwn,
      Map<String, String> additionalInformation) {
    super(uuid, id, capex, opex, sRated, cosPhiRated);
    this.etaEl = etaEl;
    this.etaThermal = etaThermal;
    this.pThermal = pThermal;
    this.pOwn = pOwn;
    setAdditionalInformation(additionalInformation);
  }

  public ComparableQuantity<Dimensionless> getEtaEl() {
    return etaEl;
  }

  public ComparableQuantity<Dimensionless> getEtaThermal() {
    return etaThermal;
  }

  public ComparableQuantity<Power> getPThermal() {
    return pThermal;
  }

  public ComparableQuantity<Power> getPOwn() {
    return pOwn;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChpTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return QuantityUtils.equals(etaEl, that.etaEl)
        && QuantityUtils.equals(etaThermal, that.etaThermal)
        && QuantityUtils.equals(pThermal, that.pThermal)
        && QuantityUtils.equals(pOwn, that.pOwn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), etaEl, etaThermal, pThermal, pOwn);
  }

  @Override
  public String toString() {
    return "ChpTypeInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", capex="
        + getCapex()
        + ", opex="
        + getOpex()
        + ", sRated="
        + getSRated()
        + ", cosPhiRated="
        + getCosPhiRated()
        + ", etaEl="
        + etaEl
        + ", etaThermal="
        + etaThermal
        + ", pThermal="
        + pThermal
        + ", pOwn="
        + pOwn
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public ChpTypeInputCopyBuilder copy() {
    return new ChpTypeInputCopyBuilder(this);
  }

  public static class ChpTypeInputCopyBuilder
      extends SystemParticipantTypeInputCopyBuilder<ChpTypeInputCopyBuilder> {
    private ComparableQuantity<Dimensionless> etaEl;

    private ComparableQuantity<Dimensionless> etaThermal;

    private ComparableQuantity<Power> pThermal;

    private ComparableQuantity<Power> pOwn;

    protected ChpTypeInputCopyBuilder(ChpTypeInput entity) {
      super(entity);
      this.etaEl = entity.etaEl;
      this.etaThermal = entity.etaThermal;
      this.pThermal = entity.pThermal;
      this.pOwn = entity.pOwn;
    }

    public ChpTypeInputCopyBuilder etaEl(ComparableQuantity<Dimensionless> etaEl) {
      this.etaEl = etaEl;
      return thisInstance();
    }

    protected ComparableQuantity<Dimensionless> getEtaEl() {
      return etaEl;
    }

    public ChpTypeInputCopyBuilder etaThermal(ComparableQuantity<Dimensionless> etaThermal) {
      this.etaThermal = etaThermal;
      return thisInstance();
    }

    protected ComparableQuantity<Dimensionless> getEtaThermal() {
      return etaThermal;
    }

    public ChpTypeInputCopyBuilder pThermal(ComparableQuantity<Power> pThermal) {
      this.pThermal = pThermal;
      return thisInstance();
    }

    protected ComparableQuantity<Power> getPThermal() {
      return pThermal;
    }

    public ChpTypeInputCopyBuilder pOwn(ComparableQuantity<Power> pOwn) {
      this.pOwn = pOwn;
      return thisInstance();
    }

    protected ComparableQuantity<Power> getPOwn() {
      return pOwn;
    }

    @Override
    public ChpTypeInputCopyBuilder scale(double factor) {
      return capex(getCapex().multiply(factor));
      sRated(getsRated().multiply(factor));
      pThermal(getpThermal().multiply(factor));
      pOwn(getpOwn().multiply(factor));
      return thisInstance();
    }

    @Override
    public ChpTypeInput build() {
      return new ChpTypeInput(
          getUuid(),
          getId(),
          getCapex(),
          getOpex(),
          etaEl,
          etaThermal,
          getSRated(),
          getCosPhiRated(),
          pThermal,
          pOwn,
          getAdditionalInformation());
    }

    @Override
    protected ChpTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
