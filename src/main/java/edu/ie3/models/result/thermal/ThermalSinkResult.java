/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.result.thermal;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Power;

/** Represents calculation results of a {@link edu.ie3.models.input.thermal.ThermalSinkInput} */
public abstract class ThermalSinkResult extends ThermalUnitResult {

  /**
   * Standard constructor which includes auto generation of the resulting output models uuid.
   *
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param qDot thermal heat demand of the sink
   */
  public ThermalSinkResult(ZonedDateTime timestamp, UUID inputModel, Quantity<Power> qDot) {
    super(timestamp, inputModel, qDot);
  }

  /**
   * Standard constructor without uuid generation.
   *
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param qDot thermal heat demand of the sink
   */
  public ThermalSinkResult(
      UUID uuid, ZonedDateTime timestamp, UUID inputModel, Quantity<Power> qDot) {
    super(uuid, timestamp, inputModel, qDot);
  }
}
