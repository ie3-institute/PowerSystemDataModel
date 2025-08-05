/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Describes a TimeSeries with repetitive values that can be calculated from a pattern
 *
 * @param <E> the type parameter
 * @param <V> the type parameter
 * @param <R> the type parameter
 */
public abstract class RepetitiveTimeSeries<
        E extends TimeSeriesEntry<V>, V extends Value, R extends Value>
    extends TimeSeries<E, V, R> {

  /**
   * Instantiates a new Repetitive time series.
   *
   * @param uuid the uuid
   * @param entries the entries
   */
  protected RepetitiveTimeSeries(UUID uuid, Set<E> entries) {
    super(uuid, entries);
  }

  /**
   * Calculate the value at the given time step based on a pattern
   *
   * @param time Questioned time
   * @return The value for the queried time
   */
  protected abstract R calc(ZonedDateTime time);

  @Override
  public Optional<R> getValue(ZonedDateTime time) {
    return Optional.ofNullable(calc(time));
  }

  @Override
  public List<ZonedDateTime> getTimeKeysAfter(ZonedDateTime time) {
    // dummy value
    return getNextDateTime(time).map(List::of).orElseGet(Collections::emptyList);
  }
}
