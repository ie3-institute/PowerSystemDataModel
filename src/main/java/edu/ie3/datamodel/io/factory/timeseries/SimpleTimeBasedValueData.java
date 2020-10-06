/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import java.util.Map;

/**
 * Data, that is used to build a {@link TimeBasedValue} within a Factory
 *
 * @param <V> Type of inner value class
 */
public class SimpleTimeBasedValueData<V extends Value> extends TimeBasedValueData<V> {
  /**
   * Creates a new SimpleTimeBasedValueData object
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param valueClass Class of the underlying value
   */
  public SimpleTimeBasedValueData(Map<String, String> fieldsToAttributes, Class<V> valueClass) {
    super(fieldsToAttributes, valueClass);
  }

  @Override
  public String toString() {
    return "SimpleTimeBasedValueData{"
        + "fieldsToAttributes="
        + fieldsToAttributes
        + ", entityClass="
        + entityClass
        + "valueClass="
        + valueClass
        + '}';
  }
}
