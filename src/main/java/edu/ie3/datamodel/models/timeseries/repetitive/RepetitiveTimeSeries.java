/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import java.lang.reflect.Array;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.ToIntFunction;

/** Describes a TimeSeries with repetitive values that can be calculated from a pattern */
public abstract class RepetitiveTimeSeries<
        E extends TimeSeriesEntry<V>, V extends Value, R extends Value>
    extends TimeSeries<E, V, R> {

  private final V[] entries;

  @SuppressWarnings("unchecked")
  protected RepetitiveTimeSeries(
      Set<E> entries, Class<V> valueClass, ToIntFunction<E> keyExtractor) {
    super(entries, Comparator.comparing(keyExtractor::applyAsInt), keyExtractor::applyAsInt);

    this.entries = (V[]) Array.newInstance(valueClass, entries.size());
    entries.forEach(e -> this.entries[keyExtractor.applyAsInt(e)] = e.getValue());
  }

  /**
   * Method to retrieve the value at the given index.
   *
   * @param index of the value
   * @return the value
   */
  protected V get(int index) {
    return entries[index];
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
}
