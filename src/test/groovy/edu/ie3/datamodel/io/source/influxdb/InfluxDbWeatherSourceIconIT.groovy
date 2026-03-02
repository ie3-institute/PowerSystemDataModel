/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.influxdb

import edu.ie3.datamodel.exceptions.NoDataException
import edu.ie3.datamodel.io.connectors.InfluxDbConnector
import edu.ie3.datamodel.io.factory.timeseries.IconTimeBasedWeatherValueFactory
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.test.common.IconWeatherTestData
import edu.ie3.test.helper.TestContainerHelper
import edu.ie3.test.helper.WeatherSourceTestHelper
import edu.ie3.util.geo.GeoUtils
import edu.ie3.util.interval.ClosedInterval
import org.testcontainers.containers.Container
import org.testcontainers.containers.InfluxDBContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
class InfluxDbWeatherSourceIconIT extends Specification implements WeatherSourceTestHelper, TestContainerHelper {

  @Shared
  InfluxDBContainer influxDbContainer = new InfluxDBContainer(DockerImageName.parse("influxdb").withTag("1.8.10"))
  .withAuthEnabled(false)
  .withDatabase("test_weather")

  @Shared
  InfluxDbWeatherSource source

  def setupSpec() {
    // Copy import file into docker and then import it via influx CLI
    // more information on file format and usage here: https://docs.influxdata.com/influxdb/v1.7/tools/shell/#import-data-from-a-file-with-import
    MountableFile influxWeatherImportFile = getMountableFile("_weather/icon/weather.txt")
    influxDbContainer.copyFileToContainer(influxWeatherImportFile, "/home/weather_icon.txt")

    Container.ExecResult res = influxDbContainer.execInContainer("influx", "-import", "-path=/home/weather_icon.txt", "-precision=ms")
    assert res.stderr.empty

    def connector = new InfluxDbConnector(influxDbContainer.url, "test_weather", "test_scenario")
    def weatherFactory = new IconTimeBasedWeatherValueFactory()
    source = new InfluxDbWeatherSource(connector, IconWeatherTestData.coordinateSource, weatherFactory)
  }

  def "The test container can establish a valid connection"() {
    when:
    def connector = new InfluxDbConnector(influxDbContainer.url, "test_weather", "test_scenario")

    then:
    connector.isConnectionValid()
  }

  def "An InfluxDbWeatherSource can read and correctly parse a single value for a specific date and coordinate"() {
    given:
    def expectedTimeBasedValue = new TimeBasedValue(IconWeatherTestData.TIME_15H , IconWeatherTestData.WEATHER_VALUE_67775_15H)

    when:
    def optTimeBasedValue = source.getWeather(IconWeatherTestData.TIME_15H , IconWeatherTestData.COORDINATE_67775)

    then:
    optTimeBasedValue != null
    equalsIgnoreUUID(optTimeBasedValue, expectedTimeBasedValue)
  }

