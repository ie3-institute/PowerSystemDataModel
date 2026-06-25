/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.util.quantities.interfaces.ThermalCapacitance;
import edu.ie3.util.quantities.interfaces.ThermalResistivity;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import org.jspecify.annotations.NonNull;
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
    implements InputEntity {
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
  public LayerInput {
    // Validation
    Objects.requireNonNull(uuid, "Layer UUID cannot be null");
    Objects.requireNonNull(name, "Layer name cannot be null");
    Objects.requireNonNull(material, "Layer material cannot be null");
    Objects.requireNonNull(innerDiameter, "Inner diameter cannot be null");
    Objects.requireNonNull(outerDiameter, "Outer diameter cannot be null");
    Objects.requireNonNull(thermalResistivity, "Thermal resistivity cannot be null");
    Objects.requireNonNull(thermalCapacitance, "Thermal capacitance cannot be null");
    Objects.requireNonNull(area, "Area Optional cannot be null");

    if (name.isEmpty()) {
      throw new IllegalArgumentException("Layer name cannot be empty");
    }

    // Geometry consistency: outerDiameter >= innerDiameter
    if (outerDiameter.getValue().doubleValue() < innerDiameter.getValue().doubleValue()) {
      throw new IllegalArgumentException(
          String.format(
              "Outer diameter (%.6f) must be >= inner diameter (%.6f)",
              outerDiameter.getValue().doubleValue(), innerDiameter.getValue().doubleValue()));
    }

    // Positive values check
    if (innerDiameter.getValue().doubleValue() < 0) {
      throw new IllegalArgumentException("Inner diameter must be >= 0");
    }
    if (outerDiameter.getValue().doubleValue() < 0) {
      throw new IllegalArgumentException("Outer diameter must be >= 0");
    }
    if (thermalResistivity.getValue().doubleValue() < 0) {
      throw new IllegalArgumentException("Thermal resistivity must be >= 0");
    }
    if (thermalCapacitance.getValue().doubleValue() < 0) {
      throw new IllegalArgumentException("Thermal capacitance must be >= 0");
    }
  }

  @Override
  public Map<String, String> getAdditionalInformation() {
    return Map.of();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o
        instanceof
        LayerInput(
            UUID uuid1,
            String name1,
            CableMaterial material1,
            ComparableQuantity<Length> diameter,
            ComparableQuantity<Length> outerDiameter1,
            ComparableQuantity<?> resistivity,
            ComparableQuantity<?> capacitance,
            Optional<ComparableQuantity<Area>> area1))) return false;
    return uuid.equals(uuid1)
        && name.equals(name1)
        && material == material1
        && innerDiameter.equals(diameter)
        && outerDiameter.equals(outerDiameter1)
        && thermalResistivity.equals(resistivity)
        && thermalCapacitance.equals(capacitance)
        && area.equals(area1);
  }

  @Override
  public @NonNull String toString() {
    return "LayerInput{"
        + "uuid='"
        + uuid
        + "name='"
        + name
        + '\''
        + ", material="
        + material
        + ", innerDiameter="
        + innerDiameter
        + ", outerDiameter="
        + outerDiameter
        + ", thermalResistivity="
        + thermalResistivity
        + ", thermalCapacitance="
        + thermalCapacitance
        + ", area="
        + area
        + '}';
  }
}
