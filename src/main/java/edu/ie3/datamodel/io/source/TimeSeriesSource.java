/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.timeseries.SimpleTimeBasedValueData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * The interface definition of a source, that is able to provide one specific time series for one
 * model
 */
public abstract class TimeSeriesSource<V extends Value> {

  protected Class<V> valueClass;
  protected TimeBasedSimpleValueFactory<V> valueFactory;

  protected TimeSeriesSource(Class<V> valueClass, TimeBasedSimpleValueFactory<V> factory) {
    this.valueFactory = factory;
    this.valueClass = valueClass;
  }

  /**
   * Build a {@link TimeBasedValue} of type {@code V}, whereas the underlying {@link Value} does not
   * need any additional information.
   *
   * @param fieldToValues Mapping from field id to values
   * @return {@link Try} of simple time based value
   */
  protected Try<TimeBasedValue<V>, FactoryException> createTimeBasedValue(
      Map<String, String> fieldToValues) {
    SimpleTimeBasedValueData<V> factoryData =
        new SimpleTimeBasedValueData<>(fieldToValues, valueClass);
    return valueFactory.get(factoryData);
  }

  public abstract IndividualTimeSeries<V> getTimeSeries();

  public abstract IndividualTimeSeries<V> getTimeSeries(ClosedInterval<ZonedDateTime> timeInterval)
      throws SourceException;

  public abstract Optional<V> getValue(ZonedDateTime time) throws SourceException;
}
