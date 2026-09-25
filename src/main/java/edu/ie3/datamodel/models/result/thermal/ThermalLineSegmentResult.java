/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.thermal;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.ResultEntity;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;

/** Represents calculation results of a thermal line segment. */
public class ThermalLineSegmentResult extends ResultEntity {

  /** Temperature of the thermal line segment in standard units. */
  private final ComparableQuantity<Temperature> lineSegmentTemperature;

  /**
   * Creates a new thermal line segment result.
   *
   * @param dateTime the point in time for which the result is valid
   * @param lineSegmentUuid the UUID of the corresponding thermal line segment
   * @param lineSegmentTemperature the temperature of the thermal line segment
   */
  public ThermalLineSegmentResult(
      ZonedDateTime dateTime,
      UUID lineSegmentUuid,
      ComparableQuantity<Temperature> lineSegmentTemperature) {
    super(dateTime, lineSegmentUuid);
    this.lineSegmentTemperature = lineSegmentTemperature.to(StandardUnits.TEMPERATURE);
  }

  /**
   * @return the UUID of the corresponding thermal line segment
   */
  public UUID getLineSegmentUuid() {
    return getInputModel();
  }

  /**
   * @return the temperature of the thermal line segment in standard units
   */
  public ComparableQuantity<Temperature> getLineSegmentTemperature() {
    return lineSegmentTemperature;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    ThermalLineSegmentResult that = (ThermalLineSegmentResult) o;

    return Objects.equals(lineSegmentTemperature, that.lineSegmentTemperature);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), lineSegmentTemperature);
  }

  @Override
  public String toString() {
    return "ThermalLineSegmentResult{"
        + "time="
        + getTime()
        + ", lineSegmentUuid="
        + getLineSegmentUuid()
        + ", lineSegmentTemperature="
        + lineSegmentTemperature
        + '}';
  }
}
