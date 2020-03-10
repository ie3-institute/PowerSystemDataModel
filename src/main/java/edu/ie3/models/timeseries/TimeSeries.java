/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.timeseries;

import edu.ie3.models.UniqueEntity;
import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.Value;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/** Describes a Series of {@link edu.ie3.models.value.Value values} */
abstract class TimeSeries<T extends Value> extends UniqueEntity {

  public TimeSeries() {
    super();
  }

  public TimeSeries(UUID uuid) {
    super(uuid);
  }

  /** @return the value at the given time step as a TimeBasedValue */
  protected Optional<TimeBasedValue<T>> getTimeBasedValue(Optional<ZonedDateTime> optionalTime) {
    if (optionalTime.isPresent()) {
      ZonedDateTime zdt = optionalTime.get();
      return getTimeBasedValue(zdt);
    } else {
      return Optional.empty();
    }
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
   * @return the most recent available value before or at the given time step as a TimeBasedValue
   */
  public abstract Optional<TimeBasedValue<T>> getPreviousTimeBasedValue(ZonedDateTime time);

  /** @return the next available value after or at the given time step as a TimeBasedValue */
  public abstract Optional<TimeBasedValue<T>> getNextTimeBasedValue(ZonedDateTime time);

  /**
   * If you prefer to keep the time with the value, please use {@link TimeSeries#getTimeBasedValue}
   * instead
   *
   * @return An option on the raw value at the given time step
   */
  public abstract Optional<T> getValue(ZonedDateTime time);
}
