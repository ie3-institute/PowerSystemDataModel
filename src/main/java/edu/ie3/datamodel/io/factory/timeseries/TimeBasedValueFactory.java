/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.Factory;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;

/**
 * Abstract class that is able to build {@link TimeBasedValue}s from "flat" information
 *
 * @param <D> Type of "flat" information as a sub class of {@link TimeBasedValue}.
 * @param <V> Type of the targeted inner {@link Value}, that is carried.
 */
public abstract class TimeBasedValueFactory<D extends TimeBasedValueData<V>, V extends Value>
    extends Factory<V, D, TimeBasedValue<V>> {

  protected TimeBasedValueFactory(Class<? extends V>... valueClasses) {
    super(valueClasses);
  }
}
