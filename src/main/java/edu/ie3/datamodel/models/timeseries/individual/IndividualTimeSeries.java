/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.individual;

import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.value.Value;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/** Describes a TimeSeries with individual values per time step */
public class IndividualTimeSeries<V extends Value> extends TimeSeries<TimeBasedValue<V>, V> {
  /** Maps a time to its respective value to retrieve faster */
  private final Map<ZonedDateTime, TimeBasedValue<V>> timeToValue;

  public IndividualTimeSeries(UUID uuid, Set<TimeBasedValue<V>> values) {
    super(uuid, values);

    timeToValue =
        values.stream()
            .collect(Collectors.toMap(TimeBasedValue::getTime, timeBasedValue -> timeBasedValue));
  }

  /**
   * Returns the sorted set of all entries known to this time series
   *
   * @return An unmodifiable sorted set of all known time based values of this time series
   */
  @Override
  public SortedSet<TimeBasedValue<V>> getEntries() {
    TreeSet<TimeBasedValue<V>> sortedEntries = new TreeSet<>(timeToValue.values());
    return Collections.unmodifiableSortedSet(sortedEntries);
  }

  @Override
  public Optional<TimeBasedValue<V>> getTimeBasedValue(ZonedDateTime time) {
    return Optional.ofNullable(timeToValue.get(time));
  }

  @Override
  public Optional<V> getValue(ZonedDateTime time) {
    return getTimeBasedValue(time).map(TimeBasedValue::getValue);
  }

  @Override
  protected Optional<ZonedDateTime> getPreviousDateTime(ZonedDateTime time) {
    return timeToValue.keySet().stream()
        .filter(valueTime -> valueTime.compareTo(time) <= 0)
        .max(Comparator.naturalOrder());
  }

  @Override
  protected Optional<ZonedDateTime> getNextDateTime(ZonedDateTime time) {
    return timeToValue.keySet().stream()
        .filter(valueTime -> valueTime.compareTo(time) >= 0)
        .min(Comparator.naturalOrder());
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
    return "IndividualTimeSeries{" + "uuid=" + getUuid() + ", #entries=" + timeToValue.size() + '}';
  }
}
