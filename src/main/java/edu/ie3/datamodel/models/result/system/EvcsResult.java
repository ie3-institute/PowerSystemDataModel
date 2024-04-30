/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.system;

import edu.ie3.datamodel.models.input.system.EvcsInput;
import java.time.ZonedDateTime;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Represents calculation results of a {@link EvcsInput} */
public class EvcsResult extends SystemParticipantResult {
  /**
   * Standard constructor with automatic uuid generation.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   */
  public EvcsResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q) {
    super(time, inputModel, p, q);
  }

  @Override
  public String toString() {
    return "EvcsResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", p="
        + getP()
        + ", q="
        + getQ()
        + '}';
  }
}
