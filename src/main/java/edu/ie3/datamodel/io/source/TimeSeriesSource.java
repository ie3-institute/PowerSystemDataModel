/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.factory.timeseries.SimpleTimeBasedValueData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * The interface definition of a source, that is able to provide one specific time series for one
 * model
 */
public interface TimeSeriesSource<V extends Value> extends DataSource {

  /**
   * Obtain the full time series
   *
   * @return the time series
   */
  IndividualTimeSeries<V> getTimeSeries();

  /**
   * Get the time series for the given time interval. If the interval is bigger than the time series
   * itself, only the parts of the time series within the interval are handed back.
   *
   * @param timeInterval Desired time interval to cover
   * @return The parts of of interest of the time series
   */
  IndividualTimeSeries<V> getTimeSeries(ClosedInterval<ZonedDateTime> timeInterval);

  /**
   * Get the time series value for a specific time
   *
   * @param time The queried time
   * @return Option on a value for that time
   */
  Optional<V> getValue(ZonedDateTime time);

  /**
   * Build a {@link TimeBasedValue} of type {@code V}, whereas the underlying {@link Value} does not
   * need any additional information.
   *
   * @param fieldToValues Mapping from field id to values
   * @param valueClass Class of the desired underlying value
   * @param factory Factory to process the "flat" information
   * @return Optional simple time based value
   */
  default Optional<TimeBasedValue<V>> buildTimeBasedValue(
      Map<String, String> fieldToValues,
      Class<V> valueClass,
      TimeBasedSimpleValueFactory<V> factory) {
    SimpleTimeBasedValueData<V> factoryData =
        new SimpleTimeBasedValueData<>(fieldToValues, valueClass);
    return factory.get(factoryData);
  }
}
