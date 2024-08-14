/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity;
import java.util.UUID;
import javax.measure.quantity.Power;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Volume;
import tech.units.indriya.ComparableQuantity;

public class DomesticHotWaterStorageInput extends CylindricalStorageInput {

  /**
   * Constructor for DomesticHotWaterStorageInput
   *
   * @param uuid Unique identifier of a domestic hot water storage
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
  public DomesticHotWaterStorageInput(
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
    super(
        uuid,
        id,
        operator,
        operationTime,
        bus,
        storageVolumeLvl,
        inletTemp,
        returnTemp,
        c,
        pThermalMax);
  }

  /**
   * Alternative constructor for DomesticHotWaterStorageInput
   *
   * @param uuid Unique identifier of a domestic hot water storage
   * @param id Identifier of the thermal unit
   * @param bus Thermal bus, a thermal unit is connected to
   * @param storageVolumeLvl Available storage volume
   * @param inletTemp Temperature of the inlet
   * @param returnTemp Temperature of the outlet
   * @param c Specific heat capacity of the storage medium
   * @param pThermalMax Maximum thermal power of the storage
   */
  public DomesticHotWaterStorageInput(
      UUID uuid,
      String id,
      ThermalBusInput bus,
      ComparableQuantity<Volume> storageVolumeLvl,
      ComparableQuantity<Temperature> inletTemp,
      ComparableQuantity<Temperature> returnTemp,
      ComparableQuantity<SpecificHeatCapacity> c,
      ComparableQuantity<Power> pThermalMax) {
    super(uuid, id, bus, storageVolumeLvl, inletTemp, returnTemp, c, pThermalMax);
  }

  @Override
  public DomesticHotWaterStorageInputCopyBuilder copy() {
    return new DomesticHotWaterStorageInputCopyBuilder(this);
  }

  @Override
  public String toString() {
    return "DomesticHotWaterStorageInput{"
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
        + getStorageVolumeLvl()
        + ", inletTemp="
        + getInletTemp()
        + ", returnTemp="
        + getReturnTemp()
        + ", c="
        + getC()
        + ", pThermalMax="
        + getpThermalMax()
        + '}';
  }

  public class DomesticHotWaterStorageInputCopyBuilder extends CylindricalStorageInputCopyBuilder {

    public DomesticHotWaterStorageInputCopyBuilder(DomesticHotWaterStorageInput entity) {
      super(entity);
    }

    public DomesticHotWaterStorageInputCopyBuilder storageVolumeLvl(
        ComparableQuantity<Volume> storageVolumeLvl) {
      this.storageVolumeLvl = storageVolumeLvl;
      return this;
    }

    public DomesticHotWaterStorageInputCopyBuilder inletTemp(
        ComparableQuantity<Temperature> inletTemp) {
      this.inletTemp = inletTemp;
      return this;
    }

    public DomesticHotWaterStorageInputCopyBuilder returnTemp(
        ComparableQuantity<Temperature> returnTemp) {
      this.returnTemp = returnTemp;
      return this;
    }

    public DomesticHotWaterStorageInputCopyBuilder c(ComparableQuantity<SpecificHeatCapacity> c) {
      this.c = c;
      return this;
    }

    public DomesticHotWaterStorageInputCopyBuilder pThermalMax(
        ComparableQuantity<Power> pThermalMax) {
      this.pThermalMax = pThermalMax;
      return this;
    }

    @Override
    public DomesticHotWaterStorageInputCopyBuilder scale(Double factor) {
      storageVolumeLvl(getStorageVolumeLvl().multiply(factor));
      pThermalMax(getpThermalMax().multiply(factor));
      return this;
    }

    @Override
    public DomesticHotWaterStorageInput build() {
      return new DomesticHotWaterStorageInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getThermalBus(),
          getStorageVolumeLvl(),
          getInletTemp(),
          getReturnTemp(),
          getC(),
          getpThermalMax());
    }

    @Override
    protected DomesticHotWaterStorageInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
