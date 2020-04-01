/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.UniqueEntity;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Describes an entity of a time series by mapping a value to a timestamp
 *
 * @param <T> type of value
 */
public class TimeBasedValue<T extends Value> extends UniqueEntity {

  private T value;

  private ZonedDateTime time;

  public TimeBasedValue(UUID uuid, ZonedDateTime time, T value) {
    super(uuid);
    this.time = time;
    this.value = value;
  }

  public TimeBasedValue(ZonedDateTime time, T value) {
    this(UUID.randomUUID(), time, value);
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public ZonedDateTime getTime() {
    return time;
  }

  public void setTime(ZonedDateTime time) {
    this.time = time;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    TimeBasedValue<?> that = (TimeBasedValue<?>) o;
    return Objects.equals(value, that.value) && Objects.equals(time, that.time);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value, time);
  }

  @Override
  public String toString() {
    return "TimeBasedValue{" + value + "}@" + time;
  }
}
