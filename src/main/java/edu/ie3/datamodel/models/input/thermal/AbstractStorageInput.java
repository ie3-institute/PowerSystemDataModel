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
   * Instantiates a new Abstract storage input.
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
   * Instantiates a new Abstract storage input.
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
   * Gets storage volume lvl.
   *
   * @return the storage volume lvl
   */
  public ComparableQuantity<Volume> getStorageVolumeLvl() {
    return storageVolumeLvl;
  }

  /**
   * Gets inlet temp.
   *
   * @return the inlet temp
   */
  public ComparableQuantity<Temperature> getInletTemp() {
    return inletTemp;
  }

  /**
   * Gets return temp.
   *
   * @return the return temp
   */
  public ComparableQuantity<Temperature> getReturnTemp() {
    return returnTemp;
  }

  /**
   * Gets c.
   *
   * @return the c
   */
  public ComparableQuantity<SpecificHeatCapacity> getC() {
    return c;
  }

  /**
   * Gets thermal max.
   *
   * @return the thermal max
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
   * @param <B> the type parameter
   */
  protected abstract static class AbstractStorageInputCopyBuilder<
          B extends AbstractStorageInputCopyBuilder<B>>
      extends ThermalStorageInputCopyBuilder<B> {

    private ComparableQuantity<Volume> storageVolumeLvl;
    private ComparableQuantity<Temperature> inletTemp;
    private ComparableQuantity<Temperature> returnTemp;
    private ComparableQuantity<SpecificHeatCapacity> c;
    private ComparableQuantity<Power> pThermalMax;

    /**
     * Instantiates a new Abstract storage input copy builder.
     *
     * @param entity the entity
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
     * Storage volume lvl b.
     *
     * @param storageVolumeLvl the storage volume lvl
     * @return the b
     */
    public B storageVolumeLvl(ComparableQuantity<Volume> storageVolumeLvl) {
      this.storageVolumeLvl = storageVolumeLvl;
      return thisInstance();
    }

    /**
     * Inlet temp b.
     *
     * @param inletTemp the inlet temp
     * @return the b
     */
    public B inletTemp(ComparableQuantity<Temperature> inletTemp) {
      this.inletTemp = inletTemp;
      return thisInstance();
    }

    /**
     * Return temp b.
     *
     * @param returnTemp the return temp
     * @return the b
     */
    public B returnTemp(ComparableQuantity<Temperature> returnTemp) {
      this.returnTemp = returnTemp;
      return thisInstance();
    }

    /**
     * C b.
     *
     * @param c the c
     * @return the b
     */
    public B c(ComparableQuantity<SpecificHeatCapacity> c) {
      this.c = c;
      return thisInstance();
    }

    /**
     * P thermal max b.
     *
     * @param pThermalMax the p thermal max
     * @return the b
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
     * Gets storage volume lvl.
     *
     * @return the storage volume lvl
     */
    public ComparableQuantity<Volume> getStorageVolumeLvl() {
      return storageVolumeLvl;
    }

    /**
     * Gets inlet temp.
     *
     * @return the inlet temp
     */
    public ComparableQuantity<Temperature> getInletTemp() {
      return inletTemp;
    }

    /**
     * Gets return temp.
     *
     * @return the return temp
     */
    public ComparableQuantity<Temperature> getReturnTemp() {
      return returnTemp;
    }

    /**
     * Gets c.
     *
     * @return the c
     */
    public ComparableQuantity<SpecificHeatCapacity> getC() {
      return c;
    }

    /**
     * Gets thermal max.
     *
     * @return the thermal max
     */
    public ComparableQuantity<Power> getpThermalMax() {
      return pThermalMax;
    }
  }
}
