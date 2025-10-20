/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;

/**
 * Describes a ground temperature value. This class extends {@link TemperatureValue} to represent
 * temperature at a specific depth in the ground.
 */
public class GroundTemperatureValue extends TemperatureValue {

  /**
   * Constructs a new GroundTemperatureValue.
   *
   * @param temperature The temperature quantity (typically in K)
   */
  public GroundTemperatureValue(ComparableQuantity<Temperature> temperature) {
    super(temperature);
  }

  @Override
  public String toString() {
    return "GroundTemperatureValue{" + "temperature=" + getTemperature().orElse(null) + '}';
  }
}
