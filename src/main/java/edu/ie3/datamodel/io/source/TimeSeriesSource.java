/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory;
import edu.ie3.datamodel.io.source.csv.CsvDataSource;
import edu.ie3.datamodel.io.source.sql.SqlDataSource;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import edu.ie3.util.interval.ClosedInterval;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The interface definition of a source, that is able to provide one specific time series for one
 * model
 */
public class TimeSeriesSource<V extends Value> extends TimeSeriesRelatedSource<V> implements DataSource {

  protected FunctionalDataSource dataSource;
  protected IndividualTimeSeries<V> timeSeries;

  protected UUID timeSeriesUuid;

  public TimeSeriesSource(
          FunctionalDataSource _dataSource,
          UUID timeSeriesUuid,
          String specialPlace,
          Class<V> valueClass,
          TimeBasedSimpleValueFactory<V> factory
          ) {
    this.dataSource = _dataSource;
    this.timeSeriesUuid = timeSeriesUuid;

    try {
        this.timeSeries = buildIndividualTimeSeries(
                    timeSeriesUuid,
                    specialPlace,
                    fieldToValue -> this.buildTimeBasedValue(fieldToValue, valueClass, factory));
      } catch (SourceException e) {
          throw new IllegalArgumentException(
                  "Unable to obtain time series with UUID '"
                          + timeSeriesUuid
                          + "'. Please check arguments!",
                  e);
      }
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

  /**
   * Attempts to read a time series with given unique identifier and file path. Single entries are
   * obtained entries with the help of {@code fieldToValueFunction}.
   *
   * @param timeSeriesUuid unique identifier of the time series
   * @param fieldToValueFunction function, that is able to transfer a mapping (from field to value)
   *     onto a specific instance of the targeted entry class
   * @throws SourceException If the file cannot be read properly
   * @return An option onto an individual time series
   */

  public<V extends Value> IndividualTimeSeries<V> buildIndividualTimeSeries(
          UUID timeSeriesUuid,
          String specialPlace,
          Function<Map<String, String>, Optional<TimeBasedValue<V>>> fieldToValueFunction)
          throws SourceException {
              Set<TimeBasedValue<V>> timeBasedValues =
                      dataSource.getSourceData(TimeBasedValue.class, specialPlace)
                              .map(fieldToValueFunction)
                              .flatMap(Optional::stream)
                              .collect(Collectors.toSet());
              return new IndividualTimeSeries<>(timeSeriesUuid, timeBasedValues);
  }


}
