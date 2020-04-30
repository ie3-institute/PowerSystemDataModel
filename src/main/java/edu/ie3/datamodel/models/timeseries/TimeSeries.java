/*
 * © 2020. TU Dortmund University,
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
 */
public abstract class TimeSeries<E extends TimeSeriesEntry<V>, V extends Value>
    extends UniqueEntity {
  protected final Set<E> entries;

  public TimeSeries(Set<E> entries) {
    super();
    this.entries = Collections.unmodifiableSet(entries);
  }

  public TimeSeries(UUID uuid, Set<E> entries) {
    super(uuid);
    this.entries = Collections.unmodifiableSet(entries);
  }

  /**
   * Get the time based value for the queried time
   *
   * @param time Reference in time
   * @return the value at the given time step as a TimeBasedValue
   */
  public Optional<TimeBasedValue<V>> getTimeBasedValue(ZonedDateTime time) {
    V content = getValue(time).orElse(null);

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
  public abstract Optional<V> getValue(ZonedDateTime time);

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
   * Get the most recent available value before or at the given time step as a TimeBasedValue
   *
   * @param time Reference in time
   * @return the most recent available value before or at the given time step as a TimeBasedValue
   */
  public Optional<TimeBasedValue<V>> getPreviousTimeBasedValue(ZonedDateTime time) {
    return getPreviousDateTime(time).map(this::getTimeBasedValue).map(Optional::get);
  }

  /**
   * Get the next available value after or at the given time step as a TimeBasedValue
   *
   * @param time Reference in time
   * @return the next available value after or at the given time step as a TimeBasedValue
   */
  public Optional<TimeBasedValue<V>> getNextTimeBasedValue(ZonedDateTime time) {
    return getNextDateTime(time).map(this::getTimeBasedValue).map(Optional::get);
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
    TimeSeries<?, ?> that = (TimeSeries<?, ?>) o;
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
