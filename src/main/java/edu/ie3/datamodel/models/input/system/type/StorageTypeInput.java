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
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import javax.measure.quantity.Time;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.StorageInput} */
public class StorageTypeInput extends SystemParticipantTypeInput {
  /** Energy capacity (typically in kWh) */
  private final Quantity<Energy> eStorage;
  /** Maximum permissible active power (typically in kW) */
  private final Quantity<Power> pMax;
  /** Charging/Discharging rate at constant power (typically per unit) */
  private final Quantity<DimensionlessRate> cpRate;
  /** Efficiency of the charging and discharging process (typically in %) */
  private final Quantity<Dimensionless> eta;
  /** Minimum permissible depth of discharge (typically in %) */
  private final Quantity<Dimensionless> dod;
  /** Maximum life time of the storage (typically in ms) */
  private final Quantity<Time> lifeTime;
  /** Maximum amount of full charging cycles */
  private final int lifeCycle;

  /**
   * @param uuid of the input entity
   * @param id of this type of Storage
   * @param capex capital expense for this type of Storage (typically in €)
   * @param opex operating expense for this type of Storage (typically in €)
   * @param eStorage stored energy capacity
   * @param sRated Rated apparent power of integrated inverter
   * @param cosphiRated power factor for integrated inverter
   * @param pMax maximum permissible charge/discharge power
   * @param cpRate charging/discharging rate (constant power)
   * @param eta efficiency of the charging and discharging process
   * @param dod maximum permissible depth of discharge
   * @param lifeTime maximum life time of the storage
   * @param lifeCycle maximum amount of full charging/discharging cycles
   */
  public StorageTypeInput(
      UUID uuid,
      String id,
      Quantity<Currency> capex,
      Quantity<EnergyPrice> opex,
      Quantity<Energy> eStorage,
      Quantity<Power> sRated,
      double cosphiRated,
      Quantity<Power> pMax,
      Quantity<DimensionlessRate> cpRate,
      Quantity<Dimensionless> eta,
      Quantity<Dimensionless> dod,
      Quantity<Time> lifeTime,
      int lifeCycle) {
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosphiRated);
    this.eStorage = eStorage.to(StandardUnits.ENERGY_IN);
    this.pMax = pMax.to(StandardUnits.ACTIVE_POWER_IN);
    this.cpRate = cpRate.to(StandardUnits.CP_RATE);
    this.eta = eta.to(StandardUnits.EFFICIENCY);
    this.dod = dod.to(StandardUnits.DOD);
    this.lifeTime = lifeTime.to(StandardUnits.LIFE_TIME);
    this.lifeCycle = lifeCycle;
  }

  public Quantity<Dimensionless> getEta() {
    return eta;
  }

  public Quantity<Dimensionless> getDod() {
    return dod;
  }

  public Quantity<Time> getLifeTime() {
    return lifeTime;
  }

  public int getLifeCycle() {
    return lifeCycle;
  }

  public Quantity<Energy> geteStorage() {
    return eStorage;
  }

  public Quantity<Power> getpMax() {
    return pMax;
  }

  public Quantity<DimensionlessRate> getcpRate() {
    return cpRate;
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
        && cpRate.equals(that.cpRate)
        && eta.equals(that.eta)
        && dod.equals(that.dod)
        && lifeTime.equals(that.lifeTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), eStorage, pMax, cpRate, eta, dod, lifeTime, lifeCycle);
  }

  @Override
  public String toString() {
    return "StorageTypeInput{"
        + "eStorage="
        + eStorage
        + ", pMax="
        + pMax
        + ", cpRate="
        + cpRate
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
