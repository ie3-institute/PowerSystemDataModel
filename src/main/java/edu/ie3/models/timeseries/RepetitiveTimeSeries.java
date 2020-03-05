/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.timeseries;

import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.Value;
import java.time.ZonedDateTime;

/** Describes a TimeSeries with repetitive values that can be calculated from a pattern */
public abstract class RepetitiveTimeSeries<T extends Value> extends TimeSeries<T> {

  /** Calculate the value at the given timestep based on a pattern */
  public abstract T calc(ZonedDateTime time);

  @Override
  public TimeBasedValue<T> getTimeBasedValue(ZonedDateTime time) {
    return new TimeBasedValue<>(time, getValue(time));
  }

  @Override
  public T getValue(ZonedDateTime time) {
    return calc(time);
  }
}
