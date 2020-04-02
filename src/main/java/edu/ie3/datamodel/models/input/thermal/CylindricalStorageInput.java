/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Volume;
import tec.uom.se.ComparableQuantity;

/** Thermal storage with cylindrical shape */
public class CylindricalStorageInput extends ThermalStorageInput {
  /** Available storage volume (typically in m³) */
  private final ComparableQuantity<Volume> storageVolumeLvl; // TODO #65 Quantity replaced
  /** Minimum permissible storage volume (typically in m³) */
  private final ComparableQuantity<Volume> storageVolumeLvlMin; // TODO #65 Quantity replaced
  /** Temperature of the inlet (typically in C) */
  private final ComparableQuantity<Temperature> inletTemp; // TODO #65 Quantity replaced
  /** Temperature of the outlet (typically in C) */
  private final ComparableQuantity<Temperature> returnTemp; // TODO #65 Quantity replaced
  /** Specific heat capacity of the storage medium (typically in kWh/K*m³) */
  private final ComparableQuantity<SpecificHeatCapacity> c; // TODO #65 Quantity replaced

  /**
   * @param uuid Unique identifier of a cylindrical storage
   * @param id Identifier of the thermal unit
   * @param bus Thermal bus, a thermal unit is connected to
   * @param storageVolumeLvl Available storage volume
   * @param storageVolumeLvlMin Minimum permissible storage volume
   * @param inletTemp Temperature of the inlet
   * @param returnTemp Temperature of the outlet
   * @param c Specific heat capacity of the storage medium
   */
  public CylindricalStorageInput(
      UUID uuid,
      String id,
      ThermalBusInput bus,
      ComparableQuantity<Volume> storageVolumeLvl, // TODO #65 Quantity replaced
      ComparableQuantity<Volume> storageVolumeLvlMin, // TODO #65 Quantity replaced
      ComparableQuantity<Temperature> inletTemp, // TODO #65 Quantity replaced
      ComparableQuantity<Temperature> returnTemp, // TODO #65 Quantity replaced
      ComparableQuantity<SpecificHeatCapacity> c) { // TODO #65 Quantity replaced
    super(uuid, id, bus);
    this.storageVolumeLvl = storageVolumeLvl.to(StandardUnits.VOLUME);
    this.storageVolumeLvlMin = storageVolumeLvlMin.to(StandardUnits.VOLUME);
    this.inletTemp = inletTemp.to(StandardUnits.TEMPERATURE);
    this.returnTemp = returnTemp.to(StandardUnits.TEMPERATURE);
    this.c = c.to(StandardUnits.SPECIFIC_HEAT_CAPACITY);
  }

  public ComparableQuantity<Volume> getStorageVolumeLvl() {
    return storageVolumeLvl;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<Volume> getStorageVolumeLvlMin() {
    return storageVolumeLvlMin;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<Temperature> getInletTemp() {
    return inletTemp;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<Temperature> getReturnTemp() {
    return returnTemp;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<SpecificHeatCapacity> getC() {
    return c;
  } // TODO #65 Quantity replaced

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CylindricalStorageInput that = (CylindricalStorageInput) o;
    return storageVolumeLvl.equals(that.storageVolumeLvl)
        && storageVolumeLvlMin.equals(that.storageVolumeLvlMin)
        && inletTemp.equals(that.inletTemp)
        && returnTemp.equals(that.returnTemp)
        && c.equals(that.c);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), storageVolumeLvl, storageVolumeLvlMin, inletTemp, returnTemp, c);
  }

  @Override
  public String toString() {
    return "CylindricalStorageInput{"
        + "storageVolumeLvl="
        + storageVolumeLvl
        + ", storageVolumeLvlMin="
        + storageVolumeLvlMin
        + ", inletTemp="
        + inletTemp
        + ", returnTemp="
        + returnTemp
        + ", c="
        + c
        + '}';
  }
}
