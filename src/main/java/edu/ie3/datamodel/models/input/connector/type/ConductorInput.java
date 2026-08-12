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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import tech.units.indriya.ComparableQuantity;

/**
 * Represents the conducting core of a cable with its specific geometric and thermal properties.
 * Unlike {@link LayerInput} layers, the conductor has no inner diameter and includes compaction
 * information.
 *
 * @param uuid UUID of the ConductorInput
 * @param name Human-readable id
 * @param material Material of the conductor (e.g., copper or aluminium)
 * @param crossSection Real nominal cross-sectional area (electrically effective)
 * @param diameter Geometric outer diameter of the conductor
 * @param isCompacted Whether the conductor is compacted
 * @param thermalResistivity Thermal resistivity of the conductor material
 * @param thermalCapacitance Thermal capacitance of the conductor material
 * @param area Optional real cross-sectional area (if different from geometric calculation)
 */
public record ConductorInput(
    UUID uuid,
    String name,
    CableMaterial material,
    ComparableQuantity<Area> crossSection,
    ComparableQuantity<Length> diameter,
    boolean isCompacted,
    ComparableQuantity<ThermalResistivity> thermalResistivity,
    ComparableQuantity<ThermalCapacitance> thermalCapacitance,
    @Nullable ComparableQuantity<Area> area)
    implements InputEntity, Serializable {

  /**
   * Create a new conductor with all required parameters.
   *
   * @param uuid UUID of the ConductorInput
   * @param name Human-readable id
   * @param material Material of the conductor
   * @param crossSection Real nominal cross-sectional area (electrically effective)
   * @param diameter Geometric outer diameter
   * @param isCompacted Whether the conductor is compacted
   * @param thermalResistivity Thermal resistivity
   * @param area Optional real cross-sectional area
   */
  public ConductorInput(
      UUID uuid,
      String name,
      CableMaterial material,
      ComparableQuantity<Area> crossSection,
      ComparableQuantity<Length> diameter,
      boolean isCompacted,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<ThermalCapacitance> thermalCapacitance,
      Optional<ComparableQuantity<Area>> area) {

    this(
        uuid,
        name,
        material,
        crossSection,
        diameter,
        isCompacted,
        thermalResistivity,
        thermalCapacitance,
        Objects.requireNonNull(area, "Area optional must not be null").orElse(null));
  }

  public Optional<ComparableQuantity<Area>> areaOptional() {
    return Optional.ofNullable(area);
  }

  @Override
  public Map<String, String> getAdditionalInformation() {
    return Map.of();
  }

  @Override
  public @NonNull String toString() {
    return "ConductorInput{"
        + "uuid="
        + uuid
        + ", name="
        + name
        + ", material="
        + material
        + ", crossSection="
        + crossSection
        + ", diameter="
        + diameter
        + ", isCompacted="
        + isCompacted
        + ", thermalResistivity="
        + thermalResistivity
        + ", thermalCapacitance="
        + thermalCapacitance
        + ", area="
        + areaOptional()
        + '}';
  }
}
