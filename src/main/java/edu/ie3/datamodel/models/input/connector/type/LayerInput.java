/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.util.quantities.interfaces.ThermalCapacitance;
import edu.ie3.util.quantities.interfaces.ThermalResistivity;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import tech.units.indriya.ComparableQuantity;

/**
 * Represents a concentric layer of a cable, including insulation, filler, armor, and jacket layers.
 *
 * @param uuid UUID of this layer
 * @param name Name/designation of this layer
 * @param material Material of this layer
 * @param innerDiameter Inner diameter of this layer
 * @param outerDiameter Outer diameter of this layer
 * @param thermalResistivity Thermal resistivity of the material
 * @param thermalCapacitance Thermal capacitance of the material
 * @param area Optional real cross-sectional area (if different from geometry)
 */
public record LayerInput(
    UUID uuid,
    String name,
    CableMaterial material,
    ComparableQuantity<Length> innerDiameter,
    ComparableQuantity<Length> outerDiameter,
    ComparableQuantity<ThermalResistivity> thermalResistivity,
    ComparableQuantity<ThermalCapacitance> thermalCapacitance,
    Optional<ComparableQuantity<Area>> area)
    implements InputEntity, Serializable {
  /**
   * Create a new layer with all required parameters.
   *
   * @param name Designation of this layer
   * @param material Material of this layer
   * @param innerDiameter Inner diameter
   * @param outerDiameter Outer diameter
   * @param thermalResistivity Thermal resistivity
   * @param thermalCapacitance Thermal capacitance
   * @param area Optional real cross-sectional area
   * @throws IllegalArgumentException if validation constraints are violated
   */
  public LayerInput {}

  @Override
  public Map<String, String> getAdditionalInformation() {
    return Map.of();
  }
}
