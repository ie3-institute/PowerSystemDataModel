/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

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
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.WecInput} */
public class WecTypeInput extends SystemParticipantTypeInput {
  /** Betz curve of this type */
  private final WecCharacteristicInput cpCharacteristic;
  /** Efficiency of converter for this type of WEC (typically in %) */
  private final ComparableQuantity<Dimensionless> etaConv;
  /** Swept Area of blades for this type of WEC (typically in m²) */
  private final ComparableQuantity<Area> rotorArea;
  /** Height from ground to center of rotor for this type of WEC (typically in m) */
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
  public WecTypeInputCopyBuilder copy() {
    return new WecTypeInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof WecTypeInput that)) return false;
    if (!super.equals(o)) return false;
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
        + ", id="
        + getId()
        + "capex="
        + getCapex()
        + ", opex="
        + getOpex()
        + ", sRated="
        + getsRated()
        + ", cosphiRated="
        + getCosPhiRated()
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

  /**
   * A builder pattern based approach to create copies of {@link WecTypeInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link WecTypeInput}
   */
  public static class WecTypeInputCopyBuilder
      extends SystemParticipantTypeInputCopyBuilder<WecTypeInput.WecTypeInputCopyBuilder> {

    private WecCharacteristicInput cpCharacteristic;
    private ComparableQuantity<Dimensionless> etaConv;
    private ComparableQuantity<Area> rotorArea;
    private ComparableQuantity<Length> hubHeight;

    private WecTypeInputCopyBuilder(WecTypeInput entity) {
      super(entity);
      this.cpCharacteristic = entity.getCpCharacteristic();
      this.etaConv = entity.getEtaConv();
      this.rotorArea = entity.getRotorArea();
      this.hubHeight = entity.getHubHeight();
    }

    public WecTypeInputCopyBuilder setCpCharacteristic(WecCharacteristicInput cpCharacteristic) {
      this.cpCharacteristic = cpCharacteristic;
      return this;
    }

    public WecTypeInputCopyBuilder setEtaConv(ComparableQuantity<Dimensionless> etaConv) {
      this.etaConv = etaConv;
      return this;
    }

    public WecTypeInputCopyBuilder setRotorArea(ComparableQuantity<Area> rotorArea) {
      this.rotorArea = rotorArea;
      return this;
    }

    public WecTypeInputCopyBuilder setHubHeight(ComparableQuantity<Length> hubHeight) {
      this.hubHeight = hubHeight;
      return this;
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
    public WecTypeInput.WecTypeInputCopyBuilder scale(Double factor) {
      setCapex(getCapex().multiply(factor));
      setsRated(getsRated().multiply(factor));
      setRotorArea(getRotorArea().multiply(factor));
      return this;
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
          hubHeight);
    }

    @Override
    protected WecTypeInput.WecTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
