/*
 * © 2020. TU Dortmund University,
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
import tec.uom.se.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.EvInput} */
public class EvTypeInput extends SystemParticipantTypeInput {
  /** Energy capacity of the storage (typically in kWh) */
  private final ComparableQuantity<Energy> eStorage; // TODO doublecheck
  /** Consumed electric energy per driven distance (typically in kWh/km) */
  private final ComparableQuantity<SpecificEnergy> eCons; // TODO doublecheck

  /**
   * @param uuid of the input entity
   * @param id of this type of EV
   * @param capex Capital expense for this type of EV (typically in €)
   * @param opex Operating expense for this type of EV (typically in €)
   * @param eStorage Energy capacity of the storage
   * @param eCons Consumed electric energy per driven distance
   * @param sRated Rated apparent power for this type of EV (typically in kW)
   * @param cosphiRated Power factor for this type of EV
   */
  public EvTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency>
          capex, // TODO doublecheck - no return value, but superclass expects comparable
      ComparableQuantity<EnergyPrice>
          opex, // TODO doublecheck - no return value, but superclass expects comparable
      ComparableQuantity<Energy> eStorage, // TODO doublecheck
      ComparableQuantity<SpecificEnergy> eCons, // TODO doublecheck
      ComparableQuantity<Power>
          sRated, // TODO doublecheck - no return value, but superclass expects comparable
      double cosphiRated) {
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosphiRated);
    this.eStorage = eStorage;
    this.eCons = eCons;
  }

  public ComparableQuantity<Energy> geteStorage() {
    return eStorage;
  } // TODO doublecheck

  public ComparableQuantity<SpecificEnergy> geteCons() {
    return eCons;
  } // TODO doublecheck

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    EvTypeInput that = (EvTypeInput) o;
    return eStorage.equals(that.eStorage) && eCons.equals(that.eCons);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), eStorage, eCons);
  }

  @Override
  public String toString() {
    return "EvTypeInput{" + "eStorage=" + eStorage + ", eCons=" + eCons + '}';
  }
}
