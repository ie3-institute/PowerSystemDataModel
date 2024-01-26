/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.individual;

import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Describes an entity of a time series by mapping a value to a timestamp
 *
 * @param <T> type of value
 */
public class TimeBasedValue<T extends Value> extends TimeSeriesEntry<T>
    implements Comparable<TimeBasedValue<? extends Value>> {
  private final ZonedDateTime time;
  private final T value;

  public TimeBasedValue(ZonedDateTime time, T value) {
    super(value);
    this.time = time;
    this.value = value;
  }

  public ZonedDateTime getTime() {
    return time;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    TimeBasedValue<?> that = (TimeBasedValue<?>) o;
    return time.equals(that.time);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), time);
  }

  @Override
  public String toString() {
    return "TimeBasedValue{" + "time=" + time + ", value=" + value + '}';
  }

  @Override
  public int compareTo(TimeBasedValue<? extends Value> timeBasedValue) {
    return time.compareTo(timeBasedValue.time);
  }
}
