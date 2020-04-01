/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/** Describes a TimeSeries with repetitive values that can be calculated from a pattern */
public abstract class RepetitiveTimeSeries<E extends TimeSeriesEntry<V>, V extends Value>
    extends TimeSeries<E, V> {
  public RepetitiveTimeSeries(Set<E> entries) {
    super(entries);
  }

  public RepetitiveTimeSeries(UUID uuid, Set<E> entries) {
    super(uuid, entries);
  }

  /** Calculate the value at the given timestep based on a pattern */
  protected abstract V calc(ZonedDateTime time);

  @Override
  public Optional<V> getValue(ZonedDateTime time) {
    return Optional.of(calc(time));
  }
}
