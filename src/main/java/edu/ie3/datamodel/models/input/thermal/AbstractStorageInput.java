/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.io.source.SourceValidator;
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
  /* Static fields. */
  public static final String STORAGE_VOLUME_LVL = "storageVolumeLvl";
  public static final String INLET_TEMP = "inletTemp";
  public static final String RETURN_TEMP = "returnTemp";
  public static final String C = "c";
  public static final String P_THERMAL_MAX = "pThermalMax";

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

  public AbstractStorageInput(
      ThermalUnitInput thermalUnitInput,
      ComparableQuantity<Volume> storageVolumeLvl,
      ComparableQuantity<Temperature> inletTemp,
      ComparableQuantity<Temperature> returnTemp,
      ComparableQuantity<SpecificHeatCapacity> c,
      ComparableQuantity<Power> pThermalMax) {
    super(thermalUnitInput);
    this.storageVolumeLvl = storageVolumeLvl.to(StandardUnits.VOLUME);
    this.inletTemp = inletTemp.to(StandardUnits.TEMPERATURE);
    this.returnTemp = returnTemp.to(StandardUnits.TEMPERATURE);
    this.c = c.to(StandardUnits.SPECIFIC_HEAT_CAPACITY);
    this.pThermalMax = pThermalMax.to(StandardUnits.ACTIVE_POWER_IN);
  }

  public AbstractStorageInput(AbstractStorageInput input) {
    super(input);
    this.storageVolumeLvl = input.storageVolumeLvl;
    this.inletTemp = input.inletTemp;
    this.returnTemp = input.returnTemp;
    this.c = input.c;
    this.pThermalMax = input.pThermalMax;
  }

  public static SourceValidator.Fields abstractThermalStorageFields() {
    return assetFields().add(STORAGE_VOLUME_LVL, INLET_TEMP, RETURN_TEMP, C, P_THERMAL_MAX);
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
   * AbstractStorageInput}
   */
  protected abstract static class AbstractStorageInputCopyBuilder<
          B extends AbstractStorageInputCopyBuilder<B>>
      extends ThermalStorageInputCopyBuilder<B> {

    private ComparableQuantity<Volume> storageVolumeLvl;
    private ComparableQuantity<Temperature> inletTemp;
    private ComparableQuantity<Temperature> returnTemp;
    private ComparableQuantity<SpecificHeatCapacity> c;
    private ComparableQuantity<Power> pThermalMax;

    protected AbstractStorageInputCopyBuilder(AbstractStorageInput entity) {
      super(entity);

      this.storageVolumeLvl = entity.getStorageVolumeLvl();
      this.inletTemp = entity.getInletTemp();
      this.returnTemp = entity.getReturnTemp();
      this.c = entity.getC();
      this.pThermalMax = entity.getpThermalMax();
    }

    public B storageVolumeLvl(ComparableQuantity<Volume> storageVolumeLvl) {
      this.storageVolumeLvl = storageVolumeLvl;
      return thisInstance();
    }

    public B inletTemp(ComparableQuantity<Temperature> inletTemp) {
      this.inletTemp = inletTemp;
      return thisInstance();
    }

    public B returnTemp(ComparableQuantity<Temperature> returnTemp) {
      this.returnTemp = returnTemp;
      return thisInstance();
    }

    public B c(ComparableQuantity<SpecificHeatCapacity> c) {
      this.c = c;
      return thisInstance();
    }

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
  }
}