  def "An InfluxDbWeatherSource can read multiple time series values for multiple coordinates"() {
    given:
    def coordinates = [
      IconWeatherTestData.COORDINATE_67775,
      IconWeatherTestData.COORDINATE_67776
    ]
    def timeInterval = new ClosedInterval(IconWeatherTestData.TIME_16H , IconWeatherTestData.TIME_17H)
    def timeseries67775 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(IconWeatherTestData.TIME_16H , IconWeatherTestData.WEATHER_VALUE_67775_16H),
          new TimeBasedValue(IconWeatherTestData.TIME_17H , IconWeatherTestData.WEATHER_VALUE_67775_17H)
        ]
        as Set<TimeBasedValue>)
    def timeseries67776 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(IconWeatherTestData.TIME_16H , IconWeatherTestData.WEATHER_VALUE_67776_16H)
        ] as Set<TimeBasedValue>)

    when:
    def coordinateToTimeSeries = source.getWeather(timeInterval, coordinates)

    then:
    coordinateToTimeSeries.keySet().size() == 2
    equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67775), timeseries67775)
    equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67776), timeseries67776)
  }

  def "An InfluxDbWeatherSource can read all weather data in a given time interval"() {
    given:
    def timeInterval = new ClosedInterval(IconWeatherTestData.TIME_15H , IconWeatherTestData.TIME_17H)
    def timeseries67775 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(IconWeatherTestData.TIME_15H, IconWeatherTestData.WEATHER_VALUE_67775_15H),
          new TimeBasedValue(IconWeatherTestData.TIME_16H, IconWeatherTestData.WEATHER_VALUE_67775_16H),
          new TimeBasedValue(IconWeatherTestData.TIME_17H, IconWeatherTestData.WEATHER_VALUE_67775_17H)
        ] as Set<TimeBasedValue>)
    def timeseries67776 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(IconWeatherTestData.TIME_15H, IconWeatherTestData.WEATHER_VALUE_67776_15H),
          new TimeBasedValue(IconWeatherTestData.TIME_16H, IconWeatherTestData.WEATHER_VALUE_67776_16H)
        ] as Set<TimeBasedValue>)

    when:
    def coordinateToTimeSeries = source.getWeather(timeInterval)

    then:
    coordinateToTimeSeries.keySet().size() == 2
    equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67775).entries, timeseries67775.entries)
    equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67776).entries, timeseries67776.entries)
  }

  def "An InfluxDbWeatherSource will throw NoDataException when being unable to map a coordinate to its ID"() {
    given:
    def validCoordinate = IconWeatherTestData.COORDINATE_67775
    def invalidCoordinate = GeoUtils.buildPoint(7d, 48d)
    def time = IconWeatherTestData.TIME_15H
    def timeInterval = new ClosedInterval(IconWeatherTestData.TIME_15H , IconWeatherTestData.TIME_17H)

    when: "requesting weather for an invalid coordinate at a specific date"
    source.getWeather(time, invalidCoordinate)

    then: "NoDataException is thrown"
    def ex1 = thrown(NoDataException)
    ex1.message.contains("No coordinate ID found for the given point")
    ex1.message.contains(invalidCoordinate.toString())

    when: "requesting weather for an invalid coordinate in a time interval"
    source.getWeather(timeInterval, invalidCoordinate)

    then: "NoDataException is thrown"
    def ex2 = thrown(NoDataException)
    ex2.message.contains("No coordinate ID found for the given point")
    ex2.message.contains(invalidCoordinate.toString())

    when: "requesting weather for mixed valid and invalid coordinates"
    def result = source.getWeather(timeInterval, [
      validCoordinate,
      invalidCoordinate
    ])

    then: "only the valid coordinate's data is returned"
    result.size() == 1
    result.containsKey(validCoordinate)
    !result.containsKey(invalidCoordinate)
  }

  def "An InfluxDbWeatherSource falls back to the last known value when no exact weather data is found at a specific time"() {
    given:
    def futureTime = IconWeatherTestData.TIME_17H.plusHours(2)
    def expectedFallback = new TimeBasedValue(IconWeatherTestData.TIME_17H, IconWeatherTestData.WEATHER_VALUE_67775_17H)

    when:
    def result = source.getWeather(futureTime, IconWeatherTestData.COORDINATE_67775)

    then:
    result != null
    equalsIgnoreUUID(result, expectedFallback)
  }

  def "An InfluxDbWeatherSource throws NoDataException when no weather data is found at a specific time and no earlier data is available"() {
    given:
    def timeBeforeAllData = IconWeatherTestData.TIME_15H.minusHours(1)

    when:
    source.getWeather(timeBeforeAllData, IconWeatherTestData.COORDINATE_67775)

    then:
    def ex = thrown(NoDataException)
    ex.message.contains("No weather data found for coordinate")
    ex.message.contains("no earlier data available")
  }

  def "An InfluxDbWeatherSource throws NoDataException when the fallback is beyond the maximum allowed steps"() {
    given:
    def farFutureTime = IconWeatherTestData.TIME_17H.plusHours(10)

    when:
    source.getWeather(farFutureTime, IconWeatherTestData.COORDINATE_67775)

    then:
    def ex = thrown(NoDataException)
    ex.message.contains("No weather data found for coordinate")
    ex.message.contains("exceeds the maximum fallback")
  }

  def "The InfluxDbWeatherSource returns all time keys after a given time key correctly"() {
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
