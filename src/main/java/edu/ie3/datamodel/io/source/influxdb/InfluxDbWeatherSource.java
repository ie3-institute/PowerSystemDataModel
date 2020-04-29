/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.influxdb;

import edu.ie3.datamodel.io.connectors.InfluxDbConnector;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedEntryData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedEntryFactory;
import edu.ie3.datamodel.io.source.CoordinateSource;
import edu.ie3.datamodel.io.source.WeatherSource;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.util.interval.ClosedInterval;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.locationtech.jts.geom.Point;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InfluxDbWeatherSource implements WeatherSource {
  private final InfluxDbConnector connector;
  private final CoordinateSource coordinateSource;
  private final TimeBasedEntryFactory timeBasedEntryFactory = new TimeBasedEntryFactory();

  public InfluxDbWeatherSource(InfluxDbConnector connector, CoordinateSource coordinateSource) {
    this.connector = connector;
    this.coordinateSource = coordinateSource;
  }

  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval) {
    try (InfluxDB session = connector.getSession()) {
      String query = createQueryStringForInterval(timeInterval);
      QueryResult queryResult = session.query(new Query(query));
      Stream<Optional<TimeBasedValue>> optValues = optTimeBasedValueStream(queryResult);
      Set<TimeBasedValue<WeatherValue>> timeBasedValues =
          filterEmptyOptionals(optValues).collect(Collectors.toSet());
      Map<Point, Set<TimeBasedValue<WeatherValue>>> coordinateToValues =
          timeBasedValues.stream()
              .collect(
                  Collectors.groupingBy(
                      timeBasedWeatherValue -> timeBasedWeatherValue.getValue().getCoordinate(),
                      Collectors.toSet()));
      return coordinateToValues.entrySet().stream()
          .collect(
              Collectors.toMap(
                  Map.Entry::getKey, e -> new IndividualTimeSeries<>(null, e.getValue())));
    }
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
    if (coordinates == null) return getWeather(timeInterval);
    HashMap<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = new HashMap<>();
    try (InfluxDB session = connector.getSession()) {
      for (Point coordinate : coordinates) {
        String query = createQueryStringForIntervalAndCoordinate(timeInterval, coordinate);
        QueryResult queryResult = session.query(new Query(query));
        Stream<Optional<TimeBasedValue>> optValues = optTimeBasedValueStream(queryResult);
        Set<TimeBasedValue<WeatherValue>> timeBasedValues =
            filterEmptyOptionals(optValues).collect(Collectors.toSet());
        IndividualTimeSeries<WeatherValue> timeSeries =
            new IndividualTimeSeries<>(null, timeBasedValues);
        coordinateToTimeSeries.put(coordinate, timeSeries);
      }
    }
    return coordinateToTimeSeries;
  }

  public List<TimeBasedValue> getWeatherForCoordinate(
      ClosedInterval<ZonedDateTime> timeInterval, Point coordinate) {
    try (InfluxDB session = connector.getSession()) {
      String query = createQueryStringForIntervalAndCoordinate(timeInterval, coordinate);
      QueryResult queryResult = session.query(new Query(query));
      Stream<Optional<TimeBasedValue>> optValues = optTimeBasedValueStream(queryResult);
      return filterEmptyOptionals(optValues).collect(Collectors.toList());
    }
  }

  @Override
  public Optional<TimeBasedValue> getWeather(ZonedDateTime date, Point coordinate) {
    try (InfluxDB session = connector.getSession()) {
      String query = createQueryStringForDateAndCoordinate(date, coordinate);
      QueryResult queryResult = session.query(new Query(query));
      return filterEmptyOptionals(optTimeBasedValueStream(queryResult)).findFirst();
    }
  }

  private Stream<Optional<TimeBasedValue>> optTimeBasedValueStream(QueryResult queryResult) {
    Map<String, Set<Map<String, String>>> measurementsMap =
        InfluxDbConnector.parseQueryResult(queryResult, "weather");
    return measurementsMap.get("weather").stream()
        .map(fields -> new TimeBasedEntryData(fields, WeatherValue.class))
        .map(timeBasedEntryFactory::getEntity);
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
    return "koordinatenid='" + coordinateSource.getId(coordinate) + "'";
  }

  protected <T extends TimeBasedValue> Stream<T> filterEmptyOptionals(
      Stream<Optional<T>> elements) {
    return elements.filter(Optional::isPresent).map(Optional::get);
  }
}
