/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.models.value.Value;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Mapping from a {@link SystemParticipantInput} to it's according {@link TimeSeries}
 *
 * @param <S> Type of system participant
 * @param <E> Type of the entries, the time series is foreseen to contain
 * @param <V> Type of the values, the entries will have
 */
public class TimeSeriesMapping<
        S extends SystemParticipantInput, E extends TimeSeriesEntry<V>, V extends Value>
    extends UniqueEntity {
  private final Map<S, TimeSeries<E, V>> mapping;

  public TimeSeriesMapping(UUID uuid, Map<S, TimeSeries<E, V>> mapping) {
    super(uuid);
    this.mapping = Collections.unmodifiableMap(mapping);
  }

  public Map<S, TimeSeries<E, V>> getMapping() {
    return mapping;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    TimeSeriesMapping<?, ?, ?> that = (TimeSeriesMapping<?, ?, ?>) o;
    return mapping.equals(that.mapping);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), mapping);
  }

  @Override
  public String toString() {
    return "TimeSeriesMapping{" + "uuid=" + uuid + ", #mappings=" + mapping.size() + '}';
  }
}
