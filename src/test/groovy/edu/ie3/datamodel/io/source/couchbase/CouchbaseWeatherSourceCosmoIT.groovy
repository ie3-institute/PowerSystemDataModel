/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.couchbase

import edu.ie3.datamodel.exceptions.NoDataException
import edu.ie3.datamodel.io.factory.timeseries.CosmoTimeBasedWeatherValueFactory
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueFactory
import edu.ie3.datamodel.io.source.IdCoordinateSource
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.test.common.CosmoWeatherTestData
import edu.ie3.util.geo.GeoUtils
import edu.ie3.util.interval.ClosedInterval
import org.locationtech.jts.geom.Point
import org.testcontainers.spock.Testcontainers

@Testcontainers
class CouchbaseWeatherSourceCosmoIT extends AbstractCouchbaseWeatherSourceIT {
  @Override
  String getJsonResourcePath() {
    return "src/test/resources/edu/ie3/datamodel/io/source/couchbase/_weather/cosmo/weather.json"
  }

  @Override
  TimeBasedWeatherValueFactory getWeatherFactory() {
    return new CosmoTimeBasedWeatherValueFactory()
  }

  @Override
  IdCoordinateSource getCoordinateSource() {
    return CosmoWeatherTestData.coordinateSource
  }

  def "A CouchbaseWeatherSource can read and correctly parse a single value for a specific date and coordinate"() {
    given:
    def expectedTimeBasedValue = new TimeBasedValue(CosmoWeatherTestData.TIME_15H, CosmoWeatherTestData.WEATHER_VALUE_193186_15H)

    when:
    def optTimeBasedValue = source.getWeather(CosmoWeatherTestData.TIME_15H, CosmoWeatherTestData.COORDINATE_193186)

    then:
    optTimeBasedValue != null
    equalsIgnoreUUID(optTimeBasedValue, expectedTimeBasedValue)
  }

