/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.DimensionlessRate;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import tec.uom.se.ComparableQuantity;

import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import javax.measure.quantity.Time;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.StorageInput} */
public class StorageTypeInput extends SystemParticipantTypeInput {
  /** Energy capacity (typically in kWh) */
  private final ComparableQuantity<Energy> eStorage; // TODO doublecheck
  /** Maximum permissible active power (typically in kW) */
  private final ComparableQuantity<Power> pMax; // TODO doublecheck
  /** Maximum permissible gradient of active power change (typically % / h) */
  private final ComparableQuantity<DimensionlessRate> activePowerGradient; // TODO doublecheck
  /** Efficiency of the charging and discharging process (typically in %) */
  private final ComparableQuantity<Dimensionless> eta; // TODO doublecheck
  /** Minimum permissible depth of discharge (typically in %) */
  private final ComparableQuantity<Dimensionless> dod; // TODO doublecheck
  /** Maximum life time of the storage (typically in ms) */
  private final ComparableQuantity<Time> lifeTime; // TODO doublecheck
  /** Maximum amount of full charging cycles */
  private final int lifeCycle;

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
   * @param dod maximum permissible depth of discharge
   * @param lifeTime maximum life time of the storage
   * @param lifeCycle maximum amount of full charging/discharging cycles
   */
  public StorageTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex, // TODO doublecheck - no return value, but superclass expects comparable
      ComparableQuantity<EnergyPrice> opex, // TODO doublecheck - no return value, but superclass expects comparable
      ComparableQuantity<Energy> eStorage, // TODO doublecheck
      ComparableQuantity<Power> sRated, // TODO doublecheck - no return value, but superclass expects comparable
      double cosPhiRated,
      ComparableQuantity<Power> pMax, // TODO doublecheck
      ComparableQuantity<DimensionlessRate> activePowerGradient, // TODO doublecheck
      ComparableQuantity<Dimensionless> eta, // TODO doublecheck
      ComparableQuantity<Dimensionless> dod, // TODO doublecheck
      ComparableQuantity<Time> lifeTime, // TODO doublecheck
      int lifeCycle) {
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosPhiRated);
    this.eStorage = eStorage.to(StandardUnits.ENERGY_IN);
    this.pMax = pMax.to(StandardUnits.ACTIVE_POWER_IN);
    this.activePowerGradient = activePowerGradient.to(StandardUnits.ACTIVE_POWER_GRADIENT);
    this.eta = eta.to(StandardUnits.EFFICIENCY);
    this.dod = dod.to(StandardUnits.DOD);
    this.lifeTime = lifeTime.to(StandardUnits.LIFE_TIME);
    this.lifeCycle = lifeCycle;
  }

  public ComparableQuantity<Dimensionless> getEta() { // TODO doublecheck
    return eta;
  }

  public ComparableQuantity<Dimensionless> getDod() { // TODO doublecheck
    return dod;
  }

  public ComparableQuantity<Time> getLifeTime() { // TODO doublecheck
    return lifeTime;
  }

  public int getLifeCycle() {
    return lifeCycle;
  }

  public ComparableQuantity<Energy> geteStorage() { // TODO doublecheck
    return eStorage;
  }

  public ComparableQuantity<Power> getpMax() { // TODO doublecheck
    return pMax;
  }

  public ComparableQuantity<DimensionlessRate> getActivePowerGradient() { // TODO doublecheck
    return activePowerGradient;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    StorageTypeInput that = (StorageTypeInput) o;
    return lifeCycle == that.lifeCycle
        && eStorage.equals(that.eStorage)
        && pMax.equals(that.pMax)
        && activePowerGradient.equals(that.activePowerGradient)
        && eta.equals(that.eta)
        && dod.equals(that.dod)
        && lifeTime.equals(that.lifeTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), eStorage, pMax, activePowerGradient, eta, dod, lifeTime, lifeCycle);
  }

  @Override
  public String toString() {
    return "StorageTypeInput{"
        + "eStorage="
        + eStorage
        + ", pMax="
        + pMax
        + ", cpRate="
        + activePowerGradient
        + ", eta="
        + eta
        + ", dod="
        + dod
        + ", lifeTime="
        + lifeTime
        + ", lifeCycle="
        + lifeCycle
        + '}';
  }
}
