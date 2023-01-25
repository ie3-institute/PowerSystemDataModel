/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.timeseries.SimpleTimeBasedValueData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * The interface definition of a source, that is able to provide one specific time series for one
 * model
 */
public class TimeSeriesSource<V extends Value> implements DataSource {

  public final FunctionalDataSource dataSource;
  private final IndividualTimeSeries<V> timeSeries;

  public TimeSeriesSource (
          FunctionalDataSource _dataSource,
          UUID timeSeriesUuid,
          Class<V> valueClass,
          TimeBasedSimpleValueFactory<V> factory
          ) {
    this.dataSource = _dataSource;

    String filePath = "";

    this.timeSeries = dataSource.buildIndividualTimeSeries(
                    timeSeriesUuid,
                    filePath,
                    fieldToValue -> this.buildTimeBasedValue(fieldToValue, valueClass, factory));
  }

  /**
   * Obtain the full time series
   *
   * @return the time series
   */
  public IndividualTimeSeries<V> getTimeSeries() { return timeSeries; }

  /**
   * Get the time series for the given time interval. If the interval is bigger than the time series
   * itself, only the parts of the time series within the interval are handed back.
   *
   * @param timeInterval Desired time interval to cover
   * @return The parts of interest of the time series
   */
  public IndividualTimeSeries<V> getTimeSeries(ClosedInterval<ZonedDateTime> timeInterval) { return TimeSeriesUtils.trimTimeSeriesToInterval(timeSeries, timeInterval); }

  /**
   * Get the time series value for a specific time
   *
   * @param time The queried time
   * @return Option on a value for that time
   */
  public Optional<V> getValue(ZonedDateTime time) { return timeSeries.getValue(time); }

  public Optional<TimeBasedValue<V>> buildTimeBasedValue(
          Map<String, String> fieldToValues,
          Class<V> valueClass,
          TimeBasedSimpleValueFactory<V> factory) {
    SimpleTimeBasedValueData<V> factoryData =
            new SimpleTimeBasedValueData<>(fieldToValues, valueClass);
    return factory.get(factoryData);
  }


}
