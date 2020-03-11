/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.thermal;

import edu.ie3.datamodel.models.StandardUnits;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Power;
import javax.measure.quantity.Temperature;

/** Represents the results of {@link edu.ie3.datamodel.models.input.thermal.ThermalHouseInput} */
public class ThermalHouseResult extends ThermalSinkResult {
  /** Indoor room temperature of the house */
  private Quantity<Temperature> indoorTemperature;

  /**
   * Standard constructor which includes auto generation of the resulting output models uuid.
   *
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param qDot thermal heat demand of the sink
   * @param indoorTemperature Indoor room temperature
   */
  public ThermalHouseResult(
      ZonedDateTime timestamp,
      UUID inputModel,
      Quantity<Power> qDot,
      Quantity<Temperature> indoorTemperature) {
    super(timestamp, inputModel, qDot);
    this.indoorTemperature = indoorTemperature.to(StandardUnits.TEMPERATURE);
  }

  /**
   * Standard constructor without uuid generation.
   *
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param qDot thermal heat demand of the sink
   * @param indoorTemperature Indoor room temperature
   */
  public ThermalHouseResult(
      UUID uuid,
      ZonedDateTime timestamp,
      UUID inputModel,
      Quantity<Power> qDot,
      Quantity<Temperature> indoorTemperature) {
    super(uuid, timestamp, inputModel, qDot);
    this.indoorTemperature = indoorTemperature.to(StandardUnits.TEMPERATURE);
  }

  public Quantity<Temperature> getIndoorTemperature() {
    return indoorTemperature;
  }

  public void setIndoorTemperature(Quantity<Temperature> indoorTemperature) {
    this.indoorTemperature = indoorTemperature.to(StandardUnits.TEMPERATURE);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ThermalHouseResult that = (ThermalHouseResult) o;
    return indoorTemperature.equals(that.indoorTemperature);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), indoorTemperature);
  }

  @Override
  public String toString() {
    return "ThermalHouseResult{" + "indoorTemperature=" + indoorTemperature + '}';
  }
}
