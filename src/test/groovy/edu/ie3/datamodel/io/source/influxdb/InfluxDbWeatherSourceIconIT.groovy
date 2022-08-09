/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.influxdb

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
		def weatherFactory = new IconTimeBasedWeatherValueFactory("yyyy-MM-dd'T'HH:mm:ss[.S[S][S]]'Z'")
		source = new InfluxDbWeatherSource(connector, IconWeatherTestData.coordinateSource, weatherFactory)
	}

	def "The test container can establish a valid connection"() {
		when:
		def connector = new InfluxDbConnector(influxDbContainer.url, "test_weather", "test_scenario")

		then:
		connector.connectionValid
	}

	def "An InfluxDbWeatherSource can read and correctly parse a single value for a specific date and coordinate"() {
		given:
		def expectedTimeBasedValue = new TimeBasedValue(IconWeatherTestData.TIME_15H , IconWeatherTestData.WEATHER_VALUE_67775_15H)

		when:
		def optTimeBasedValue = source.getWeather(IconWeatherTestData.TIME_15H , IconWeatherTestData.COORDINATE_67775)

		then:
		optTimeBasedValue.present
		equalsIgnoreUUID(optTimeBasedValue.get(), expectedTimeBasedValue)
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
					new TimeBasedValue(IconWeatherTestData.TIME_17H , IconWeatherTestData.WEATHER_VALUE_67775_17H)]
				as Set<TimeBasedValue>)
		def timeseries67776 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(IconWeatherTestData.TIME_16H , IconWeatherTestData.WEATHER_VALUE_67776_16H)] as Set<TimeBasedValue>)

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
					new TimeBasedValue(IconWeatherTestData.TIME_17H, IconWeatherTestData.WEATHER_VALUE_67775_17H)] as Set<TimeBasedValue>)
		def timeseries67776 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(IconWeatherTestData.TIME_15H, IconWeatherTestData.WEATHER_VALUE_67776_15H),
					new TimeBasedValue(IconWeatherTestData.TIME_16H, IconWeatherTestData.WEATHER_VALUE_67776_16H)] as Set<TimeBasedValue>)

		when:
		def coordinateToTimeSeries = source.getWeather(timeInterval)

		then:
		coordinateToTimeSeries.keySet().size() == 2
		equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67775).entries, timeseries67775.entries)
		equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67776).entries, timeseries67776.entries)
	}

	def "An InfluxDbWeatherSource will return an equivalent to 'empty' when being unable to map a coordinate to it's ID"() {
		given:
		def validCoordinate = IconWeatherTestData.COORDINATE_67775
		def invalidCoordinate = GeoUtils.buildPoint(7d, 48d)
		def time = IconWeatherTestData.TIME_15H
		def timeInterval = new ClosedInterval(IconWeatherTestData.TIME_15H , IconWeatherTestData.TIME_17H)
		def emptyTimeSeries = new IndividualTimeSeries(UUID.randomUUID(), Collections.emptySet())
		def timeseries67775 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(IconWeatherTestData.TIME_15H, IconWeatherTestData.WEATHER_VALUE_67775_15H),
					new TimeBasedValue(IconWeatherTestData.TIME_16H, IconWeatherTestData.WEATHER_VALUE_67775_16H),
					new TimeBasedValue(IconWeatherTestData.TIME_17H, IconWeatherTestData.WEATHER_VALUE_67775_17H)] as Set<TimeBasedValue>)

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
		equalsIgnoreUUID(coordinatesToTimeSeries.get(validCoordinate), timeseries67775)
	}
}
