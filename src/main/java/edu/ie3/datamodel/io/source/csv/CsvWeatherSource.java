/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.csv.FileNamingStrategy;
import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.source.IdCoordinateSource;
import edu.ie3.datamodel.io.source.WeatherSource;
import edu.ie3.datamodel.models.timeseries.TimeSeriesContainer;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Point;

/** Implements a WeatherSource for CSV files by using the CsvTimeSeriesSource as a base */
public class CsvWeatherSource extends CsvTimeSeriesSource implements WeatherSource {
  private final Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries;

  /**
   * Initializes a CsvWeatherSource and immediately imports weather data, which will be kept for the
   * lifetime of this source
   *
   * @param csvSep the separator string for csv columns
   * @param folderPath path to the folder holding the time series files
   * @param fileNamingStrategy strategy for the naming of time series files
   * @param coordinateSource a coordinate source to map ids to points
   */
  public CsvWeatherSource(
      String csvSep,
      String folderPath,
      FileNamingStrategy fileNamingStrategy,
      IdCoordinateSource coordinateSource) {
    super(csvSep, folderPath, fileNamingStrategy, coordinateSource);
    coordinateToTimeSeries = getWeatherTimeSeries();
  }

  @Override
  public TimeSeriesContainer getTimeSeries() {
    return new TimeSeriesContainer(
        coordinateToTimeSeries,
        Collections.emptySet(),
        Collections.emptySet(),
        Collections.emptySet(),
        Collections.emptySet(),
        Collections.emptySet(),
        Collections.emptySet());
  }

  /**
   * Creates reader for all available weather time series files and then continues to parse them
   *
   * @return a map of coordinates to their time series
   */
  private Map<Point, IndividualTimeSeries<WeatherValue>> getWeatherTimeSeries() {
    /* Get only weather time series reader */
    Map<ColumnScheme, Set<CsvFileConnector.TimeSeriesReadingData>> colTypeToReadingData =
        connector.initTimeSeriesReader(ColumnScheme.WEATHER);

    /* Reading in weather time series */
    Set<CsvFileConnector.TimeSeriesReadingData> weatherReadingData =
        colTypeToReadingData.get(ColumnScheme.WEATHER);

    return readWeatherTimeSeries(weatherReadingData);
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval) {
    return trimMapToInterval(coordinateToTimeSeries, timeInterval);
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
    Map<Point, IndividualTimeSeries<WeatherValue>> filteredMap =
        coordinateToTimeSeries.entrySet().stream()
            .filter(entry -> coordinates.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return trimMapToInterval(filteredMap, timeInterval);
  }

  @Override
  public Optional<TimeBasedValue<WeatherValue>> getWeather(ZonedDateTime date, Point coordinate) {
    IndividualTimeSeries<WeatherValue> timeSeries = coordinateToTimeSeries.get(coordinate);
    if (timeSeries == null) return Optional.empty();
    return timeSeries.getTimeBasedValue(date);
  }

  /**
   * Trims all time series in a map to the given time interval
   *
   * @param map the map to trim the time series value of
   * @param timeInterval the interval to trim the data to
   * @return a map with trimmed time series
   */
  private static Map<Point, IndividualTimeSeries<WeatherValue>> trimMapToInterval(
      Map<Point, IndividualTimeSeries<WeatherValue>> map,
      ClosedInterval<ZonedDateTime> timeInterval) {
    // decided against parallel mode here as it likely wouldn't pay off as the expected coordinate
    // count is too low
    return map.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry -> trimTimeSeriesToInterval(entry.getValue(), timeInterval)));
  }

  /**
   * Trims a time series to the given time interval
   *
   * @param timeSeries the time series to trim
   * @param timeInterval the interval to trim the data to
   * @return the trimmed time series
   */
  private static IndividualTimeSeries<WeatherValue> trimTimeSeriesToInterval(
      IndividualTimeSeries<WeatherValue> timeSeries, ClosedInterval<ZonedDateTime> timeInterval) {
    return new IndividualTimeSeries<>(
        timeSeries.getUuid(),
        timeSeries.getEntries().stream()
            .parallel()
            .filter(value -> timeInterval.includes(value.getTime()))
            .collect(Collectors.toSet()));
  }
}
