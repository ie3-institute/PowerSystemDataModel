/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Describes a Series of {@link edu.ie3.datamodel.models.value.Value values}
 *
 * @param <E> Type of the entries, the time series is foreseen to contain
 * @param <V> Type of the values, the entries will have
 * @param <R> Type of the value, the time series will return
 */
public abstract class TimeSeries<E extends TimeSeriesEntry<V>, V extends Value, R extends Value>
    extends UniqueEntity {
  private final Set<E> entries;

  protected TimeSeries(Set<E> entries) {
    this.entries = Collections.unmodifiableSet(entries);
  }

  protected TimeSeries(UUID uuid, Set<E> entries) {
    super(uuid);
    this.entries = Collections.unmodifiableSet(entries);
  }

  /**
   * Get the time based value for the queried time
   *
   * @param time Reference in time
   * @return the value at the given time step as a TimeBasedValue
   */
  public Optional<TimeBasedValue<R>> getTimeBasedValue(ZonedDateTime time) {
    R content = getValue(time).orElse(null);

    if (content != null) {
      return Optional.of(new TimeBasedValue<>(time, content));
    } else {
      return Optional.empty();
    }
  }

  /**
   * If you prefer to keep the time with the value, please use {@link TimeSeries#getTimeBasedValue}
   * instead
   *
   * @param time Queried time
   * @return An option on the raw value at the given time step
   */
  public abstract Optional<R> getValue(ZonedDateTime time);

  /**
   * Get the next earlier known time instant
   *
   * @param time Reference in time
   * @return The next earlier known time instant
   */
  protected abstract Optional<ZonedDateTime> getPreviousDateTime(ZonedDateTime time);

  /**
   * Get the next later known time instant
   *
   * @param time Reference in time
   * @return The next later known time instant
   */
  protected abstract Optional<ZonedDateTime> getNextDateTime(ZonedDateTime time);

  /**
   * Get all {@link ZonedDateTime}s after the given time.
   *
   * @param time given time
   * @return a list of all time keys
   */
  public abstract List<ZonedDateTime> getTimeKeysAfter(ZonedDateTime time);

  /**
   * Get the most recent available value before or at the given time step as a TimeBasedValue
   *
   * @param time Reference in time
   * @return the most recent available value before or at the given time step as a TimeBasedValue
   */
  public Optional<TimeBasedValue<R>> getPreviousTimeBasedValue(ZonedDateTime time) {
    return getPreviousDateTime(time).flatMap(this::getTimeBasedValue);
  }

  /**
   * Get the next available value after or at the given time step as a TimeBasedValue
   *
   * @param time Reference in time
   * @return the next available value after or at the given time step as a TimeBasedValue
   */
  public Optional<TimeBasedValue<R>> getNextTimeBasedValue(ZonedDateTime time) {
    return getNextDateTime(time).flatMap(this::getTimeBasedValue);
  }

  /**
   * Returns all unique entries
   *
   * @return all unique entries
   */
  public Set<E> getEntries() {
    return entries;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    TimeSeries<?, ?, ?> that = (TimeSeries<?, ?, ?>) o;
    return entries.equals(that.entries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), entries);
  }

  @Override
  public String toString() {
    return "TimeSeries{" + "uuid=" + getUuid() + ", #entries=" + entries.size() + '}';
  }
}
