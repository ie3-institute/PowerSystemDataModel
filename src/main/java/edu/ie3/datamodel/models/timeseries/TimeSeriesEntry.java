/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries;

import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.value.Value;
import java.util.Objects;

/**
 * This is an abstract class describing a unique entry to a time series
 *
 * @param <V> Type of the contained value
 */
public abstract class TimeSeriesEntry<V extends Value> implements Entity {
  protected final V value;

  protected TimeSeriesEntry(V value) {
    this.value = value;
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
    return "TimeSeriesEntry{" + "value=" + value + '}';
  }
}
