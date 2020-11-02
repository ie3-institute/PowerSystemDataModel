/*
 * © 2020. TU Dortmund University,
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
import edu.ie3.util.TimeUtil;
import edu.ie3.util.interval.ClosedInterval;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Point;

/** SQL source for weather data */
public class SqlWeatherSource implements WeatherSource {
  private static final Logger logger = LogManager.getLogger(SqlWeatherSource.class);

  private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.S";
  private static final String DEFAULT_WEATHER_FETCHING_ERROR = "Error while fetching weather";
  private static final String WHERE = " WHERE ";

  private final SqlConnector connector;
  private final IdCoordinateSource idCoordinateSource;
  private final String weatherTableName;
  private final String schemaName;
  private final String coordinateColumnName;
  private final String timeColumnName;
  private final TimeBasedWeatherValueFactory weatherFactory;

  /**
   * Initializes a new SqlWeatherSource
   *
   * @param connector the connector needed for database connection
   * @param idCoordinateSource a coordinate source to map ids to points
   * @param schemaName the database schema to use
   * @param weatherTableName the name of the table containing weather data
   * @param coordinateColumnName the name of the column containing coordinate IDs
   * @param timeColumnName the name of the column containing timestamps
   */
  public SqlWeatherSource(
      SqlConnector connector,
      IdCoordinateSource idCoordinateSource,
      String weatherTableName,
      String schemaName,
      String coordinateColumnName,
      String timeColumnName) {
    this(
        connector,
        idCoordinateSource,
        weatherTableName,
        schemaName,
        coordinateColumnName,
        timeColumnName,
        DEFAULT_TIMESTAMP_PATTERN);
  }

  /**
   * Initializes a new SqlWeatherSource
   *
   * @param connector the connector needed for database connection
   * @param idCoordinateSource a coordinate source to map ids to points
   * @param schemaName the database schema to use
   * @param weatherTableName the name of the table containing weather data
   * @param coordinateColumnName the name of the column containing coordinate IDs
   * @param timeColumnName the name of the column containing timestamps
   * @param timestampPattern the pattern of the timestamps
   */
  public SqlWeatherSource(
      SqlConnector connector,
      IdCoordinateSource idCoordinateSource,
      String schemaName,
      String weatherTableName,
      String coordinateColumnName,
      String timeColumnName,
      String timestampPattern) {
    this.connector = connector;
    this.idCoordinateSource = idCoordinateSource;
    this.schemaName = schemaName;
    this.weatherTableName = weatherTableName;
    this.coordinateColumnName = coordinateColumnName;
    this.timeColumnName = timeColumnName;
    this.weatherFactory = new TimeBasedWeatherValueFactory(timestampPattern);
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval) {
    List<TimeBasedValue<WeatherValue>> timeBasedValues = Collections.emptyList();
    try (Statement stmt = connector.getConnection().createStatement()) {
      ResultSet resultSet =
          connector.executeQuery(stmt, createQueryStringForTimeInterval(timeInterval));
      List<Map<String, String>> fieldMaps = SqlConnector.extractFieldMaps(resultSet);
      timeBasedValues = toTimeBasedWeatherValues(fieldMaps);
      if (!resultSet.isClosed()) resultSet.close();
    } catch (SQLException e) {
      logger.error(DEFAULT_WEATHER_FETCHING_ERROR, e);
    }
    return mapWeatherValuesToPoints(timeBasedValues);
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
    List<TimeBasedValue<WeatherValue>> timeBasedValues = Collections.emptyList();
    Set<Integer> coordinateIds =
        coordinates.stream()
            .map(idCoordinateSource::getId)
            .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
            .collect(Collectors.toSet());
    if (coordinateIds.isEmpty()) {
      logger.warn("Unable to match coordinates to coordinate ID");
      return Collections.emptyMap();
    }
    try (Statement stmt = connector.getConnection().createStatement()) {
      ResultSet resultSet =
          connector.executeQuery(
              stmt, createQueryStringForTimeIntervalAndCoordinates(timeInterval, coordinateIds));
      List<Map<String, String>> fieldMaps = SqlConnector.extractFieldMaps(resultSet);
      timeBasedValues = toTimeBasedWeatherValues(fieldMaps);
      if (!resultSet.isClosed()) resultSet.close();
    } catch (SQLException e) {
      logger.error(DEFAULT_WEATHER_FETCHING_ERROR, e);
    }
    return mapWeatherValuesToPoints(timeBasedValues);
  }

