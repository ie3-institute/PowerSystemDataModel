/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.source.hibernate;

import com.vividsolutions.jts.geom.Point;
import edu.ie3.dataconnection.dataconnectors.DataConnector;
import edu.ie3.dataconnection.dataconnectors.HibernateConnector;
import edu.ie3.dataconnection.source.WeatherSource;
import edu.ie3.dataconnection.source.csv.CsvCoordinateSource;
import edu.ie3.models.hibernate.input.HibernateWeatherInput;
import edu.ie3.models.timeseries.IndividualTimeSeries;
import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.WeatherValues;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class HibernateWeatherSource implements WeatherSource {

  private final HibernateConnector connector;

  public HibernateWeatherSource(HibernateConnector connector) {
    this.connector = connector;
  }

  public Map<Point, IndividualTimeSeries<WeatherValues>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval) {
    List queryParameter = Arrays.asList(timeInterval.getLower(), timeInterval.getUpper());
    List<HibernateWeatherInput> queryResults =
        connector.execNamedQuery("HibernateWeatherInput.WeatherInInterval", queryParameter);
    return mapQueryResults(queryResults);
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValues>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
    if (coordinates == null) return getWeather(timeInterval);
    List<Integer> coordinateIds =
        coordinates.stream().map(CsvCoordinateSource::getId).collect(Collectors.toList());
    List queryParameter =
        Arrays.asList(coordinateIds, timeInterval.getLower(), timeInterval.getUpper());
    List<HibernateWeatherInput> queryResults =
        connector.execNamedQuery(
            "HibernateWeatherInput.WeatherWithMultipleCoordinatesInInterval", queryParameter);
    return mapQueryResults(queryResults);
  }

  public List<TimeBasedValue<WeatherValues>> getWeatherForCoordinate(
      ClosedInterval<ZonedDateTime> timeInterval, Point coordinate) {
    List queryParameter =
        Arrays.asList(
            CsvCoordinateSource.getId(coordinate),
            timeInterval.getLower(),
            timeInterval.getUpper());
    List<HibernateWeatherInput> queryResults =
        connector.execNamedQuery(
            "HibernateWeatherInput.WeatherWithCoordinateInInterval", queryParameter);
    return queryResults.stream()
        .map(HibernateWeatherInput::toTimeBasedWeatherValues)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<TimeBasedValue<WeatherValues>> getWeather(ZonedDateTime date, Point coordinate) {
    List queryParameter = Arrays.asList(CsvCoordinateSource.getId(coordinate), date);
    HibernateWeatherInput queryResult =
        (HibernateWeatherInput)
            connector.execSingleResultNamedQuery(
                "HibernateWeatherInput.WeatherWithCoordinateAndDate", queryParameter);
    return Optional.ofNullable(queryResult.toTimeBasedWeatherValues());
  }

  @Override
  public DataConnector getDataConnector() {
    return connector;
  }

  public Map<Point, IndividualTimeSeries<WeatherValues>> mapQueryResults(
      List<HibernateWeatherInput> queryResults) {
    HashMap<Point, IndividualTimeSeries<WeatherValues>> coordinateToTimeSeries = new HashMap<>();
    if (queryResults != null) {
      List<TimeBasedValue<WeatherValues>> weatherInputs =
          queryResults.stream()
              .map(HibernateWeatherInput::toTimeBasedWeatherValues)
              .collect(Collectors.toList());

      for (Point coordinate :
          weatherInputs.stream()
              .map(hWeather -> hWeather.getValue().getCoordinate())
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
}
