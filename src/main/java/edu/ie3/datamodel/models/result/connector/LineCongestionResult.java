/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.connector;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.measure.quantity.ElectricCurrent;
import tech.units.indriya.ComparableQuantity;

/**
 * Represents congestion results of a {@link edu.ie3.datamodel.models.input.connector.LineInput}.
 */
public class LineCongestionResult extends ConnectorCongestionResult {

  /**
   * Standard constructor for congestion results.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param iMin minimum required current
   */
  public LineCongestionResult(
      ZonedDateTime time, UUID inputModel, ComparableQuantity<ElectricCurrent> iMin) {
    super(time, inputModel, iMin);
  }

  @Override
  public String toString() {
    return "LineCongestionResult{time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", iMin="
        + getRequired()
        + '}';
  }
}
