/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import edu.ie3.util.quantities.interfaces.SpecificEnergy;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.EvInput} */
public class EvTypeInput extends SystemParticipantTypeInput {
  /** Energy capacity of the storage (typically in kWh) */
  private final ComparableQuantity<Energy> eStorage;

  /** Consumed electric energy per driven distance (typically in kWh/km) */
  private final ComparableQuantity<SpecificEnergy> eCons;

  /** power for DC (typically in kW) */
  private final ComparableQuantity<Power> sRatedDC;

  /**
   * Instantiates a new Ev type input.
   *
   * @param uuid of the input entity
   * @param id of this type of EV
   * @param capex Capital expense for this type of EV (typically in €)
   * @param opex Operating expense for this type of EV (typically in €)
   * @param eStorage Energy capacity of the storage
   * @param eCons Consumed electric energy per driven distance
   * @param sRated Rated apparent power for this type of EV (typically in kVA)
   * @param cosphiRated Power factor for this type of EV
   * @param sRatedDC power for DC (typically in kW)
   */
  public EvTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex,
      ComparableQuantity<EnergyPrice> opex,
      ComparableQuantity<Energy> eStorage,
      ComparableQuantity<SpecificEnergy> eCons,
      ComparableQuantity<Power> sRated,
      double cosphiRated,
      ComparableQuantity<Power> sRatedDC) {
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosphiRated);
    this.eStorage = eStorage.to(StandardUnits.ENERGY_IN);
    this.eCons = eCons.to(StandardUnits.ENERGY_PER_DISTANCE);
    this.sRatedDC = sRatedDC.to(StandardUnits.ACTIVE_POWER_IN);
  }

  /**
   * Gets storage.
   *
   * @return the storage
   */
  public ComparableQuantity<Energy> geteStorage() {
    return eStorage;
  }

  /**
   * Gets cons.
   *
   * @return the cons
   */
  public ComparableQuantity<SpecificEnergy> geteCons() {
    return eCons;
  }

  /**
   * Gets rated dc.
   *
   * @return the rated dc
   */
  public ComparableQuantity<Power> getsRatedDC() {
    return sRatedDC;
  }

  @Override
  public EvTypeInputCopyBuilder copy() {
    return new EvTypeInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EvTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return eStorage.equals(that.eStorage)
        && eCons.equals(that.eCons)
        && sRatedDC.equals(that.sRatedDC);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), eStorage, eCons, sRatedDC);
  }

  @Override
  public String toString() {
    return "EvTypeInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + "capex="
        + getCapex()
        + ", opex="
        + getOpex()
        + ", sRated="
        + getsRated()
        + ", cosphiRated="
        + getCosPhiRated()
        + "eStorage="
        + eStorage
        + ", eCons="
        + eCons
        + ", sRatedDC="
        + sRatedDC
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link EvTypeInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link EvTypeInput}
   */
  public static class EvTypeInputCopyBuilder
      extends SystemParticipantTypeInputCopyBuilder<EvTypeInput.EvTypeInputCopyBuilder> {

    private ComparableQuantity<Energy> eStorage;
    private ComparableQuantity<SpecificEnergy> eCons;
    private ComparableQuantity<Power> sRatedDC;

    private EvTypeInputCopyBuilder(EvTypeInput entity) {
      super(entity);
      this.eStorage = entity.geteStorage();
      this.eCons = entity.geteCons();
      this.sRatedDC = entity.getsRatedDC();
    }

    /**
     * Sets storage.
     *
     * @param eStorage the e storage
     * @return the storage
     */
    public EvTypeInputCopyBuilder seteStorage(ComparableQuantity<Energy> eStorage) {
      this.eStorage = eStorage;
      return thisInstance();
    }

    /**
     * Sets cons.
     *
     * @param eCons the e cons
     * @return the cons
     */
    public EvTypeInputCopyBuilder seteCons(ComparableQuantity<SpecificEnergy> eCons) {
      this.eCons = eCons;
      return thisInstance();
    }

    /**
     * Sets rated dc.
     *
     * @param sRatedDC the s rated dc
     * @return the rated dc
     */
    public EvTypeInputCopyBuilder setsRatedDC(ComparableQuantity<Power> sRatedDC) {
      this.sRatedDC = sRatedDC;
      return thisInstance();
    }

    /**
     * Gets storage.
     *
     * @return the storage
     */
    public ComparableQuantity<Energy> geteStorage() {
      return eStorage;
    }

    /**
     * Gets cons.
     *
     * @return the cons
     */
    public ComparableQuantity<SpecificEnergy> geteCons() {
      return eCons;
    }

    /**
     * Gets rated dc.
     *
     * @return the rated dc
     */
    public ComparableQuantity<Power> getsRatedDC() {
      return sRatedDC;
    }

    @Override
    public EvTypeInput.EvTypeInputCopyBuilder scale(Double factor) {
      capex(getCapex().multiply(factor));
      sRated(getsRated().multiply(factor));
      seteStorage(geteStorage().multiply(factor));
      seteCons(geteCons().multiply(factor));
      setsRatedDC(getsRatedDC().multiply(factor));
      return thisInstance();
    }

    @Override
    public EvTypeInput build() {
      return new EvTypeInput(
          getUuid(),
          getId(),
          getCapex(),
          getOpex(),
          eStorage,
          eCons,
          getsRated(),
          getCosPhiRated(),
          getsRatedDC());
    }

    @Override
    protected EvTypeInput.EvTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
