/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.result.system;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Power;

/** Represents calculation results of a {@link edu.ie3.models.input.system.FixedFeedInInput} */
public class FixedFeedInResult extends SystemParticipantResult {

  /**
   * Standard constructor with automatic uuid generation.
   *
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   */
  public FixedFeedInResult(
      ZonedDateTime timestamp, UUID inputModel, Quantity<Power> p, Quantity<Power> q) {
    super(timestamp, inputModel, p, q);
  }

  /**
   * Standard constructor which allows uuid provision
   *
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   */
  public FixedFeedInResult(
      UUID uuid, ZonedDateTime timestamp, UUID inputModel, Quantity<Power> p, Quantity<Power> q) {
    super(uuid, timestamp, inputModel, p, q);
  }
}
