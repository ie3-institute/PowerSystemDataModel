/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import java.util.Objects;
import javax.measure.Quantity;
import javax.measure.quantity.Temperature;

/** Describes a temperature value */
public class TemperatureValue implements Value {
  /** Temperature (typically in K) */
  private Quantity<Temperature> temperature;

  /** @param temperature (typically in K) */
  public TemperatureValue(Quantity<Temperature> temperature) {
    this.temperature = temperature.to(StandardUnits.TEMPERATURE);
  }

  public Quantity<Temperature> getTemperature() {
    return temperature;
  }

  public void setTemperature(Quantity<Temperature> temperature) {
    this.temperature = temperature.to(StandardUnits.TEMPERATURE);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TemperatureValue that = (TemperatureValue) o;
    return temperature.equals(that.temperature);
  }

  @Override
  public int hashCode() {
    return Objects.hash(temperature);
  }
}
