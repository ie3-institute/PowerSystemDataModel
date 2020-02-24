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
import javax.measure.quantity.Area;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;

/** Describes the type of a {@link edu.ie3.models.input.system.WecInput} */
public class WecTypeInput extends SystemParticipantTypeInput {
  /** Efficiency of converter for this type of WEC (typically in %) */
  private Quantity<Dimensionless> etaConv;
  /** Rated apparent power for this type of WEC (typically in kVA) */
  private Quantity<Power> sRated;
  /** Swept Area of blades for this type of WEC (typically in m²) */
  private Quantity<Area> rotorArea;
  /** Height from ground to center of rotor for this type of WEC (typically in m) */
  private Quantity<Length> hubHeight;

  /**
   * @param uuid of the input entity
   * @param id of this type of WEC
   * @param capex Captial expense for this type of WEC (typically in €)
   * @param opex Operating expense for this type of WEC (typically in €)
   * @param cosphi Power factor for this type of WEC
   * @param etaConv Efficiency of converter for this type of WEC (typically in %)
   * @param sRated Rated apparent power for this type of WEC (typically in kVA)
   * @param rotorArea Swept Area of blades for this type of WEC (typically in m²)
   * @param hubHeight Height from ground to center of rotor for this type of WEC (typically in m)
   */
  public WecTypeInput(
      UUID uuid,
      String id,
      Quantity<Currency> capex,
      Quantity<EnergyPrice> opex,
      double cosphi,
      Quantity<Dimensionless> etaConv,
      Quantity<Power> sRated,
      Quantity<Area> rotorArea,
      Quantity<Length> hubHeight) {
    super(uuid, id, capex, opex, cosphi);
    this.etaConv = etaConv.to(StandardUnits.EFFICIENCY);
    this.sRated = sRated.to(StandardUnits.S_RATED);
    this.rotorArea = rotorArea.to(StandardUnits.ROTOR_AREA);
    this.hubHeight = hubHeight.to(StandardUnits.HUB_HEIGHT);
  }

  public Quantity<Dimensionless> getEtaConv() {
    return etaConv;
  }

  public void setEtaConv(Quantity<Dimensionless> etaConv) {
    this.etaConv = etaConv.to(StandardUnits.EFFICIENCY);
  }

  public Quantity<Power> getSRated() {
    return sRated;
  }

  public void setSRated(Quantity<Power> sRated) {
    this.sRated = sRated.to(StandardUnits.S_RATED);
  }

  public Quantity<Area> getRotorArea() {
    return rotorArea;
  }

  public void setRotorArea(Quantity<Area> rotorArea) {
    this.rotorArea = rotorArea.to(StandardUnits.ROTOR_AREA);
  }

  public Quantity<Length> getHubHeight() {
    return hubHeight;
  }

  public void setHubHeight(Quantity<Length> hubHeight) {
    this.hubHeight = hubHeight.to(StandardUnits.HUB_HEIGHT);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    WecTypeInput that = (WecTypeInput) o;
    return etaConv.equals(that.etaConv)
        && sRated.equals(that.sRated)
        && rotorArea.equals(that.rotorArea)
        && hubHeight.equals(that.hubHeight);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), etaConv, sRated, rotorArea, hubHeight);
  }
}
