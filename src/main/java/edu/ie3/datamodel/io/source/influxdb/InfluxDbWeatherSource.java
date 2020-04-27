/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.influxdb;

import edu.ie3.dataconnection.source.csv.CsvCoordinateSource;
import edu.ie3.datamodel.io.connectors.InfluxDbConnector;
import edu.ie3.datamodel.io.source.WeatherSource;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.models.influxdb.input.weather.InfluxDbWeatherInput;
import edu.ie3.util.interval.ClosedInterval;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.locationtech.jts.geom.Point;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InfluxDbWeatherSource implements WeatherSource {
  private static final Logger mainLogger = LogManager.getLogger("Main");
  private final InfluxDbConnector connector;

  public InfluxDbWeatherSource(InfluxDbConnector connector) {
    this.connector = connector;
  }

  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval) {
    // init HashMap
    Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = new HashMap<>();
    List<InfluxDbWeatherInput> influxWeatherInputs = null;
    try (InfluxDB session = connector.getSession()) {
      String query = createQueryStringForInterval(timeInterval);
      QueryResult queryResult = session.query(new Query(query));

      InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
      influxWeatherInputs = resultMapper.toPOJO(queryResult, InfluxDbWeatherInput.class);
    } catch (Exception e) {
      mainLogger.error(e);
    }

    if (influxWeatherInputs != null) {
      Map<Point, Set<TimeBasedValue<WeatherValue>>> pointListMap =  influxWeatherInputs
              .stream()
              .map(InfluxDbWeatherInput::toTimeBasedWeatherValue)
              .collect(Collectors.groupingBy(timeBasedWeatherValue -> timeBasedWeatherValue.getValue().getCoordinate(), Collectors.toSet()));
      
      coordinateToTimeSeries = pointListMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new IndividualTimeSeries<>(null, e.getValue())));
    }
    return coordinateToTimeSeries;
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
    if (coordinates == null) return getWeather(timeInterval);
    HashMap<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = new HashMap<>();
    try (InfluxDB session = connector.getSession()) {
      InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
      for (Point coordinate : coordinates) {
        String query = createQueryStringForIntervalAndCoordinate(timeInterval, coordinate);
        QueryResult queryResult = session.query(new Query(query));
        List<InfluxDbWeatherInput> influxWeatherInputs =
            resultMapper.toPOJO(queryResult, InfluxDbWeatherInput.class);
        if (influxWeatherInputs != null && !influxWeatherInputs.isEmpty()) {
          Set<TimeBasedValue<WeatherValue>> timeBasedValues =
              influxWeatherInputs.stream()
                  .map(InfluxDbWeatherInput::toTimeBasedWeatherValue)
                  .collect(Collectors.toSet());
          IndividualTimeSeries<WeatherValue> timeSeries = new IndividualTimeSeries<>(null, timeBasedValues);
          coordinateToTimeSeries.put(coordinate, timeSeries);
        }
      }
    } catch (Exception e) {
      mainLogger.error(e);
    }
    return coordinateToTimeSeries;
  }

  public List<TimeBasedValue<WeatherValue>> getWeatherForCoordinate(
      ClosedInterval<ZonedDateTime> timeInterval, Point coordinate) {
    List<InfluxDbWeatherInput> influxWeatherInputs = null;
    try (InfluxDB session = connector.getSession()) {
      String query = createQueryStringForIntervalAndCoordinate(timeInterval, coordinate);
      QueryResult queryResult = session.query(new Query(query));

      InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
      influxWeatherInputs = resultMapper.toPOJO(queryResult, InfluxDbWeatherInput.class);
    } catch (Exception e) {
      mainLogger.error(e);
    }
    return influxWeatherInputs != null
        ? influxWeatherInputs.stream()
            .map(InfluxDbWeatherInput::toTimeBasedWeatherValue)
            .collect(Collectors.toList())
        : Collections.emptyList();
  }

  @Override
  public Optional<TimeBasedValue<WeatherValue>> getWeather(ZonedDateTime date, Point coordinate) {
    List<InfluxDbWeatherInput> influxWeatherInputs = null;
    try (InfluxDB session = connector.getSession()) {
      String query = createQueryStringForDateAndCoordinate(date, coordinate);
      QueryResult queryResult = session.query(new Query(query));
      InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
      influxWeatherInputs = resultMapper.toPOJO(queryResult, InfluxDbWeatherInput.class);
    } catch (Exception e) {
      mainLogger.error(e);
    }
    if (influxWeatherInputs == null || influxWeatherInputs.isEmpty()) return Optional.empty();
    return Optional.ofNullable(influxWeatherInputs.get(0).toTimeBasedWeatherValue());
  }

  public String createQueryStringForIntervalAndCoordinate(
      ClosedInterval<ZonedDateTime> timeInterval, Point coordinate) {
    return createQueryStringForInterval(timeInterval)
        + " and "
        + createCoordinateConstraintString(coordinate);
  }

  public String createQueryStringForDateAndCoordinate(ZonedDateTime date, Point coordinate) {
    return createQueryStringForDate(date) + " and " + createCoordinateConstraintString(coordinate);
  }

  public String createQueryStringForInterval(ClosedInterval<ZonedDateTime> timeInterval) {
    String basicQuery = createBasicQueryString();
    String timeConstraint =
        "time >= "
            + timeInterval.getLower().toInstant().toEpochMilli() * 1000000
            + " and time <= "
            + timeInterval.getUpper().toInstant().toEpochMilli() * 1000000;
    return basicQuery + " where " + timeConstraint;
  }

  public String createQueryStringForDate(ZonedDateTime date) {
    String basicQuery = createBasicQueryString();
    String timeConstraint = "time=" + date.toInstant().toEpochMilli() * 1000000;
    return basicQuery + " where " + timeConstraint;
  }

  public String createBasicQueryString() {
    return "Select * from weather";
  }

  public String createCoordinateConstraintString(Point coordinate) {
    return "koordinatenid='" + CsvCoordinateSource.getId(coordinate) + "'";
  }

  @Override
  public void shutdown() {
    connector.shutdown();
  }
}
