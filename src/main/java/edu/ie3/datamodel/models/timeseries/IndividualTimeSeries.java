/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries;

import edu.ie3.datamodel.models.value.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import java.time.ZonedDateTime;
import java.util.*;

/** Describes a TimeSeries with individual values per time step */
public class IndividualTimeSeries<T extends Value> extends TimeSeries<T> {
  /** Maps a time to its respective value to retrieve faster */
  private HashMap<ZonedDateTime, T> timeToValue = new HashMap<>();

  public IndividualTimeSeries() {
    super();
  }

  public IndividualTimeSeries(UUID uuid) {
    super(uuid);
  }

  public IndividualTimeSeries(UUID uuid, Map<ZonedDateTime, T> timeToValue) {
    super(uuid);
    addAll(timeToValue);
  }

  public IndividualTimeSeries(Map<ZonedDateTime, T> timeToValue) {
    super();
    addAll(timeToValue);
  }

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
    timeToValue.put(time, value);
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
  public Optional<T> getValue(ZonedDateTime time) {
    return Optional.ofNullable(timeToValue.get(time));
  }

  @Override
  public Optional<TimeBasedValue<T>> getPreviousTimeBasedValue(ZonedDateTime time) {
    Optional<ZonedDateTime> lastZdt =
        timeToValue.keySet().stream()
            .filter(valueTime -> valueTime.compareTo(time) <= 0)
            .max(Comparator.naturalOrder());
    return getTimeBasedValue(lastZdt);
  }

  @Override
  public Optional<TimeBasedValue<T>> getNextTimeBasedValue(ZonedDateTime time) {
    Optional<ZonedDateTime> nextZdt =
        timeToValue.keySet().stream()
            .filter(valueTime -> valueTime.compareTo(time) >= 0)
            .min(Comparator.naturalOrder());
    return getTimeBasedValue(nextZdt);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    IndividualTimeSeries<?> that = (IndividualTimeSeries<?>) o;
    return Objects.equals(timeToValue, that.timeToValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), timeToValue);
  }

  @Override
  public String toString() {
    return "IndividualTimeSeries{" + "timeToValue=" + timeToValue + '}';
  }
}
