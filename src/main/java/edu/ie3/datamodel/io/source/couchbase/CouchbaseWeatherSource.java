/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.couchbase;

import com.couchbase.client.core.error.DecodingFailureException;
import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryResult;
import edu.ie3.datamodel.io.connectors.CouchbaseConnector;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueFactory;
import edu.ie3.datamodel.io.source.IdCoordinateSource;
import edu.ie3.datamodel.io.source.WeatherSource;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Couchbase Source for weather data */
public class CouchbaseWeatherSource extends WeatherSource {
  private static final Logger logger = LoggerFactory.getLogger(CouchbaseWeatherSource.class);
  private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ssxxx";
  /** The start of the document key, comparable to a table name in relational databases */
  private static final String DEFAULT_KEY_PREFIX = "weather";

  private final String keyPrefix;
  private final CouchbaseConnector connector;
  private final String coordinateIdColumnName;
  private final String timeStampPattern;

  /**
   * Instantiate a weather source utilising a connection to a couchbase instance obtained via the
   * connector. This convenient constructor uses {@link CouchbaseWeatherSource#DEFAULT_KEY_PREFIX}
   * as key prefix.
   *
   * @param connector Connector, that establishes the connection to the couchbase instance
   * @param coordinateSource Source to obtain actual coordinates from
   * @param coordinateIdColumnName Name of the column containing the information about the
   *     coordinate identifier
   * @param weatherFactory Factory to transfer field to value mapping into actual java object
   *     instances
   * @param timeStampPattern Pattern of time stamps to parse
   */
  public CouchbaseWeatherSource(
      CouchbaseConnector connector,
      IdCoordinateSource coordinateSource,
      String coordinateIdColumnName,
      TimeBasedWeatherValueFactory weatherFactory,
      String timeStampPattern) {
    this(
        connector,
        coordinateSource,
        coordinateIdColumnName,
        DEFAULT_KEY_PREFIX,
        weatherFactory,
        timeStampPattern);
  }

  /**
   * Instantiate a weather source utilising a connection to a couchbase instance obtained via the
   * connector
   *
   * @param connector Connector, that establishes the connection to the couchbase instance
   * @param idCoordinateSource Source to obtain actual coordinates from
   * @param coordinateIdColumnName Name of the column containing the information about the
   *     coordinate identifier
   * @param keyPrefix Prefix of entries, that belong to weather
   * @param weatherFactory Factory to transfer field to value mapping into actual java object
   *     instances
   * @param timeStampPattern Pattern of time stamps to parse
   */
  public CouchbaseWeatherSource(
      CouchbaseConnector connector,
      IdCoordinateSource idCoordinateSource,
      String coordinateIdColumnName,
      String keyPrefix,
      TimeBasedWeatherValueFactory weatherFactory,
      String timeStampPattern) {
    super(idCoordinateSource, weatherFactory);
    this.connector = connector;
    this.coordinateIdColumnName = coordinateIdColumnName;
    this.keyPrefix = keyPrefix;
    this.timeStampPattern = timeStampPattern;
  }

  @Override
  public Optional<Set<String>> getSourceFields() {
    return connector.getSourceFields();
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval) {
    logger.warn(
        "By not providing coordinates you are forcing couchbase to check all possible coordinates one by one."
            + " This is not very performant. Please consider providing specific coordinates instead.");
    return getWeather(timeInterval, idCoordinateSource.getAllCoordinates());
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
    HashMap<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = new HashMap<>();
    for (Point coordinate : coordinates) {
      Optional<Integer> coordinateId = idCoordinateSource.getId(coordinate);
      if (coordinateId.isPresent()) {
        String query = createQueryStringForIntervalAndCoordinate(timeInterval, coordinateId.get());
        CompletableFuture<QueryResult> futureResult = connector.query(query);
        QueryResult queryResult = futureResult.join();
        List<JsonObject> jsonWeatherInputs = Collections.emptyList();
        try {
          jsonWeatherInputs = queryResult.rowsAsObject();
        } catch (DecodingFailureException ex) {
          logger.error("Querying weather inputs failed!", ex);
        }
        if (jsonWeatherInputs != null && !jsonWeatherInputs.isEmpty()) {
          Set<TimeBasedValue<WeatherValue>> weatherInputs =
              jsonWeatherInputs.stream()
                  .map(this::toTimeBasedWeatherValue)
                  .flatMap(Optional::stream)
                  .collect(Collectors.toSet());
          IndividualTimeSeries<WeatherValue> weatherTimeSeries =
              new IndividualTimeSeries<>(weatherInputs);
          coordinateToTimeSeries.put(coordinate, weatherTimeSeries);
        }
      } else logger.warn("Unable to match coordinate {} to a coordinate ID", coordinate);
    }
    return coordinateToTimeSeries;
  }

  @Override
  public Optional<TimeBasedValue<WeatherValue>> getWeather(ZonedDateTime date, Point coordinate) {
    Optional<Integer> coordinateId = idCoordinateSource.getId(coordinate);
    if (coordinateId.isEmpty()) {
      logger.warn("Unable to match coordinate {} to a coordinate ID", coordinate);
      return Optional.empty();
    }
    try {
      CompletableFuture<GetResult> futureResult =
          connector.get(generateWeatherKey(date, coordinateId.get()));
      GetResult getResult = futureResult.join();
      JsonObject jsonWeatherInput = getResult.contentAsObject();
      return toTimeBasedWeatherValue(jsonWeatherInput);
    } catch (DecodingFailureException ex) {
      logger.error("Decoding to TimeBasedWeatherValue failed!", ex);
      return Optional.empty();
    } catch (DocumentNotFoundException ex) {
      return Optional.empty();
    } catch (CompletionException ex) {
      if (ex.getCause() instanceof DocumentNotFoundException) return Optional.empty();
      else throw ex;
    }
  }

