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
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.ChpInput} */
public class ChpTypeInput extends SystemParticipantTypeInput {
  /** Electrical efficiency (typically in %) */
  private final ComparableQuantity<Dimensionless> etaEl; // TODO doublecheck
  /** Thermal efficiency (typically in %) */
  private final ComparableQuantity<Dimensionless> etaThermal; // TODO doublecheck
  /** Rated thermal power (typically in kW) */
  private final ComparableQuantity<Power> pThermal; // TODO doublecheck
  /** Internal consumption (typically in kW) */
  private final ComparableQuantity<Power> pOwn; // TODO doublecheck

  /**
   * @param uuid of the input entity
   * @param id of this type of CHP
   * @param capex Capital expense for this type of CHP (typically in €)
   * @param opex Operating expense for this type of CHP (typically in €)
   * @param etaEl Electrical efficiency
   * @param etaThermal Thermal efficiency
   * @param sRated Rated electrical apparent power
   * @param cosphiRated Power factor for this type of CHP
   * @param pThermal Rated thermal power
   * @param pOwn Internal consumption
   */
  public ChpTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex, // TODO doublecheck - no return value, but superclass expects comparable
      ComparableQuantity<EnergyPrice> opex, // TODO doublecheck - no return value, but superclass expects comparable
      ComparableQuantity<Dimensionless> etaEl, // TODO doublecheck
      ComparableQuantity<Dimensionless> etaThermal, // TODO doublecheck
      ComparableQuantity<Power> sRated, // TODO doublecheck - no return value, but superclass expects comparable
      double cosphiRated,
      ComparableQuantity<Power> pThermal, // TODO doublecheck
      ComparableQuantity<Power> pOwn) { // TODO doublecheck
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosphiRated);
    this.etaEl = etaEl.to(StandardUnits.EFFICIENCY);
    this.etaThermal = etaThermal.to(StandardUnits.EFFICIENCY);
    this.pThermal = pThermal.to(StandardUnits.ACTIVE_POWER_IN);
    this.pOwn = pOwn.to(StandardUnits.ACTIVE_POWER_IN);
  }

  public ComparableQuantity<Dimensionless> getEtaEl() {
    return etaEl;
  } // TODO doublecheck

  public ComparableQuantity<Dimensionless> getEtaThermal() {
    return etaThermal;
  } // TODO doublecheck

  public ComparableQuantity<Power> getpThermal() {
    return pThermal;
  } // TODO doublecheck

  public ComparableQuantity<Power> getpOwn() {
    return pOwn;
  } // TODO doublecheck

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ChpTypeInput that = (ChpTypeInput) o;
    return etaEl.equals(that.etaEl)
        && etaThermal.equals(that.etaThermal)
        && pThermal.equals(that.pThermal)
        && pOwn.equals(that.pOwn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), etaEl, etaThermal, pThermal, pOwn);
  }

  @Override
  public String toString() {
    return "ChpTypeInput{"
        + "etaEl="
        + etaEl
        + ", etaThermal="
        + etaThermal
        + ", pThermal="
        + pThermal
        + ", pOwn="
        + pOwn
        + '}';
  }
}
