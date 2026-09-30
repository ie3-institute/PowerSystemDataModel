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
  POLYPROPYLENE;

  /**
   * Get the default thermal properties resistivity and capacitance for this material.
   *
   * @return A pair of thermal resistivity and thermal capacitance
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
              Quantities.getQuantity(2.4e6, JOULE_PER_CUBIC_METRE_KELVIN));
      case PVC ->
          new ThermalProperties(
              Quantities.getQuantity(5.0, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(1.7e6, JOULE_PER_CUBIC_METRE_KELVIN));
      case SEMI_COND_SCREEN ->
          new ThermalProperties(
              Quantities.getQuantity(2.5, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(2.4e6, JOULE_PER_CUBIC_METRE_KELVIN));
      case SC_TAPE ->
          new ThermalProperties(
              Quantities.getQuantity(6.0, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(2.4e6, JOULE_PER_CUBIC_METRE_KELVIN));
      case LEAD ->
          new ThermalProperties(
              Quantities.getQuantity(1.0 / 35.0, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(1463892.0, JOULE_PER_CUBIC_METRE_KELVIN));
      case STEEL ->
          new ThermalProperties(
              Quantities.getQuantity(1.0 / 45.0, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(3756000.0, JOULE_PER_CUBIC_METRE_KELVIN));
      case POLYPROPYLENE ->
          new ThermalProperties(
              Quantities.getQuantity(6.0, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(2.0e6, JOULE_PER_CUBIC_METRE_KELVIN));
    };
  }

  /**
   * Get the default electrical resistivity for this material at reference conditions.
   *
   * @return Electrical resistivity
   * @throws IllegalArgumentException if the material has no electrical resistivity data (e.g.
   *     non-conductive insulation)
   */
  public ComparableQuantity<ElectricalResistivity> getElectricalResistivity() {
    return switch (this) {
      case COPPER -> Quantities.getQuantity(1.7241e-8, OHM_METRE);
      case ALUMINIUM -> Quantities.getQuantity(2.8264e-8, OHM_METRE);
      case STEEL -> Quantities.getQuantity(13.8e-8, OHM_METRE);
      case LEAD -> Quantities.getQuantity(21.4e-8, OHM_METRE);
      default ->
          throw new IllegalArgumentException(
              "No electrical resistivity data available for material: " + this);
    };
  }

  /**
   * Get the temperature coefficient for electrical resistivity of this material.
   *
   * @return Temperature coefficient
   * @throws IllegalArgumentException if the material has no temperature coefficient data (e.g.
   *     non-conductive insulation)
   */
  public double getElectricalResistivityTemperatureCoefficient() {
    return switch (this) {
      case COPPER -> 3.93e-3;
      case ALUMINIUM -> 4.03e-3;
      case LEAD -> 4.0e-3;
      case STEEL -> 4.5e-3;
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
