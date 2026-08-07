/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import static edu.ie3.util.quantities.PowerSystemUnits.*;

import edu.ie3.util.quantities.interfaces.ElectricalResistivity;
import edu.ie3.util.quantities.interfaces.ThermalCapacitance;
import edu.ie3.util.quantities.interfaces.ThermalResistivity;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

/**
 * Enumeration of cable materials with their default thermal and electrical properties. Provides
 * default values based on physical material properties.
 */
public enum CableMaterial {
  /** Copper conductor material */
  COPPER,
  /** Aluminium conductor material */
  ALUMINIUM,
  /** Cross-linked polyethylene (XLPE) insulation */
  XLPE,
  /** Polyethylene (PE) insulation */
  PE,
  /** Polyvinyl chloride (PVC) insulation */
  PVC,
  /** Semi-conductive screen material */
  SEMI_COND_SCREEN,
  /** Screening tape material */
  SC_TAPE,
  /** Lead sheathing material */
  LEAD,
  /** Steel armoring material */
  STEEL,
  /** Polypropylene material */
  POLYPROPYLEN,
  /** Unknown material type */
  UNKNOWN;

  /**
   * Parses a material string into a Cable Material.
   *
   * @return An enum of the cable material if it can be parsed.
   */
  public static CableMaterial fromString(String s) {
    if (s == null) return UNKNOWN;
    return switch (s.trim().toLowerCase()) {
      case "copper" -> COPPER;
      case "copperwoventape", "sc_tape" -> SC_TAPE;
      case "aluminium" -> ALUMINIUM;
      case "xlpe" -> XLPE;
      case "pe" -> PE;
      case "pvc" -> PVC;
      case "semicondscreen", "semi_cond_screen" -> SEMI_COND_SCREEN;
      case "lead" -> LEAD;
      case "steel" -> STEEL;
      case "polypropylen", "pp" -> POLYPROPYLEN;
      default -> UNKNOWN;
    };
  }

  /**
   * Get the default thermal properties resistivity and capacitance for this material.
   *
   * @return A pair of thermal resistivity and thermal capacitance
   * @throws IllegalArgumentException if the material type is unknown
   */
  public ThermalProperties getThermalProperties() {
    return switch (this) {
      case COPPER ->
          new ThermalProperties(
              Quantities.getQuantity(1.0 / 384.0, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(3449600.0, JOULE_PER_CUBIC_METRE_KELVIN));
      case ALUMINIUM ->
          new ThermalProperties(
              Quantities.getQuantity(1.0 / 237.0, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(2420913.3, JOULE_PER_CUBIC_METRE_KELVIN));
      case XLPE, PE ->
          new ThermalProperties(
              Quantities.getQuantity(3.5, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(2.4, JOULE_PER_CUBIC_METRE_KELVIN));
      case PVC ->
          new ThermalProperties(
              Quantities.getQuantity(5.0, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(1.7, JOULE_PER_CUBIC_METRE_KELVIN));
      case SEMI_COND_SCREEN ->
          new ThermalProperties(
              Quantities.getQuantity(2.5, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(2.4, JOULE_PER_CUBIC_METRE_KELVIN));
      case SC_TAPE ->
          new ThermalProperties(
              Quantities.getQuantity(6.0, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(2.4, JOULE_PER_CUBIC_METRE_KELVIN));
      case LEAD ->
          new ThermalProperties(
              Quantities.getQuantity(1.0 / 35.0, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(1463892.0, JOULE_PER_CUBIC_METRE_KELVIN));
      case STEEL ->
          new ThermalProperties(
              Quantities.getQuantity(1.0 / 45.0, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(3756000.0, JOULE_PER_CUBIC_METRE_KELVIN));
      case POLYPROPYLEN ->
          new ThermalProperties(
              Quantities.getQuantity(6.0, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(2.0, JOULE_PER_CUBIC_METRE_KELVIN));
      case UNKNOWN ->
          throw new IllegalArgumentException(
              "Cannot provide thermal properties for unknown material");
    };
  }

  /**
   * Get the default electrical resistivity for this material at reference conditions.
   *
   * @return Electrical resistivity
   * @throws IllegalArgumentException if the material type is unknown
   */
  public ComparableQuantity<ElectricalResistivity> getElectricalResistivity() {
    return switch (this) {
      case COPPER -> Quantities.getQuantity(1.7241e-8, OHM_METRE);
      case ALUMINIUM -> Quantities.getQuantity(2.8264e-8, OHM_METRE);
      case STEEL -> Quantities.getQuantity(13.8e-8, OHM_METRE);
      case LEAD -> Quantities.getQuantity(21.4e-8, OHM_METRE);
      case UNKNOWN ->
          throw new IllegalArgumentException(
              "Cannot provide electrical resistivity for unknown material");
      default ->
          throw new IllegalArgumentException(
              "No electrical resistivity data available for material: " + this);
    };
  }

  /**
   * Get the temperature coefficient for electrical resistivity of this material.
   *
   * @return Temperature coefficient
   * @throws IllegalArgumentException if the material type is unknown
   */
  public double getElectricalResistivityTemperatureCoefficient() {
    return switch (this) {
      case COPPER -> 3.93e-3;
      case ALUMINIUM -> 4.03e-3;
      case LEAD -> 4.0e-3;
      case STEEL -> 4.5e-3;
      case UNKNOWN ->
          throw new IllegalArgumentException(
              "Cannot provide temperature coefficient for unknown material");
      default ->
          throw new IllegalArgumentException(
              "No temperature coefficient data available for material: " + this);
    };
  }

  /** Container class for thermal properties of a cable material. */
  public record ThermalProperties(
      ComparableQuantity<ThermalResistivity> resistivity,
      ComparableQuantity<ThermalCapacitance> capacitance) {
    /**
     * Create thermal properties.
     *
     * @param resistivity Thermal resistivity
     * @param capacitance Thermal capacitance
     */
    public ThermalProperties {}

    /**
     * Compact constructor for validation of record components.
     *
     * @throws IllegalArgumentException if any property is null
     */
    public ThermalProperties {
      if (resistivity == null || capacitance == null) {
        throw new IllegalArgumentException("Thermal properties must not be null.");
      }
    }
  }
}