  @Override
  public Optional<TimeBasedValue<WeatherValue>> getWeather(ZonedDateTime date, Point coordinate) {
    List<TimeBasedValue<WeatherValue>> timeBasedValues = Collections.emptyList();
    Optional<Integer> coordinateId = idCoordinateSource.getId(coordinate);
    if (!coordinateId.isPresent()) {
      logger.warn("Unable to match coordinate {} to a coordinate ID", coordinate);
      return Optional.empty();
    }
    try (Statement stmt = connector.getConnection().createStatement()) {
      ResultSet resultSet =
          connector.executeQuery(
              stmt, createQueryStringForTimeAndCoordinate(date, coordinateId.get()));
      List<Map<String, String>> fieldMaps = SqlConnector.extractFieldMaps(resultSet);
      timeBasedValues = toTimeBasedWeatherValues(fieldMaps);
      if (!resultSet.isClosed()) resultSet.close();
    } catch (SQLException e) {
      logger.error(DEFAULT_WEATHER_FETCHING_ERROR, e);
    }
    if (timeBasedValues.isEmpty()) return Optional.empty();
    if (timeBasedValues.size() > 1)
      logger.warn("Retrieved more than one result value, using the first");
    return Optional.of(timeBasedValues.get(0));
  }

  /**
   * Creates a basic query string without closing semicolon
   *
   * @return basic query string without semicolon
   */
  private String createBasicQueryString() {
    return "SELECT * FROM " + schemaName + "." + weatherTableName;
  }

  /**
   * Creates a basic query to retrieve all entities in the given time frame
   *
   * @param timeInterval the time frame for the query
   * @return the query string
   */
  private String createQueryStringForTimeInterval(ClosedInterval<ZonedDateTime> timeInterval) {
    return createBasicQueryString() + WHERE + createTimeConstraint(timeInterval) + ";";
  }

  /**
   * Creates a basic query to retrieve an entry for the given time and coordinate
   *
   * @param time the timestamp for the query
   * @param coordinateId the queried coordinate ID
   * @return the query string
   */
  private String createQueryStringForTimeAndCoordinate(ZonedDateTime time, int coordinateId) {
    return createBasicQueryString()
        + WHERE
        + createCoordinateConstraint(coordinateId)
        + " AND "
        + createTimeConstraint(time)
        + ";";
  }

  /**
   * Creates a basic query to retrieve all entities in the given time frame and coordinates
   *
   * @param timeInterval the time frame for the query
   * @param coordinateIds the allowed coordinate IDs
   * @return the query string
   */
  private String createQueryStringForTimeIntervalAndCoordinates(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Integer> coordinateIds) {
    return createBasicQueryString()
        + WHERE
        + createCoordinateConstraint(coordinateIds)
        + " AND "
        + createTimeConstraint(timeInterval)
        + ";";
  }

  /**
   * Creates a simple time constraint like "time='2020-04-28 15:00:00'"
   *
   * @param time the time to use
   * @return the constraint string
   */
  private String createTimeConstraint(ZonedDateTime time) {
    return timeColumnName + "='" + TimeUtil.withDefaults.toString(time) + "'";
  }

  /**
   * Creates a time frame constraint like "time BETWEEN '2020-04-28 15:00:00' AND '2020-04-28
   * 17:00:00'"
   *
   * @param timeInterval the time interval to use
   * @return the constraint string
   */
  private String createTimeConstraint(ClosedInterval<ZonedDateTime> timeInterval) {
    return timeColumnName
        + " BETWEEN '"
        + TimeUtil.withDefaults.toString(timeInterval.getLower())
        + "' AND '"
        + TimeUtil.withDefaults.toString(timeInterval.getUpper())
        + "'";
  }

  /**
   * Creates a simple coordinate constraint
   *
   * @param coordinateId the coordinate ID
   * @return the constraint string
   */
  private String createCoordinateConstraint(int coordinateId) {
    return coordinateColumnName + "=" + coordinateId;
  }

  /**
   * Creates a constraint for multiple coordinates, like: "coordinate IN (193186, 193187, 193188)"
   *
   * @param coordinateIds the coordinate points
   * @return the constraint string
   */
  private String createCoordinateConstraint(Collection<Integer> coordinateIds) {
    String constraint = coordinateColumnName + " IN (";
    constraint += coordinateIds.stream().map(Object::toString).collect(Collectors.joining(", "));
    constraint += ")";
    return constraint;
  }

  /**
   * Converts a collection of field to value maps into TimeBasedValues
   *
   * @param fieldMaps the field to value maps, one for each TimeBasedValue
   * @return a list of TimeBasedValues
   */
  private List<TimeBasedValue<WeatherValue>> toTimeBasedWeatherValues(
      Collection<Map<String, String>> fieldMaps) {
    return fieldMaps.stream()
        .map(this::toTimeBasedWeatherValue)
        .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
        .collect(Collectors.toList());
  }

  /**
   * Converts a field to value map into a TimeBasedValue, removes the "tid"
   *
   * @param fieldMap the field to value map for one TimeBasedValue
   * @return an Optional of that TimeBasedValue
   */
  private Optional<TimeBasedValue<WeatherValue>> toTimeBasedWeatherValue(
      Map<String, String> fieldMap) {
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
    String coordinateValue = fieldMap.remove(coordinateColumnName);
    fieldMap.putIfAbsent("uuid", UUID.randomUUID().toString());
    int coordinateId = Integer.parseInt(coordinateValue);
    Optional<Point> coordinate = idCoordinateSource.getCoordinate(coordinateId);
    if (!coordinate.isPresent()) {
      logger.warn("Unable to match coordinate ID {} to a point", coordinateId);
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
