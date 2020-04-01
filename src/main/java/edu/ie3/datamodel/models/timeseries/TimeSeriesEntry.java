/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.value.Value;
import java.util.Objects;
import java.util.UUID;

/**
 * This is an abstract class describing a unique entry to a time series
 *
 * @param <V> Type of the contained value
 */
public abstract class TimeSeriesEntry<V extends Value> extends UniqueEntity {
  protected final V value;

  public TimeSeriesEntry(UUID uuid, V value) {
    super(uuid);
    this.value = value;
  }

  public TimeSeriesEntry(V value) {
    this(UUID.randomUUID(), value);
  }

  public V getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    TimeSeriesEntry<?> entry = (TimeSeriesEntry<?>) o;
    return value.equals(entry.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value);
  }

  @Override
  public String toString() {
    return "Entry{" + "uuid=" + uuid + ", value=" + value + '}';
  }
}
