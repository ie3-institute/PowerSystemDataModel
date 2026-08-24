/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.DimensionlessRate;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.StorageInput}. */
public class StorageTypeInput extends SystemParticipantTypeInput {
  /** . */
  private final ComparableQuantity<Energy> eStorage;

  /** . */
  private final ComparableQuantity<Power> pMax;

  /** . */
  private final ComparableQuantity<DimensionlessRate> activePowerGradient;

  /** . */
  private final ComparableQuantity<Dimensionless> eta;

  /**
   * @param uuid of the input entity
   * @param id of this type of Storage
   * @param capex capital expense for this type of Storage (typically in €)
   * @param opex operating expense for this type of Storage (typically in €/MWh)
   * @param eStorage stored energy capacity
   * @param sRated Rated apparent power of integrated inverter
   * @param cosPhiRated power factor for integrated inverter
   * @param pMax maximum permissible active power of the integrated inverter
   * @param activePowerGradient maximum permissible gradient of active power change
   * @param eta efficiency of the charging and discharging process
   */
  public StorageTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex,
      ComparableQuantity<EnergyPrice> opex,
      ComparableQuantity<Energy> eStorage,
      ComparableQuantity<Power> sRated,
      double cosPhiRated,
      ComparableQuantity<Power> pMax,
      ComparableQuantity<DimensionlessRate> activePowerGradient,
      ComparableQuantity<Dimensionless> eta) {
    super(uuid, id, capex, opex, sRated, cosPhiRated);
    this.eStorage = eStorage;
    this.pMax = pMax;
    this.activePowerGradient = activePowerGradient;
    this.eta = eta;
  }

  /**
   * @param uuid of the input entity
   * @param id of this type of Storage
   * @param capex capital expense for this type of Storage (typically in €)
   * @param opex operating expense for this type of Storage (typically in €/MWh)
   * @param eStorage stored energy capacity
   * @param sRated Rated apparent power of integrated inverter
   * @param cosPhiRated power factor for integrated inverter
   * @param pMax maximum permissible active power of the integrated inverter
   * @param activePowerGradient maximum permissible gradient of active power change
   * @param eta efficiency of the charging and discharging process
   * @param additionalInformation Of the input
   */
  public StorageTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex,
      ComparableQuantity<EnergyPrice> opex,
      ComparableQuantity<Energy> eStorage,
      ComparableQuantity<Power> sRated,
      double cosPhiRated,
      ComparableQuantity<Power> pMax,
      ComparableQuantity<DimensionlessRate> activePowerGradient,
      ComparableQuantity<Dimensionless> eta,
      Map<String, String> additionalInformation) {
    super(uuid, id, capex, opex, sRated, cosPhiRated);
    this.eStorage = eStorage;
    this.pMax = pMax;
    this.activePowerGradient = activePowerGradient;
    this.eta = eta;
    setAdditionalInformation(additionalInformation);
  }

  public ComparableQuantity<Energy> geteStorage() {
    return eStorage;
  }

  public ComparableQuantity<Power> getpMax() {
    return pMax;
  }

  public ComparableQuantity<DimensionlessRate> getActivePowerGradient() {
    return activePowerGradient;
  }

  public ComparableQuantity<Dimensionless> getEta() {
    return eta;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof StorageTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return eStorage.equals(that.eStorage)
        && pMax.equals(that.pMax)
        && activePowerGradient.equals(that.activePowerGradient)
        && eta.equals(that.eta);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), eStorage, pMax, activePowerGradient, eta);
  }

  @Override
  public String toString() {
    return "StorageTypeInput{"
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
        + ", pMax="
        + pMax
        + ", activePowerGradient="
        + activePowerGradient
        + ", eta="
        + eta
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public StorageTypeInputCopyBuilder copy() {
    return new StorageTypeInputCopyBuilder(this);
  }

  public static class StorageTypeInputCopyBuilder
      extends SystemParticipantTypeInputCopyBuilder<StorageTypeInputCopyBuilder> {
    private ComparableQuantity<Energy> eStorage;

    private ComparableQuantity<Power> pMax;

    private ComparableQuantity<DimensionlessRate> activePowerGradient;

    private ComparableQuantity<Dimensionless> eta;

    protected StorageTypeInputCopyBuilder(StorageTypeInput entity) {
      super(entity);
      this.eStorage = entity.eStorage;
      this.pMax = entity.pMax;
      this.activePowerGradient = entity.activePowerGradient;
      this.eta = entity.eta;
    }

    public StorageTypeInputCopyBuilder eStorage(ComparableQuantity<Energy> eStorage) {
      this.eStorage = eStorage;
      return thisInstance();
    }

    protected ComparableQuantity<Energy> geteStorage() {
      return eStorage;
    }

    public StorageTypeInputCopyBuilder pMax(ComparableQuantity<Power> pMax) {
      this.pMax = pMax;
      return thisInstance();
    }

    protected ComparableQuantity<Power> getpMax() {
      return pMax;
    }

    public StorageTypeInputCopyBuilder activePowerGradient(
        ComparableQuantity<DimensionlessRate> activePowerGradient) {
      this.activePowerGradient = activePowerGradient;
      return thisInstance();
    }

    protected ComparableQuantity<DimensionlessRate> getActivePowerGradient() {
      return activePowerGradient;
    }

    public StorageTypeInputCopyBuilder eta(ComparableQuantity<Dimensionless> eta) {
      this.eta = eta;
      return thisInstance();
    }

    protected ComparableQuantity<Dimensionless> getEta() {
      return eta;
    }

    @Override
    public StorageTypeInputCopyBuilder scale(double factor) {
      capex(getCapex().multiply(factor));
      sRated(getsRated().multiply(factor));
      eStorage(geteStorage().multiply(factor));
      pMax(getpMax().multiply(factor));
      return thisInstance();
    }

    @Override
    public StorageTypeInput build() {
      return new StorageTypeInput(
          getUuid(),
          getId(),
          getCapex(),
          getOpex(),
          eStorage,
          getsRated(),
          getCosPhiRated(),
          pMax,
          activePowerGradient,
          eta,
          getAdditionalInformation());
    }

    @Override
    protected StorageTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
