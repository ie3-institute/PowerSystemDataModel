/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.thermal;

import edu.ie3.datamodel.utils.QuantityUtils;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;

/** Represents the results of {@link edu.ie3.datamodel.models.input.thermal.ThermalHouseInput}. */
public class ThermalHouseResult extends ThermalSinkResult {
  /** Indoor room temperature of the house. */
  private ComparableQuantity<Temperature> indoorTemperature;

  /**
   * Standard constructor for thermal house result.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param qDot thermal heat demand of the sink
   * @param indoorTemperature Indoor room temperature
   */
  public ThermalHouseResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Power> qDot,
      ComparableQuantity<Temperature> indoorTemperature) {
    super(time, inputModel, qDot);
    this.indoorTemperature = indoorTemperature;
  }

  public ComparableQuantity<Temperature> getIndoorTemperature() {
    return indoorTemperature;
  }

  public void setIndoorTemperature(ComparableQuantity<Temperature> indoorTemperature) {
    this.indoorTemperature = indoorTemperature;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ThermalHouseResult that)) return false;
    if (!super.equals(o)) return false;
    return QuantityUtils.equals(indoorTemperature, that.indoorTemperature);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), indoorTemperature);
  }

  @Override
  public String toString() {
    return "ThermalHouseResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", qDot="
        + getqDot()
        + ", indoorTemperature="
        + indoorTemperature
        + "}";
  }
}
