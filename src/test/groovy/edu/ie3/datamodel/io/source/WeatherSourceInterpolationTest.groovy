/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.util.TimeUtil
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import spock.lang.Shared
import spock.lang.Specification

import java.time.ZonedDateTime

class WeatherSourceInterpolationTest extends Specification {

  @Shared
  def geometryFactory = new GeometryFactory()

  def "interpolateMissingValues fills gaps between known weather values"() {
    given:
    def coordinate = geometryFactory.createPoint(new Coordinate(7.0, 51.0))
    def t1 = TimeUtil.withDefaults.toZonedDateTime("2020-01-01T00:00:00Z")
    def t2 = TimeUtil.withDefaults.toZonedDateTime("2020-01-01T01:00:00Z")
    def t3 = TimeUtil.withDefaults.toZonedDateTime("2020-01-01T02:00:00Z")
    def t4 = TimeUtil.withDefaults.toZonedDateTime("2020-01-01T03:00:00Z")
    def value1 = new WeatherValue(coordinate, 100,  200,  10,  5, 180)
    def value3 = new WeatherValue(coordinate, 300 , 400, 20, 10, 270)

    def series = [
      new TimeBasedValue(t1, value1),
      new TimeBasedValue(t3, value3),
      new TimeBasedValue(t4, value3)
    ] as Set<TimeBasedValue>

    def mockSource = new InterpolatingWeatherSource()

    when:
    def interpolatedSeries = mockSource.interpolateMissingValues(series)

    then:
    interpolatedSeries.size() == 4
    def interpolated = interpolatedSeries.find { it.time == t2 }
    interpolated != null
    with(interpolated.value as WeatherValue) {
      getTemperature().value.doubleValue() == 15.0
      getWindVelocity().value.doubleValue() == 7.5
      getWindDirection().value.doubleValue() == 225.0
    }
  }

  private static class InterpolatingWeatherSource extends WeatherSource {
    @Override
    Optional<TimeBasedValue<WeatherValue>> getWeather(ZonedDateTime dateTime, Point coordinate) {
      return Optional.empty()
    }
  }
}