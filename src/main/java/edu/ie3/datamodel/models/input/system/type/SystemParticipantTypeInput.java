/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.SystemParticipantInput} */
public abstract class SystemParticipantTypeInput extends AssetTypeInput {
  /** Capital expense for this type of system participant (typically in €) */
  private final ComparableQuantity<Currency> capex;

  /** Operating expense for this type of system participant (typically in €/MWh) */
  private final ComparableQuantity<EnergyPrice> opex;

  /** Rated apparent power of the type (in kVA) */
  private final ComparableQuantity<Power> sRated;

  /** Power factor for this type of system participant */
  private final double cosPhiRated;

  /**
   * Instantiates a new System participant type input.
   *
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

  /**
   * Gets capex.
   *
   * @return the capex
   */
  public ComparableQuantity<Currency> getCapex() {
    return capex;
  }

  /**
   * Gets opex.
   *
   * @return the opex
   */
  public ComparableQuantity<EnergyPrice> getOpex() {
    return opex;
  }

  /**
   * Gets rated.
   *
   * @return the rated
   */
  public ComparableQuantity<Power> getsRated() {
    return sRated;
  }

  /**
   * Gets cos phi rated.
   *
   * @return the cos phi rated
   */
  public double getCosPhiRated() {
    return cosPhiRated;
  }

  public abstract SystemParticipantTypeInputCopyBuilder<?> copy();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SystemParticipantTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return Double.compare(that.cosPhiRated, cosPhiRated) == 0
        && capex.equals(that.capex)
        && opex.equals(that.opex)
        && sRated.equals(that.sRated);
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
        + "capex="
        + capex
        + ", opex="
        + opex
        + ", sRated="
        + sRated
        + ", cosphiRated="
        + cosPhiRated
        + '}';
  }

  /**
   * Abstract class for all builders that build child entities of abstract class {@link
   * SystemParticipantTypeInput}*.
   *
   * @param <B> The builder type extending from {@link SystemParticipantTypeInputCopyBuilder}.
   */
  public abstract static class SystemParticipantTypeInputCopyBuilder<
          B extends SystemParticipantTypeInput.SystemParticipantTypeInputCopyBuilder<B>>
      extends AssetTypeInput.AssetTypeInputCopyBuilder<B> {

    private ComparableQuantity<Currency> capex;
    private ComparableQuantity<EnergyPrice> opex;
    private ComparableQuantity<Power> sRated;
    private double cosPhiRated;

    /**
     * Instantiates a new System participant type input copy builder.
     *
     * @param entity the entity
     */
    protected SystemParticipantTypeInputCopyBuilder(SystemParticipantTypeInput entity) {
      super(entity);
      this.capex = entity.getCapex();
      this.opex = entity.getOpex();
      this.sRated = entity.getsRated();
      this.cosPhiRated = entity.getCosPhiRated();
    }

    /**
     * Capex b.
     *
     * @param capex the capex
     * @return the b
     */
    public B capex(ComparableQuantity<Currency> capex) {
      this.capex = capex;
      return thisInstance();
    }

    /**
     * Opex b.
     *
     * @param opex the opex
     * @return the b
     */
    public B opex(ComparableQuantity<EnergyPrice> opex) {
      this.opex = opex;
      return thisInstance();
    }

    /**
     * S rated b.
     *
     * @param sRated the s rated
     * @return the b
     */
    public B sRated(ComparableQuantity<Power> sRated) {
      this.sRated = sRated;
      return thisInstance();
    }

    /**
     * Cos phi rated b.
     *
     * @param cosPhiRated the cos phi rated
     * @return the b
     */
    public B cosPhiRated(double cosPhiRated) {
      this.cosPhiRated = cosPhiRated;
      return thisInstance();
    }

    /**
     * Gets capex.
     *
     * @return the capex
     */
    public ComparableQuantity<Currency> getCapex() {
      return capex;
    }

    /**
     * Gets opex.
     *
     * @return the opex
     */
    public ComparableQuantity<EnergyPrice> getOpex() {
      return opex;
    }

    /**
     * Gets rated.
     *
     * @return the rated
     */
    public ComparableQuantity<Power> getsRated() {
      return sRated;
    }

    /**
     * Gets cos phi rated.
     *
     * @return the cos phi rated
     */
    public double getCosPhiRated() {
      return cosPhiRated;
    }

    /**
     * Scales the type input entity in a way that tries to preserve proportions that are related to
     * power. This means that capacity, consumption etc. are scaled with the same factor.
     *
     * @param factor The factor to scale with
     * @return A copy builder with scaled relevant properties
     */
    public abstract B scale(Double factor);

    @Override
    public abstract SystemParticipantTypeInput build();

    @Override
    protected abstract B thisInstance();
  }
}
