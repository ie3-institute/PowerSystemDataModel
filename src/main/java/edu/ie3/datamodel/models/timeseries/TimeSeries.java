/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries;

import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.Uniqueness;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Describes a Series of {@link edu.ie3.datamodel.models.value.Value values}
 *
 * @param <E> Type of the entries, the time series is foreseen to contain
 * @param <V> Type of the values, the entries will have
 * @param <R> Type of the value, the time series will return
 */
public abstract class TimeSeries<E extends TimeSeriesEntry<V>, V extends Value, R extends Value>
    implements Entity, Uniqueness {
  private final UUID uuid;
  private final NavigableSet<E> entries;

  protected TimeSeries(Set<E> entries, Comparator<E> comparator, Function<E, ?> keyExtractor) {
    this(UUID.randomUUID(), entries, comparator, keyExtractor);
  }

  protected TimeSeries(
      UUID uuid, Collection<E> entries, Comparator<E> comparator, Function<E, ?> keyExtractor) {
    this.uuid = uuid;

    // check for duplicates
    int uniqueKeys = entries.stream().map(keyExtractor).collect(Collectors.toSet()).size();

    if (uniqueKeys != entries.size()) {
      throw new IllegalStateException("Duplicate keys present!");
    }

    this.entries = new TreeSet<>(comparator);
    this.entries.addAll(entries);
  }

  @Override
  public UUID getUuid() {
    return uuid;
  }

  /** Returns {@code true} if the time series has no entries. */
  public boolean isEmpty() {
    return entries.isEmpty();
  }

  /** Returns {@code true} if the time series has at least one entry. */
  public boolean nonEmpty() {
    return !isEmpty();
  }

  /** Returns the number of entries in the time series. */
  public int size() {
    return entries.size();
  }

  /**
   * Get the time based value for the queried time
   *
   * @param time Reference in time
   * @return the value at the given time step as a TimeBasedValue
   */
  public Optional<TimeBasedValue<R>> getTimeBasedValue(ZonedDateTime time) {
    return getValue(time).map(v -> new TimeBasedValue<>(time, v));
  }

  /** Returns an option for the first value of the time series. */
  public Optional<E> first() {
    if (entries.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(entries.first());
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
  public abstract Optional<ZonedDateTime> getPreviousDateTime(ZonedDateTime time);

  /**
   * Get the next later known time instant
   *
   * @param time Reference in time
   * @return The next later known time instant
   */
  public abstract Optional<ZonedDateTime> getNextDateTime(ZonedDateTime time);

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
  public NavigableSet<E> getEntries() {
    return Collections.unmodifiableNavigableSet(entries);
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
    return "TimeSeries{" + "#entries=" + entries.size() + '}';
  }
}
