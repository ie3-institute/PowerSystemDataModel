/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Volume;
import tech.units.indriya.ComparableQuantity;

/** Thermal storage with cylindrical shape */
public abstract class AbstractStorageInput extends ThermalStorageInput {
  /** Available storage volume (typically in m³) */
  private final ComparableQuantity<Volume> storageVolumeLvl;

  /** Temperature of the inlet (typically in C) */
  private final ComparableQuantity<Temperature> inletTemp;

  /** Temperature of the outlet (typically in C) */
  private final ComparableQuantity<Temperature> returnTemp;

  /** Specific heat capacity of the storage medium (typically in kWh/K*m³) */
  private final ComparableQuantity<SpecificHeatCapacity> c;

  /** Maximum permissible thermal power (typically in kW) */
  private final ComparableQuantity<Power> pThermalMax;

  /**
   * Abstract constructor for a cylindrical storage input with specified parameters.
   *
   * @param uuid Unique identifier of a cylindrical storage
   * @param id Identifier of the thermal unit
   * @param operator operator of the asset
   * @param operationTime operation time of the asset
   * @param bus Thermal bus, a thermal unit is connected to
   * @param storageVolumeLvl Available storage volume
   * @param inletTemp Temperature of the inlet
   * @param returnTemp Temperature of the outlet
   * @param c Specific heat capacity of the storage medium
   * @param pThermalMax Maximum thermal power of the storage
   */
  public AbstractStorageInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      ThermalBusInput bus,
      ComparableQuantity<Volume> storageVolumeLvl,
      ComparableQuantity<Temperature> inletTemp,
      ComparableQuantity<Temperature> returnTemp,
      ComparableQuantity<SpecificHeatCapacity> c,
      ComparableQuantity<Power> pThermalMax) {
    super(uuid, id, operator, operationTime, bus);
    this.storageVolumeLvl = storageVolumeLvl.to(StandardUnits.VOLUME);
    this.inletTemp = inletTemp.to(StandardUnits.TEMPERATURE);
    this.returnTemp = returnTemp.to(StandardUnits.TEMPERATURE);
    this.c = c.to(StandardUnits.SPECIFIC_HEAT_CAPACITY);
    this.pThermalMax = pThermalMax.to(StandardUnits.ACTIVE_POWER_IN);
  }

  /**
   * Constructor for a cylindrical storage input without an operator or operation time.
   *
   * @param uuid Unique identifier of a cylindrical storage
   * @param id Identifier of the thermal unit
   * @param bus Thermal bus, a thermal unit is connected to
   * @param storageVolumeLvl Available storage volume
   * @param inletTemp Temperature of the inlet
   * @param returnTemp Temperature of the outlet
   * @param c Specific heat capacity of the storage medium
   * @param pThermalMax Maximum thermal power of the storage
   */
  public AbstractStorageInput(
      UUID uuid,
      String id,
      ThermalBusInput bus,
      ComparableQuantity<Volume> storageVolumeLvl,
      ComparableQuantity<Temperature> inletTemp,
      ComparableQuantity<Temperature> returnTemp,
      ComparableQuantity<SpecificHeatCapacity> c,
      ComparableQuantity<Power> pThermalMax) {
    super(uuid, id, bus);
    this.storageVolumeLvl = storageVolumeLvl.to(StandardUnits.VOLUME);
    this.inletTemp = inletTemp.to(StandardUnits.TEMPERATURE);
    this.returnTemp = returnTemp.to(StandardUnits.TEMPERATURE);
    this.c = c.to(StandardUnits.SPECIFIC_HEAT_CAPACITY);
    this.pThermalMax = pThermalMax.to(StandardUnits.ACTIVE_POWER_IN);
  }

  /**
   * Returns available storage volume.
   *
   * @return The available volume as a {@link ComparableQuantity} in cubic meters.
   */
  public ComparableQuantity<Volume> getStorageVolumeLvl() {
    return storageVolumeLvl;
  }

  /**
   * Returns temperature at which fluid enters.
   *
   * @return The temperature at which fluid enters as a {@link ComparableQuantity} in Celsius.
   */
  public ComparableQuantity<Temperature> getInletTemp() {
    return inletTemp;
  }

  /**
   * Returns temperature at which fluid exits.
   *
   * @return The temperature at which fluid exits as a {@link ComparableQuantity} in Celsius.
   */
  public ComparableQuantity<Temperature> getReturnTemp() {
    return returnTemp;
  }

  /**
   * Returns specific heat capacity.
   *
   * @return The specific heat capacity as a {@link ComparableQuantity} typically measured in
   *     kWh/K*m³.
   */
  public ComparableQuantity<SpecificHeatCapacity> getC() {
    return c;
  }

  /**
   * Returns maximum permissible thermal power.
   *
   * @return The maximum permissible thermal power as a {@link ComparableQuantity} typically
   *     measured in kW.
   */
  public ComparableQuantity<Power> getpThermalMax() {
    return pThermalMax;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AbstractStorageInput that)) return false;
    if (!super.equals(o)) return false;
    return storageVolumeLvl.equals(that.storageVolumeLvl)
        && inletTemp.equals(that.inletTemp)
        && returnTemp.equals(that.returnTemp)
        && c.equals(that.c)
        && pThermalMax.equals(that.getpThermalMax());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), storageVolumeLvl, inletTemp, returnTemp, c, pThermalMax);
  }

  @Override
  public String toString() {
    return "CylindricalStorageInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", bus="
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
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link AbstractStorageInput} entities with
   * altered field values. For detailed field descriptions refer to java docs of {@link
   * AbstractStorageInput}*
   *
   * @param <B> The type of the builder extending from {@link AbstractStorageInputCopyBuilder}.
   */
  protected abstract static class AbstractStorageInputCopyBuilder<
          B extends AbstractStorageInputCopyBuilder<B>>
      extends ThermalStorageInputCopyBuilder<B> {

    private ComparableQuantity<Volume> storageVolumeLvl;

    /** Temperature of the inlet (typically in °C) */
    private ComparableQuantity<Temperature> inletTemp;

    /** Temperature of the outlet (typically in °C) */
    private ComparableQuantity<Temperature> returnTemp;

    /** Specific heat capacity of the storage medium (typically in kWh/K*m³) */
    private ComparableQuantity<SpecificHeatCapacity> c;

    /** Maximum permissible thermal power (typically in kW) */
    private ComparableQuantity<Power> pThermalMax;

    /**
     * A builder pattern based approach to create copies of {@link AbstractStorageInput} entities
     * with altered field values. For detailed field descriptions refer to java docs for {@link
     * AbstractStorageInput}*
     *
     * @param entity the entity that will be copied.
     */
    protected AbstractStorageInputCopyBuilder(AbstractStorageInput entity) {
      super(entity);

      this.storageVolumeLvl = entity.getStorageVolumeLvl();
      this.inletTemp = entity.getInletTemp();
      this.returnTemp = entity.getReturnTemp();
      this.c = entity.getC();
      this.pThermalMax = entity.getpThermalMax();
    }

    /**
     * Sets the available storage volume level for this storage input.
     *
     * @param storageVolumeLvl The available storage volume as a {@link ComparableQuantity} of type
     *     {@link Volume}.
     * @return This builder instance for method chaining.
     */
    public B storageVolumeLvl(ComparableQuantity<Volume> storageVolumeLvl) {
      this.storageVolumeLvl = storageVolumeLvl;
      return thisInstance();
    }

    /**
     * Sets the temperature at which fluid enters for this storage input.
     *
     * @param inletTemp The inlet temperature as a {@link ComparableQuantity} of type {@link
     *     Temperature}.
     * @return This builder instance for method chaining.
     */
    public B inletTemp(ComparableQuantity<Temperature> inletTemp) {
      this.inletTemp = inletTemp;
      return thisInstance();
    }

    /**
     * Sets the temperature at which fluid exits for this storage input.
     *
     * @param returnTemp The return temperature as a {@link ComparableQuantity} of type {@link
     *     Temperature}.
     * @return This builder instance for method chaining.
     */
    public B returnTemp(ComparableQuantity<Temperature> returnTemp) {
      this.returnTemp = returnTemp;
      return thisInstance();
    }

    /**
     * Sets the specific heat capacity of the fluid for this storage input.
     *
     * @param c The specific heat capacity as a {@link ComparableQuantity} of type
     *     SpecificHeatCapacity.
     * @return This builder instance for method chaining.
     */
    public B c(ComparableQuantity<SpecificHeatCapacity> c) {
      this.c = c;
      return thisInstance();
    }

    /**
     * Sets the thermal power that this this storage input is capable.
     *
     * @param pThermalMax The maximum thermal power as a {@link ComparableQuantity} of type {@link
     *     Power}.
     * @return This builder instance for method chaining.
     */
    public B pThermalMax(ComparableQuantity<Power> pThermalMax) {
      this.pThermalMax = pThermalMax;
      return thisInstance();
    }

    @Override
    public B scale(Double factor) {
      storageVolumeLvl(storageVolumeLvl.multiply(factor));
      pThermalMax(pThermalMax.multiply(factor));
      return thisInstance();
    }

    /**
     * Returns available storage volume.
     *
     * @return The available volume as a {@link ComparableQuantity} in cubic meters.
     */
    public ComparableQuantity<Volume> getStorageVolumeLvl() {
      return storageVolumeLvl;
    }

    /**
     * Returns temperature at which fluid enters.
     *
     * @return The temperature at which fluid enters as a {@link ComparableQuantity} in Celsius.
     */
    public ComparableQuantity<Temperature> getInletTemp() {
      return inletTemp;
    }

    /**
     * Returns temperature at which fluid exits.
     *
     * @return The temperature at which fluid exits as a {@link ComparableQuantity} in Celsius.
     */
    public ComparableQuantity<Temperature> getReturnTemp() {
      return returnTemp;
    }

    /**
     * Returns specific heat capacity.
     *
     * @return The specific heat capacity as a {@link ComparableQuantity} typically measured in
     *     kWh/K*m³.
     */
    public ComparableQuantity<SpecificHeatCapacity> getC() {
      return c;
    }

    /**
     * Returns maximum permissible thermal power.
     *
     * @return The maximum permissible thermal power as a {@link ComparableQuantity} typically
     *     measured in kW.
     */
    public ComparableQuantity<Power> getpThermalMax() {
      return pThermalMax;
    }
  }
}
