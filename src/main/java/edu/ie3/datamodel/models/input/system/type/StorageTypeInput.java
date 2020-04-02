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
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import javax.measure.quantity.Time;
import tec.uom.se.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.StorageInput} */
public class StorageTypeInput extends SystemParticipantTypeInput {
  /** Energy capacity (typically in kWh) */
  private final ComparableQuantity<Energy> eStorage; // TODO #65 Quantity replaced
  /** Maximum permissible active power (typically in kW) */
  private final ComparableQuantity<Power> pMax; // TODO #65 Quantity replaced
  /** Maximum permissible gradient of active power change (typically % / h) */
  private final ComparableQuantity<DimensionlessRate>
      activePowerGradient; // TODO #65 Quantity replaced
  /** Efficiency of the charging and discharging process (typically in %) */
  private final ComparableQuantity<Dimensionless> eta; // TODO #65 Quantity replaced
  /** Minimum permissible depth of discharge (typically in %) */
  private final ComparableQuantity<Dimensionless> dod; // TODO #65 Quantity replaced
  /** Maximum life time of the storage (typically in ms) */
  private final ComparableQuantity<Time> lifeTime; // TODO #65 Quantity replaced
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
      ComparableQuantity<Currency> capex, // TODO #65 Quantity replaced
      ComparableQuantity<EnergyPrice> opex, // TODO #65 Quantity replaced
      ComparableQuantity<Energy> eStorage, // TODO #65 Quantity replaced
      ComparableQuantity<Power> sRated, // TODO #65 Quantity replaced
      double cosPhiRated,
      ComparableQuantity<Power> pMax, // TODO #65 Quantity replaced
      ComparableQuantity<DimensionlessRate> activePowerGradient, // TODO #65 Quantity replaced
      ComparableQuantity<Dimensionless> eta, // TODO #65 Quantity replaced
      ComparableQuantity<Dimensionless> dod, // TODO #65 Quantity replaced
      ComparableQuantity<Time> lifeTime, // TODO #65 Quantity replaced
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

  public ComparableQuantity<Dimensionless> getEta() {
    return eta;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<Dimensionless> getDod() {
    return dod;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<Time> getLifeTime() {
    return lifeTime;
  } // TODO #65 Quantity replaced

  public int getLifeCycle() {
    return lifeCycle;
  }

  public ComparableQuantity<Energy> geteStorage() {
    return eStorage;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<Power> getpMax() {
    return pMax;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<DimensionlessRate> getActivePowerGradient() {
    return activePowerGradient;
  } // TODO #65 Quantity replaced

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
