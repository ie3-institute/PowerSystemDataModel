/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

import edu.ie3.datamodel.annotations.FieldName;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput;
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
  /** Betz curve of this type */
  @FieldName("cp_characteristic")
  private final WecCharacteristicInput cpCharacteristic;
  /** Efficiency of converter for this type of WEC (typically in %) */
  @FieldName("eta_conv")
  private final ComparableQuantity<Dimensionless> etaConv;
  /** Swept Area of blades for this type of WEC (typically in m²) */
  @FieldName("rotor_area")
  private final ComparableQuantity<Area> rotorArea;
  /** Height from ground to center of rotor for this type of WEC (typically in m) */
  @FieldName("hub_height")
  private final ComparableQuantity<Length> hubHeight;

  /**
   * @param uuid of the input entity
   * @param id of this type of WEC
   * @param capex Captial expense for this type of WEC (typically in €)
   * @param opex Operating expense for this type of WEC (typically in €)
   * @param cosphiRated Power factor for this type of WEC
   * @param cpCharacteristic Betz curve of this type
   * @param etaConv Efficiency of converter for this type of WEC (typically in %)
   * @param sRated Rated apparent power for this type of WEC (typically in kVA)
   * @param rotorArea Swept Area of blades for this type of WEC (typically in m²)
   * @param hubHeight Height from ground to center of rotor for this type of WEC (typically in m)
   */
  public WecTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex,
      ComparableQuantity<EnergyPrice> opex,
      ComparableQuantity<Power> sRated,
      double cosphiRated,
      WecCharacteristicInput cpCharacteristic,
      ComparableQuantity<Dimensionless> etaConv,
      ComparableQuantity<Area> rotorArea,
      ComparableQuantity<Length> hubHeight) {
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosphiRated);
    this.cpCharacteristic = cpCharacteristic;
    this.etaConv = etaConv.to(StandardUnits.EFFICIENCY);
    this.rotorArea = rotorArea.to(StandardUnits.ROTOR_AREA);
    this.hubHeight = hubHeight.to(StandardUnits.HUB_HEIGHT);
  }

  public WecCharacteristicInput getCpCharacteristic() {
    return cpCharacteristic;
  }

  public ComparableQuantity<Dimensionless> getEtaConv() {
    return etaConv;
  }

  public ComparableQuantity<Area> getRotorArea() {
    return rotorArea;
  }

  public ComparableQuantity<Length> getHubHeight() {
    return hubHeight;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    WecTypeInput that = (WecTypeInput) o;
    return cpCharacteristic.equals(that.cpCharacteristic)
        && etaConv.equals(that.etaConv)
        && rotorArea.equals(that.rotorArea)
        && hubHeight.equals(that.hubHeight);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), cpCharacteristic, etaConv, rotorArea, hubHeight);
  }

  @Override
  public String toString() {
    return "WecTypeInput{"
        + "uuid="
        + getUuid()
        + ", cpCharacteristic="
        + cpCharacteristic
        + ", etaConv="
        + etaConv
        + ", rotorArea="
        + rotorArea
        + ", hubHeight="
        + hubHeight
        + '}';
  }
}
