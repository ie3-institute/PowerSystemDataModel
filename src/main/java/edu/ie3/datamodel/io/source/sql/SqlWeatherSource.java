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
import edu.ie3.util.TimeTools;
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
  private static final Logger mainLogger = LogManager.getLogger("Main");

  private static final String DEFAULT_WEATHER_TABLE = "weather";
  private static final String DEFAULT_WEATHER_SCHEMA = "public";
  private static final String BASIC_QUERY =
      "SELECT * FROM " + DEFAULT_WEATHER_SCHEMA + "." + DEFAULT_WEATHER_TABLE;
  private static final String DEFAULT_COORDINATE_COLUMN = "coordinate";
  private static final String DEFAULT_TIME_COLUMN = "time";

  private static final String DEFAULT_WEATHER_FETCHING_ERROR = "Error while fetching weather";

  private final SqlConnector connector;
  private final IdCoordinateSource idCoordinateSource;
  private final TimeBasedWeatherValueFactory weatherFactory;

  /**
   * Initializes a new SqlWeatherSource
   *
   * @param connector the connector needed for database connection
   * @param idCoordinateSource a coordinate source to map ids to points
   */
  public SqlWeatherSource(SqlConnector connector, IdCoordinateSource idCoordinateSource) {
    this.connector = connector;
    this.idCoordinateSource = idCoordinateSource;
    this.weatherFactory = new TimeBasedWeatherValueFactory("yyyy-MM-dd HH:mm:ss.S");
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
      mainLogger.error(DEFAULT_WEATHER_FETCHING_ERROR, e);
    }
    return mapWeatherValuesToPoints(timeBasedValues);
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
    List<TimeBasedValue<WeatherValue>> timeBasedValues = Collections.emptyList();
    try (Statement stmt = connector.getConnection().createStatement()) {
      ResultSet resultSet =
          connector.executeQuery(
              stmt, createQueryStringForTimeIntervalAndCoordinates(timeInterval, coordinates));
      List<Map<String, String>> fieldMaps = SqlConnector.extractFieldMaps(resultSet);
      timeBasedValues = toTimeBasedWeatherValues(fieldMaps);
      if (!resultSet.isClosed()) resultSet.close();
    } catch (SQLException e) {
      mainLogger.error(DEFAULT_WEATHER_FETCHING_ERROR, e);
    }
    return mapWeatherValuesToPoints(timeBasedValues);
  }

  @Override
  public Optional<TimeBasedValue<WeatherValue>> getWeather(ZonedDateTime date, Point coordinate) {
    List<TimeBasedValue<WeatherValue>> timeBasedValues = Collections.emptyList();
    try (Statement stmt = connector.getConnection().createStatement()) {
      ResultSet resultSet =
          connector.executeQuery(stmt, createQueryStringForTimeAndCoordinate(date, coordinate));
      List<Map<String, String>> fieldMaps = SqlConnector.extractFieldMaps(resultSet);
      timeBasedValues = toTimeBasedWeatherValues(fieldMaps);
      if (!resultSet.isClosed()) resultSet.close();
    } catch (SQLException e) {
      mainLogger.error(DEFAULT_WEATHER_FETCHING_ERROR, e);
    }
    if (timeBasedValues.isEmpty()) return Optional.empty();
    if (timeBasedValues.size() > 1)
      mainLogger.warn("Retrieved more than one result value, using the first");
    return Optional.of(timeBasedValues.get(0));
  }

  /**
   * Creates a basic query to retrieve all entities in the given time frame
   *
   * @param timeInterval the time frame for the query
   * @return the query string
   */
  private String createQueryStringForTimeInterval(ClosedInterval<ZonedDateTime> timeInterval) {
    return BASIC_QUERY + " WHERE " + createTimeConstraint(timeInterval) + ";";
  }

  /**
   * Creates a basic query to retrieve an entry for the given time and coordinate
   *
   * @param time the timestamp for the query
   * @param coordinate the queried coordinate
   * @return the query string
   */
  private String createQueryStringForTimeAndCoordinate(ZonedDateTime time, Point coordinate) {
    return BASIC_QUERY
        + " WHERE "
        + createCoordinateConstraint(coordinate)
        + " AND "
        + createTimeConstraint(time)
        + ";";
  }

  /**
   * Creates a basic query to retrieve all entities in the given time frame and coordinates
   *
   * @param timeInterval the time frame for the query
   * @param coordinates the allowed coordinates
   * @return the query string
   */
  private String createQueryStringForTimeIntervalAndCoordinates(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
    return BASIC_QUERY
        + " WHERE "
        + createCoordinateConstraint(coordinates)
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
  private static String createTimeConstraint(ZonedDateTime time) {
    return DEFAULT_TIME_COLUMN + "='" + TimeTools.toString(time) + "'";
  }

  /**
   * Creates a time frame constraint like "time BETWEEN '2020-04-28 15:00:00' AND '2020-04-28
   * 17:00:00'"
   *
   * @param timeInterval the time interval to use
   * @return the constraint string
   */
  private static String createTimeConstraint(ClosedInterval<ZonedDateTime> timeInterval) {
    return DEFAULT_TIME_COLUMN
        + " BETWEEN '"
        + TimeUtil.withDefaults.toString(timeInterval.getLower())
        + "' AND '"
        + TimeUtil.withDefaults.toString(timeInterval.getUpper())
        + "'";
  }

  /**
   * Creates a simple coordinate constraint for which the point is mapped to it's id, like:
   * "coordinate=193186"
   *
   * @param coordinate the coordinate point
   * @return the constraint string
   */
  private String createCoordinateConstraint(Point coordinate) {
    return DEFAULT_COORDINATE_COLUMN + "=" + idCoordinateSource.getId(coordinate);
  }

  /**
   * Creates a constraint for multiple cooridnates, for which the points are mapped to their ids,
   * like: "coordinate IN (193186, 193187, 193188)"
   *
   * @param coordinates the coordinate points
   * @return the constraint string
   */
  private String createCoordinateConstraint(Collection<Point> coordinates) {
    String constraint = DEFAULT_COORDINATE_COLUMN + " IN (";
    constraint +=
        coordinates.stream()
            .map(idCoordinateSource::getId)
            .map(Object::toString)
            .collect(Collectors.joining(", "));
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
        .map(tbv -> (TimeBasedValue<WeatherValue>) tbv)
        .collect(Collectors.toList());
  }

  /**
   * Converts a field to value map into a TimeBasedValue, removes the "tid"
   *
   * @param fieldMap the field to value map for one TimeBasedValue
   * @return an Optional of that TimeBasedValue
   */
  private Optional<TimeBasedValue> toTimeBasedWeatherValue(Map<String, String> fieldMap) {
    fieldMap.remove("tid");
    TimeBasedWeatherValueData data = toTimeBasedWeatherValueData(fieldMap);
    return weatherFactory.getEntity(data);
  }

  /**
   * Converts a field to value map into TimeBasedWeatherValueData, extracts the coordinate id from
   * the field map and uses the {@link IdCoordinateSource} to map it to a point
   *
   * @param fieldMap the field to value map for one TimeBasedValue
   * @return the TimeBasedWeatherValueData
   */
  private TimeBasedWeatherValueData toTimeBasedWeatherValueData(Map<String, String> fieldMap) {
    String coordinateValue = fieldMap.remove(DEFAULT_COORDINATE_COLUMN);
    Point coordinate = idCoordinateSource.getCoordinate(Integer.parseInt(coordinateValue));
    return new TimeBasedWeatherValueData(fieldMap, coordinate);
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
