/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.utils.QuantityUtils;
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Volume;
import tech.units.indriya.ComparableQuantity;

/** Thermal storage with cylindrical shape. */
public abstract class AbstractStorageInput extends ThermalStorageInput {
  /** Available storage volume (typically in m³). */
  private final ComparableQuantity<Volume> storageVolumeLvl;

  /** Temperature of the inlet (typically in C). */
  private final ComparableQuantity<Temperature> inletTemp;

  /** Temperature of the outlet (typically in C). */
  private final ComparableQuantity<Temperature> returnTemp;

  /** Specific heat capacity of the storage medium (typically in kWh/K*m³). */
  private final ComparableQuantity<SpecificHeatCapacity> c;

  /** Maximum permissible thermal power (typically in kW). */
  private final ComparableQuantity<Power> pThermalMax;

  /**
   * @param uuid Unique identifier of a cylindrical storage
   * @param id Identifier of the thermal unit
   * @param operator operator of the asset
   * @param operationTime operation time of the asset
   * @param thermalBus Thermal bus, a thermal unit is connected to
   * @param storageVolumeLvl Available storage volume
   * @param inletTemp Temperature of the inlet
   * @param returnTemp Temperature of the outlet
   * @param c Specific heat capacity of the storage medium
   * @param pThermalMax Maximum thermal power of the storage
   */
  protected AbstractStorageInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      ThermalBusInput thermalBus,
      ComparableQuantity<Volume> storageVolumeLvl,
      ComparableQuantity<Temperature> inletTemp,
      ComparableQuantity<Temperature> returnTemp,
      ComparableQuantity<SpecificHeatCapacity> c,
      ComparableQuantity<Power> pThermalMax) {
    super(uuid, id, operator, operationTime, thermalBus);
    this.storageVolumeLvl = storageVolumeLvl;
    this.inletTemp = inletTemp;
    this.returnTemp = returnTemp;
    this.c = c;
    this.pThermalMax = pThermalMax;
  }

  /**
   * @param uuid Unique identifier of a cylindrical storage
   * @param id Identifier of the thermal unit
   * @param thermalBus Thermal bus, a thermal unit is connected to
   * @param storageVolumeLvl Available storage volume
   * @param inletTemp Temperature of the inlet
   * @param returnTemp Temperature of the outlet
   * @param c Specific heat capacity of the storage medium
   * @param pThermalMax Maximum thermal power of the storage
   */
  protected AbstractStorageInput(
      UUID uuid,
      String id,
      ThermalBusInput thermalBus,
      ComparableQuantity<Volume> storageVolumeLvl,
      ComparableQuantity<Temperature> inletTemp,
      ComparableQuantity<Temperature> returnTemp,
      ComparableQuantity<SpecificHeatCapacity> c,
      ComparableQuantity<Power> pThermalMax) {
    super(uuid, id, thermalBus);
    this.storageVolumeLvl = storageVolumeLvl;
    this.inletTemp = inletTemp;
    this.returnTemp = returnTemp;
    this.c = c;
    this.pThermalMax = pThermalMax;
  }

  public ComparableQuantity<Volume> getStorageVolumeLvl() {
    return storageVolumeLvl;
  }

  public ComparableQuantity<Temperature> getInletTemp() {
    return inletTemp;
  }

  public ComparableQuantity<Temperature> getReturnTemp() {
    return returnTemp;
  }

  public ComparableQuantity<SpecificHeatCapacity> getC() {
    return c;
  }

  public ComparableQuantity<Power> getpThermalMax() {
    return pThermalMax;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AbstractStorageInput that)) return false;
    if (!super.equals(o)) return false;
    return QuantityUtils.equals(storageVolumeLvl, that.storageVolumeLvl)
        && QuantityUtils.equals(inletTemp, that.inletTemp)
        && QuantityUtils.equals(returnTemp, that.returnTemp)
        && QuantityUtils.equals(c, that.c)
        && QuantityUtils.equals(pThermalMax, that.pThermalMax);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), storageVolumeLvl, inletTemp, returnTemp, c, pThermalMax);
  }

  @Override
  public String toString() {
    return "AbstractStorageInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", thermalBus="
        + getThermalBus().getUuid()
        + ", storageVolumeLvl="
        + storageVolumeLvl
        + ", inletTemp="
        + inletTemp
        + ", returnTemp="
        + returnTemp
        + ", c="
        + c
        + ", pThermalMax="
        + pThermalMax
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public abstract AbstractStorageInputCopyBuilder<?> copy();

  public abstract static class AbstractStorageInputCopyBuilder<
          B extends AbstractStorageInputCopyBuilder<B>>
      extends ThermalStorageInputCopyBuilder<B> {
    private ComparableQuantity<Volume> storageVolumeLvl;

    private ComparableQuantity<Temperature> inletTemp;

    private ComparableQuantity<Temperature> returnTemp;

    private ComparableQuantity<SpecificHeatCapacity> c;

    private ComparableQuantity<Power> pThermalMax;

    protected AbstractStorageInputCopyBuilder(AbstractStorageInput entity) {
      super(entity);
      this.storageVolumeLvl = entity.storageVolumeLvl;
      this.inletTemp = entity.inletTemp;
      this.returnTemp = entity.returnTemp;
      this.c = entity.c;
      this.pThermalMax = entity.pThermalMax;
    }

    public B storageVolumeLvl(ComparableQuantity<Volume> storageVolumeLvl) {
      this.storageVolumeLvl = storageVolumeLvl;
      return thisInstance();
    }

    protected ComparableQuantity<Volume> getStorageVolumeLvl() {
      return storageVolumeLvl;
    }

    public B inletTemp(ComparableQuantity<Temperature> inletTemp) {
      this.inletTemp = inletTemp;
      return thisInstance();
    }

    protected ComparableQuantity<Temperature> getInletTemp() {
      return inletTemp;
    }

    public B returnTemp(ComparableQuantity<Temperature> returnTemp) {
      this.returnTemp = returnTemp;
      return thisInstance();
    }

    protected ComparableQuantity<Temperature> getReturnTemp() {
      return returnTemp;
    }

    public B c(ComparableQuantity<SpecificHeatCapacity> c) {
      this.c = c;
      return thisInstance();
    }

    protected ComparableQuantity<SpecificHeatCapacity> getC() {
      return c;
    }

    public B pThermalMax(ComparableQuantity<Power> pThermalMax) {
      this.pThermalMax = pThermalMax;
      return thisInstance();
    }

    protected ComparableQuantity<Power> getpThermalMax() {
      return pThermalMax;
    }

    @Override
    public B scale(double factor) {
      storageVolumeLvl(storageVolumeLvl.multiply(factor));
      pThermalMax(pThermalMax.multiply(factor));
      return thisInstance();
    }

    @Override
    public abstract AbstractStorageInput build();

    @Override
    protected abstract B thisInstance();
  }
}
