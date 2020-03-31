/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.value.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/** Describes a Series of {@link edu.ie3.datamodel.models.value.Value values} */
public abstract class TimeSeries<T extends Value> extends UniqueEntity {

  public TimeSeries() {
    super();
  }

  public TimeSeries(UUID uuid) {
    super(uuid);
  }

  /** @return the value at the given time step as a TimeBasedValue */
  public Optional<TimeBasedValue<T>> getTimeBasedValue(ZonedDateTime time) {
    T content = getValue(time).orElse(null);

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
   * @return An option on the raw value at the given time step
   */
  public abstract Optional<T> getValue(ZonedDateTime time);

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
   * @return the most recent available value before or at the given time step as a TimeBasedValue
   */
  public Optional<TimeBasedValue<T>> getPreviousTimeBasedValue(ZonedDateTime time) {
    return getPreviousDateTime(time).map(this::getTimeBasedValue).map(Optional::get);
  }

  /** @return the next available value after or at the given time step as a TimeBasedValue */
  public Optional<TimeBasedValue<T>> getNextTimeBasedValue(ZonedDateTime time) {
    return getNextDateTime(time).map(this::getTimeBasedValue).map(Optional::get);
  }
}
