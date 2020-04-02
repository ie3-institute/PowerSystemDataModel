/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.HeatCapacity;
import edu.ie3.util.quantities.interfaces.ThermalConductance;
import java.util.Objects;
import java.util.UUID;
import tec.uom.se.ComparableQuantity;

/** Quite simple thermal model of a house to serve as a heat sink */
public class ThermalHouseInput extends ThermalSinkInput {
  /** Thermal, transitional losses of the included thermal house model (typically in kW/K) */
  private final ComparableQuantity<ThermalConductance> ethLosses; // TODO doublecheck
  /** Thermal capacity of the included thermal house model (typically in kWh) */
  private final ComparableQuantity<HeatCapacity> ethCapa; // TODO doublecheck

  /**
   * @param uuid Unique identifier of a thermal house model
   * @param id Identifier of the model
   * @param bus Thermal bus, the model is connected to
   * @param ethLosses Thermal, transitional losses of the included thermal house model
   * @param ethCapa Thermal capacity of the included thermal house model
   */
  public ThermalHouseInput(
      UUID uuid,
      String id,
      ThermalBusInput bus,
      ComparableQuantity<ThermalConductance> ethLosses, // TODO doublecheck
      ComparableQuantity<HeatCapacity> ethCapa) { // TODO doublecheck
    super(uuid, id, bus);
    this.ethLosses = ethLosses.to(StandardUnits.THERMAL_TRANSMISSION);
    this.ethCapa = ethCapa.to(StandardUnits.HEAT_CAPACITY);
  }

  public ComparableQuantity<ThermalConductance> getEthLosses() {
    return ethLosses;
  } // TODO doublecheck

  public ComparableQuantity<HeatCapacity> getEthCapa() {
    return ethCapa;
  } // TODO doublecheck

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ThermalHouseInput that = (ThermalHouseInput) o;
    return ethLosses.equals(that.ethLosses) && ethCapa.equals(that.ethCapa);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), ethLosses, ethCapa);
  }

  @Override
  public String toString() {
    return "ThermalHouseInput{" + "ethLosses=" + ethLosses + ", ethCapa=" + ethCapa + '}';
  }
}
