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

/** Thermal storage with cylindrical shape. */
public class CylindricalStorageInput extends AbstractStorageInput {
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
  public CylindricalStorageInput(
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
   * @param additionalInformation That were provided by the source
   */
  public CylindricalStorageInput(
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
   * @param uuid Unique identifier of a cylindrical storage
   * @param id Identifier of the thermal unit
   * @param thermalBus Thermal bus, a thermal unit is connected to
   * @param storageVolumeLvl Available storage volume
   * @param inletTemp Temperature of the inlet
   * @param returnTemp Temperature of the outlet
   * @param c Specific heat capacity of the storage medium
   * @param pThermalMax Maximum thermal power of the storage
   */
  public CylindricalStorageInput(
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
    if (!(o instanceof CylindricalStorageInput that)) return false;
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
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
  public CylindricalStorageInputCopyBuilder copy() {
    return new CylindricalStorageInputCopyBuilder(this);
  }

  public static class CylindricalStorageInputCopyBuilder
      extends AbstractStorageInputCopyBuilder<CylindricalStorageInputCopyBuilder> {
    protected CylindricalStorageInputCopyBuilder(CylindricalStorageInput entity) {
      super(entity);
    }

    @Override
    public CylindricalStorageInput build() {
      return new CylindricalStorageInput(
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
    protected CylindricalStorageInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
