/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system.type;

import edu.ie3.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import javax.measure.quantity.Time;

/** Describes the type of a {@link edu.ie3.models.input.system.StorageInput} */
public class StorageTypeInput extends SystemParticipantTypeInput {
  /** Energy capacity (typically in kWh) */
  private final Quantity<Energy> eStorage;
  /** Minimum permissible active power (typically in kW) */
  private final Quantity<Power> pMin;
  /** Maximum permissible active power (typically in kW) */
  private final Quantity<Power> pMax;
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
   * @param capex Captial expense for this type of Storage (typically in €)
   * @param opex Operating expense for this type of Storage (typically in €)
   * @param eStorage Energy capacity
   * @param sRated Rated apparent power
   * @param cosphiRated Power factor for this type of Storage
   * @param pMin Minimum permissible active power
   * @param pMax Maximum permissible active power
   * @param eta Efficiency of the charging and discharging process
   * @param dod Minimum permissible depth of discharge
   * @param lifeTime Maximum life time of the storage
   * @param lifeCycle Maximum amount of full charging cycles
   */
  public StorageTypeInput(
      UUID uuid,
      String id,
      Quantity<Currency> capex,
      Quantity<EnergyPrice> opex,
      Quantity<Energy> eStorage,
      Quantity<Power> sRated,
      double cosphiRated,
      Quantity<Power> pMin,
      Quantity<Power> pMax,
      Quantity<Dimensionless> eta,
      Quantity<Dimensionless> dod,
      Quantity<Time> lifeTime,
      int lifeCycle) {
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosphiRated);
    this.eStorage = eStorage.to(StandardUnits.ENERGY_IN);
    this.pMin = pMin.to(StandardUnits.ACTIVE_POWER_IN);
    this.pMax = pMax.to(StandardUnits.ACTIVE_POWER_IN);
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

  public Quantity<Power> getpMin() {
    return pMin;
  }

  public Quantity<Power> getpMax() {
    return pMax;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    StorageTypeInput that = (StorageTypeInput) o;
    return lifeCycle == that.lifeCycle
        && eStorage.equals(that.eStorage)
        && pMin.equals(that.pMin)
        && pMax.equals(that.pMax)
        && eta.equals(that.eta)
        && dod.equals(that.dod)
        && lifeTime.equals(that.lifeTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), eStorage, pMin, pMax, eta, dod, lifeTime, lifeCycle);
  }
}
