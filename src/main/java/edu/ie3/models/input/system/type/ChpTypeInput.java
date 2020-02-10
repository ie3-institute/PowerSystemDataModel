/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system.type;

import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.thermal.ThermalStorageInput;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;

/** Describes the type of a {@link edu.ie3.models.input.system.ChpInput} */
public class ChpTypeInput extends SystemParticipantTypeInput {
  /** Electrical efficiency (typically in %) */
  private Quantity<Dimensionless> etaEl;
  /** Thermal efficiency (typically in %) */
  private Quantity<Dimensionless> etaThermal;
  /** Rated electrical active power (typically in kW) */
  private Quantity<Power> pEl;
  /** Rated thermal power (typically in kW) */
  private Quantity<Power> pThermal;
  /** Internal consumption (typically in kW) */
  private Quantity<Power> pOwn;
  /* Thermal storage model */
  private ThermalStorageInput thermalStorage;

  /**
   * @param uuid of the input entity
   * @param id of this type of CHP
   * @param capex Capital expense for this type of CHP (typically in €)
   * @param opex Operating expense for this type of CHP (typically in €)
   * @param cosphi Power factor for this type of CHP
   * @param etaEl Electrical efficiency
   * @param etaThermal Thermal efficiency
   * @param sRated Rated electrical apparent power
   * @param pThermal Rated thermal power
   * @param pOwn Internal consumption
   * @param thermalStorage Thermal storage model
   */
  public ChpTypeInput(
      UUID uuid,
      String id,
      Quantity<Currency> capex,
      Quantity<EnergyPrice> opex,
      double cosphi,
      Quantity<Dimensionless> etaEl,
      Quantity<Dimensionless> etaThermal,
      Quantity<Power> sRated,
      Quantity<Power> pThermal,
      Quantity<Power> pOwn,
      ThermalStorageInput thermalStorage) {
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosphi);
    this.etaEl = etaEl.to(StandardUnits.EFFICIENCY);
    this.etaThermal = etaThermal.to(StandardUnits.EFFICIENCY);
    this.pEl = pEl.to(StandardUnits.ACTIVE_POWER_IN);
    this.pThermal = pThermal.to(StandardUnits.ACTIVE_POWER_IN);
    this.pOwn = pOwn.to(StandardUnits.ACTIVE_POWER_IN);
    this.thermalStorage = thermalStorage;
  }

  public Quantity<Dimensionless> getEtaEl() {
    return etaEl;
  }

  public void setEtaEl(Quantity<Dimensionless> etaEl) {
    this.etaEl = etaEl.to(StandardUnits.EFFICIENCY);
  }

  public Quantity<Dimensionless> getEtaThermal() {
    return etaThermal;
  }

  public void setEtaThermal(Quantity<Dimensionless> etaThermal) {
    this.etaThermal = etaThermal.to(StandardUnits.EFFICIENCY);
  }

  public Quantity<Power> getPEl() {
    return pEl;
  }

  public void setPEl(Quantity<Power> pEl) {
    this.pEl = pEl.to(StandardUnits.ACTIVE_POWER_IN);
  }

  public Quantity<Power> getPThermal() {
    return pThermal;
  }

  public void setPThermal(Quantity<Power> pThermal) {
    this.pThermal = pThermal.to(StandardUnits.ACTIVE_POWER_IN);
  }

  public Quantity<Power> getPOwn() {
    return pOwn;
  }

  public void setPOwn(Quantity<Power> pOwn) {
    this.pOwn = pOwn.to(StandardUnits.ACTIVE_POWER_IN);
  }

  public ThermalStorageInput getThermalStorage() {
    return thermalStorage;
  }

  public void setThermalStorage(ThermalStorageInput thermalStorage) {
    this.thermalStorage = thermalStorage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ChpTypeInput that = (ChpTypeInput) o;
    return etaEl.equals(that.etaEl)
        && etaThermal.equals(that.etaThermal)
        && pEl.equals(that.pEl)
        && pThermal.equals(that.pThermal)
        && pOwn.equals(that.pOwn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), etaEl, etaThermal, pEl, pThermal, pOwn);
  }
}
