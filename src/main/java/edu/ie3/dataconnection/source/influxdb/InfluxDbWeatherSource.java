/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.source.influxdb;

import com.vividsolutions.jts.geom.Point;
import edu.ie3.dataconnection.dataconnectors.DataConnector;
import edu.ie3.dataconnection.dataconnectors.InfluxDbConnector;
import edu.ie3.dataconnection.source.WeatherSource;
import edu.ie3.dataconnection.source.csv.CsvCoordinateSource;
import edu.ie3.models.influxdb.input.weather.InfluxDbWeatherInput;
import edu.ie3.models.timeseries.IndividualTimeSeries;
import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.WeatherValues;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;

public class InfluxDbWeatherSource implements WeatherSource {

  private final InfluxDbConnector connector;

  public InfluxDbWeatherSource(InfluxDbConnector connector) {
    this.connector = connector;
  }

  public Map<Point, IndividualTimeSeries<WeatherValues>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval) {
    // init HashMap
    HashMap<Point, IndividualTimeSeries<WeatherValues>> coordinateToTimeSeries = new HashMap<>();
    List<InfluxDbWeatherInput> influxWeatherInputs;
    try (InfluxDB session = connector.getSession()) {
      String query = createQueryStringForInterval(timeInterval);
      QueryResult queryResult = session.query(new Query(query));

      InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
      influxWeatherInputs = resultMapper.toPOJO(queryResult, InfluxDbWeatherInput.class);
    }

    if (influxWeatherInputs != null) {

      List<TimeBasedValue<WeatherValues>> weatherInputs =
          influxWeatherInputs.stream()
              .map(InfluxDbWeatherInput::toTimeBasedWeatherValues)
              .collect(Collectors.toList());

      for (Point coordinate :
          weatherInputs.stream()
              .map(influxWeather -> influxWeather.getValue().getCoordinate())
              .distinct()
              .collect(Collectors.toList())) {
        coordinateToTimeSeries.put(coordinate, new IndividualTimeSeries<>());
      }

      for (TimeBasedValue<WeatherValues> weather : weatherInputs) {
        Point coordinate = weather.getValue().getCoordinate();
        coordinateToTimeSeries.get(coordinate).add(weather);
      }
    }
    return coordinateToTimeSeries;
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValues>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
    if (coordinates == null) return getWeather(timeInterval);

    HashMap<Point, IndividualTimeSeries<WeatherValues>> coordinateToTimeSeries = new HashMap<>();

    for (Point coordinate : coordinates) {
      List<TimeBasedValue<WeatherValues>> weatherInputs =
          getWeatherForCoordinate(timeInterval, coordinate);
      IndividualTimeSeries<WeatherValues> timeSeries = new IndividualTimeSeries<WeatherValues>();
      timeSeries.addAll(weatherInputs);
      coordinateToTimeSeries.put(coordinate, timeSeries);
    }

    return coordinateToTimeSeries;
  }

  public List<TimeBasedValue<WeatherValues>> getWeatherForCoordinate(
      ClosedInterval<ZonedDateTime> timeInterval, Point coordinate) {
    List<InfluxDbWeatherInput> influxWeatherInputs;
    try (InfluxDB session = connector.getSession()) {
      String query = createQueryStringForIntervalAndCoordinate(timeInterval, coordinate);
      System.out.println(query);
      QueryResult queryResult = session.query(new Query(query));

      InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
      influxWeatherInputs = resultMapper.toPOJO(queryResult, InfluxDbWeatherInput.class);
    }
    return influxWeatherInputs != null
        ? influxWeatherInputs.stream()
            .map(InfluxDbWeatherInput::toTimeBasedWeatherValues)
            .collect(Collectors.toList())
        : Collections.emptyList();
  }

  @Override
  public Optional<TimeBasedValue<WeatherValues>> getWeather(ZonedDateTime date, Point coordinate) {
    List<InfluxDbWeatherInput> influxWeatherInputs;
    try (InfluxDB session = connector.getSession()) {
      String query = createQueryStringForDateAndCoordinate(date, coordinate);
      System.out.println(query);
      QueryResult queryResult = session.query(new Query(query));
      InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
      influxWeatherInputs = resultMapper.toPOJO(queryResult, InfluxDbWeatherInput.class);
    }
    if (influxWeatherInputs == null || influxWeatherInputs.isEmpty()) return Optional.empty();
    return Optional.ofNullable(influxWeatherInputs.get(0).toTimeBasedWeatherValues());
  }

  @Override
  public DataConnector getDataConnector() {
    return connector;
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
            + timeInterval.getLower().toInstant().toEpochMilli()
            + "000000"
            + " and time <= "
            + timeInterval.getUpper().toInstant().toEpochMilli()
            + "000000";
    return basicQuery + " where " + timeConstraint;
  }

  public String createQueryStringForDate(ZonedDateTime date) {
    String basicQuery = createBasicQueryString();
    String timeConstraint = "time=" + date.toInstant().toEpochMilli() + "000000";
    return basicQuery + " where " + timeConstraint;
  }

  public String createBasicQueryString() {
    return "Select * from weather";
  }

  public String createCoordinateConstraintString(Point coordinate) {
    return "\"koordinatenid\"='" + CsvCoordinateSource.getId(coordinate) + "'";
  }
}
