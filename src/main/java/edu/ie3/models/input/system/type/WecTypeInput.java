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
  private final Quantity<Dimensionless> etaConv;
  /** Swept Area of blades for this type of WEC (typically in m²) */
  private final Quantity<Area> rotorArea;
  /** Height from ground to center of rotor for this type of WEC (typically in m) */
  private final Quantity<Length> hubHeight;

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
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosphi);
    this.etaConv = etaConv.to(StandardUnits.EFFICIENCY);
    this.rotorArea = rotorArea.to(StandardUnits.ROTOR_AREA);
    this.hubHeight = hubHeight.to(StandardUnits.HUB_HEIGHT);
  }

  public Quantity<Dimensionless> getEtaConv() {
    return etaConv;
  }

  public Quantity<Area> getRotorArea() {
    return rotorArea;
  }

  public Quantity<Length> getHubHeight() {
    return hubHeight;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    WecTypeInput that = (WecTypeInput) o;
    return etaConv.equals(that.etaConv)
        && rotorArea.equals(that.rotorArea)
        && hubHeight.equals(that.hubHeight);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), etaConv, rotorArea, hubHeight);
  }
}
