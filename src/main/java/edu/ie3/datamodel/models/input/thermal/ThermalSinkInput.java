/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import java.util.UUID;

/** Common properties to all thermal sinks */
public abstract class ThermalSinkInput extends ThermalUnitInput {
  /**
   * @param uuid Unique identifier of a thermal sink input model
   * @param id Identifier of the thermal unit
   * @param bus Thermal bus, a thermal unit is connected to
   */
  ThermalSinkInput(UUID uuid, String id, ThermalBusInput bus) {
    super(uuid, id, bus);
  }
}
