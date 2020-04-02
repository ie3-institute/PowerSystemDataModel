/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.system;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.measure.quantity.Power;
import tec.uom.se.ComparableQuantity;

/** Represents calculation results of a {@link edu.ie3.datamodel.models.input.system.ChpInput} */
public class ChpResult extends SystemParticipantResult {

  /**
   * Standard constructor with automatic uuid generation.
   *
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   */
  public ChpResult(
      ZonedDateTime timestamp,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q) { // TODO #65 Quantity replaced
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
  public ChpResult(
      UUID uuid,
      ZonedDateTime timestamp,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q) { // TODO #65 Quantity replaced
    super(uuid, timestamp, inputModel, p, q);
  }
}
