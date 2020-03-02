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

/** Describes a TimeSeries with individual values per time step */
public class IndividualTimeSeries<T extends Value> extends TimeSeries<T> {
  /** Maps a TimeBasedValue to its time to retrieve faster */
  private HashMap<ZonedDateTime, TimeBasedValue<T>> timeToTimeBasedValue = new HashMap<>();

  /**
   * Adding a map from {@link ZonedDateTime} to the value apparent a this point in time
   *
   * @param map The map that should be added
   */
  public void addAll(Map<ZonedDateTime, T> map) {
    map.forEach(this::add);
  }

  /**
   * Creates a {@link TimeBasedValue} from this data and adds it to the internal map
   *
   * @param time of this value
   * @param value The actual value
   */
  public void add(ZonedDateTime time, T value) {
    this.add(new TimeBasedValue<T>(time, value));
  }

  /**
   * Adds the individual value to the internal map
   *
   * @param timeBasedValue A value with time information
   */
  public void add(TimeBasedValue<T> timeBasedValue) {
    timeToTimeBasedValue.put(timeBasedValue.getTime(), timeBasedValue);
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
