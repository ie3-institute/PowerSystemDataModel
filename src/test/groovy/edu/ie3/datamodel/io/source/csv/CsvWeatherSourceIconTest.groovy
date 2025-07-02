/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.factory.timeseries.IconTimeBasedWeatherValueFactory
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.source.IdCoordinateSource
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.test.common.IconWeatherTestData
import edu.ie3.test.common.WeatherTestData
import edu.ie3.test.helper.WeatherSourceTestHelper
import edu.ie3.util.geo.GeoUtils
import edu.ie3.util.interval.ClosedInterval
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Point
import spock.lang.Shared
import spock.lang.Specification

class CsvWeatherSourceIconTest extends Specification implements CsvTestDataMeta {

  @Shared
  CsvWeatherSource source

  @Shared
  IdCoordinateSource coordinateSource

  def setupSpec() {
    coordinateSource = WeatherTestData.coordinateSource
    def weatherFactory = new IconTimeBasedWeatherValueFactory()
    source = new CsvWeatherSource(",", weatherIconFolderPath, new FileNamingStrategy(), coordinateSource, weatherFactory)
  }

  def "A CsvWeatherSource can read and correctly parse a single value for a specific date and coordinate"() {
    given:
    def expectedTimeBasedValue = new TimeBasedValue(IconWeatherTestData.TIME_15H, IconWeatherTestData.WEATHER_VALUE_67775_15H)

    when:
    def optTimeBasedValue = source.getWeather(IconWeatherTestData.TIME_15H, IconWeatherTestData.COORDINATE_67775)

    then:
    optTimeBasedValue.present
    WeatherSourceTestHelper.equalsIgnoreUUID(optTimeBasedValue.get(), expectedTimeBasedValue)
  }

  def "A CsvWeatherSource can read multiple time series values for multiple coordinates"() {
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
    Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = source.getWeather(timeInterval, coordinates)

    then:
    coordinateToTimeSeries.keySet().size() == 2
    WeatherSourceTestHelper.equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67775), timeSeries67775)
    WeatherSourceTestHelper.equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67776), timeSeries67776)
  }


  def "A CsvWeatherSource can read all weather data in a given time interval"() {
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
    Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = source.getWeather(timeInterval)

    then:
    coordinateToTimeSeries.keySet().size() == 2
    WeatherSourceTestHelper.equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67775).entries, timeSeries67775.entries)
    WeatherSourceTestHelper.equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67776).entries, timeSeries67776.entries)
  }

  def "The CsvWeatherSource is able to extract correct coordinate from field to value mapping"() {
    given:
    def expectedCoordinate = GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(new Coordinate(7.4116482, 51.4843281))
    def coordinateSource = Mock(IdCoordinateSource)
    coordinateSource.getCoordinate(_) >> { args -> args[0] == 67775 ? Optional.of(expectedCoordinate) : Optional.empty() }
    def weatherFactory = new IconTimeBasedWeatherValueFactory()
    def source = new CsvWeatherSource(",", weatherIconFolderPath, new FileNamingStrategy(), coordinateSource, weatherFactory)
    def fieldToValues = new TreeMap<>(String.CASE_INSENSITIVE_ORDER)
    fieldToValues.putAll(
        [
          "datum"       : "2019-08-01 01:00:00",
          "coordinateId": "67775",
        ])

    when:
    def actual = source.extractCoordinate(fieldToValues)

    then:
    actual.present
    actual.get() == expectedCoordinate
  }

  def "The CsvWeatherSource returns no WeatherValue, if the coordinate field is empty"() {
    given:
    def coordinateSource = new WeatherTestData.DummyIdCoordinateSource()
    def weatherFactory = new IconTimeBasedWeatherValueFactory()
    def source = new CsvWeatherSource(",", weatherIconFolderPath, new FileNamingStrategy(), coordinateSource, weatherFactory)
    def fieldToValues = ["coordinateId": ""]

    when:
    def actual = source.buildWeatherValue(fieldToValues)

    then:
    actual.empty
  }

  def "The CsvWeatherSource returns no WeatherValue, if the coordinate field is missing"() {
    given:
    def coordinateSource = new WeatherTestData.DummyIdCoordinateSource()
    def weatherFactory = new IconTimeBasedWeatherValueFactory()
    def source = new CsvWeatherSource(",", weatherIconFolderPath, new FileNamingStrategy(), coordinateSource, weatherFactory)
    def fieldToValues = [:]

    when:
    def actual = source.buildWeatherValue(fieldToValues)

    then:
    actual.empty
  }

  def "The CsvWeatherSource returns no WeatherValue, if the coordinate cannot be obtained"() {
    given:
    def coordinateSource = Mock(IdCoordinateSource)
    coordinateSource.getCoordinate(_) >> Optional.empty()
    def weatherFactory = new IconTimeBasedWeatherValueFactory()
    def source = new CsvWeatherSource(",", weatherIconFolderPath, new FileNamingStrategy(), coordinateSource, weatherFactory)
    def fieldToValues = ["coordinateId": "67777"]

    when:
    def actual = source.buildWeatherValue(fieldToValues)

    then:
    actual.empty
  }

  def "The CsvWeatherSource returns all time keys after a given time key correctly"() {
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
}