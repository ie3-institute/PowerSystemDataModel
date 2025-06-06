/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.influxdb

import edu.ie3.datamodel.io.connectors.InfluxDbConnector
import edu.ie3.datamodel.io.factory.timeseries.CosmoTimeBasedWeatherValueFactory
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.test.common.CosmoWeatherTestData
import edu.ie3.test.helper.TestContainerHelper
import edu.ie3.test.helper.WeatherSourceTestHelper
import edu.ie3.util.geo.GeoUtils
import edu.ie3.util.interval.ClosedInterval
import org.locationtech.jts.geom.Point
import org.testcontainers.containers.Container
import org.testcontainers.containers.InfluxDBContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
class InfluxDbWeatherSourceCosmoIT extends Specification implements TestContainerHelper, WeatherSourceTestHelper {

  @Shared
  InfluxDBContainer influxDbContainer = new InfluxDBContainer(DockerImageName.parse("influxdb").withTag("1.8.10"))
  .withAuthEnabled(false)
  .withDatabase("test_weather")

  @Shared
  InfluxDbWeatherSource source

  def setupSpec() {
    // Copy import file into docker and then import it via influx CLI
    // more information on file format and usage here: https://docs.influxdata.com/influxdb/v1.7/tools/shell/#import-data-from-a-file-with-import
    MountableFile influxWeatherImportFile = getMountableFile("_weather/cosmo/weather.txt")
    influxDbContainer.copyFileToContainer(influxWeatherImportFile, "/home/weather_cosmo.txt")

    Container.ExecResult res = influxDbContainer.execInContainer("influx", "-import", "-path=/home/weather_cosmo.txt", "-precision=ms")
    assert res.stderr.empty

    def connector = new InfluxDbConnector(influxDbContainer.url, "test_weather", "test_scenario")
    def weatherFactory = new CosmoTimeBasedWeatherValueFactory()
    source = new InfluxDbWeatherSource(connector, CosmoWeatherTestData.coordinateSource, weatherFactory)
  }

  def "The test container can establish a valid connection"() {
    when:
    def connector = new InfluxDbConnector(influxDbContainer.url,"test_weather", "test_scenario")

    then:
    connector.isConnectionValid()
  }

  def "An InfluxDbWeatherSource can read and correctly parse a single value for a specific date and coordinate"() {
    given:
    def expectedTimeBasedValue = new TimeBasedValue(CosmoWeatherTestData.TIME_15H, CosmoWeatherTestData.WEATHER_VALUE_193186_15H)

    when:
    def optTimeBasedValue = source.getWeather(CosmoWeatherTestData.TIME_15H, CosmoWeatherTestData.COORDINATE_193186)

    then:
    optTimeBasedValue.present
    equalsIgnoreUUID(optTimeBasedValue.get(), expectedTimeBasedValue)
  }