  def "A CouchbaseWeatherSource can read multiple time series values for multiple coordinates"() {
    given:
    def coordinates = [
      CosmoWeatherTestData.COORDINATE_193186,
      CosmoWeatherTestData.COORDINATE_193187
    ]
    def timeInterval = new ClosedInterval(CosmoWeatherTestData.TIME_16H, CosmoWeatherTestData.TIME_17H)
    def timeSeries193186 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(CosmoWeatherTestData.TIME_16H, CosmoWeatherTestData.WEATHER_VALUE_193186_16H),
          new TimeBasedValue(CosmoWeatherTestData.TIME_17H, CosmoWeatherTestData.WEATHER_VALUE_193186_17H)
        ]
        as Set<TimeBasedValue>)
    def timeSeries193187 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(CosmoWeatherTestData.TIME_16H, CosmoWeatherTestData.WEATHER_VALUE_193187_16H)
        ] as Set<TimeBasedValue>)
    when:
    Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = source.getWeather(timeInterval, coordinates)
    then:
    coordinateToTimeSeries.keySet().size() == 2
    equalsIgnoreUUID(coordinateToTimeSeries.get(CosmoWeatherTestData.COORDINATE_193186), timeSeries193186)
    equalsIgnoreUUID(coordinateToTimeSeries.get(CosmoWeatherTestData.COORDINATE_193187), timeSeries193187)
  }

  def "A CouchbaseWeatherSource can read all weather data in a given time interval"() {
    given:
    def timeInterval = new ClosedInterval(CosmoWeatherTestData.TIME_15H, CosmoWeatherTestData.TIME_17H)
    def timeSeries193186 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(CosmoWeatherTestData.TIME_15H, CosmoWeatherTestData.WEATHER_VALUE_193186_15H),
          new TimeBasedValue(CosmoWeatherTestData.TIME_16H, CosmoWeatherTestData.WEATHER_VALUE_193186_16H),
          new TimeBasedValue(CosmoWeatherTestData.TIME_17H, CosmoWeatherTestData.WEATHER_VALUE_193186_17H)
        ] as Set<TimeBasedValue>)
    def timeSeries193187 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(CosmoWeatherTestData.TIME_15H, CosmoWeatherTestData.WEATHER_VALUE_193187_15H),
          new TimeBasedValue(CosmoWeatherTestData.TIME_16H, CosmoWeatherTestData.WEATHER_VALUE_193187_16H)
        ] as Set<TimeBasedValue>)
    def timeSeries193188 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(CosmoWeatherTestData.TIME_15H, CosmoWeatherTestData.WEATHER_VALUE_193188_15H)
        ] as Set<TimeBasedValue>)
    when:
    Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = source.getWeather(timeInterval)
    then:
    coordinateToTimeSeries.keySet().size() == 3
    equalsIgnoreUUID(coordinateToTimeSeries.get(CosmoWeatherTestData.COORDINATE_193186).entries, timeSeries193186.entries)
    equalsIgnoreUUID(coordinateToTimeSeries.get(CosmoWeatherTestData.COORDINATE_193187).entries, timeSeries193187.entries)
    equalsIgnoreUUID(coordinateToTimeSeries.get(CosmoWeatherTestData.COORDINATE_193188).entries, timeSeries193188.entries)
  }

  def "A CouchbaseWeatherSource falls back to the last known value when no exact weather data is found at a specific time"() {
    given:
    def futureTime = CosmoWeatherTestData.TIME_17H.plusHours(3)
    def expectedFallback = new TimeBasedValue(CosmoWeatherTestData.TIME_17H, CosmoWeatherTestData.WEATHER_VALUE_193186_17H)

    when:
    def result = source.getWeather(futureTime, CosmoWeatherTestData.COORDINATE_193186)

    then:
    result != null
    equalsIgnoreUUID(result, expectedFallback)
  }

  def "A CouchbaseWeatherSource throws NoDataException when no weather data is found at a specific time and no earlier data is available"() {
    given:
    def timeBeforeAllData = CosmoWeatherTestData.TIME_15H.minusHours(1)

    when:
    source.getWeather(timeBeforeAllData, CosmoWeatherTestData.COORDINATE_193186)

    then:
    def ex = thrown(NoDataException)
    ex.message.contains("No weather data found for coordinate")
    ex.message.contains("no earlier data available")
  }

  def "A CouchbaseWeatherSource throws NoDataException when the fallback is beyond the maximum allowed steps"() {
    given:
    def farFutureTime = CosmoWeatherTestData.TIME_17H.plusHours(4)

    when:
    source.getWeather(farFutureTime, CosmoWeatherTestData.COORDINATE_193186)

    then:
    def ex = thrown(NoDataException)
    ex.message.contains("No weather data found for coordinate")
    ex.message.contains("exceeds the maximum fallback")
  }

  def "A CouchbaseWeatherSource returns all time keys after a given time key correctly"() {
    given:
    def time = CosmoWeatherTestData.TIME_15H

    when:
    def actual = source.getTimeKeysAfter(time)

    then:
    actual.size() == 2

    actual.get(CosmoWeatherTestData.COORDINATE_193186) == [
      CosmoWeatherTestData.TIME_16H,
      CosmoWeatherTestData.TIME_17H
    ]
    actual.get(CosmoWeatherTestData.COORDINATE_193187) == [CosmoWeatherTestData.TIME_16H]
  }

  def "The CouchbaseWeatherSource returns all time keys after a given time for a specific coordinate"() {
    given:
    def time = CosmoWeatherTestData.TIME_15H

    when:
    def actual = source.getTimeKeysAfter(time, CosmoWeatherTestData.COORDINATE_193186)

    then:
    actual == [
      CosmoWeatherTestData.TIME_16H,
      CosmoWeatherTestData.TIME_17H
    ]
  }

  def "A CouchbaseWeatherSource throws NoDataException for invalid coordinate"() {
    given:
    def invalidCoordinate = GeoUtils.buildPoint(999d, 999d)

    when:
    source.getWeather(CosmoWeatherTestData.TIME_15H, invalidCoordinate)

    then:
    def ex = thrown(NoDataException)
    ex.message.contains("No coordinate ID found for the given point")
    ex.message.contains(invalidCoordinate.toString())
  }

  def "A CouchbaseWeatherSource returns partial results for mixed valid and invalid coordinates"() {
    given:
    def validCoordinate = CosmoWeatherTestData.COORDINATE_193186
    def invalidCoordinate = GeoUtils.buildPoint(999d, 999d)
    def timeInterval = new ClosedInterval(CosmoWeatherTestData.TIME_15H, CosmoWeatherTestData.TIME_17H)

    when:
    def result = source.getWeather(timeInterval, [
      validCoordinate,
      invalidCoordinate
    ])

    then:
    result.size() == 1
    result.containsKey(validCoordinate)
    !result.containsKey(invalidCoordinate)
  }
}
