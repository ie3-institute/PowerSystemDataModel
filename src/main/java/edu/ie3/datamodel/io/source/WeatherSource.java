/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.csv.CsvIndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.factory.timeseries.IdCoordinateFactory;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueFactory;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.source.DataSource;
import edu.ie3.datamodel.io.source.FunctionalDataSource;
import edu.ie3.datamodel.io.source.IdCoordinateSource;
import edu.ie3.datamodel.io.source.WeatherSource;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import edu.ie3.util.interval.ClosedInterval;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Implements a WeatherSource for CSV files by using the CsvTimeSeriesSource as a base */
public class WeatherSource implements DataSource {
  protected static final Logger log = LoggerFactory.getLogger(WeatherSource.class);

  //public FunctionalDataSource dataSource;

  protected TimeBasedWeatherValueFactory weatherFactory;
  protected Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries;
  protected IdCoordinateSource idCoordinateSource;

  protected static final String COORDINATE_ID = "coordinateid";

  public WeatherSource(
          IdCoordinateSource idCoordinateSource,
          TimeBasedWeatherValueFactory weatherFactory
  ) {
    this.idCoordinateSource = idCoordinateSource;
    this.weatherFactory = weatherFactory;

    //coordinateToTimeSeries = getWeatherTimeSeries();
  }

  /**
   * Creates reader for all available weather time series files and then continues to parse them
   *
   * @return a map of coordinates to their time series
   */
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeatherTimeSeries() {
    return null;
  }

  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
          ClosedInterval<ZonedDateTime> timeInterval) {
    return trimMapToInterval(coordinateToTimeSeries, timeInterval);
  }

  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
          ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
    Map<Point, IndividualTimeSeries<WeatherValue>> filteredMap =
            coordinateToTimeSeries.entrySet().stream()
                    .filter(entry -> coordinates.contains(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return trimMapToInterval(filteredMap, timeInterval);
  }

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
  private Map<Point, IndividualTimeSeries<WeatherValue>> trimMapToInterval(
          Map<Point, IndividualTimeSeries<WeatherValue>> map,
          ClosedInterval<ZonedDateTime> timeInterval) {
    // decided against parallel mode here as it likely wouldn't pay off as the expected coordinate
    // count is too low
    return map.entrySet().stream()
            .collect(
                    Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> TimeSeriesUtils.trimTimeSeriesToInterval(entry.getValue(), timeInterval)));
  }


  /**
   * Builds a {@link TimeBasedValue} of type {@link WeatherValue} from given "flat " input
   * information. If the single model cannot be built, an empty optional is handed back.
   *
   * @param fieldToValues "flat " input information as a mapping from field to value
   * @return Optional time based weather value
   */
  private Optional<TimeBasedValue<WeatherValue>> buildWeatherValue(
          Map<String, String> fieldToValues) {
    /* Try to get the coordinate from entries */
    Optional<Point> maybeCoordinate = extractCoordinate(fieldToValues);
    return maybeCoordinate
            .map(
                    coordinate -> {
                      /* Remove coordinate entry from fields */
                      fieldToValues.remove(weatherFactory.getCoordinateIdFieldString());

                      /* Build factory data */
                      TimeBasedWeatherValueData factoryData =
                              new TimeBasedWeatherValueData(fieldToValues, coordinate);
                      return weatherFactory.get(factoryData);
                    })
            .orElseGet(
                    () -> {
                      log.error("Unable to find coordinate for entry '{}'.", fieldToValues);
                      return Optional.empty();
                    });
  }

  /**
   * Extract the coordinate identifier from the field to value mapping and obtain the actual
   * coordinate in collaboration with the source.
   *
   * @param fieldToValues "flat " input information as a mapping from field to value
   * @return Optional time based weather value
   */
  private Optional<Point> extractCoordinate(Map<String, String> fieldToValues) {
    String coordinateString = fieldToValues.get(weatherFactory.getCoordinateIdFieldString());
    if (Objects.isNull(coordinateString) || coordinateString.isEmpty()) {
      log.error(
              "Cannot parse weather value. Unable to find field '{}' in data: {}",
              weatherFactory.getCoordinateIdFieldString(),
              fieldToValues);
      return Optional.empty();
    }
    int coordinateId = Integer.parseInt(coordinateString);
    //return coordinateSource.getCoordinate(coordinateId);
    return null;
  }

  /**
   * Merge two individual time series into a new time series with the UUID of the first parameter
   *
   * @param a the first time series to merge
   * @param b the second time series to merge
   * @return merged time series with a's UUID
   */
  private <V extends Value> IndividualTimeSeries<V> mergeTimeSeries(
          IndividualTimeSeries<V> a, IndividualTimeSeries<V> b) {
    SortedSet<TimeBasedValue<V>> entries = a.getEntries();
    entries.addAll(b.getEntries());
    return new IndividualTimeSeries<>(a.getUuid(), entries);
  }










  //------------------------------------------------------------------------------

  /**
   * Converts a field to value map into TimeBasedWeatherValueData, extracts the coordinate id from
   * the field map and uses the {@link IdCoordinateSource} to map it to a point
   *
   * @param fieldMap the field to value map for one TimeBasedValue
   * @return the TimeBasedWeatherValueData
   */
  protected Optional<TimeBasedWeatherValueData> toTimeBasedWeatherValueData(
          Map<String, String> fieldMap) {
    String coordinateValue = fieldMap.remove(COORDINATE_ID);
    fieldMap.putIfAbsent("uuid", UUID.randomUUID().toString());
    int coordinateId = Integer.parseInt(coordinateValue);
    Optional<Point> coordinate = idCoordinateSource.getCoordinate(coordinateId);
    if (coordinate.isEmpty()) {
      log.warn("Unable to match coordinate ID {} to a point", coordinateId);
      return Optional.empty();
    }
    return Optional.of(new TimeBasedWeatherValueData(fieldMap, coordinate.get()));
  }

  /**
   * Maps a collection of TimeBasedValues into time series for each contained coordinate point
   *
   * @param timeBasedValues the values to map
   * @return a map of coordinate point to time series
   */
  protected Map<Point, IndividualTimeSeries<WeatherValue>> mapWeatherValuesToPoints(
          Collection<TimeBasedValue<WeatherValue>> timeBasedValues) {
    Map<Point, Set<TimeBasedValue<WeatherValue>>> coordinateToValues =
            timeBasedValues.stream()
                    .collect(
                            Collectors.groupingBy(
                                    timeBasedWeatherValue -> timeBasedWeatherValue.getValue().getCoordinate(),
                                    Collectors.toSet()));
    Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = new HashMap<>();
    for (Map.Entry<Point, Set<TimeBasedValue<WeatherValue>>> entry :
            coordinateToValues.entrySet()) {
      Set<TimeBasedValue<WeatherValue>> values = entry.getValue();
      IndividualTimeSeries<WeatherValue> timeSeries = new IndividualTimeSeries<>(null, values);
      coordinateToTimeSeries.put(entry.getKey(), timeSeries);
    }
    return coordinateToTimeSeries;
  }


  /**
   * Converts a field to value map into a TimeBasedValue, removes the "tid"
   *
   * @param fieldMap the field to value map for one TimeBasedValue
   * @return an Optional of that TimeBasedValue
   */

  protected Optional<TimeBasedValue<WeatherValue>> createEntity(Map<String, String> fieldMap) {
    fieldMap.remove("tid");
    Optional<TimeBasedWeatherValueData> data = toTimeBasedWeatherValueData(fieldMap);
    if (data.isEmpty()) return Optional.empty();
    return weatherFactory.get(data.get());
  }




}