  @Override
  public Map<Point, List<ZonedDateTime>> getTimeKeysAfter(ZonedDateTime time) {
    String query = createQueryStringForFollowingTimeKeys(time);
    CompletableFuture<QueryResult> futureResult = connector.query(query);
    QueryResult queryResult = futureResult.join();
    List<JsonObject> jsonWeatherInputs = Collections.emptyList();
    try {
      jsonWeatherInputs = queryResult.rowsAsObject();
    } catch (DecodingFailureException ex) {
      logger.error("Querying weather inputs failed!", ex);
    }
    if (jsonWeatherInputs != null && !jsonWeatherInputs.isEmpty()) {
      return groupTime(
          jsonWeatherInputs.stream()
              .map(
                  json -> {
                    int coordinateId = json.getInt(COORDINATE_ID);
                    Optional<Point> coordinate = idCoordinateSource.getCoordinate(coordinateId);
                    ZonedDateTime timestamp =
                        weatherFactory.toZonedDateTime(
                            json.getString(weatherFactory.getTimeFieldString()));
                    if (coordinate.isEmpty()) {
                      log.warn("Unable to match coordinate ID {} to a point", coordinateId);
                    }
                    return Pair.of(coordinate, timestamp);
                  })
              .filter(value -> value.getValue().isAfter(time)));
    }
    return Collections.emptyMap();
  }

  /**
   * Generates a key for weather documents with the pattern: {@code
   * weather::<coordinate_id>::<time>}
   *
   * @param time the timestamp for the weather data
   * @param coordinateId the coordinate Id of the weather data
   * @return a weather document key
   */
  private String generateWeatherKey(ZonedDateTime time, Integer coordinateId) {
    String key = keyPrefix + "::";
    key += coordinateId + "::";
    key += time.format(DateTimeFormatter.ofPattern(timeStampPattern));
    return key;
  }

  /**
   * Create a query string to search for documents for the given coordinate in the given time
   * interval by querying a range of document keys
   *
   * @param timeInterval the time interval for which the documents are queried
   * @param coordinateId the coordinate ID for which the documents are queried
   * @return the query string
   */
  private String createQueryStringForIntervalAndCoordinate(
      ClosedInterval<ZonedDateTime> timeInterval, int coordinateId) {
    String basicQuery =
        "SELECT " + connector.getBucketName() + ".* FROM " + connector.getBucketName();
    String whereClause =
        " WHERE META().id >= '" + generateWeatherKey(timeInterval.getLower(), coordinateId);
    whereClause +=
        "' AND META().id <= '" + generateWeatherKey(timeInterval.getUpper(), coordinateId) + "'";
    return basicQuery + whereClause;
  }

  /**
   * Create a query string to search for all time keys that comes after the given time.
   *
   * @param time given timestamp
   * @return the query string
   */
  public String createQueryStringForFollowingTimeKeys(ZonedDateTime time) {
    String basicQuery = "SELECT a.coordinateid, a.time FROM " + connector.getBucketName() + " AS a";
    String whereClause = " WHERE META().id > '" + generateWeatherKey(time, 0) + "'";
    return basicQuery + whereClause;
  }

  /**
   * Converts a JsonObject into TimeBasedWeatherValueData by extracting all fields into a field to
   * value map and then removing the coordinate from it to supply as a parameter
   *
   * @param jsonObj the JsonObject to convert
   * @return the Data object
   */
  private Optional<TimeBasedWeatherValueData> toTimeBasedWeatherValueData(JsonObject jsonObj) {
    Integer coordinateId = jsonObj.getInt(coordinateIdColumnName);
    jsonObj.removeKey(coordinateIdColumnName);
    Optional<Point> coordinate = idCoordinateSource.getCoordinate(coordinateId);
    if (coordinate.isEmpty()) {
      logger.warn("Unable to match coordinate ID {} to a coordinate", coordinateId);
      return Optional.empty();
    }
    Map<String, String> fieldToValueMap =
        jsonObj.toMap().entrySet().stream()
            .collect(
                Collectors.toMap(Map.Entry::getKey, entry -> String.valueOf(entry.getValue())));
    fieldToValueMap.putIfAbsent("uuid", UUID.randomUUID().toString());
    return Optional.of(new TimeBasedWeatherValueData(fieldToValueMap, coordinate.get()));
  }

  /**
   * Converts a JsonObject into a time based weather value by converting it to a
   * TimeBasedWeatherValueData first, then using the TimeBasedWeatherValueFactory to create an
   * entity
   *
   * @param jsonObj the JsonObject to convert
   * @return an optional weather value
   */
  private Optional<TimeBasedValue<WeatherValue>> toTimeBasedWeatherValue(JsonObject jsonObj) {
    Optional<TimeBasedWeatherValueData> data = toTimeBasedWeatherValueData(jsonObj);
    if (data.isEmpty()) {
      logger.warn("Unable to parse json object");
      logger.debug("The following json could not be parsed:\n{}", jsonObj);
      return Optional.empty();
    }
    return weatherFactory.get(data.get()).getData();
  }
}
