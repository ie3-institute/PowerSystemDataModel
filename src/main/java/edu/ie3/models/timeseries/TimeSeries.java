/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.timeseries;

import edu.ie3.models.UniqueEntity;
import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.Value;
import java.time.ZonedDateTime;

/** Describes a Series of {@link edu.ie3.models.value.TimeBasedValue TimeBasedValues} */
abstract class TimeSeries<T extends Value> extends UniqueEntity {

  /** @return the value at the given timestep as a TimeBasedValue */
  abstract TimeBasedValue<T> getTimeBasedValue(ZonedDateTime time);

  /**
   * If you prefer to keep the time with the value, please use {@link TimeSeries#getTimeBasedValue}
   * instead
   *
   * @return the raw value at the given timestep
   */
  abstract T getValue(ZonedDateTime time);
}
