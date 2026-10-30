/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.io.naming.FieldNamingStrategy.WEATHER_COORDINATE_ID;

import edu.ie3.datamodel.exceptions.NoDataException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.FieldMap;
import edu.ie3.datamodel.io.factory.timeseries.CosmoTimeBasedWeatherValueFactory;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueFactory;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.util.interval.ClosedInterval;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.locationtech.jts.geom.Point;

/** Abstract class for WeatherSource by Csv and Sql Data */
public abstract class WeatherSource extends EntitySource {

  protected TimeBasedWeatherValueFactory weatherFactory;

  protected IdCoordinateSource idCoordinateSource;

  /**
   * Maximum number of time steps to look back when falling back to the last known weather value. If
   * the most recent known data is more than this many steps before the requested time, no fallback
   * is used and a {@link edu.ie3.datamodel.exceptions.NoDataException} is thrown.
   */
  public static final int MAX_FALLBACK_STEPS = 3;

  /**
   * Checks whether a fallback timestamp is acceptable based on the maximum number of allowed steps.
   * The step size is inferred from two consecutive known timestamps. If the step size cannot be
   * determined (only one data point available before the requested time), the fallback is accepted.
   *
   * @param requested the originally requested time
   * @param fallback the most recent known timestamp before the requested time
   * @param stepReference the timestamp just before the fallback (used to infer step size), or
   *     {@code null} if unavailable
   * @return {@code true} if the fallback is within {@link #MAX_FALLBACK_STEPS} of the requested
   *     time
   */
  protected static boolean isFallbackAcceptable(
      ZonedDateTime requested, ZonedDateTime fallback, ZonedDateTime stepReference)
      throws SourceException {
    if (stepReference == null) {
      log.warn(
          "Cannot determine time step size for fallback (only one data point available before {}). "
              + "Accepting fallback from {} unconditionally.",
          requested,
          fallback);
      return true;
    }
    Duration step = Duration.between(stepReference, fallback);
    if (step.isNegative()) {
      throw new SourceException(
          "Data inconsistency detected: step reference "
              + stepReference
              + " is after fallback timestamp "
              + fallback
              + " when checking fallback for "
              + requested
              + ". Cannot determine a valid time step.");
    }
    if (step.isZero()) {
      // Two consecutive known timestamps are identical, step size cannot be determined
      log.warn(
          "Cannot determine time step size for fallback (duplicate timestamps at {}). "
              + "Accepting fallback from {} unconditionally.",
          fallback,
          fallback);
      return true;
    }
    Duration gap = Duration.between(fallback, requested);
    return gap.compareTo(step.multipliedBy(MAX_FALLBACK_STEPS)) <= 0;
  }

  protected WeatherSource(
      IdCoordinateSource idCoordinateSource, TimeBasedWeatherValueFactory weatherFactory) {
    this.idCoordinateSource = idCoordinateSource;
    this.weatherFactory = weatherFactory;
  }

