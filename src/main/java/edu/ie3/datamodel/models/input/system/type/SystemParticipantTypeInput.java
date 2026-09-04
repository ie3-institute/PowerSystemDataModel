/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.utils.QuantityUtils;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.SystemParticipantInput}. */
public abstract class SystemParticipantTypeInput extends AssetTypeInput {
  /** Capital expense for this type of system participant (typically in €). */
  private final ComparableQuantity<Currency> capex;

  /** Operating expense for this type of system participant (typically in €/MWh). */
  private final ComparableQuantity<EnergyPrice> opex;

  /** Rated apparent power of the type (in kVA). */
  private final ComparableQuantity<Power> sRated;

  /** Power factor for this type of system participant. */
  private final double cosPhiRated;

  /**
   * @param uuid of the input entity
   * @param id of this type of system participant
   * @param capex Captial expense for this type of system participant (typically in €)
   * @param opex Operating expense for this type of system participant (typically in €/MWh)
   * @param sRated Rated apparent power
   * @param cosPhiRated Power factor for this type of system participant
   */
  protected SystemParticipantTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex,
      ComparableQuantity<EnergyPrice> opex,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    super(uuid, id);
    this.capex = capex;
    this.opex = opex;
    this.sRated = sRated;
    this.cosPhiRated = cosPhiRated;
  }

  public ComparableQuantity<Currency> getCapex() {
    return capex;
  }

  public ComparableQuantity<EnergyPrice> getOpex() {
    return opex;
  }

  public ComparableQuantity<Power> getSRated() {
    return sRated;
  }

  public double getCosPhiRated() {
    return cosPhiRated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SystemParticipantTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return QuantityUtils.equals(capex, that.capex)
        && QuantityUtils.equals(opex, that.opex)
        && QuantityUtils.equals(sRated, that.sRated)
        && cosPhiRated == that.cosPhiRated;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), capex, opex, sRated, cosPhiRated);
  }

  @Override
  public String toString() {
    return "SystemParticipantTypeInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", capex="
        + capex
        + ", opex="
        + opex
        + ", sRated="
        + sRated
        + ", cosPhiRated="
        + cosPhiRated
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public abstract SystemParticipantTypeInputCopyBuilder<?> copy();

  public abstract static class SystemParticipantTypeInputCopyBuilder<
          B extends SystemParticipantTypeInputCopyBuilder<B>>
      extends AssetTypeInputCopyBuilder<B> {
    private ComparableQuantity<Currency> capex;

    private ComparableQuantity<EnergyPrice> opex;

    private ComparableQuantity<Power> sRated;

    private double cosPhiRated;

    protected SystemParticipantTypeInputCopyBuilder(SystemParticipantTypeInput entity) {
      super(entity);
      this.capex = entity.capex;
      this.opex = entity.opex;
      this.sRated = entity.sRated;
      this.cosPhiRated = entity.cosPhiRated;
    }

    public B capex(ComparableQuantity<Currency> capex) {
      this.capex = capex;
      return thisInstance();
    }

    protected ComparableQuantity<Currency> getCapex() {
      return capex;
    }

    public B opex(ComparableQuantity<EnergyPrice> opex) {
      this.opex = opex;
      return thisInstance();
    }

    protected ComparableQuantity<EnergyPrice> getOpex() {
      return opex;
    }

    public B sRated(ComparableQuantity<Power> sRated) {
      this.sRated = sRated;
      return thisInstance();
    }

    protected ComparableQuantity<Power> getSRated() {
      return sRated;
    }

    public B cosPhiRated(double cosPhiRated) {
      this.cosPhiRated = cosPhiRated;
      return thisInstance();
    }

    protected double getCosPhiRated() {
      return cosPhiRated;
    }

    /**
     * Scales the input entity in a way that tries to preserve proportions that are related to
     * power. This means that capacity, consumption etc. are scaled with the same factor. Related
     * properties associated with the input type (if applicable) are scaled as well.
     *
     * @param factor The factor to scale with
     * @return A copy builder with scaled relevant propertiesScales the input entity in a way that
     *     tries to preserve proportions that are related to power. This means that capacity,
     *     consumption etc. are scaled with the same factor. Related properties associated with the
     *     input type (if applicable) are scaled as well.
     * @param factor The factor to scale with
     * @return A copy builder with scaled relevant properties
     */
    @Override
    public B scale(double factor) {
      return null;
      return thisInstance();
    }

    @Override
    public abstract SystemParticipantTypeInput build();

    @Override
    protected abstract B thisInstance();
  }
}
