/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.couchbase

import edu.ie3.datamodel.exceptions.NoDataException
import edu.ie3.datamodel.io.factory.timeseries.IconTimeBasedWeatherValueFactory
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueFactory
import edu.ie3.datamodel.io.source.IdCoordinateSource
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.test.common.IconWeatherTestData
import edu.ie3.util.geo.GeoUtils
import edu.ie3.util.interval.ClosedInterval
import org.testcontainers.spock.Testcontainers

@Testcontainers
class CouchbaseWeatherSourceIconIT extends AbstractCouchbaseWeatherSourceIT {
  @Override
  String getJsonResourcePath() {
    return "src/test/resources/edu/ie3/datamodel/io/source/couchbase/_weather/icon/weather.json"
  }

  @Override
  TimeBasedWeatherValueFactory getWeatherFactory() {
    return new IconTimeBasedWeatherValueFactory()
  }

  @Override
  IdCoordinateSource getCoordinateSource() {
    return IconWeatherTestData.coordinateSource
  }

  def "A CouchbaseWeatherSource can read and correctly parse a single value for a specific date and coordinate"() {
    given:
    def expectedTimeBasedValue = new TimeBasedValue(IconWeatherTestData.TIME_15H, IconWeatherTestData.WEATHER_VALUE_67775_15H)

    when:
    def optTimeBasedValue = source.getWeather(IconWeatherTestData.TIME_15H, IconWeatherTestData.COORDINATE_67775)

    then:
    optTimeBasedValue != null
    equalsIgnoreUUID(optTimeBasedValue, expectedTimeBasedValue)
  }

  def "A CouchbaseWeatherSource can read multiple time series values for multiple coordinates"() {
    given:
    def coordinates = [
      IconWeatherTestData.COORDINATE_67775,
      IconWeatherTestData.COORDINATE_67776
    ]
    def timeInterval = new ClosedInterval(IconWeatherTestData.TIME_16H, IconWeatherTestData.TIME_17H)
    def timeSeries67775 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(IconWeatherTestData.TIME_16H, IconWeatherTestData.WEATHER_VALUE_67775_16H),
          new TimeBasedValue(IconWeatherTestData.TIME_17H, IconWeatherTestData.WEATHER_VALUE_67775_17H)
        ]
        as Set<TimeBasedValue>)
    def timeSeries67776 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(IconWeatherTestData.TIME_16H, IconWeatherTestData.WEATHER_VALUE_67776_16H)
        ] as Set<TimeBasedValue>)

    when:
    def coordinateToTimeSeries = source.getWeather(timeInterval, coordinates)

    then:
    coordinateToTimeSeries.keySet().size() == 2
    equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67775), timeSeries67775)
    equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67776), timeSeries67776)
  }

  def "A CouchbaseWeatherSource can read all weather data in a given time interval"() {
    given:
    def timeInterval = new ClosedInterval(IconWeatherTestData.TIME_15H, IconWeatherTestData.TIME_17H)
    def timeSeries67775 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(IconWeatherTestData.TIME_15H, IconWeatherTestData.WEATHER_VALUE_67775_15H),
          new TimeBasedValue(IconWeatherTestData.TIME_16H, IconWeatherTestData.WEATHER_VALUE_67775_16H),
          new TimeBasedValue(IconWeatherTestData.TIME_17H, IconWeatherTestData.WEATHER_VALUE_67775_17H)
        ] as Set<TimeBasedValue>)
    def timeSeries67776 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(IconWeatherTestData.TIME_15H, IconWeatherTestData.WEATHER_VALUE_67776_15H),
          new TimeBasedValue(IconWeatherTestData.TIME_16H, IconWeatherTestData.WEATHER_VALUE_67776_16H)
        ] as Set<TimeBasedValue>)

    when:
    def coordinateToTimeSeries = source.getWeather(timeInterval)

    then:
    coordinateToTimeSeries.keySet().size() == 2
    equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67775).entries, timeSeries67775.entries)
    equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67776).entries, timeSeries67776.entries)
  }

  def "The CouchbaseWeatherSource returns all time keys after a given time key correctly"() {
    given:
    def time = IconWeatherTestData.TIME_15H

    when:
    def actual = source.getTimeKeysAfter(time)

    then:
    actual.size() == 2

    actual.get(IconWeatherTestData.COORDINATE_67775) == [
      IconWeatherTestData.TIME_16H,
      IconWeatherTestData.TIME_17H
    ]
    actual.get(IconWeatherTestData.COORDINATE_67776) == [IconWeatherTestData.TIME_16H]
  }

  def "The CouchbaseWeatherSource returns all time keys after a given time for a specific coordinate"() {
    given:
    def time = IconWeatherTestData.TIME_15H

    when:
    def actual = source.getTimeKeysAfter(time, IconWeatherTestData.COORDINATE_67775)

    then:
    actual == [
      IconWeatherTestData.TIME_16H,
      IconWeatherTestData.TIME_17H
    ]
  }

  def "A CouchbaseWeatherSource throws NoDataException for invalid coordinate"() {
    given:
    def invalidCoordinate = GeoUtils.buildPoint(999d, 999d)

    when:
    source.getWeather(IconWeatherTestData.TIME_15H, invalidCoordinate)

    then:
    def ex = thrown(NoDataException)
    ex.message.contains("No coordinate ID found for the given point")
    ex.message.contains(invalidCoordinate.toString())
  }

  def "A CouchbaseWeatherSource falls back to the last known value when no exact weather data is found at a specific time"() {
    given:
    def futureTime = IconWeatherTestData.TIME_17H.plusHours(3)
    def expectedFallback = new TimeBasedValue(IconWeatherTestData.TIME_17H, IconWeatherTestData.WEATHER_VALUE_67775_17H)

    when:
    def result = source.getWeather(futureTime, IconWeatherTestData.COORDINATE_67775)

    then:
    result != null
    equalsIgnoreUUID(result, expectedFallback)
  }

  def "A CouchbaseWeatherSource throws NoDataException when no weather data is found at a specific time and no earlier data is available"() {
    given:
    def timeBeforeAllData = IconWeatherTestData.TIME_15H.minusHours(1)

    when:
    source.getWeather(timeBeforeAllData, IconWeatherTestData.COORDINATE_67775)

    then:
    def ex = thrown(NoDataException)
    ex.message.contains("No weather data found for coordinate")
    ex.message.contains("no earlier data available")
  }

  def "A CouchbaseWeatherSource throws NoDataException when the fallback is beyond the maximum allowed steps"() {
    given:
    def farFutureTime = IconWeatherTestData.TIME_17H.plusHours(4)

    when:
    source.getWeather(farFutureTime, IconWeatherTestData.COORDINATE_67775)

    then:
    def ex = thrown(NoDataException)
    ex.message.contains("No weather data found for coordinate")
    ex.message.contains("exceeds the maximum fallback")
  }

  def "A CouchbaseWeatherSource returns partial results for mixed valid and invalid coordinates"() {
    given:
    def validCoordinate = IconWeatherTestData.COORDINATE_67775
    def invalidCoordinate = GeoUtils.buildPoint(999d, 999d)
    def timeInterval = new ClosedInterval(IconWeatherTestData.TIME_15H, IconWeatherTestData.TIME_17H)

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
