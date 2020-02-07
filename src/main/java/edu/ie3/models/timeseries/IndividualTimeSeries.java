/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.timeseries;

import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.Value;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;

/** Describes a TimeSeries with individual values per timestep */
public class IndividualTimeSeries<T extends Value> implements TimeSeries<T> {
  /** Maps a TimeBasedValue to its time to retrieve faster */
  private HashMap<ZonedDateTime, TimeBasedValue<T>> timeToTimeBasedValue = new HashMap<>();

  /**
   * Creates a {@link TimeBasedValue} from this data and adds it to the internal map
   *
   * @param time of this value
   * @param value
   */
  public void add(ZonedDateTime time, T value) {
    this.add(new TimeBasedValue<T>(time, value));
  }

  /**
   * Adds the individual value to the internal map
   *
   * @param timeBasedValue
   */
  public void add(TimeBasedValue<T> timeBasedValue) {
    timeToTimeBasedValue.put(timeBasedValue.getTime(), timeBasedValue);
  }

  /** Adds values of a collection to the internal map */
  public void addAll(Collection<TimeBasedValue<T>> timeBasedValues) {
    timeBasedValues.forEach(this::add);
  }

  @Override
  public TimeBasedValue<T> getTimeBasedValue(ZonedDateTime time) {
    return timeToTimeBasedValue.get(time);
  }

  @Override
  public T getValue(ZonedDateTime time) {
    return getTimeBasedValue(time).getValue();
  }
}
