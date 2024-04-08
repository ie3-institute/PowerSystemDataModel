/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueFactory;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Abstract class for WeatherSource by Csv and Sql Data */
public abstract class WeatherSource {

  protected static final Logger log = LoggerFactory.getLogger(WeatherSource.class);

  protected TimeBasedWeatherValueFactory weatherFactory;

  protected IdCoordinateSource idCoordinateSource;

  protected static final String COORDINATE_ID = "coordinateid";

  protected WeatherSource(
      IdCoordinateSource idCoordinateSource, TimeBasedWeatherValueFactory weatherFactory) {
    this.idCoordinateSource = idCoordinateSource;
    this.weatherFactory = weatherFactory;
  }

  /**
   * Method to retrieve the fields found in the source.
   *
   * @param entityClass class of the source
   * @return an option for fields found in the source
   */
  public abstract <C extends WeatherValue> Optional<Set<String>> getSourceFields(
      Class<C> entityClass) throws SourceException;

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  public abstract Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval) throws SourceException;

  public abstract Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates)
      throws SourceException;

  public abstract Optional<TimeBasedValue<WeatherValue>> getWeather(
      ZonedDateTime date, Point coordinate) throws SourceException;

  public abstract Map<Point, List<ZonedDateTime>> getTimeKeysAfter(ZonedDateTime time)
      throws SourceException;

  public List<ZonedDateTime> getTimeKeysAfter(ZonedDateTime time, Point coordinate)
      throws SourceException {
    return getTimeKeysAfter(time).getOrDefault(coordinate, Collections.emptyList());
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

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
    Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeriesMap = new HashMap<>();
    for (Map.Entry<Point, Set<TimeBasedValue<WeatherValue>>> entry :
        coordinateToValues.entrySet()) {
      Set<TimeBasedValue<WeatherValue>> values = entry.getValue();
      IndividualTimeSeries<WeatherValue> timeSeries = new IndividualTimeSeries<>(values);
      coordinateToTimeSeriesMap.put(entry.getKey(), timeSeries);
    }
    return coordinateToTimeSeriesMap;
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
   * Converts a stream of fields to value map into a TimeBasedValue, removes the "tid"
   *
   * @param factory TimeBasedWeatherValueFactory
   * @param inputStream stream of fields to convert into TimeBasedValues
   * @return a list of that TimeBasedValues
   */
  public List<TimeBasedValue<WeatherValue>> buildTimeBasedValues(
      TimeBasedWeatherValueFactory factory, Stream<Map<String, String>> inputStream)
      throws SourceException {
    return Try.scanStream(
            inputStream.map(
                fieldsToAttributes -> {
                  fieldsToAttributes.remove("tid");
                  Optional<TimeBasedWeatherValueData> data =
                      toTimeBasedWeatherValueData(fieldsToAttributes);
                  return factory.get(
                      Try.from(data, () -> new SourceException("Missing data in: " + data)));
                }),
            "TimeBasedValue<WeatherValue>")
        .transform(Stream::toList, SourceException::new)
        .getOrThrow();
  }
}
