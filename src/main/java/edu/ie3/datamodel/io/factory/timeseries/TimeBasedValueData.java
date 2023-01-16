/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.FactoryData;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;

/**
 * Abstract definition of data, that is used to build a {@link TimeBasedValue} within a Factory
 *
 * @param <V> Type of inner value class
 */
public abstract class TimeBasedValueData<V extends Value> extends FactoryData {

  /**
   * Creates a new TimeBasedValueData object
   *
   * @param mapWithRowIndex object containing an attribute map: field name to value and a row index
   * @param valueClass Class of the underlying value
   */
  protected TimeBasedValueData(MapWithRowIndex mapWithRowIndex, Class<V> valueClass) {
    super(mapWithRowIndex, valueClass);
  }

  @Override
  public String toString() {
    return "TimeBasedValueData{" + "fieldsToAttributes=" + getFieldsToValues() + "} ";
  }
}
