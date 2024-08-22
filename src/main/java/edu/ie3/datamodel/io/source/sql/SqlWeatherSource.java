/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import static edu.ie3.datamodel.io.source.sql.SqlDataSource.createBaseQueryString;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueFactory;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.source.IdCoordinateSource;
import edu.ie3.datamodel.io.source.WeatherSource;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.util.interval.ClosedInterval;
import java.sql.Array;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Point;

/** SQL source for weather data */
public class SqlWeatherSource extends WeatherSource {

  private final SqlDataSource dataSource;

  private static final String WHERE = " WHERE ";
  private final String tableName;

  /**
   * Queries that are available within this source. Motivation to have them as field value is to
   * avoid creating a new string each time, bc they're always the same.
   */
  private final String queryTimeInterval;

  private final String queryTimeAndCoordinate;
  private final String queryTimeIntervalAndCoordinates;
  private final String queryTimeKeysAfter;
  private final String getQueryTimeKeysAfterAndCoordinate;

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
    super(idCoordinateSource, weatherFactory);
    String factoryCoordinateFieldName = weatherFactory.getCoordinateIdFieldString();
    this.dataSource = new SqlDataSource(connector, schemaName, new DatabaseNamingStrategy());
    this.tableName = weatherTableName;

    String dbTimeColumnName =
        dataSource.getDbColumnName(weatherFactory.getTimeFieldString(), weatherTableName);
    String dbCoordinateIdColumnName =
        dataSource.getDbColumnName(factoryCoordinateFieldName, weatherTableName);

    // setup queries
    this.queryTimeInterval =
        createQueryStringForTimeInterval(schemaName, weatherTableName, dbTimeColumnName);
    this.queryTimeAndCoordinate =
        createQueryStringForTimeAndCoordinate(
            schemaName, weatherTableName, dbTimeColumnName, dbCoordinateIdColumnName);
    this.queryTimeIntervalAndCoordinates =
        createQueryStringForTimeIntervalAndCoordinates(
            schemaName, weatherTableName, dbTimeColumnName, dbCoordinateIdColumnName);

    this.queryTimeKeysAfter =
        createQueryStringForTimeKeysAfter(schemaName, weatherTableName, dbTimeColumnName);
    this.getQueryTimeKeysAfterAndCoordinate =
        createQueryStringForTimeKeysAfterAndCoordinate(
            schemaName, weatherTableName, dbTimeColumnName, dbCoordinateIdColumnName);
  }

  @Override
  public Optional<Set<String>> getSourceFields() {
    return dataSource.getSourceFields(tableName);
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval) throws SourceException {
    List<TimeBasedValue<WeatherValue>> timeBasedValues =
        buildTimeBasedValues(
            weatherFactory,
            dataSource.executeQuery(
                queryTimeInterval,
                ps -> {
                  ps.setTimestamp(1, Timestamp.from(timeInterval.getLower().toInstant()));
                  ps.setTimestamp(2, Timestamp.from(timeInterval.getUpper().toInstant()));
                }));
    return mapWeatherValuesToPoints(timeBasedValues);
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates)
      throws SourceException {
    Set<Integer> coordinateIds =
        coordinates.stream()
            .map(idCoordinateSource::getId)
            .flatMap(Optional::stream)
            .collect(Collectors.toSet());
    if (coordinateIds.isEmpty()) {
      log.warn("Unable to match coordinates to coordinate ID");
      return Collections.emptyMap();
    }

    List<TimeBasedValue<WeatherValue>> timeBasedValues =
        buildTimeBasedValues(
            weatherFactory,
            dataSource.executeQuery(
                queryTimeIntervalAndCoordinates,
                ps -> {
                  Array coordinateIdArr =
                      ps.getConnection().createArrayOf("integer", coordinateIds.toArray());
                  ps.setArray(1, coordinateIdArr);
                  ps.setTimestamp(2, Timestamp.from(timeInterval.getLower().toInstant()));
                  ps.setTimestamp(3, Timestamp.from(timeInterval.getUpper().toInstant()));
                }));

    return mapWeatherValuesToPoints(timeBasedValues);
  }

  @Override
  public Optional<TimeBasedValue<WeatherValue>> getWeather(ZonedDateTime date, Point coordinate)
      throws SourceException {
    Optional<Integer> coordinateId = idCoordinateSource.getId(coordinate);
    if (coordinateId.isEmpty()) {
      log.warn("Unable to match coordinate {} to a coordinate ID", coordinate);
      return Optional.empty();
    }

    List<TimeBasedValue<WeatherValue>> timeBasedValues =
        buildTimeBasedValues(
            weatherFactory,
            dataSource.executeQuery(
                queryTimeAndCoordinate,
                ps -> {
                  ps.setInt(1, coordinateId.get());
                  ps.setTimestamp(2, Timestamp.from(date.toInstant()));
                }));

    if (timeBasedValues.isEmpty()) return Optional.empty();
    if (timeBasedValues.size() > 1)
      log.warn("Retrieved more than one result value, using the first");
    return Optional.of(timeBasedValues.get(0));
  }

  @Override
  public Map<Point, List<ZonedDateTime>> getTimeKeysAfter(ZonedDateTime time)
      throws SourceException {
    return toTimeKeys(
        dataSource.executeQuery(
            queryTimeKeysAfter, ps -> ps.setTimestamp(1, Timestamp.from(time.toInstant()))),
        weatherFactory);
  }

  @Override
  public List<ZonedDateTime> getTimeKeysAfter(ZonedDateTime time, Point coordinate)
      throws SourceException {
    Optional<Integer> coordinateId = idCoordinateSource.getId(coordinate);
    if (coordinateId.isEmpty()) {
      log.warn("Unable to match coordinate {} to a coordinate ID", coordinate);
      return Collections.emptyList();
    }

    return dataSource
        .executeQuery(
            getQueryTimeKeysAfterAndCoordinate,
            ps -> {
              ps.setInt(1, coordinateId.get());
              ps.setTimestamp(2, Timestamp.from(time.toInstant()));
            })
        .map(fieldMap -> weatherFactory.extractTime(fieldMap))
        .sorted()
        .toList();
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

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
   * Creates a base query to retrieve all time keys after the given time with the following pattern:
   * <br>
   * {@code <base query> WHERE <time column> > ?;}
   *
   * @param schemaName the name of the database schema
   * @param weatherTableName the name of the database table
   * @param timeColumnName the name of the column holding the timestamp info
   * @return the query string
   */
  private static String createQueryStringForTimeKeysAfter(
      String schemaName, String weatherTableName, String timeColumnName) {
    return "SELECT coordinate_id, time FROM "
        + schemaName
        + "."
        + weatherTableName
        + WHERE
        + timeColumnName
        + " > ?;";
  }

  /**
   * Creates a basic query to retrieve an entry for the given time and coordinate with the following
   * pattern: <br>
   * {@code <base query> WHERE <coordinate column>=? AND <time column> > ?;}
   *
   * @param schemaName the name of the database schema
   * @param weatherTableName the name of the database table
   * @param timeColumnName the name of the column holding the timestamp info
   * @param coordinateColumnName name of the column holding the coordinate id
   * @return the query string
   */
  private static String createQueryStringForTimeKeysAfterAndCoordinate(
      String schemaName,
      String weatherTableName,
      String timeColumnName,
      String coordinateColumnName) {
    return "SELECT coordinate_id, time FROM "
        + schemaName
        + "."
        + weatherTableName
        + WHERE
        + coordinateColumnName
        + "=? AND "
        + timeColumnName
        + " > ?;";
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
}
