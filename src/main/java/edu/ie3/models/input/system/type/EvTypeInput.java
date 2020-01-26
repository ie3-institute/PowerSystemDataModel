/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system.type;

import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import edu.ie3.util.quantities.interfaces.SpecificEnergy;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

/** Describes the type of a {@link edu.ie3.models.input.system.EvInput} */
public class EvTypeInput extends SystemParticipantTypeInput {
  /** Energy capacity of the storage (typically in kWh) */
  Quantity<Energy> eStorage;
  /** Consumed electric energy per driven distance (typically in kWh/km) */
  Quantity<SpecificEnergy> eCons;
  /** Rated apparent power for this type of EV (typically in kW) */
  Quantity<Power> sRated;

  /**
   * @param uuid of the input entity
   * @param id of this type of EV
   * @param capex Captial expense for this type of EV (typically in €)
   * @param opex Operating expense for this type of EV (typically in €)
   * @param cosphi Power factor for this type of EV
   * @param eStorage Energy capacity of the storage
   * @param eCons Consumed electric energy per driven distance
   * @param sRated Rated apparent power for this type of EV (typically in kW)
   */
  public EvTypeInput(
      UUID uuid,
      String id,
      Quantity<Currency> capex,
      Quantity<EnergyPrice> opex,
      double cosphi,
      Quantity<Energy> eStorage,
      Quantity<SpecificEnergy> eCons,
      Quantity<Power> sRated) {
    super(uuid, id, capex, opex, cosphi);
    this.eStorage = eStorage;
    this.eCons = eCons;
    this.sRated = sRated;
  }

  public Quantity<Energy> getEStorage() {
    return eStorage;
  }

  public void setEStorage(Quantity<Energy> eStorage) {
    this.eStorage = eStorage;
  }

  public Quantity<SpecificEnergy> getECons() {
    return eCons;
  }

  public void setECons(Quantity<SpecificEnergy> eCons) {
    this.eCons = eCons;
  }

  public Quantity<Power> getSRated() {
    return sRated;
  }

  public void setSRated(Quantity<Power> sRated) {
    this.sRated = sRated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    EvTypeInput that = (EvTypeInput) o;
    return eStorage.equals(that.eStorage) && eCons.equals(that.eCons) && sRated.equals(that.sRated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), eStorage, eCons, sRated);
  }
}