  def "An InfluxDbWeatherSource can read multiple time series values for multiple coordinates"() {
    given:
    def coordinates = [
      CosmoWeatherTestData.COORDINATE_193186,
      CosmoWeatherTestData.COORDINATE_193187
    ]
    def timeInterval = new ClosedInterval(CosmoWeatherTestData.TIME_16H, CosmoWeatherTestData.TIME_17H)
    def timeseries_193186 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(CosmoWeatherTestData.TIME_16H, CosmoWeatherTestData.WEATHER_VALUE_193186_16H),
          new TimeBasedValue(CosmoWeatherTestData.TIME_17H, CosmoWeatherTestData.WEATHER_VALUE_193186_17H)
        ]
        as Set<TimeBasedValue>)
    def timeseries_193187 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(CosmoWeatherTestData.TIME_16H, CosmoWeatherTestData.WEATHER_VALUE_193187_16H)
        ] as Set<TimeBasedValue>)

    when:
    Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = source.getWeather(timeInterval, coordinates)

    then:
    coordinateToTimeSeries.keySet().size() == 2
    equalsIgnoreUUID(coordinateToTimeSeries.get(CosmoWeatherTestData.COORDINATE_193186), timeseries_193186)
    equalsIgnoreUUID(coordinateToTimeSeries.get(CosmoWeatherTestData.COORDINATE_193187), timeseries_193187)
  }

  def "An InfluxDbWeatherSource can read all weather data in a given time interval"() {
    given:
    def timeInterval = new ClosedInterval(CosmoWeatherTestData.TIME_15H, CosmoWeatherTestData.TIME_17H)
    def timeseries_193186 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(CosmoWeatherTestData.TIME_15H, CosmoWeatherTestData.WEATHER_VALUE_193186_15H),
          new TimeBasedValue(CosmoWeatherTestData.TIME_16H, CosmoWeatherTestData.WEATHER_VALUE_193186_16H),
          new TimeBasedValue(CosmoWeatherTestData.TIME_17H, CosmoWeatherTestData.WEATHER_VALUE_193186_17H)
        ] as Set<TimeBasedValue>)
    def timeseries_193187 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(CosmoWeatherTestData.TIME_15H, CosmoWeatherTestData.WEATHER_VALUE_193187_15H),
          new TimeBasedValue(CosmoWeatherTestData.TIME_16H, CosmoWeatherTestData.WEATHER_VALUE_193187_16H)
        ] as Set<TimeBasedValue>)
    def timeseries_193188 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(CosmoWeatherTestData.TIME_15H, CosmoWeatherTestData.WEATHER_VALUE_193188_15H)
        ] as Set<TimeBasedValue>)

    when:
    Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = source.getWeather(timeInterval)

    then:
    coordinateToTimeSeries.keySet().size() == 3
    equalsIgnoreUUID(coordinateToTimeSeries.get(CosmoWeatherTestData.COORDINATE_193186).entries, timeseries_193186.entries)
    equalsIgnoreUUID(coordinateToTimeSeries.get(CosmoWeatherTestData.COORDINATE_193187).entries, timeseries_193187.entries)
    equalsIgnoreUUID(coordinateToTimeSeries.get(CosmoWeatherTestData.COORDINATE_193188).entries, timeseries_193188.entries)
  }

  def "An InfluxDbWeatherSource will return an equivalent to 'empty' when being unable to map a coordinate to its ID"() {
    given:
    def validCoordinate = CosmoWeatherTestData.COORDINATE_193186
    def invalidCoordinate = GeoUtils.buildPoint(7d, 48d)
    def time = CosmoWeatherTestData.TIME_15H
    def timeInterval = new ClosedInterval(CosmoWeatherTestData.TIME_15H, CosmoWeatherTestData.TIME_17H)
    def emptyTimeSeries = new IndividualTimeSeries(UUID.randomUUID(), Collections.emptySet())
    def timeseries_193186 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(CosmoWeatherTestData.TIME_15H, CosmoWeatherTestData.WEATHER_VALUE_193186_15H),
          new TimeBasedValue(CosmoWeatherTestData.TIME_16H, CosmoWeatherTestData.WEATHER_VALUE_193186_16H),
          new TimeBasedValue(CosmoWeatherTestData.TIME_17H, CosmoWeatherTestData.WEATHER_VALUE_193186_17H)
        ] as Set<TimeBasedValue>)

    when:
    def coordinateAtDate = source.getWeather(time, invalidCoordinate)
    def coordinateInInterval = source.getWeather(timeInterval, invalidCoordinate)
    def coordinatesToTimeSeries = source.getWeather(timeInterval, [
      validCoordinate,
      invalidCoordinate
    ])

    then:
    coordinateAtDate == Optional.empty()
    equalsIgnoreUUID(coordinateInInterval, emptyTimeSeries)
    coordinatesToTimeSeries.keySet() == [validCoordinate].toSet()
    equalsIgnoreUUID(coordinatesToTimeSeries.get(validCoordinate), timeseries_193186)
  }

  def "A InfluxDbWeatherSource returns all time keys after a given time key correctly"() {
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
}
