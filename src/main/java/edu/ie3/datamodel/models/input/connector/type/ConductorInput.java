/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.Uniqueness;
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
import tech.units.indriya.ComparableQuantity;

/**
 * Represents the conducting core of a cable with its specific geometric and thermal properties.
 * Unlike {@link LayerInput} layers, the conductor has no inner diameter and includes compaction
 * information.
 */
public class ConductorInput implements InputEntity, Uniqueness, Serializable {

  private final UUID uuid;
  private final String name;
  private final CableMaterial material;
  private final ComparableQuantity<Area> crossSection;
  private final ComparableQuantity<Length> diameter;
  private final boolean isCompacted;
  private final ComparableQuantity<ThermalResistivity> thermalResistivity;
  private final ComparableQuantity<ThermalCapacitance> thermalCapacitance;
  private final ComparableQuantity<Area> area;

  /**
   * Constructor for the conductor of a cable.
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
  public ConductorInput(
      UUID uuid,
      String name,
      CableMaterial material,
      ComparableQuantity<Area> crossSection,
      ComparableQuantity<Length> diameter,
      boolean isCompacted,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<ThermalCapacitance> thermalCapacitance,
      ComparableQuantity<Area> area) {
    this.uuid = uuid;
    this.name = name;
    this.material = material;
    this.crossSection = crossSection;
    this.diameter = diameter;
    this.isCompacted = isCompacted;
    this.thermalResistivity = thermalResistivity;
    this.thermalCapacitance = thermalCapacitance;
    this.area = area;
  }

  public Optional<ComparableQuantity<Area>> areaOptional() {
    return Optional.ofNullable(area);
  }

  public UUID uuid() {
    return uuid;
  }

  @Override
  public UUID getUuid() {
    return uuid;
  }

  public String name() {
    return name;
  }

  public CableMaterial material() {
    return material;
  }

  public ComparableQuantity<Area> crossSection() {
    return crossSection;
  }

  public ComparableQuantity<Length> diameter() {
    return diameter;
  }

  public boolean isCompacted() {
    return isCompacted;
  }

  public ComparableQuantity<ThermalResistivity> thermalResistivity() {
    return thermalResistivity;
  }

  public ComparableQuantity<ThermalCapacitance> thermalCapacitance() {
    return thermalCapacitance;
  }

  public ComparableQuantity<Area> area() {
    return area;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ConductorInput)) return false;
    ConductorInput that = (ConductorInput) o;
    return isCompacted == that.isCompacted
        && uuid.equals(that.uuid)
        && name.equals(that.name)
        && material == that.material
        && crossSection.equals(that.crossSection)
        && diameter.equals(that.diameter)
        && thermalResistivity.equals(that.thermalResistivity)
        && thermalCapacitance.equals(that.thermalCapacitance)
        && Objects.equals(area, that.area);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        uuid,
        name,
        material,
        crossSection,
        diameter,
        isCompacted,
        thermalResistivity,
        thermalCapacitance,
        area);
  }
}
