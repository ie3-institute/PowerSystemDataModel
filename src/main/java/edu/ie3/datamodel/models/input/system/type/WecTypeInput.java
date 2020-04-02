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
import javax.measure.quantity.Area;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import tec.uom.se.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.WecInput} */
public class WecTypeInput extends SystemParticipantTypeInput {
  /** Efficiency of converter for this type of WEC (typically in %) */
  private final ComparableQuantity<Dimensionless> etaConv; // TODO #65 Quantity replaced
  /** Swept Area of blades for this type of WEC (typically in m²) */
  private final ComparableQuantity<Area> rotorArea; // TODO #65 Quantity replaced
  /** Height from ground to center of rotor for this type of WEC (typically in m) */
  private final ComparableQuantity<Length> hubHeight; // TODO #65 Quantity replaced

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
      ComparableQuantity<Currency> capex, // TODO #65 Quantity replaced
      ComparableQuantity<EnergyPrice> opex, // TODO #65 Quantity replaced
      double cosphi,
      ComparableQuantity<Dimensionless> etaConv, // TODO #65 Quantity replaced
      ComparableQuantity<Power> sRated, // TODO #65 Quantity replaced
      ComparableQuantity<Area> rotorArea, // TODO #65 Quantity replaced
      ComparableQuantity<Length> hubHeight) { // TODO #65 Quantity replaced
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosphi);
    this.etaConv = etaConv.to(StandardUnits.EFFICIENCY);
    this.rotorArea = rotorArea.to(StandardUnits.ROTOR_AREA);
    this.hubHeight = hubHeight.to(StandardUnits.HUB_HEIGHT);
  }

  public ComparableQuantity<Dimensionless> getEtaConv() {
    return etaConv;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<Area> getRotorArea() {
    return rotorArea;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<Length> getHubHeight() {
    return hubHeight;
  } // TODO #65 Quantity replaced

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

  @Override
  public String toString() {
    return "WecTypeInput{"
        + "etaConv="
        + etaConv
        + ", rotorArea="
        + rotorArea
        + ", hubHeight="
        + hubHeight
        + '}';
  }
}
