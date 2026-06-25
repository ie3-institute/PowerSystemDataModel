/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.util.quantities.interfaces.ElectricalResistivity;
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
 * Represents a cable screen layer with specific parameters for conductor shielding. Extends the
 * properties of a standard layer with wire-specific parameters.
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
public record ScreenLayerInput(
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
    ComparableQuantity<ElectricalResistivity> electricalResistivity)
    implements InputEntity {
  /**
   * Create a new screen layer with all required parameters.
   *
   * @param uuid UUID of the screen layer
   * @param name Designation of this screen layer
   * @param material Material of the screen
   * @param innerDiameter Inner diameter
   * @param outerDiameter Outer diameter
   * @param thermalResistivity Thermal resistivity
   * @param thermalCapacitance Thermal capacitance
   * @param area Optional real cross-sectional area
   * @param wiresNumber Number of individual wires
   * @param wireDiameter Diameter of individual wire
   * @param lengthOfLay Optional length of lay (pitch)
   * @param electricalResistivity Electrical resistivity of the screen material
   */
  public ScreenLayerInput {
    // Validation
    Objects.requireNonNull(uuid, "Layer UUID cannot be null");
    Objects.requireNonNull(name, "Screen layer name cannot be null");
    Objects.requireNonNull(material, "Screen material cannot be null");
    Objects.requireNonNull(innerDiameter, "Inner diameter cannot be null");
    Objects.requireNonNull(outerDiameter, "Outer diameter cannot be null");
    Objects.requireNonNull(thermalResistivity, "Thermal resistivity cannot be null");
    Objects.requireNonNull(thermalCapacitance, "Thermal capacitance cannot be null");
    Objects.requireNonNull(area, "Area Optional cannot be null");
    Objects.requireNonNull(wireDiameter, "Wire diameter cannot be null");
    Objects.requireNonNull(lengthOfLay, "Length of lay Optional cannot be null");
    Objects.requireNonNull(electricalResistivity, "Material resistivity cannot be null");

    if (name.isEmpty()) {
      throw new IllegalArgumentException("Screen layer name cannot be empty");
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
    if (wiresNumber < 1) {
      throw new IllegalArgumentException("Number of wires must be >= 1");
    }
    if (wireDiameter.getValue().doubleValue() < 0) {
      throw new IllegalArgumentException("Wire diameter must be >= 0");
    }
    if (electricalResistivity.getValue().doubleValue() < 0) {
      throw new IllegalArgumentException("Material resistivity must be >= 0");
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
        ScreenLayerInput(
            UUID uuid1,
            String name1,
            CableMaterial material1,
            ComparableQuantity<Length> diameter,
            ComparableQuantity<Length> outerDiameter1,
            ComparableQuantity<ThermalResistivity> resistivity,
            ComparableQuantity<ThermalCapacitance> capacitance,
            Optional<ComparableQuantity<Area>> area1,
            int number,
            ComparableQuantity<Length> wireDiameter1,
            Optional<ComparableQuantity<Length>> ofLay,
            ComparableQuantity<ElectricalResistivity> materialResistivity1))) return false;
    return uuid.equals(uuid1)
        && wiresNumber == number
        && name.equals(name1)
        && material == material1
        && innerDiameter.equals(diameter)
        && outerDiameter.equals(outerDiameter1)
        && thermalResistivity.equals(resistivity)
        && thermalCapacitance.equals(capacitance)
        && area.equals(area1)
        && wireDiameter.equals(wireDiameter1)
        && lengthOfLay.equals(ofLay)
        && electricalResistivity.equals(materialResistivity1);
  }

  @Override
  public @NonNull String toString() {
    return "ScreenLayerInput{"
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
}
