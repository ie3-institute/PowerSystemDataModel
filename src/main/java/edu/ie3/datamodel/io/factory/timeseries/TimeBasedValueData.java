/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract definition of data, that is used to build a {@link TimeBasedValue} within a Factory
 *
 * @param <V> Type of inner value class
 */
public abstract class TimeBasedValueData<V extends Value> extends EntityData {
  protected final Class<V> valueClass;

  /**
   * Creates a new TimeBasedValueData object
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param valueClass Class of the underlying value
   */
  public TimeBasedValueData(Map<String, String> fieldsToAttributes, Class<V> valueClass) {
    super(fieldsToAttributes, TimeBasedValue.class);
    this.valueClass = valueClass;
  }

  public Class<V> getValueClass() {
    return valueClass;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TimeBasedValueData)) return false;
    if (!super.equals(o)) return false;
    TimeBasedValueData<?> that = (TimeBasedValueData<?>) o;
    return valueClass.equals(that.valueClass);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), valueClass);
  }

  @Override
  public String toString() {
    return "TimeBasedValueData{"
        + "fieldsToAttributes="
        + getFieldsToValues()
        + ", entityClass="
        + getEntityClass()
        + "valueClass="
        + valueClass
        + "} "
        + super.toString();
  }
}
