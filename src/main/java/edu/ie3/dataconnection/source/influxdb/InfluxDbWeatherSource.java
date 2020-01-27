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
import edu.ie3.models.influxdb.input.weather.InfluxDbWeatherInput;
import edu.ie3.models.timeseries.IndividualTimeSeries;
import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.WeatherValues;
import edu.ie3.util.Interval;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InfluxDbWeatherSource implements WeatherSource {

  private final InfluxDbConnector connector;


  public InfluxDbWeatherSource(InfluxDbConnector connector) {
    this.connector = connector;
  }

  @Override
  public HashMap<Point, IndividualTimeSeries<WeatherValues>> fetchWeather(Interval<ZonedDateTime> timeInterval) {
    //init HashMap
    HashMap<Point, IndividualTimeSeries<WeatherValues>> coordinateToTimeSeries = new HashMap<>();
    List<InfluxDbWeatherInput> influxWeatherInputs;
    try(InfluxDB session = connector.getSession()) {
      QueryResult queryResult = session.query(new Query(createQueryStringForInterval(timeInterval)));

      InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
      influxWeatherInputs = resultMapper
              .toPOJO(queryResult, InfluxDbWeatherInput.class);
    }

    if(influxWeatherInputs!=null) {

      List<TimeBasedValue<WeatherValues>> weatherInputs = influxWeatherInputs.stream()
              .map(InfluxDbWeatherInput::toTimeBasedWeatherValues).collect(Collectors.toList());

      for (Point coordinate : weatherInputs.stream().map(influxWeather -> influxWeather.getValue().getCoordinate()).distinct().collect(Collectors.toList())) {
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
  public HashMap<Point, IndividualTimeSeries<WeatherValues>> fetchWeather(Interval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
    if(coordinates==null) return fetchWeather(timeInterval);

    HashMap<Point, IndividualTimeSeries<WeatherValues>> coordinateToTimeSeries = new HashMap<>();

    for(Point coordinate: coordinates){
      List<TimeBasedValue<WeatherValues>> weatherInputs = getWeatherForCoordinate(timeInterval, coordinate);
      IndividualTimeSeries<WeatherValues> timeSeries = new IndividualTimeSeries<WeatherValues>();
      timeSeries.addAll(weatherInputs);
    }

    return coordinateToTimeSeries;
  }


  public List<TimeBasedValue<WeatherValues>> getWeatherForCoordinate(Interval<ZonedDateTime> timeInterval, Point coordinate){
    List<InfluxDbWeatherInput> influxWeatherInputs;
    try(InfluxDB session = connector.getSession()) {
      QueryResult queryResult = session.query(new Query(createQueryStringForIntervalAndCoordinate(timeInterval, coordinate)));

      InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
      influxWeatherInputs = resultMapper
              .toPOJO(queryResult, InfluxDbWeatherInput.class);
    }
    return influxWeatherInputs != null ? influxWeatherInputs.stream()
            .map(InfluxDbWeatherInput::toTimeBasedWeatherValues).collect(Collectors.toList()) : Collections.emptyList();
  }


  @Override
  public Optional<TimeBasedValue<WeatherValues>> getWeather(ZonedDateTime date, Point coordinate) {
    List<InfluxDbWeatherInput> influxWeatherInputs;
    try(InfluxDB session = connector.getSession()) {
      QueryResult queryResult = session.query(new Query(createQueryStringForDateAndCoordinate(date, coordinate)));
      InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
      influxWeatherInputs = resultMapper
              .toPOJO(queryResult, InfluxDbWeatherInput.class);
    }
    if(influxWeatherInputs == null) return Optional.empty();
    return Optional.ofNullable(influxWeatherInputs.get(0).toTimeBasedWeatherValues());
  }

  @Override
  public DataConnector getDataConnector() {
    return connector;
  }

  public String createQueryStringForIntervalAndCoordinate(Interval<ZonedDateTime> timeInterval, Point coordinate){
    return  createQueryStringForInterval(timeInterval) + " and " + createCoordinateConstraintString(coordinate);
  }

  public String createQueryStringForDateAndCoordinate(ZonedDateTime date, Point coordinate){
    return  createQueryStringForDate(date) + " and " + createCoordinateConstraintString(coordinate);
  }

  public String createQueryStringForInterval(Interval<ZonedDateTime> timeInterval){
    String basicQuery = createBasicQueryString();
    String timeConstraint = "time >= " + timeInterval.getLower().toString()
            + "and time <= " + timeInterval.getUpper().toString();
    return basicQuery + " where " + timeConstraint;
  }

  public String createQueryStringForDate(ZonedDateTime date){
    String basicQuery = createBasicQueryString();
    String timeConstraint = "time=" + date.toInstant().toEpochMilli();
    return basicQuery + " where " + timeConstraint;
  }

  public String createBasicQueryString(){
    return "Select * from weather";
  }

  public String createCoordinateConstraintString(Point coordinate){
    return "\"lat\"='" + coordinate.getY() + "' and \"lon\"='" + coordinate.getX() + "'";
  }
}
