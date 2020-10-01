/*
 * © 2020. TU Dortmund University,
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
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Point;

/** Couchbase Source for weather data */
public class CouchbaseWeatherSource implements WeatherSource {
  private static final Logger logger = LogManager.getLogger(CouchbaseWeatherSource.class);
  private static final String COORDINATE_ID_COLUMN_NAME = "coordinate";
  private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ssxxx";
  private final TimeBasedWeatherValueFactory weatherFactory;

  private final CouchbaseConnector connector;
  private final IdCoordinateSource coordinateSource;

  public CouchbaseWeatherSource(CouchbaseConnector connector, IdCoordinateSource coordinateSource) {
    this.connector = connector;
    this.coordinateSource = coordinateSource;
    this.weatherFactory = new TimeBasedWeatherValueFactory(TIMESTAMP_PATTERN);
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval) {
    logger.warn(
        "By not providing coordinates you are forcing couchbase to check all possible coordinates one by one."
            + " This is not very performant. Please consider providing specific coordinates instead.");
    return getWeather(timeInterval, coordinateSource.getAllValidCoordinates());
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
    HashMap<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = new HashMap<>();
    for (Point coordinate : coordinates) {
      String query = createQueryStringForIntervalAndCoordinate(timeInterval, coordinate);
      CompletableFuture<QueryResult> futureResult = connector.query(query);
      QueryResult queryResult = futureResult.join();
      List<JsonObject> jsonWeatherInputs = Collections.emptyList();
      try {
        jsonWeatherInputs = queryResult.rowsAsObject();
      } catch (DecodingFailureException ex) {
        logger.error(ex);
      }
      if (jsonWeatherInputs != null && !jsonWeatherInputs.isEmpty()) {
        Set<TimeBasedValue<WeatherValue>> weatherInputs =
            jsonWeatherInputs.stream()
                .map(this::toTimeBasedWeatherValue)
                .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
                .collect(Collectors.toSet());
        IndividualTimeSeries<WeatherValue> weatherTimeSeries =
            new IndividualTimeSeries<>(null, weatherInputs);
        coordinateToTimeSeries.put(coordinate, weatherTimeSeries);
      }
    }
    return coordinateToTimeSeries;
  }

  @Override
  public Optional<TimeBasedValue<WeatherValue>> getWeather(ZonedDateTime date, Point coordinate) {
    try {
      CompletableFuture<GetResult> futureResult =
          connector.get(generateWeatherKey(date, coordinate));
      GetResult getResult = futureResult.join();
      JsonObject jsonWeatherInput = getResult.contentAsObject();
      return toTimeBasedWeatherValue(jsonWeatherInput);
    } catch (DecodingFailureException ex) {
      logger.error(ex);
      return Optional.empty();
    } catch (DocumentNotFoundException ex) {
      return Optional.empty();
    } catch (CompletionException ex) {
      if (ex.getCause() instanceof DocumentNotFoundException) return Optional.empty();
      else throw ex;
    }
  }

  /**
   * Generates a key for weather documents with the pattern: {@code
   * weather::<coordinate_id>::<time>}
   *
   * @param time the timestamp for the weather data
   * @param coordinate the coordinate of the weather data
   * @return a weather document key
   */
  public String generateWeatherKey(ZonedDateTime time, Point coordinate) {
    String key = "weather::";
    key += coordinateSource.getId(coordinate) + "::";
    key += time.format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN));
    return key;
  }

  /**
   * Create a query string to search for documents for the given coordinate in the given time
   * interval by querying a range of document keys
   *
   * @param timeInterval the time interval for which the documents are queried
   * @param coordinate the coordinate for which the documents are queried
   * @return the query string
   */
  public String createQueryStringForIntervalAndCoordinate(
      ClosedInterval<ZonedDateTime> timeInterval, Point coordinate) {
    String basicQuery =
        "SELECT " + connector.getBucketName() + ".* FROM " + connector.getBucketName();
    String whereClause =
        " WHERE META().id >= '" + generateWeatherKey(timeInterval.getLower(), coordinate);
    whereClause +=
        "' AND META().id <= '" + generateWeatherKey(timeInterval.getUpper(), coordinate) + "'";
    return basicQuery + whereClause;
  }

  /**
   * Converts a JsonObject into TimeBasedWeatherValueData ba extracting all fields into a field to
   * value map and then removing the coordinate from it to supply as a parameter
   *
   * @param jsonObj the JsonObject to convert
   * @return the Data object
   */
  private TimeBasedWeatherValueData toTimeBasedWeatherValueData(JsonObject jsonObj) {
    Integer coordinateId = jsonObj.getInt(COORDINATE_ID_COLUMN_NAME);
    jsonObj.removeKey(COORDINATE_ID_COLUMN_NAME);
    Point coordinate = coordinateSource.getCoordinate(coordinateId);
    Map<String, String> fieldToValueMap =
        jsonObj.toMap().entrySet().stream()
            .collect(
                Collectors.toMap(Map.Entry::getKey, entry -> String.valueOf(entry.getValue())));
    return new TimeBasedWeatherValueData(fieldToValueMap, coordinate);
  }

  /**
   * Converts a JsonObject into a time based weather value by converting it to a
   * TimeBasedWeatherValueData first, then using the TimeBasedWeatherValueFactory to create an
   * entity
   *
   * @param jsonObj the JsonObject to convert
   * @return an optional weather value
   */
  public Optional<TimeBasedValue<WeatherValue>> toTimeBasedWeatherValue(JsonObject jsonObj) {
    TimeBasedWeatherValueData data = toTimeBasedWeatherValueData(jsonObj);
    TimeBasedValue<WeatherValue> timeBasedValue = weatherFactory.getEntity(data).orElse(null);
    return Optional.ofNullable(timeBasedValue);
  }
}
