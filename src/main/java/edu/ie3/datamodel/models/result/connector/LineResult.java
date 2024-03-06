/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.connector;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;
import tech.units.indriya.ComparableQuantity;

/**
 * Represents calculation results of a {@link edu.ie3.datamodel.models.input.connector.LineInput}
 */
public class LineResult extends ConnectorResult {
  /**
   * Standard constructor with automatic uuid generation.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param iAMag electric current magnitude @ port A, normally provided in Ampere
   * @param iAAng electric current angle @ Port A in degree
   * @param iBMag electric current magnitude @ port B, normally provided in Ampere
   * @param iBAng electric current angle @ Port B in degree
   */
  public LineResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<ElectricCurrent> iAMag,
      ComparableQuantity<Angle> iAAng,
      ComparableQuantity<ElectricCurrent> iBMag,
      ComparableQuantity<Angle> iBAng) {
    super(time, inputModel, iAMag, iAAng, iBMag, iBAng);
  }

  @Override
  public String toString() {
    return "LineResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", iAMag="
        + getiAMag()
        + ", iAAng="
        + getiAAng()
        + ", iBMag="
        + getiBMag()
        + ", iBAng="
        + getiBAng()
        + '}';
  }
}