  /** Returns the class of the value returned by this source. */
  protected Class<? extends WeatherValue> getInputClass() {
    if (weatherFactory instanceof CosmoTimeBasedWeatherValueFactory) {
      return WeatherValue.CosmoWeatherValue.class;
    } else {
      return WeatherValue.IconWeatherValue.class;
    }
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
   * Returns the weather for the given time interval, mapped against the coordinate ids.
   *
   * <p>Preferable over {@link #getWeather(ClosedInterval)}, as matching coordinates relies on the
   * hash code of {@link Point} and therefore on the exact double values of its coordinates.
   *
   * @param timeInterval the queried time interval
   * @return a map of coordinate id to time series
   */
  public abstract Map<Integer, IndividualTimeSeries<WeatherValue>> getWeatherById(
      ClosedInterval<ZonedDateTime> timeInterval) throws SourceException, NoDataException;

  /**
   * Returns the weather for the given time interval, mapped against the coordinates. Coordinate ids
   * that cannot be matched to a coordinate are logged and skipped.
   *
   * @param timeInterval the queried time interval
   * @return a map of coordinate to time series
   */
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval) throws SourceException, NoDataException {
    return toCoordinateKeys(getWeatherById(timeInterval));
  }

  /**
   * Returns the weather for the given time interval and coordinate ids.
   *
   * <p>Preferable over {@link #getWeather(ClosedInterval, Collection)}, as matching coordinates
   * relies on the hash code of {@link Point}.
   *
   * <p>In contrast to {@link #getWeather(ClosedInterval, Collection)} this does not throw, if no
   * data is found at all. Ids without data are simply missing from the returned map.
   *
   * @param timeInterval the queried time interval
   * @param coordinateIds the queried coordinate ids
   * @return a map of coordinate id to time series
   */
  public abstract Map<Integer, IndividualTimeSeries<WeatherValue>> getWeatherById(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Integer> coordinateIds)
      throws SourceException, NoDataException;

  /**
   * Returns the weather for the given time interval and coordinates. Coordinates that cannot be
   * matched to an id are logged and skipped.
   *
   * @param timeInterval the queried time interval
   * @param coordinates the queried coordinates
   * @return a map of coordinate to time series
   */
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates)
      throws SourceException, NoDataException {
    if (coordinates.isEmpty())
      throw new NoDataException("No coordinates provided for weather data query.");

    Set<Integer> coordinateIds = new LinkedHashSet<>();
    List<Point> unknownCoordinates = new ArrayList<>();

    for (Point coordinate : coordinates) {
      Optional<Integer> id = idCoordinateSource.getId(coordinate);

      if (id.isPresent()) {
        coordinateIds.add(id.get());
      } else {
        unknownCoordinates.add(coordinate);
      }
    }

    if (!unknownCoordinates.isEmpty())
      log.warn(
          "Unable to find coordinate IDs for the following coordinates, skipping: {}",
          unknownCoordinates);

    return validateAndWarnMissing(
        toCoordinateKeys(getWeatherById(timeInterval, coordinateIds)), coordinates, timeInterval);
  }

  /**
   * Returns the weather value for the given date and coordinate.
   *
   * <p>If no exact match exists at {@code date}, a fallback to the last known value is attempted.
   * The fallback is accepted if the gap between the most recent known timestamp and {@code date} is
   * at most {@link #MAX_FALLBACK_STEPS} time steps. When a fallback is used, a warning is logged
   * and the returned {@link TimeBasedValue} carries the <em>fallback's own timestamp</em>, not
   * {@code date}.
   *
   * @param date the requested date and time
   * @param coordinate the requested coordinate
   * @return the weather value at {@code date}, or the nearest preceding value within the fallback
   *     window
   * @throws NoDataException if no data exists for the coordinate, if the nearest preceding value
   *     exceeds the fallback window, or if no prior data is available at all
   * @throws SourceException if a technical failure prevents reading from the source
   */
  public abstract TimeBasedValue<WeatherValue> getWeather(ZonedDateTime date, Point coordinate)
      throws SourceException, NoDataException;

  /**
   * Returns all time keys after the given timestamp, mapped against the coordinate ids.
   *
   * @param time the timestamp to start from
   * @return a map of coordinate id to time keys
   */
  public abstract Map<Integer, List<ZonedDateTime>> getTimeKeysAfterById(ZonedDateTime time)
      throws SourceException;

  /**
   * Returns all time keys after the given timestamp for the given coordinate id.
   *
   * @param time the timestamp to start from
   * @param coordinateId the queried coordinate id
   * @return the time keys of that coordinate id
   */
  public List<ZonedDateTime> getTimeKeysAfterById(ZonedDateTime time, int coordinateId)
      throws SourceException {
    return getTimeKeysAfterById(time).getOrDefault(coordinateId, Collections.emptyList());
  }

