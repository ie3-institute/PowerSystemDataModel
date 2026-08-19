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
import tech.units.indriya.ComparableQuantity;

/**
 * Represents a concentric layer of a cable, including insulation, filler, armor, and jacket layers.
 */
public class LayerInput implements InputEntity, Uniqueness, Serializable {

  private final UUID uuid;
  private final String name;
  private final CableMaterial material;
  private final ComparableQuantity<Length> innerDiameter;
  private final ComparableQuantity<Length> outerDiameter;
  private final ComparableQuantity<ThermalResistivity> thermalResistivity;
  private final ComparableQuantity<ThermalCapacitance> thermalCapacitance;
  private final Optional<ComparableQuantity<Area>> area;

  /**
   * Constructor of a cable layer.
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
  public LayerInput(
      UUID uuid,
      String name,
      CableMaterial material,
      ComparableQuantity<Length> innerDiameter,
      ComparableQuantity<Length> outerDiameter,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<ThermalCapacitance> thermalCapacitance,
      Optional<ComparableQuantity<Area>> area) {
    this.uuid = uuid;
    this.name = name;
    this.material = material;
    this.innerDiameter = innerDiameter;
    this.outerDiameter = outerDiameter;
    this.thermalResistivity = thermalResistivity;
    this.thermalCapacitance = thermalCapacitance;
    this.area = area;
  }

  @Override
  public UUID getUuid() {
    return uuid;
  }

  public UUID uuid() {
    return uuid;
  }

  public String name() {
    return name;
  }

  public CableMaterial material() {
    return material;
  }

  public ComparableQuantity<Length> innerDiameter() {
    return innerDiameter;
  }

  public ComparableQuantity<Length> outerDiameter() {
    return outerDiameter;
  }

  public ComparableQuantity<ThermalResistivity> thermalResistivity() {
    return thermalResistivity;
  }

  public ComparableQuantity<ThermalCapacitance> thermalCapacitance() {
    return thermalCapacitance;
  }

  public Optional<ComparableQuantity<Area>> area() {
    return area;
  }

  @Override
  public Map<String, String> getAdditionalInformation() {
    return Map.of();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LayerInput)) return false;
    LayerInput that = (LayerInput) o;
    return uuid.equals(that.uuid)
        && name.equals(that.name)
        && material == that.material
        && innerDiameter.equals(that.innerDiameter)
        && outerDiameter.equals(that.outerDiameter)
        && thermalResistivity.equals(that.thermalResistivity)
        && thermalCapacitance.equals(that.thermalCapacitance)
        && area.equals(that.area);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        uuid,
        name,
        material,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        thermalCapacitance,
        area);
  }
}
