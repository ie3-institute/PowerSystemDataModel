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
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import javax.measure.quantity.Time;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.StorageInput} */
public class StorageTypeInput extends SystemParticipantTypeInput {
  /** Energy capacity (typically in kWh) */
  private final ComparableQuantity<Energy> eStorage;

  /** Maximum permissible active power (typically in kW) */
  private final ComparableQuantity<Power> pMax;

  /** Maximum permissible gradient of active power change (typically % / h) */
  private final ComparableQuantity<DimensionlessRate> activePowerGradient;

  /** Efficiency of the charging and discharging process (typically in %) */
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
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosPhiRated);
    this.eStorage = eStorage.to(StandardUnits.ENERGY_IN);
    this.pMax = pMax.to(StandardUnits.ACTIVE_POWER_IN);
    this.activePowerGradient = activePowerGradient.to(StandardUnits.ACTIVE_POWER_GRADIENT);
    this.eta = eta.to(StandardUnits.EFFICIENCY);
  }

  public ComparableQuantity<Dimensionless> getEta() {
    return eta;
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
        + ", pMax="
        + pMax
        + ", cpRate="
        + activePowerGradient
        + ", eta="
        + eta
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
    private ComparableQuantity<Dimensionless> eta;
    private ComparableQuantity<Dimensionless> dod;
    private ComparableQuantity<Time> lifeTime;
    private int lifeCycle;

    private StorageTypeInputCopyBuilder(StorageTypeInput entity) {
      super(entity);
      this.eStorage = entity.geteStorage();
      this.pMax = entity.getpMax();
      this.activePowerGradient = entity.getActivePowerGradient();
      this.eta = entity.getEta();
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

    public StorageTypeInputCopyBuilder setEta(ComparableQuantity<Dimensionless> eta) {
      this.eta = eta;
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

    public ComparableQuantity<Dimensionless> getEta() {
      return eta;
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
          eta);
    }

    @Override
    protected StorageTypeInput.StorageTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
