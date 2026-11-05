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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import javax.measure.quantity.Time;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of {@link edu.ie3.datamodel.models.input.system.StorageInput} */
public class StorageTypeInput extends SystemParticipantTypeInput {
  /** Energy capacity (typically in kWh) */
  private final ComparableQuantity<Energy> eStorage;

  /** Maximum permissible active power (typically in kW) */
  private final ComparableQuantity<Power> pMax;

  /** Maximum permissible gradient of active power change (typically % / h) */
  private final ComparableQuantity<DimensionlessRate> activePowerGradient;

  /** Efficiency of charging operations (typically in %) */
  private final ComparableQuantity<Dimensionless> etaCharging;

  /** Efficiency of discharging operations (typically in %) */
  private final ComparableQuantity<Dimensionless> etaDischarging;

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
   * @param etaCharging efficiency of charging operations
   * @param etaDischarging efficiency of discharging operations
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
      ComparableQuantity<Dimensionless> etaCharging,
      ComparableQuantity<Dimensionless> etaDischarging) {
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosPhiRated);
    this.eStorage = eStorage.to(StandardUnits.ENERGY_IN);
    this.pMax = pMax.to(StandardUnits.ACTIVE_POWER_IN);
    this.activePowerGradient = activePowerGradient.to(StandardUnits.ACTIVE_POWER_GRADIENT);
    this.etaCharging = etaCharging.to(StandardUnits.EFFICIENCY);
    this.etaDischarging = etaDischarging.to(StandardUnits.EFFICIENCY);
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
   * @param etaCharging efficiency of charging operations
   * @param etaDischarging efficiency of discharging operations
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
      ComparableQuantity<Dimensionless> etaCharging,
      ComparableQuantity<Dimensionless> etaDischarging,
      Map<String, String> additionalInformation) {
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosPhiRated);
    this.eStorage = eStorage.to(StandardUnits.ENERGY_IN);
    this.pMax = pMax.to(StandardUnits.ACTIVE_POWER_IN);
    this.activePowerGradient = activePowerGradient.to(StandardUnits.ACTIVE_POWER_GRADIENT);
    this.etaCharging = etaCharging.to(StandardUnits.EFFICIENCY);
    this.etaDischarging = etaDischarging.to(StandardUnits.EFFICIENCY);
    setAdditionalInformation(additionalInformation);
  }

  public ComparableQuantity<Dimensionless> getEtaCharging() {
    return etaCharging;
  }

  public ComparableQuantity<Dimensionless> getEtaDischarging() {
    return etaDischarging;
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

  @Override
  public StorageTypeInputCopyBuilder copy() {
    return new StorageTypeInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof StorageTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return eStorage.equals(that.eStorage)
        && pMax.equals(that.pMax)
        && activePowerGradient.equals(that.activePowerGradient)
        && etaCharging.equals(that.etaCharging)
        && etaDischarging.equals(that.etaDischarging);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), eStorage, pMax, activePowerGradient, etaCharging, etaDischarging);
  }

  @Override
  public String toString() {
    return "StorageTypeInput{"
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
        + ", cosPhiRated="
        + getCosPhiRated()
        + "eStorage="
        + eStorage
        + ", pMax="
        + pMax
        + ", cpRate="
        + activePowerGradient
        + ", etaCharging="
        + etaCharging
        + ", etaDischarging="
        + etaDischarging
        + ", additionalInformation="
        + getAdditionalInformation()
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link StorageTypeInput} entities with
   * altered field values. For detailed field descriptions refer to java docs of {@link
   * StorageTypeInput}
   */
  public static class StorageTypeInputCopyBuilder
      extends SystemParticipantTypeInputCopyBuilder<StorageTypeInput.StorageTypeInputCopyBuilder> {

    private ComparableQuantity<Energy> eStorage;
    private ComparableQuantity<Power> pMax;
    private ComparableQuantity<DimensionlessRate> activePowerGradient;
    private ComparableQuantity<Dimensionless> etaCharging;
    private ComparableQuantity<Dimensionless> etaDischarging;
    private ComparableQuantity<Dimensionless> dod;
    private ComparableQuantity<Time> lifeTime;
    private int lifeCycle;

    private StorageTypeInputCopyBuilder(StorageTypeInput entity) {
      super(entity);
      this.eStorage = entity.geteStorage();
      this.pMax = entity.getpMax();
      this.activePowerGradient = entity.getActivePowerGradient();
      this.etaCharging = entity.getEtaCharging();
      this.etaDischarging = entity.getEtaDischarging();
    }

    public StorageTypeInputCopyBuilder seteStorage(ComparableQuantity<Energy> eStorage) {
      this.eStorage = eStorage;
      return thisInstance();
    }

    public StorageTypeInputCopyBuilder setpMax(ComparableQuantity<Power> pMax) {
      this.pMax = pMax;
      return thisInstance();
    }

    public StorageTypeInputCopyBuilder setActivePowerGradient(
        ComparableQuantity<DimensionlessRate> activePowerGradient) {
      this.activePowerGradient = activePowerGradient;
      return thisInstance();
    }

    public StorageTypeInputCopyBuilder setEtaCharging(
        ComparableQuantity<Dimensionless> etaCharging) {
      this.etaCharging = etaCharging;
      return thisInstance();
    }

    public StorageTypeInputCopyBuilder setEtaDischarging(
        ComparableQuantity<Dimensionless> etaDischarging) {
      this.etaDischarging = etaDischarging;
      return thisInstance();
    }

    public StorageTypeInputCopyBuilder setDod(ComparableQuantity<Dimensionless> dod) {
      this.dod = dod;
      return thisInstance();
    }

    public StorageTypeInputCopyBuilder setLifeTime(ComparableQuantity<Time> lifeTime) {
      this.lifeTime = lifeTime;
      return thisInstance();
    }

    public StorageTypeInputCopyBuilder setLifeCycle(int lifeCycle) {
      this.lifeCycle = lifeCycle;
      return thisInstance();
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

    public ComparableQuantity<Dimensionless> getEtaCharging() {
      return etaCharging;
    }

    public ComparableQuantity<Dimensionless> getEtaDischarging() {
      return etaDischarging;
    }

    public ComparableQuantity<Dimensionless> getDod() {
      return dod;
    }

    public ComparableQuantity<Time> getLifeTime() {
      return lifeTime;
    }

    public int getLifeCycle() {
      return lifeCycle;
    }

    @Override
    public StorageTypeInput.StorageTypeInputCopyBuilder scale(Double factor) {
      capex(getCapex().multiply(factor));
      sRated(getsRated().multiply(factor));
      seteStorage(geteStorage().multiply(factor));
      setpMax(getpMax().multiply(factor));
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
          etaCharging,
          etaDischarging,
          getAdditionalInformation());
    }

    @Override
    protected StorageTypeInput.StorageTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
