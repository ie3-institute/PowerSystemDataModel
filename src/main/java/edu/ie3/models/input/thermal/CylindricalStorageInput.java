/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.thermal;

import edu.ie3.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Volume;

/** Thermal storage with cylindrical shape */
public class CylindricalStorageInput extends ThermalStorageInput {
  /** Available storage volume (typically in m³) */
  private Quantity<Volume> storageVolumeLvl;
  /** Minimum permissible storage volume (typically in m³) */
  private Quantity<Volume> storageVolumeLvlMin;
  /** Temperature of the inlet (typically in C) */
  private Quantity<Temperature> inletTemp;
  /** Temperature of the outlet (typically in C) */
  private Quantity<Temperature> returnTemp;
  /** Specific heat capacity of the storage medium (typically in kWh/K*m³) */
  private Quantity<SpecificHeatCapacity> c;

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
  CylindricalStorageInput(
      UUID uuid,
      String id,
      ThermalBusInput bus,
      Quantity<Volume> storageVolumeLvl,
      Quantity<Volume> storageVolumeLvlMin,
      Quantity<Temperature> inletTemp,
      Quantity<Temperature> returnTemp,
      Quantity<SpecificHeatCapacity> c) {
    super(uuid, id, bus);
    this.storageVolumeLvl = storageVolumeLvl.to(StandardUnits.VOLUME);
    this.storageVolumeLvlMin = storageVolumeLvlMin.to(StandardUnits.VOLUME);
    this.inletTemp = inletTemp.to(StandardUnits.TEMPERATURE);
    this.returnTemp = returnTemp.to(StandardUnits.TEMPERATURE);
    this.c = c.to(StandardUnits.SPECIFIC_HEAT_CAPACITY);
  }

  public Quantity<Volume> getStorageVolumeLvl() {
    return storageVolumeLvl;
  }

  public void setStorageVolumeLvl(Quantity<Volume> storageVolumeLvl) {
    this.storageVolumeLvl = storageVolumeLvl.to(StandardUnits.VOLUME);
  }

  public Quantity<Volume> getStorageVolumeLvlMin() {
    return storageVolumeLvlMin;
  }

  public void setStorageVolumeLvlMin(Quantity<Volume> storageVolumeLvlMin) {
    this.storageVolumeLvlMin = storageVolumeLvlMin.to(StandardUnits.VOLUME);
  }

  public Quantity<Temperature> getInletTemp() {
    return inletTemp;
  }

  public void setInletTemp(Quantity<Temperature> inletTemp) {
    this.inletTemp = inletTemp.to(StandardUnits.TEMPERATURE);
  }

  public Quantity<Temperature> getReturnTemp() {
    return returnTemp;
  }

  public void setReturnTemp(Quantity<Temperature> returnTemp) {
    this.returnTemp = returnTemp.to(StandardUnits.TEMPERATURE);
  }

  public Quantity<SpecificHeatCapacity> getC() {
    return c;
  }

  public void setC(Quantity<SpecificHeatCapacity> c) {
    this.c = c.to(StandardUnits.SPECIFIC_HEAT_CAPACITY);
  }

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
}
