/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.metrics;

import com.vividsolutions.jts.geom.Point;
import edu.ie3.dataconnection.source.WeatherSource;
import edu.ie3.dataconnection.source.csv.CsvCoordinateSource;
import edu.ie3.models.timeseries.IndividualTimeSeries;
import edu.ie3.models.value.WeatherValues;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.lang3.time.StopWatch;

public interface WeatherTimeMetricLogger<S extends WeatherSource> {

  ZonedDateTime START_DATE = ZonedDateTime.of(2013, 4, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
  ZonedDateTime END_DATE = ZonedDateTime.of(2013, 4, 7, 23, 59, 0, 0, ZoneId.of("UTC"));
  ClosedInterval<ZonedDateTime> TIME_INTERVAL = new ClosedInterval<>(START_DATE, END_DATE);

  Collection<Point> COORDINATES = CsvCoordinateSource.getCoordinatesBetween(10000, 20000);

  S getSource();

  void logAndMeasureWeatherTime();

  default long measureWeatherTime() throws AssertionError {
    StopWatch watch = new StopWatch();
    watch.start();
    Map<Point, IndividualTimeSeries<WeatherValues>> coordinateToTimeSeries =
        getSource().getWeather(TIME_INTERVAL, COORDINATES);
    watch.stop();
    if (!WeatherHealthCheck.check(coordinateToTimeSeries))
      throw new AssertionError("Result did not succeed health check");
    return watch.getTime();
  }

  default Object[] getWeatherTimeLog(int index) {
    long time = -1L;
    boolean succeededHealthCheck;
    ZonedDateTime start = ZonedDateTime.now();
    try {
      time = measureWeatherTime();
      succeededHealthCheck = true;
    } catch (AssertionError e) {
      succeededHealthCheck = false;
    }
    return getCsvLogParams(index, start, succeededHealthCheck, time);
  }

  default Object[] getCsvLogParams(
      int index, ZonedDateTime timestamp, boolean succeededHealthCheck, long time) {
    return new Object[] {index, timestamp, succeededHealthCheck, time};
  }
}
