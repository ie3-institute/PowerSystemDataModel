/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.individual;

import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.*;

/** Describes a TimeSeries with individual values per time step */
public class IndividualTimeSeries<V extends Value> extends TimeSeries<TimeBasedValue<V>, V, V> {
  /** Maps a time to its respective value to retrieve faster */
  private final NavigableMap<ZonedDateTime, TimeBasedValue<V>> timeToValue;

  public IndividualTimeSeries(Set<TimeBasedValue<V>> values) {
    this(UUID.randomUUID(), values);
  }

  public IndividualTimeSeries(UUID uuid, Set<TimeBasedValue<V>> values) {
    super(uuid, values, TimeBasedValue::compareTo);
    timeToValue = new TreeMap<>();
    values.forEach(v -> timeToValue.put(v.getTime(), v));
  }

  private IndividualTimeSeries(UUID uuid, NavigableMap<ZonedDateTime, TimeBasedValue<V>> subMap) {
    super(uuid, subMap.sequencedValues(), TimeBasedValue::compareTo);
    this.timeToValue = subMap;
  }

  @Override
  public Optional<TimeBasedValue<V>> getTimeBasedValue(ZonedDateTime time) {
    return Optional.ofNullable(timeToValue.get(time));
  }

  public IndividualTimeSeries<V> getSubTimeSeries(ClosedInterval<ZonedDateTime> timeInterval) {
    return new IndividualTimeSeries<>(
        getUuid(),
        timeToValue.subMap(timeInterval.getLower(), true, timeInterval.getUpper(), true));
  }

  @Override
  public Optional<V> getValue(ZonedDateTime time) {
    return getTimeBasedValue(time).map(TimeBasedValue::getValue);
  }

  @Override
  public Optional<ZonedDateTime> getPreviousDateTime(ZonedDateTime time) {
    return Optional.ofNullable(timeToValue.navigableKeySet().lower(time));
  }

  @Override
  public Optional<ZonedDateTime> getNextDateTime(ZonedDateTime time) {
    return Optional.ofNullable(timeToValue.navigableKeySet().higher(time));
  }

  /**
   * Get all {@link ZonedDateTime}s after the given time.
   *
   * @param time given time
   * @return a list of all time keys
   */
  public List<ZonedDateTime> getTimeKeysAfter(ZonedDateTime time) {
    return new ArrayList<>(timeToValue.navigableKeySet().tailSet(time, false));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    IndividualTimeSeries<?> that = (IndividualTimeSeries<?>) o;
    return timeToValue.equals(that.timeToValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), timeToValue);
  }

  @Override
  public String toString() {
    return "IndividualTimeSeries{" + ", #entries=" + timeToValue.size() + '}';
  }
}
