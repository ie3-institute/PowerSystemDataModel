/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.timeseries;

import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.Value;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/** Describes a TimeSeries with repetitive values that can be calculated from a pattern */
public abstract class RepetitiveTimeSeries<T extends Value> extends TimeSeries<T> {
  public RepetitiveTimeSeries() {}

  public RepetitiveTimeSeries(UUID uuid) {
    super(uuid);
  }

  /** Calculate the value at the given timestep based on a pattern */
  public abstract T calc(ZonedDateTime time);

  @Override
  public Optional<T> getValue(ZonedDateTime time) {
    return Optional.of(calc(time));
  }

  /**
   * Get the lastly known zoned date time with available values. If it is the queried time, that one
   * is returned.
   *
   * @param time Queried time
   * @return lastly known zoned date time with available values
   */
  protected abstract Optional<ZonedDateTime> getLastZonedDateTime(ZonedDateTime time);

  @Override
  public Optional<TimeBasedValue<T>> getLastTimeBasedValue(ZonedDateTime time) {
    return getTimeBasedValue(getLastZonedDateTime(time));
  }

  /**
   * Get the next upcoming zoned date time with available values. If it is the queried time, that
   * one is returned.
   *
   * @param time Queried time
   * @return next upcoming zoned date time with available values
   */
  protected abstract Optional<ZonedDateTime> getNextZonedDateTime(ZonedDateTime time);

  @Override
  public Optional<TimeBasedValue<T>> getNextTimeBasedValue(ZonedDateTime time) {
    return getTimeBasedValue(getNextZonedDateTime(time));
  }
}
