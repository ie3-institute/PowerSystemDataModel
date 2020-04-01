/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import tec.uom.se.ComparableQuantity;

import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Power;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.HpInput} */
public class HpTypeInput extends SystemParticipantTypeInput {
  /** Thermal output of the heat pump (typically in kW), when sRated * cosphi_rated is consumed */
  private final ComparableQuantity<Power> pThermal; // TODO doublecheck

  /**
   * @param uuid of the input entity
   * @param id of this type of HP
   * @param capex Captial expense for this type of HP (typically in €)
   * @param opex Operating expense for this type of HP (typically in €)
   * @param cosphiRated Power factor for this type of HP
   * @param sRated Rated apparent power
   * @param pThermal Thermal output of the heat pump, when sRated * cosphi_rated is consumed
   *     electrically
   */
  public HpTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex, // TODO doublecheck - no return value, but superclass expects comparable
      ComparableQuantity<EnergyPrice> opex, // TODO doublecheck - no return value, but superclass expects comparable
      ComparableQuantity<Power> sRated, // TODO doublecheck - no return value, but superclass expects comparable
      double cosphiRated,
      ComparableQuantity<Power> pThermal) { // TODO doublecheck
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosphiRated);
    this.pThermal = pThermal.to(StandardUnits.ACTIVE_POWER_IN);
  }

  public ComparableQuantity<Power> getpThermal() { // TODO doublecheck
    return pThermal;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    HpTypeInput that = (HpTypeInput) o;
    return pThermal.equals(that.pThermal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), pThermal);
  }

  @Override
  public String toString() {
    return "HpTypeInput{" + "pThermal=" + pThermal + '}';
  }
}
