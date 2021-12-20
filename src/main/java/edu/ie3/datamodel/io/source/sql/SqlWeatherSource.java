/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueFactory;
import edu.ie3.datamodel.io.source.IdCoordinateSource;
import edu.ie3.datamodel.io.source.WeatherSource;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.util.interval.ClosedInterval;
import java.sql.*;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.locationtech.jts.geom.Point;

/** SQL source for weather data */
public class SqlWeatherSource extends SqlDataSource<TimeBasedValue<WeatherValue>>
    implements WeatherSource {
  private static final String WHERE = " WHERE ";

  private final IdCoordinateSource idCoordinateSource;
  private final String factoryCoordinateFieldName;
  private final TimeBasedWeatherValueFactory weatherFactory;

  /**
   * Queries that are available within this source. Motivation to have them as field value is to
   * avoid creating a new string each time, bc they're always the same.
   */
  private final String queryTimeInterval;

  private final String queryTimeAndCoordinate;
  private final String queryTimeIntervalAndCoordinates;

  /**
   * Initializes a new SqlWeatherSource
   *
   * @param connector the connector needed for database connection
   * @param idCoordinateSource a coordinate source to map ids to points
   * @param schemaName the database schema to use
   * @param weatherTableName the name of the table containing weather data
   * @param weatherFactory instance of a time based weather value factory
   */
  public SqlWeatherSource(
      SqlConnector connector,
      IdCoordinateSource idCoordinateSource,
      String schemaName,
      String weatherTableName,
      TimeBasedWeatherValueFactory weatherFactory) {
    super(connector);
    this.idCoordinateSource = idCoordinateSource;
    this.weatherFactory = weatherFactory;
    this.factoryCoordinateFieldName = weatherFactory.getCoordinateIdFieldString();

    String dbTimeColumnName =
        getDbColumnName(weatherFactory.getTimeFieldString(), connector, weatherTableName);
    String dbCoordColumnName =
        getDbColumnName(factoryCoordinateFieldName, connector, weatherTableName);

    // setup queries
    this.queryTimeInterval =
        createQueryStringForTimeInterval(schemaName, weatherTableName, dbTimeColumnName);
    this.queryTimeAndCoordinate =
        createQueryStringForTimeAndCoordinate(
            schemaName, weatherTableName, dbTimeColumnName, dbCoordColumnName);
    this.queryTimeIntervalAndCoordinates =
        createQueryStringForTimeIntervalAndCoordinates(
            schemaName, weatherTableName, dbTimeColumnName, dbCoordColumnName);
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval) {
    List<TimeBasedValue<WeatherValue>> timeBasedValues =
        executeQuery(
            queryTimeInterval,
            ps -> {
              ps.setTimestamp(1, Timestamp.from(timeInterval.getLower().toInstant()));
              ps.setTimestamp(2, Timestamp.from(timeInterval.getUpper().toInstant()));
            });
    return mapWeatherValuesToPoints(timeBasedValues);
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
    Set<Integer> coordinateIds =
        coordinates.stream()
            .map(idCoordinateSource::getId)
            .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
            .collect(Collectors.toSet());
    if (coordinateIds.isEmpty()) {
      log.warn("Unable to match coordinates to coordinate ID");
      return Collections.emptyMap();
    }

    List<TimeBasedValue<WeatherValue>> timeBasedValues =
        executeQuery(
            queryTimeIntervalAndCoordinates,
            ps -> {
              Array coordinateIdArr =
                  ps.getConnection().createArrayOf("integer", coordinateIds.toArray());
              ps.setArray(1, coordinateIdArr);
              ps.setTimestamp(2, Timestamp.from(timeInterval.getLower().toInstant()));
              ps.setTimestamp(3, Timestamp.from(timeInterval.getUpper().toInstant()));
            });

    return mapWeatherValuesToPoints(timeBasedValues);
  }

  @Override
  public Optional<TimeBasedValue<WeatherValue>> getWeather(ZonedDateTime date, Point coordinate) {
    Optional<Integer> coordinateId = idCoordinateSource.getId(coordinate);
    if (!coordinateId.isPresent()) {
      log.warn("Unable to match coordinate {} to a coordinate ID", coordinate);
      return Optional.empty();
    }

    List<TimeBasedValue<WeatherValue>> timeBasedValues =
        executeQuery(
            queryTimeAndCoordinate,
            ps -> {
              ps.setInt(1, coordinateId.get());
              ps.setTimestamp(2, Timestamp.from(date.toInstant()));
            });

    if (timeBasedValues.isEmpty()) return Optional.empty();
    if (timeBasedValues.size() > 1)
      log.warn("Retrieved more than one result value, using the first");
    return Optional.of(timeBasedValues.get(0));
  }

  /**
   * Creates a base query string without closing semicolon of the following pattern: <br>
   * {@code SELECT * FROM <schema>.<table>}
   *
   * @param schemaName the name of the database schema
   * @param weatherTableName the name of the database table
   * @return basic query string without semicolon
   */
  private static String createBaseQueryString(String schemaName, String weatherTableName) {
    return "SELECT * FROM " + schemaName + "." + weatherTableName;
  }

  /**
   * Creates a base query to retrieve all entities in the given time frame with the following
   * pattern: <br>
   * {@code <base query> WHERE <time column> BETWEEN ? AND ?;}
   *
   * @param schemaName the name of the database schema
   * @param weatherTableName the name of the database table
   * @param timeColumnName the name of the column holding the timestamp info
   * @return the query string
   */
  private static String createQueryStringForTimeInterval(
      String schemaName, String weatherTableName, String timeColumnName) {
    return createBaseQueryString(schemaName, weatherTableName)
        + WHERE
        + timeColumnName
        + " BETWEEN ? AND ?;";
  }

  /**
   * Creates a basic query to retrieve an entry for the given time and coordinate with the following
   * pattern: <br>
   * {@code <base query> WHERE <coordinate column>=? AND <time column>=?;}
   *
   * @param schemaName the name of the database schema
   * @param weatherTableName the name of the database table
   * @param timeColumnName the name of the column holding the timestamp info
   * @param coordinateColumnName name of the column holding the coordinate id
   * @return the query string
   */
  private String createQueryStringForTimeAndCoordinate(
      String schemaName,
      String weatherTableName,
      String timeColumnName,
      String coordinateColumnName) {
    return createBaseQueryString(schemaName, weatherTableName)
        + WHERE
        + coordinateColumnName
        + "=? AND "
        + timeColumnName
        + "=?;";
  }

  /**
   * Creates a basic query to retrieve all entities in the given time frame and coordinates with the
   * following pattern: <br>
   * {@code <base query> WHERE <coordinate column>= ANY (?) AND <time column> BETWEEN ? AND ?;}
   *
   * @param schemaName the name of the database schema
   * @param weatherTableName the name of the database table
   * @param timeColumnName the name of the column holding the timestamp info
   * @param coordinateColumnName name of the column holding the coordinate id
   * @return the query string
   */
  private String createQueryStringForTimeIntervalAndCoordinates(
      String schemaName,
      String weatherTableName,
      String timeColumnName,
      String coordinateColumnName) {
    return createBaseQueryString(schemaName, weatherTableName)
        + WHERE
        + coordinateColumnName
        + "= ANY (?) AND "
        + timeColumnName
        + " BETWEEN ? AND ?;";
  }

  /**
   * Converts a field to value map into a TimeBasedValue, removes the "tid"
   *
   * @param fieldMap the field to value map for one TimeBasedValue
   * @return an Optional of that TimeBasedValue
   */
  @Override
  protected Optional<TimeBasedValue<WeatherValue>> createEntity(Map<String, String> fieldMap) {
    fieldMap.remove("tid");
    Optional<TimeBasedWeatherValueData> data = toTimeBasedWeatherValueData(fieldMap);
    if (!data.isPresent()) return Optional.empty();
    return weatherFactory.get(data.get());
  }

  /**
   * Converts a field to value map into TimeBasedWeatherValueData, extracts the coordinate id from
   * the field map and uses the {@link IdCoordinateSource} to map it to a point
   *
   * @param fieldMap the field to value map for one TimeBasedValue
   * @return the TimeBasedWeatherValueData
   */
  private Optional<TimeBasedWeatherValueData> toTimeBasedWeatherValueData(
      Map<String, String> fieldMap) {
    String coordinateValue = fieldMap.remove(factoryCoordinateFieldName);
    fieldMap.putIfAbsent("uuid", UUID.randomUUID().toString());
    int coordinateId = Integer.parseInt(coordinateValue);
    Optional<Point> coordinate = idCoordinateSource.getCoordinate(coordinateId);
    if (!coordinate.isPresent()) {
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
  private Map<Point, IndividualTimeSeries<WeatherValue>> mapWeatherValuesToPoints(
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
}
