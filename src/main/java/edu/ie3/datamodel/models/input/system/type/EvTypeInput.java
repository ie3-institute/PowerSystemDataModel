/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import edu.ie3.util.quantities.interfaces.SpecificEnergy;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.EvInput}. */
public class EvTypeInput extends SystemParticipantTypeInput {
  /** Energy capacity of the storage (typically in kWh). */
  private final ComparableQuantity<Energy> eStorage;

  /** Consumed electric energy per driven distance (typically in kWh/km). */
  private final ComparableQuantity<SpecificEnergy> eCons;

  /** Power for DC (typically in kW). */
  private final ComparableQuantity<Power> sRatedDC;

  /**
   * @param uuid of the input entity
   * @param id of this type of EV
   * @param capex Capital expense for this type of EV (typically in €)
   * @param opex Operating expense for this type of EV (typically in €)
   * @param eStorage Energy capacity of the storage
   * @param eCons Consumed electric energy per driven distance
   * @param sRated Rated apparent power for this type of EV (typically in kVA)
   * @param cosPhiRated Power factor for this type of EV
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
      double cosPhiRated,
      ComparableQuantity<Power> sRatedDC) {
    super(uuid, id, capex, opex, sRated, cosPhiRated);
    this.eStorage = eStorage;
    this.eCons = eCons;
    this.sRatedDC = sRatedDC;
  }

  /**
   * @param uuid of the input entity
   * @param id of this type of EV
   * @param capex Capital expense for this type of EV (typically in €)
   * @param opex Operating expense for this type of EV (typically in €)
   * @param eStorage Energy capacity of the storage
   * @param eCons Consumed electric energy per driven distance
   * @param sRated Rated apparent power for this type of EV (typically in kVA)
   * @param cosPhiRated Power factor for this type of EV
   * @param sRatedDC power for DC (typically in kW)
   * @param additionalInformation Of the input
   */
  public EvTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex,
      ComparableQuantity<EnergyPrice> opex,
      ComparableQuantity<Energy> eStorage,
      ComparableQuantity<SpecificEnergy> eCons,
      ComparableQuantity<Power> sRated,
      double cosPhiRated,
      ComparableQuantity<Power> sRatedDC,
      Map<String, String> additionalInformation) {
    super(uuid, id, capex, opex, sRated, cosPhiRated);
    this.eStorage = eStorage;
    this.eCons = eCons;
    this.sRatedDC = sRatedDC;
    setAdditionalInformation(additionalInformation);
  }

  public ComparableQuantity<Energy> geteStorage() {
    return eStorage;
  }

  public ComparableQuantity<SpecificEnergy> geteCons() {
    return eCons;
  }

  public ComparableQuantity<Power> getsRatedDC() {
    return sRatedDC;
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
        + ", capex="
        + getCapex()
        + ", opex="
        + getOpex()
        + ", sRated="
        + getsRated()
        + ", cosPhiRated="
        + getCosPhiRated()
        + ", eStorage="
        + eStorage
        + ", eCons="
        + eCons
        + ", sRatedDC="
        + sRatedDC
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public EvTypeInputCopyBuilder copy() {
    return new EvTypeInputCopyBuilder(this);
  }

  public static class EvTypeInputCopyBuilder
      extends SystemParticipantTypeInputCopyBuilder<EvTypeInputCopyBuilder> {
    private ComparableQuantity<Energy> eStorage;

    private ComparableQuantity<SpecificEnergy> eCons;

    private ComparableQuantity<Power> sRatedDC;

    protected EvTypeInputCopyBuilder(EvTypeInput entity) {
      super(entity);
      this.eStorage = entity.eStorage;
      this.eCons = entity.eCons;
      this.sRatedDC = entity.sRatedDC;
    }

    public EvTypeInputCopyBuilder eStorage(ComparableQuantity<Energy> eStorage) {
      this.eStorage = eStorage;
      return thisInstance();
    }

    protected ComparableQuantity<Energy> geteStorage() {
      return eStorage;
    }

    public EvTypeInputCopyBuilder eCons(ComparableQuantity<SpecificEnergy> eCons) {
      this.eCons = eCons;
      return thisInstance();
    }

    protected ComparableQuantity<SpecificEnergy> geteCons() {
      return eCons;
    }

    public EvTypeInputCopyBuilder sRatedDC(ComparableQuantity<Power> sRatedDC) {
      this.sRatedDC = sRatedDC;
      return thisInstance();
    }

    protected ComparableQuantity<Power> getsRatedDC() {
      return sRatedDC;
    }

    @Override
    public EvTypeInputCopyBuilder scale(double factor) {
      capex(getCapex().multiply(factor));
      sRated(getsRated().multiply(factor));
      eStorage(geteStorage().multiply(factor));
      eCons(geteCons().multiply(factor));
      sRatedDC(getsRatedDC().multiply(factor));
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
          sRatedDC,
          getAdditionalInformation());
    }

    @Override
    protected EvTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
