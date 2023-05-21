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
import edu.ie3.util.quantities.interfaces.VolumetricFlowRate;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Volume;
import tech.units.indriya.ComparableQuantity;

/** Thermal storage with cylindrical shape */
public class CylindricalStorageInput extends ThermalStorageInput {
  /** Available storage volume (typically in m³) */
  private final ComparableQuantity<Volume> storageVolumeLvl;
  /** Minimum permissible storage volume (typically in m³) */
  private final ComparableQuantity<Volume> storageVolumeLvlMin;
  /** Temperature of the inlet (typically in C) */
  private final ComparableQuantity<Temperature> inletTemp;
  /** Temperature of the outlet (typically in C) */
  private final ComparableQuantity<Temperature> returnTemp;
  /** Specific heat capacity of the storage medium (typically in kWh/K*m³) */
  private final ComparableQuantity<SpecificHeatCapacity> c;
  /** Flow rate of the inlet and outlet (typically in m³/s) */
  private final ComparableQuantity<VolumetricFlowRate> inletFlowRate;
  /** Flow rate of the outlet and outlet (typically in m³/s) */
  private final ComparableQuantity<VolumetricFlowRate> outletFlowRate;

  /**
   * @param uuid Unique identifier of a cylindrical storage
   * @param id Identifier of the thermal unit
   * @param operator operator of the asset
   * @param operationTime operation time of the asset
   * @param bus Thermal bus, a thermal unit is connected to
   * @param storageVolumeLvl Available storage volume
   * @param storageVolumeLvlMin Minimum permissible storage volume
   * @param inletTemp Temperature of the inlet
   * @param returnTemp Temperature of the outlet
   * @param c Specific heat capacity of the storage medium
   * @param inletFlowRate flow rate of the inlet
   * @param outletFlowRate flow rate of the outlet
   */
  public CylindricalStorageInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      ThermalBusInput bus,
      ComparableQuantity<Volume> storageVolumeLvl,
      ComparableQuantity<Volume> storageVolumeLvlMin,
      ComparableQuantity<Temperature> inletTemp,
      ComparableQuantity<Temperature> returnTemp,
      ComparableQuantity<SpecificHeatCapacity> c,
      ComparableQuantity<VolumetricFlowRate> inletFlowRate,
      ComparableQuantity<VolumetricFlowRate> outletFlowRate) {
    super(uuid, id, operator, operationTime, bus);
    this.storageVolumeLvl = storageVolumeLvl.to(StandardUnits.VOLUME);
    this.storageVolumeLvlMin = storageVolumeLvlMin.to(StandardUnits.VOLUME);
    this.inletTemp = inletTemp.to(StandardUnits.TEMPERATURE);
    this.returnTemp = returnTemp.to(StandardUnits.TEMPERATURE);
    this.c = c.to(StandardUnits.SPECIFIC_HEAT_CAPACITY);
    this.inletFlowRate = inletFlowRate.to(StandardUnits.VOLUMETRIC_FLOW_RATE);
    this.outletFlowRate = outletFlowRate.to(StandardUnits.VOLUMETRIC_FLOW_RATE);
  }

  /**
   * @param uuid Unique identifier of a cylindrical storage
   * @param id Identifier of the thermal unit
   * @param bus Thermal bus, a thermal unit is connected to
   * @param storageVolumeLvl Available storage volume
   * @param storageVolumeLvlMin Minimum permissible storage volume
   * @param inletTemp Temperature of the inlet
   * @param returnTemp Temperature of the outlet
   * @param c Specific heat capacity of the storage medium
   * @param inletFlowRate flow rate of the inlet
   * @param outletFlowRate flow rate of the outlet
   */
  public CylindricalStorageInput(
      UUID uuid,
      String id,
      ThermalBusInput bus,
      ComparableQuantity<Volume> storageVolumeLvl,
      ComparableQuantity<Volume> storageVolumeLvlMin,
      ComparableQuantity<Temperature> inletTemp,
      ComparableQuantity<Temperature> returnTemp,
      ComparableQuantity<SpecificHeatCapacity> c,
      ComparableQuantity<VolumetricFlowRate> inletFlowRate,
      ComparableQuantity<VolumetricFlowRate> outletFlowRate) {
    super(uuid, id, bus);
    this.storageVolumeLvl = storageVolumeLvl.to(StandardUnits.VOLUME);
    this.storageVolumeLvlMin = storageVolumeLvlMin.to(StandardUnits.VOLUME);
    this.inletTemp = inletTemp.to(StandardUnits.TEMPERATURE);
    this.returnTemp = returnTemp.to(StandardUnits.TEMPERATURE);
    this.c = c.to(StandardUnits.SPECIFIC_HEAT_CAPACITY);
    this.inletFlowRate = inletFlowRate.to(StandardUnits.VOLUMETRIC_FLOW_RATE);
    this.outletFlowRate = outletFlowRate.to(StandardUnits.VOLUMETRIC_FLOW_RATE);
  }

  public ComparableQuantity<Volume> getStorageVolumeLvl() {
    return storageVolumeLvl;
  }

  public ComparableQuantity<Volume> getStorageVolumeLvlMin() {
    return storageVolumeLvlMin;
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

  public ComparableQuantity<VolumetricFlowRate> getInletFlowRate() {
    return inletFlowRate;
  }

  public ComparableQuantity<VolumetricFlowRate> getOutletFlowRate() {
    return outletFlowRate;
  }

  @Override
  public CylindricalStorageInputCopyBuilder copy() {
    return new CylindricalStorageInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CylindricalStorageInput that)) return false;
    if (!super.equals(o)) return false;
    return storageVolumeLvl.equals(that.storageVolumeLvl)
        && storageVolumeLvlMin.equals(that.storageVolumeLvlMin)
        && inletTemp.equals(that.inletTemp)
        && returnTemp.equals(that.returnTemp)
        && c.equals(that.c)
        && inletFlowRate.equals(that.inletFlowRate)
        && outletFlowRate.equals(that.outletFlowRate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        storageVolumeLvl,
        storageVolumeLvlMin,
        inletTemp,
        returnTemp,
        c,
        inletFlowRate,
        outletFlowRate);
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
        + ", storageVolumeLvlMin="
        + storageVolumeLvlMin
        + ", inletTemp="
        + inletTemp
        + ", returnTemp="
        + returnTemp
        + ", c="
        + c
        + ", inletFlowRate ="
        + inletFlowRate
        + ", outletFlowPower ="
        + outletFlowRate
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link CylindricalStorageInput} entities
   * with altered field values. For detailed field descriptions refer to java docs of {@link
   * CylindricalStorageInput}
   */
  public static class CylindricalStorageInputCopyBuilder
      extends ThermalUnitInput.ThermalUnitInputCopyBuilder<CylindricalStorageInputCopyBuilder> {

    private ComparableQuantity<Volume> storageVolumeLvl;
    private ComparableQuantity<Volume> storageVolumeLvlMin;
    private ComparableQuantity<Temperature> inletTemp;
    private ComparableQuantity<Temperature> returnTemp;
    private ComparableQuantity<SpecificHeatCapacity> c;
    private ComparableQuantity<VolumetricFlowRate> inletFlowRate;
    private ComparableQuantity<VolumetricFlowRate> outletFlowRate;

    private CylindricalStorageInputCopyBuilder(CylindricalStorageInput entity) {
      super(entity);
      this.storageVolumeLvl = entity.getStorageVolumeLvl();
      this.storageVolumeLvlMin = entity.getStorageVolumeLvlMin();
      this.inletTemp = entity.getInletTemp();
      this.returnTemp = entity.getReturnTemp();
      this.c = entity.getC();
      this.inletFlowRate = entity.getInletFlowRate();
      this.outletFlowRate = entity.getOutletFlowRate();
    }

    @Override
    public CylindricalStorageInput build() {
      return new CylindricalStorageInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getThermalBus(),
          storageVolumeLvl,
          storageVolumeLvlMin,
          inletTemp,
          returnTemp,
          c,
          inletFlowRate,
          outletFlowRate);
    }

    public CylindricalStorageInputCopyBuilder storageVolumeLvl(
        ComparableQuantity<Volume> storageVolumeLvl) {
      this.storageVolumeLvl = storageVolumeLvl;
      return this;
    }

    public CylindricalStorageInputCopyBuilder storageVolumeLvlMin(
        ComparableQuantity<Volume> storageVolumeLvlMin) {
      this.storageVolumeLvlMin = storageVolumeLvlMin;
      return this;
    }

    public CylindricalStorageInputCopyBuilder inletTemp(ComparableQuantity<Temperature> inletTemp) {
      this.inletTemp = inletTemp;
      return this;
    }

    public CylindricalStorageInputCopyBuilder returnTemp(
        ComparableQuantity<Temperature> returnTemp) {
      this.returnTemp = returnTemp;
      return this;
    }

    public CylindricalStorageInputCopyBuilder c(ComparableQuantity<SpecificHeatCapacity> c) {
      this.c = c;
      return this;
    }

    public CylindricalStorageInputCopyBuilder inletFlowRate(
        ComparableQuantity<VolumetricFlowRate> inletFlowRate) {
      this.inletFlowRate = inletFlowRate;
      return this;
    }

    public CylindricalStorageInputCopyBuilder outletFlowRate(
        ComparableQuantity<VolumetricFlowRate> outletFlowRate) {
      this.outletFlowRate = outletFlowRate;
      return this;
    }

    @Override
    protected CylindricalStorageInputCopyBuilder childInstance() {
      return this;
    }
  }
}
