/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.locationtech.jts.geom.Point;

/** Describes a data source for weather data */
public interface WeatherSource extends DataSource {

  /** @return weather data for the specified time range, sorted by coordinate */
  Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval);

  /** @return weather data for the specified time range and coordinates, sorted by coordinate */
  Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates);

  /** @return weather data for the specified time and coordinate */
  Optional<TimeBasedValue> getWeather(ZonedDateTime date, Point coordinate);
}
