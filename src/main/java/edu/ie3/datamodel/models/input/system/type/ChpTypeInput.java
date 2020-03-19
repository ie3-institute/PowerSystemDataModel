/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.ChpInput} */
public class ChpTypeInput extends SystemParticipantTypeInput {
  /** Electrical efficiency (typically in %) */
  private final Quantity<Dimensionless> etaEl;
  /** Thermal efficiency (typically in %) */
  private final Quantity<Dimensionless> etaThermal;
  /** Rated thermal power (typically in kW) */
  private final Quantity<Power> pThermal;
  /** Internal consumption (typically in kW) */
  private final Quantity<Power> pOwn;

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
      Quantity<Currency> capex,
      Quantity<EnergyPrice> opex,
      Quantity<Dimensionless> etaEl,
      Quantity<Dimensionless> etaThermal,
      Quantity<Power> sRated,
      double cosphiRated,
      Quantity<Power> pThermal,
      Quantity<Power> pOwn) {
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosphiRated);
    this.etaEl = etaEl.to(StandardUnits.EFFICIENCY);
    this.etaThermal = etaThermal.to(StandardUnits.EFFICIENCY);
    this.pThermal = pThermal.to(StandardUnits.ACTIVE_POWER_IN);
    this.pOwn = pOwn.to(StandardUnits.ACTIVE_POWER_IN);
  }

  public Quantity<Dimensionless> getEtaEl() {
    return etaEl;
  }

  public Quantity<Dimensionless> getEtaThermal() {
    return etaThermal;
  }

  public Quantity<Power> getpThermal() {
    return pThermal;
  }

  public Quantity<Power> getpOwn() {
    return pOwn;
  }

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
