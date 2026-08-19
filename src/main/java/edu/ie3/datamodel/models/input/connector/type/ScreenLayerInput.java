/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.Uniqueness;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.util.quantities.interfaces.ElectricalResistivity;
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
 * Represents a cable screen layer with specific parameters for conductor shielding. Extends the
 * properties of a standard layer with wire-specific parameters.
 */
public class ScreenLayerInput implements InputEntity, Uniqueness, Serializable {

  private final UUID uuid;
  private final String name;
  private final CableMaterial material;
  private final ComparableQuantity<Length> innerDiameter;
  private final ComparableQuantity<Length> outerDiameter;
  private final ComparableQuantity<ThermalResistivity> thermalResistivity;
  private final ComparableQuantity<ThermalCapacitance> thermalCapacitance;
  private final Optional<ComparableQuantity<Area>> area;
  private final int wiresNumber;
  private final ComparableQuantity<Length> wireDiameter;
  private final Optional<ComparableQuantity<Length>> lengthOfLay;
  private final ComparableQuantity<ElectricalResistivity> electricalResistivity;

  /**
   * Constructor for a screen layer
   *
   * @param uuid UUID of the screen layer
   * @param name Name/designation of this screen layer
   * @param material Material of the screen
   * @param innerDiameter Inner diameter of the screen layer
   * @param outerDiameter Outer diameter of the screen layer
   * @param thermalResistivity Thermal resistivity of the material
   * @param thermalCapacitance Thermal capacitance of the material
   * @param area Optional real cross-sectional area (e.g. if different from geometry)
   * @param wiresNumber Number of individual wires in the screen
   * @param wireDiameter Diameter of an individual wire in the screen
   * @param lengthOfLay Optional length of lay (pitch) of the screen winding
   * @param electricalResistivity Electrical resistivity specific to the screen material
   */
  public ScreenLayerInput(
      UUID uuid,
      String name,
      CableMaterial material,
      ComparableQuantity<Length> innerDiameter,
      ComparableQuantity<Length> outerDiameter,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<ThermalCapacitance> thermalCapacitance,
      Optional<ComparableQuantity<Area>> area,
      int wiresNumber,
      ComparableQuantity<Length> wireDiameter,
      Optional<ComparableQuantity<Length>> lengthOfLay,
      ComparableQuantity<ElectricalResistivity> electricalResistivity) {
    this.uuid = uuid;
    this.name = name;
    this.material = material;
    this.innerDiameter = innerDiameter;
    this.outerDiameter = outerDiameter;
    this.thermalResistivity = thermalResistivity;
    this.thermalCapacitance = thermalCapacitance;
    this.area = area;
    this.wiresNumber = wiresNumber;
    this.wireDiameter = wireDiameter;
    this.lengthOfLay = lengthOfLay;
    this.electricalResistivity = electricalResistivity;
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

  public int wiresNumber() {
    return wiresNumber;
  }

  public ComparableQuantity<Length> wireDiameter() {
    return wireDiameter;
  }

  public Optional<ComparableQuantity<Length>> lengthOfLay() {
    return lengthOfLay;
  }

  public ComparableQuantity<ElectricalResistivity> electricalResistivity() {
    return electricalResistivity;
  }

  @Override
  public Map<String, String> getAdditionalInformation() {
    return Map.of();
  }

  @Override
  public @NonNull String toString() {
    return "ScreenLayerInput{"
        + "uuid="
        + uuid
        + ", name="
        + name
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
        + ", wiresNumber="
        + wiresNumber
        + ", wireDiameter="
        + wireDiameter
        + ", lengthOfLay="
        + lengthOfLay
        + ", electricalResistivity="
        + electricalResistivity
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ScreenLayerInput)) return false;
    ScreenLayerInput that = (ScreenLayerInput) o;
    return wiresNumber == that.wiresNumber
        && uuid.equals(that.uuid)
        && name.equals(that.name)
        && material == that.material
        && innerDiameter.equals(that.innerDiameter)
        && outerDiameter.equals(that.outerDiameter)
        && thermalResistivity.equals(that.thermalResistivity)
        && thermalCapacitance.equals(that.thermalCapacitance)
        && area.equals(that.area)
        && wireDiameter.equals(that.wireDiameter)
        && lengthOfLay.equals(that.lengthOfLay)
        && electricalResistivity.equals(that.electricalResistivity);
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
        area,
        wiresNumber,
        wireDiameter,
        lengthOfLay,
        electricalResistivity);
  }
}
