/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.thermal;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Represents calculation results of a {@link
 * edu.ie3.datamodel.models.input.thermal.ThermalSinkInput}
 */
public abstract class ThermalSinkResult extends ThermalUnitResult {

  /**
   * Standard constructor which includes auto generation of the resulting output models uuid.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param qDot thermal heat demand of the sink
   */
  protected ThermalSinkResult(ZonedDateTime time, UUID inputModel, ComparableQuantity<Power> qDot) {
    super(time, inputModel, qDot);
  }

  /**
   * Standard constructor without uuid generation.
   *
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param qDot thermal heat demand of the sink
   */
  protected ThermalSinkResult(
      UUID uuid, ZonedDateTime time, UUID inputModel, ComparableQuantity<Power> qDot) {
    super(uuid, time, inputModel, qDot);
  }
}
