/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput;
import edu.ie3.datamodel.utils.QuantityUtils;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Area;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.WecInput}. */
public class WecTypeInput extends SystemParticipantTypeInput {
  /** . */
  private final WecCharacteristicInput cpCharacteristic;

  /** . */
  private final ComparableQuantity<Dimensionless> etaConv;

  /** . */
  private final ComparableQuantity<Area> rotorArea;

  /** . */
  private final ComparableQuantity<Length> hubHeight;

  /**
   * @param uuid of the input entity
   * @param id of this type of WEC
   * @param capex Captial expense for this type of WEC (typically in €)
   * @param opex Operating expense for this type of WEC (typically in €)
   * @param sRated Rated apparent power for this type of WEC (typically in kVA)
   * @param cosPhiRated Power factor for this type of WEC
   * @param cpCharacteristic Betz curve of this type
   * @param etaConv Efficiency of converter for this type of WEC (typically in %)
   * @param rotorArea Swept Area of blades for this type of WEC (typically in m²)
   * @param hubHeight Height from ground to center of rotor for this type of WEC (typically in m)
   */
  public WecTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex,
      ComparableQuantity<EnergyPrice> opex,
      ComparableQuantity<Power> sRated,
      double cosPhiRated,
      WecCharacteristicInput cpCharacteristic,
      ComparableQuantity<Dimensionless> etaConv,
      ComparableQuantity<Area> rotorArea,
      ComparableQuantity<Length> hubHeight) {
    super(uuid, id, capex, opex, sRated, cosPhiRated);
    this.cpCharacteristic = cpCharacteristic;
    this.etaConv = etaConv;
    this.rotorArea = rotorArea;
    this.hubHeight = hubHeight;
  }

  /**
   * @param uuid of the input entity
   * @param id of this type of WEC
   * @param capex Captial expense for this type of WEC (typically in €)
   * @param opex Operating expense for this type of WEC (typically in €)
   * @param sRated Rated apparent power for this type of WEC (typically in kVA)
   * @param cosPhiRated Power factor for this type of WEC
   * @param cpCharacteristic Betz curve of this type
   * @param etaConv Efficiency of converter for this type of WEC (typically in %)
   * @param rotorArea Swept Area of blades for this type of WEC (typically in m²)
   * @param hubHeight Height from ground to center of rotor for this type of WEC (typically in m)
   * @param additionalInformation That were provided by the source
   */
  public WecTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex,
      ComparableQuantity<EnergyPrice> opex,
      ComparableQuantity<Power> sRated,
      double cosPhiRated,
      WecCharacteristicInput cpCharacteristic,
      ComparableQuantity<Dimensionless> etaConv,
      ComparableQuantity<Area> rotorArea,
      ComparableQuantity<Length> hubHeight,
      Map<String, String> additionalInformation) {
    super(uuid, id, capex, opex, sRated, cosPhiRated);
    this.cpCharacteristic = cpCharacteristic;
    this.etaConv = etaConv;
    this.rotorArea = rotorArea;
    this.hubHeight = hubHeight;
    setAdditionalInformation(additionalInformation);
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
    if (!(o instanceof WecTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(cpCharacteristic, that.cpCharacteristic)
        && QuantityUtils.equals(etaConv, that.etaConv)
        && QuantityUtils.equals(rotorArea, that.rotorArea)
        && QuantityUtils.equals(hubHeight, that.hubHeight);
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
        + ", id="
        + getId()
        + ", capex="
        + getCapex()
        + ", opex="
        + getOpex()
        + ", sRated="
        + getsRated()
        + ", cosPhiRated="
        + getCosPhiRated()
        + ", cpCharacteristic="
        + cpCharacteristic
        + ", etaConv="
        + etaConv
        + ", rotorArea="
        + rotorArea
        + ", hubHeight="
        + hubHeight
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public WecTypeInputCopyBuilder copy() {
    return new WecTypeInputCopyBuilder(this);
  }

  public static class WecTypeInputCopyBuilder
      extends SystemParticipantTypeInputCopyBuilder<WecTypeInputCopyBuilder> {
    private WecCharacteristicInput cpCharacteristic;

    private ComparableQuantity<Dimensionless> etaConv;

    private ComparableQuantity<Area> rotorArea;

    private ComparableQuantity<Length> hubHeight;

    protected WecTypeInputCopyBuilder(WecTypeInput entity) {
      super(entity);
      this.cpCharacteristic = entity.cpCharacteristic;
      this.etaConv = entity.etaConv;
      this.rotorArea = entity.rotorArea;
      this.hubHeight = entity.hubHeight;
    }

    public WecTypeInputCopyBuilder cpCharacteristic(WecCharacteristicInput cpCharacteristic) {
      this.cpCharacteristic = cpCharacteristic;
      return thisInstance();
    }

    protected WecCharacteristicInput getCpCharacteristic() {
      return cpCharacteristic;
    }

    public WecTypeInputCopyBuilder etaConv(ComparableQuantity<Dimensionless> etaConv) {
      this.etaConv = etaConv;
      return thisInstance();
    }

    protected ComparableQuantity<Dimensionless> getEtaConv() {
      return etaConv;
    }

    public WecTypeInputCopyBuilder rotorArea(ComparableQuantity<Area> rotorArea) {
      this.rotorArea = rotorArea;
      return thisInstance();
    }

    protected ComparableQuantity<Area> getRotorArea() {
      return rotorArea;
    }

    public WecTypeInputCopyBuilder hubHeight(ComparableQuantity<Length> hubHeight) {
      this.hubHeight = hubHeight;
      return thisInstance();
    }

    protected ComparableQuantity<Length> getHubHeight() {
      return hubHeight;
    }

    @Override
    public WecTypeInputCopyBuilder scale(double factor) {
      capex(getCapex().multiply(factor));
      sRated(getsRated().multiply(factor));
      rotorArea(getRotorArea().multiply(factor));
      return thisInstance();
    }

    @Override
    public WecTypeInput build() {
      return new WecTypeInput(
          getUuid(),
          getId(),
          getCapex(),
          getOpex(),
          getsRated(),
          getCosPhiRated(),
          cpCharacteristic,
          etaConv,
          rotorArea,
          hubHeight,
          getAdditionalInformation());
    }

    @Override
    protected WecTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