  /**
   * Returns all time keys after the given timestamp, mapped against the coordinates.
   *
   * @param time the timestamp to start from
   * @return a map of coordinate to time keys
   */
  public Map<Point, List<ZonedDateTime>> getTimeKeysAfter(ZonedDateTime time)
      throws SourceException {
    return toCoordinateKeys(getTimeKeysAfterById(time));
  }

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
      Map<String, String> fieldMap) throws SourceException {
    String coordinateValue = fieldMap.remove(WEATHER_COORDINATE_ID);
    fieldMap.putIfAbsent("uuid", UUID.randomUUID().toString());
    int coordinateId = Integer.parseInt(coordinateValue);
    Optional<Point> coordinate = idCoordinateSource.getCoordinate(coordinateId);
    if (coordinate.isEmpty()) {
      log.warn("Unable to match coordinate ID {} to a point", coordinateId);
      return Optional.empty();
    }
    return Optional.of(
        new TimeBasedWeatherValueData(new FieldMap(fieldMap), coordinate.get(), coordinateId));
  }

  /**
   * Maps a collection of TimeBasedValues into time series for each contained coordinate id. Values
   * whose coordinate id is unknown are logged and skipped.
   *
   * @param timeBasedValues the values to map
   * @return a map of coordinate id to time series
   */
  protected Map<Integer, IndividualTimeSeries<WeatherValue>> mapWeatherValuesToIds(
      Collection<TimeBasedValue<WeatherValue>> timeBasedValues) {
    Map<Integer, Set<TimeBasedValue<WeatherValue>>> idToValues = new HashMap<>();

    for (TimeBasedValue<WeatherValue> timeBasedValue : timeBasedValues) {
      Optional<Integer> coordinateId = timeBasedValue.getValue().coordinateId();

      if (coordinateId.isEmpty()) {
        log.warn(
            "Skipping weather value at {} because its coordinate id is unknown",
            timeBasedValue.getTime());
      } else {
        idToValues.computeIfAbsent(coordinateId.get(), id -> new HashSet<>()).add(timeBasedValue);
      }
    }

    Map<Integer, IndividualTimeSeries<WeatherValue>> idToTimeSeriesMap = new HashMap<>();
    for (Map.Entry<Integer, Set<TimeBasedValue<WeatherValue>>> entry : idToValues.entrySet()) {
      idToTimeSeriesMap.put(entry.getKey(), new IndividualTimeSeries<>(entry.getValue()));
    }
    return idToTimeSeriesMap;
  }

  protected Map<Integer, List<ZonedDateTime>> toTimeKeys(
      Stream<Map<String, String>> fieldMaps, TimeBasedWeatherValueFactory factory)
      throws SourceException {
    return groupTime(
        Try.scanStream(
                fieldMaps.map(
                    fieldMap ->
                        Try.of(
                            () -> {
                              String coordinateValue = fieldMap.get(WEATHER_COORDINATE_ID);
                              int coordinateId = Integer.parseInt(coordinateValue);
                              ZonedDateTime time = factory.extractTime(fieldMap);

                              return Pair.of(coordinateId, time);
                            },
                            SourceException.class)),
                "time key",
                SourceException::new)
            .getOrThrow());
  }

  /**
   * Translates a map that is keyed by coordinate id into one that is keyed by the corresponding
   * {@link Point}. Ids that cannot be matched to a coordinate are logged and dropped.
   *
   * @param byId the map keyed by coordinate id
   * @param <V> type of the mapped values
   * @return the same content, keyed by coordinate
   * @throws SourceException if the coordinates cannot be read
   */
  protected <V> Map<Point, V> toCoordinateKeys(Map<Integer, V> byId) throws SourceException {
    Map<Point, V> result = new HashMap<>();

    for (Map.Entry<Integer, V> entry : byId.entrySet()) {
      Optional<Point> coordinate = idCoordinateSource.getCoordinate(entry.getKey());

      if (coordinate.isEmpty()) {
        log.warn("Unable to match coordinate ID {} to a point", entry.getKey());
      } else {
        result.put(coordinate.get(), entry.getValue());
      }
    }

    return result;
  }

  protected Map<Integer, List<ZonedDateTime>> groupTime(
      Stream<Pair<Integer, ZonedDateTime>> values) {
    return values
        .collect(Collectors.groupingBy(Pair::getKey, Collectors.toSet()))
        .entrySet()
        .stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().stream().map(Pair::getValue).sorted().toList()));
  }

  /**
   * Applies the fallback decision when no exact match exists at the requested time.
   *
   * <p>The caller passes the up to two most recent {@link TimeBasedValue}s before {@code date},
   * ordered most-recent first. If the list is empty, a {@link NoDataException} is thrown. Otherwise
   * {@link #isFallbackAcceptable} decides: on success a warning is logged and the most recent value
   * is returned. On failure a {@link NoDataException} describing the exceeded fallback window is
   * thrown.
   *
   * @param date the originally requested date
   * @param coordinate the requested coordinate (used only for log/error messages)
   * @param fallbackValues up to two most recent values strictly before {@code date}, ordered
   *     most-recent first
   * @return the most recent fallback value, when accepted
   * @throws NoDataException if no fallback is available or the gap exceeds the allowed steps
   * @throws SourceException if the two fallback timestamps are inconsistently ordered
   */
  protected TimeBasedValue<WeatherValue> applyFallbackOrThrow(
      ZonedDateTime date, Point coordinate, List<TimeBasedValue<WeatherValue>> fallbackValues)
      throws SourceException, NoDataException {
    if (fallbackValues.isEmpty()) {
      throw new NoDataException(
          "No weather data found for coordinate "
              + coordinate
              + " at "
              + date
              + " and no earlier data available.");
    }
    ZonedDateTime fallbackTime = fallbackValues.get(0).getTime();
    ZonedDateTime stepRef = fallbackValues.size() > 1 ? fallbackValues.get(1).getTime() : null;
    if (isFallbackAcceptable(date, fallbackTime, stepRef)) {
      log.warn(
          "No weather data for coordinate {} at {}. Using last known value from {}.",
          coordinate,
          date,
          fallbackTime);
      return fallbackValues.getFirst();
    }
    throw new NoDataException(
        "No weather data found for coordinate "
            + coordinate
            + " at "
            + date
            + ": last known value from "
            + fallbackTime
            + " exceeds the maximum fallback of "
            + MAX_FALLBACK_STEPS
            + " steps.");
  }

  /**
   * Validates that the result map is not empty and warns about coordinates with no data.
   *
   * @param result the result map to validate
   * @param coordinates the requested coordinates
   * @param timeInterval the requested time interval
   * @param <K> type of the key the results are mapped to
   * @return the result map if valid
   * @throws NoDataException if the result map is empty
   */
  protected <K> Map<K, IndividualTimeSeries<WeatherValue>> validateAndWarnMissing(
      Map<K, IndividualTimeSeries<WeatherValue>> result,
      Collection<K> coordinates,
      ClosedInterval<ZonedDateTime> timeInterval)
      throws NoDataException {
    if (result.isEmpty()) {
      throw new NoDataException(
          "No weather data found for any of the requested coordinates in the given time interval: "
              + timeInterval);
    }
    Set<K> missing =
        coordinates.stream().filter(c -> !result.containsKey(c)).collect(Collectors.toSet());
    if (!missing.isEmpty())
      log.warn("No weather data in interval {} for coordinates: {}", timeInterval, missing);
    return result;
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
   * Converts a stream of fields to value map into a TimeBasedValue, removes the "tid"
   *
   * @param factory TimeBasedWeatherValueFactory
   * @param inputStream stream of fields to convert into TimeBasedValues
   * @return a list of that TimeBasedValues
   */
  protected List<TimeBasedValue<WeatherValue>> buildTimeBasedValues(
      TimeBasedWeatherValueFactory factory, Stream<Map<String, String>> inputStream)
      throws SourceException {
    return Try.scanStream(
            inputStream.map(
                fieldsToAttributes ->
                    Try.of(
                        () -> {
                          fieldsToAttributes.remove("tid");
                          return toTimeBasedWeatherValueData(fieldsToAttributes);
                        },
                        SourceException.class)),
            "weather value data",
            SourceException::new)
        .getOrThrow()
        .flatMap(Optional::stream)
        .map(factory::get)
        .map(
            tryResult -> {
              if (tryResult.isFailure()) {
                tryResult
                    .getException()
                    .ifPresent(
                        e -> log.warn("Skipping malformed weather value: {}", e.getMessage()));
                return Optional.<TimeBasedValue<WeatherValue>>empty();
              }
              return tryResult.getData();
            })
        .flatMap(Optional::stream)
        .toList();
  }
}
