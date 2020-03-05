/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.timeseries;

import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.Value;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Describes a TimeSeries with individual values per time step */
public class IndividualTimeSeries<T extends Value> extends TimeSeries<T> {
  /** Maps a time to its respective value to retrieve faster */
  private HashMap<ZonedDateTime, T> timeToTimeBasedValue = new HashMap<>();

  /**
   * Adding a map from {@link ZonedDateTime} to the value apparent a this point in time
   *
   * @param map The map that should be added
   */
  public void addAll(Map<ZonedDateTime, T> map) {
    map.forEach(this::add);
  }

  /**
   * Adds an entry time -> value to the internal map
   *
   * @param time of this value
   * @param value The actual value
   */
  public void add(ZonedDateTime time, T value) {
    timeToTimeBasedValue.put(time, value);
  }

  /**
   * Adds the individual value to the internal map
   *
   * @param timeBasedValue A value with time information
   */
  public void add(TimeBasedValue<T> timeBasedValue) {
    this.add(timeBasedValue.getTime(), timeBasedValue.getValue());
  }

  @Override
  public TimeBasedValue<T> getTimeBasedValue(ZonedDateTime time) {
    return new TimeBasedValue<>(time, timeToTimeBasedValue.get(time));
  }

  @Override
  public T getValue(ZonedDateTime time) {
    return getTimeBasedValue(time).getValue();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    IndividualTimeSeries<?> that = (IndividualTimeSeries<?>) o;
    return Objects.equals(timeToTimeBasedValue, that.timeToTimeBasedValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), timeToTimeBasedValue);
  }
}
