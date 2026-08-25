/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Volume;
import tech.units.indriya.ComparableQuantity;

/** Thermal storage with cylindrical shape for domestic hot water. */
public class DomesticHotWaterStorageInput extends AbstractStorageInput {
  /**
   * Constructor for DomesticHotWaterStorageInput.
   *
   * @param uuid Unique identifier of a certain domestic hot water storage
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
  public DomesticHotWaterStorageInput(
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
    super(
        uuid,
        id,
        operator,
        operationTime,
        thermalBus,
        storageVolumeLvl,
        inletTemp,
        returnTemp,
        c,
        pThermalMax);
  }

  /**
   * Constructor for DomesticHotWaterStorageInput.
   *
   * @param uuid Unique identifier of a certain domestic hot water storage
   * @param id Identifier of the thermal unit
   * @param operator operator of the asset
   * @param operationTime operation time of the asset
   * @param thermalBus Thermal bus, a thermal unit is connected to
   * @param storageVolumeLvl Available storage volume
   * @param inletTemp Temperature of the inlet
   * @param returnTemp Temperature of the outlet
   * @param c Specific heat capacity of the storage medium
   * @param pThermalMax Maximum thermal power of the storage
   * @param additionalInformation That were provided by the source
   */
  public DomesticHotWaterStorageInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      ThermalBusInput thermalBus,
      ComparableQuantity<Volume> storageVolumeLvl,
      ComparableQuantity<Temperature> inletTemp,
      ComparableQuantity<Temperature> returnTemp,
      ComparableQuantity<SpecificHeatCapacity> c,
      ComparableQuantity<Power> pThermalMax,
      Map<String, String> additionalInformation) {
    super(
        uuid,
        id,
        operator,
        operationTime,
        thermalBus,
        storageVolumeLvl,
        inletTemp,
        returnTemp,
        c,
        pThermalMax);
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for DomesticHotWaterStorageInput.
   *
   * @param uuid Unique identifier of a certain domestic hot water storage
   * @param id Identifier of the thermal unit
   * @param thermalBus Thermal bus, a thermal unit is connected to
   * @param storageVolumeLvl Available storage volume
   * @param inletTemp Temperature of the inlet
   * @param returnTemp Temperature of the outlet
   * @param c Specific heat capacity of the storage medium
   * @param pThermalMax Maximum thermal power of the storage
   */
  public DomesticHotWaterStorageInput(
      UUID uuid,
      String id,
      ThermalBusInput thermalBus,
      ComparableQuantity<Volume> storageVolumeLvl,
      ComparableQuantity<Temperature> inletTemp,
      ComparableQuantity<Temperature> returnTemp,
      ComparableQuantity<SpecificHeatCapacity> c,
      ComparableQuantity<Power> pThermalMax) {
    super(uuid, id, thermalBus, storageVolumeLvl, inletTemp, returnTemp, c, pThermalMax);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DomesticHotWaterStorageInput that)) return false;
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
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
        + ", thermalBus="
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
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public DomesticHotWaterStorageInputCopyBuilder copy() {
    return new DomesticHotWaterStorageInputCopyBuilder(this);
  }

  public static class DomesticHotWaterStorageInputCopyBuilder
      extends AbstractStorageInputCopyBuilder<DomesticHotWaterStorageInputCopyBuilder> {
    protected DomesticHotWaterStorageInputCopyBuilder(DomesticHotWaterStorageInput entity) {
      super(entity);
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
          getpThermalMax(),
          getAdditionalInformation());
    }

    @Override
    protected DomesticHotWaterStorageInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
