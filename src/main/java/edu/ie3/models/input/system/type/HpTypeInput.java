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
import javax.measure.quantity.Power;

/** Describes the type of a {@link edu.ie3.models.input.system.HpInput} */
public class HpTypeInput extends SystemParticipantTypeInput {
  /** Rated active power consumption (typically in kW) */
  private Quantity<Power> pRated; // TODO: Replace with sRated
  /** Thermal output of the heat pump (typically in kW) */
  private Quantity<Power> pThermal;
  /** Electric active power consumed to deliver {@code pThermal} (typically in kW) */
  private Quantity<Power> pEl;

  /**
   * @param uuid of the input entity
   * @param id of this type of HP
   * @param capex Captial expense for this type of HP (typically in €)
   * @param opex Operating expense for this type of HP (typically in €)
   * @param cosphi Power factor for this type of HP
   * @param pRated Rated active power consumption
   * @param pThermal Thermal output of the heat pump
   * @param pEl Electric active power consumed to deliver {@code pThermal}
   */
  public HpTypeInput(
      UUID uuid,
      String id,
      Quantity<Currency> capex,
      Quantity<EnergyPrice> opex,
      Double cosphi,
      Quantity<Power> pRated,
      Quantity<Power> pThermal,
      Quantity<Power> pEl) {
    super(uuid, id, capex, opex, cosphi);
    this.pRated = pRated.to(StandardUnits.ACTIVE_POWER_IN);
    this.pThermal = pThermal.to(StandardUnits.ACTIVE_POWER_IN);
    this.pEl = pEl.to(StandardUnits.ACTIVE_POWER_IN);
  }

  public Quantity<Power> getPRated() {
    return pRated;
  }

  public void setPRated(Quantity<Power> pRated) {
    this.pRated = pRated.to(StandardUnits.ACTIVE_POWER_IN);
  }

  public Quantity<Power> getPThermal() {
    return pThermal;
  }

  public void setPThermal(Quantity<Power> pThermal) {
    this.pThermal = pThermal.to(StandardUnits.ACTIVE_POWER_IN);
  }

  public Quantity<Power> getPEl() {
    return pEl;
  }

  public void setPEl(Quantity<Power> pEl) {
    this.pEl = pEl.to(StandardUnits.ACTIVE_POWER_IN);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    HpTypeInput that = (HpTypeInput) o;
    return pRated.equals(that.pRated) && pThermal.equals(that.pThermal) && pEl.equals(that.pEl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), pRated, pThermal, pEl);
  }
}
