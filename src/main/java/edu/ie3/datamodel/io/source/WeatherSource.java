/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
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
import javax.measure.Quantity;
import org.apache.commons.lang3.tuple.Pair;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.ComparableQuantity;

/** Abstract class for WeatherSource by Csv and Sql Data */
public abstract class WeatherSource extends EntitySource {

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
   * @return an option for fields found in the source
   */
  public abstract Optional<Set<String>> getSourceFields() throws SourceException;

  @Override
  public void validate() throws ValidationException {
    validate(WeatherValue.class, this::getSourceFields, weatherFactory);
  }

  private WeatherValue interpolateWeatherValue(WeatherValue start, WeatherValue end, double ratio) {
    var direct = interpolateOptional(start.getDirectIrradiance(), end.getDirectIrradiance(), ratio);
    var diffuse =
        interpolateOptional(start.getDiffuseIrradiance(), end.getDiffuseIrradiance(), ratio);

    var temp = interpolateOptional(start.getTemperatureValue(), end.getTemperatureValue(), ratio);
    var dir = interpolateOptional(start.getWindDirection(), end.getWindDirection(), ratio);
    var vel = interpolateOptional(start.getWindVelocity(), end.getWindVelocity(), ratio);

    return new WeatherValue(start.getCoordinate(), direct, diffuse, temp, dir, vel);
  }

  private <Q extends Quantity<Q>> ComparableQuantity<Q> interpolateOptional(
      Optional<ComparableQuantity<Q>> startOpt,
      Optional<ComparableQuantity<Q>> endOpt,
      double ratio) {
    return startOpt
        .flatMap(startVal -> endOpt.map(endVal -> interpolateQuantity(startVal, endVal, ratio)))
        .orElse(null);
  }

  private <Q extends Quantity<Q>> ComparableQuantity<Q> interpolateQuantity(
      ComparableQuantity<Q> a, ComparableQuantity<Q> b, double ratio) {
    return a.add(b.subtract(a).multiply(ratio));
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  public abstract Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval) throws SourceException;

  public abstract Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates)
      throws SourceException;

  public abstract Optional<TimeBasedValue<WeatherValue>> getWeather(
      ZonedDateTime date, Point coordinate) throws SourceException;

  public Optional<WeatherValue> getWeatherInterpolated(
      ZonedDateTime date, Point coordinate, int plus, int minus) throws SourceException {

    ClosedInterval<ZonedDateTime> interpolationInterval =
        new ClosedInterval<>(date.minusHours(minus), date.plusHours(plus));
    IndividualTimeSeries<WeatherValue> ts =
        getWeather(interpolationInterval, List.of(coordinate)).get(coordinate);

    if (ts == null) {
      log.warn("No time series available for coordinate {}", coordinate);
      return Optional.empty();
    }

    Optional<WeatherValue> value = ts.getValue(date);

    if (value.isPresent() && value.get().hasPartialValues()) {
      return value;
    }

    Optional<TimeBasedValue<WeatherValue>> prevValue = ts.getPreviousTimeBasedValue(date);
    Optional<TimeBasedValue<WeatherValue>> nextValue = ts.getNextTimeBasedValue(date);

    if (prevValue.isEmpty() || nextValue.isEmpty()) {
      log.warn(
          "Not enough data to interpolate weather value at {} for coordinate {}", date, coordinate);
      return Optional.empty();
    }

    TimeBasedValue<WeatherValue> prev = prevValue.get();
    TimeBasedValue<WeatherValue> next = nextValue.get();

    Duration totalDuration = Duration.between(prev.getTime(), next.getTime());
    Duration partialDuration = Duration.between(prev.getTime(), date);

    if (totalDuration.isZero()) {
      return Optional.of(prev.getValue());
    }

    double ratio = (double) partialDuration.toSeconds() / totalDuration.toSeconds();

    WeatherValue interpolated = interpolateWeatherValue(prev.getValue(), next.getValue(), ratio);

    return Optional.of(interpolated);
  }

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

  protected Map<Point, List<ZonedDateTime>> toTimeKeys(
      Stream<Map<String, String>> fieldMaps, TimeBasedWeatherValueFactory factory) {
    return groupTime(
        fieldMaps.map(
            fieldMap -> {
              String coordinateValue = fieldMap.get(COORDINATE_ID);
              int coordinateId = Integer.parseInt(coordinateValue);
              Optional<Point> coordinate = idCoordinateSource.getCoordinate(coordinateId);
              ZonedDateTime time = factory.extractTime(fieldMap);

              if (coordinate.isEmpty()) {
                log.warn("Unable to match coordinate ID {} to a point", coordinateId);
              }
              return Pair.of(coordinate, time);
            }));
  }

  protected Map<Point, List<ZonedDateTime>> groupTime(
      Stream<Pair<Optional<Point>, ZonedDateTime>> values) {
    return values
        .filter(pair -> pair.getKey().isPresent())
        .map(pair -> Pair.of(pair.getKey().get(), pair.getValue()))
        .collect(Collectors.groupingBy(Pair::getKey, Collectors.toSet()))
        .entrySet()
        .stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().stream().map(Pair::getValue).sorted().toList()));
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
