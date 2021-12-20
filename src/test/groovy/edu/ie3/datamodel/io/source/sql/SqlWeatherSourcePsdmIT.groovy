/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.sql

import edu.ie3.datamodel.io.connectors.SqlConnector
import edu.ie3.datamodel.io.factory.timeseries.PsdmTimeBasedWeatherValueFactory
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.test.common.PsdmWeatherTestData
import edu.ie3.test.helper.WeatherSourceTestHelper
import edu.ie3.util.TimeUtil
import edu.ie3.util.geo.GeoUtils
import edu.ie3.util.interval.ClosedInterval
import org.locationtech.jts.geom.Point
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
class SqlWeatherSourcePsdmIT extends Specification implements WeatherSourceTestHelper {

	@Shared
	PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.9")

	@Shared
	SqlWeatherSource source

	static String schemaName = "public"
	static String weatherTableName = "weather"

	def setupSpec() {
		// Copy sql import script into docker
		MountableFile sqlImportFile = MountableFile.forClasspathResource("/testcontainersFiles/sql/psdm/weather.sql")
		postgreSQLContainer.copyFileToContainer(sqlImportFile, "/home/weather.sql")
		// Execute import script
		postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/weather.sql")

		def connector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)
		def weatherFactory = new PsdmTimeBasedWeatherValueFactory(TimeUtil.withDefaults)
		source = new SqlWeatherSource(connector, PsdmWeatherTestData.coordinateSource, schemaName, weatherTableName, weatherFactory)
	}

	def "A SqlWeatherSourcePsdm can read and correctly parse a single value for a specific date and coordinate"() {
		given:
		def expectedTimeBasedValue = new TimeBasedValue(PsdmWeatherTestData.TIME_15H, PsdmWeatherTestData.WEATHER_VALUE_193186_15H)

		when:
		def optTimeBasedValue = source.getWeather(PsdmWeatherTestData.TIME_15H, PsdmWeatherTestData.COORDINATE_193186)

		then:
		optTimeBasedValue.present
		equalsIgnoreUUID(optTimeBasedValue.get(), expectedTimeBasedValue )
	}

	def "A SqlWeatherSourcePsdm returns nothing for an invalid coordinate"() {
		when:
		def optTimeBasedValue = source.getWeather(PsdmWeatherTestData.TIME_15H, GeoUtils.xyToPoint(88d, 89d))

		then:
		!optTimeBasedValue.present
	}

	def "A SqlWeatherSourcePsdm can read multiple timeseries values for multiple coordinates"() {
		given:
		def coordinates = [
			PsdmWeatherTestData.COORDINATE_193186,
			PsdmWeatherTestData.COORDINATE_193187
		]
		def timeInterval = new ClosedInterval(PsdmWeatherTestData.TIME_16H, PsdmWeatherTestData.TIME_17H)
		def timeSeries193186 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(PsdmWeatherTestData.TIME_16H, PsdmWeatherTestData.WEATHER_VALUE_193186_16H),
					new TimeBasedValue(PsdmWeatherTestData.TIME_17H, PsdmWeatherTestData.WEATHER_VALUE_193186_17H)
				]
				as Set<TimeBasedValue>)
		def timeSeries193187 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(PsdmWeatherTestData.TIME_16H, PsdmWeatherTestData.WEATHER_VALUE_193187_16H)
				] as Set<TimeBasedValue>)

		when:
		Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = source.getWeather(timeInterval, coordinates)

		then:
		coordinateToTimeSeries.keySet().size() == 2
		equalsIgnoreUUID(coordinateToTimeSeries.get(PsdmWeatherTestData.COORDINATE_193186), timeSeries193186)
		equalsIgnoreUUID(coordinateToTimeSeries.get(PsdmWeatherTestData.COORDINATE_193187), timeSeries193187)
	}

	def "A SqlWeatherSourcePsdm returns nothing for invalid coordinates"() {
		given:
		def coordinates = [
			GeoUtils.xyToPoint(88d, 89d),
			GeoUtils.xyToPoint(89d, 89d)
		]
		def timeInterval = new ClosedInterval(PsdmWeatherTestData.TIME_16H, PsdmWeatherTestData.TIME_17H)

		when:
		Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = source.getWeather(timeInterval, coordinates)

		then:
		coordinateToTimeSeries.keySet().empty
	}

	def "A SqlWeatherSourcePsdm can read all weather data in a given time interval"() {
		given:
		def timeInterval = new ClosedInterval(PsdmWeatherTestData.TIME_15H, PsdmWeatherTestData.TIME_17H)
		def timeSeries193186 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(PsdmWeatherTestData.TIME_15H, PsdmWeatherTestData.WEATHER_VALUE_193186_15H),
					new TimeBasedValue(PsdmWeatherTestData.TIME_16H, PsdmWeatherTestData.WEATHER_VALUE_193186_16H),
					new TimeBasedValue(PsdmWeatherTestData.TIME_17H, PsdmWeatherTestData.WEATHER_VALUE_193186_17H)
				] as Set<TimeBasedValue>)
		def timeSeries193187 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(PsdmWeatherTestData.TIME_15H, PsdmWeatherTestData.WEATHER_VALUE_193187_15H),
					new TimeBasedValue(PsdmWeatherTestData.TIME_16H, PsdmWeatherTestData.WEATHER_VALUE_193187_16H)
				] as Set<TimeBasedValue>)
		def timeSeries193188 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(PsdmWeatherTestData.TIME_15H, PsdmWeatherTestData.WEATHER_VALUE_193188_15H)
				] as Set<TimeBasedValue>)

		when:
		Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = source.getWeather(timeInterval)

		then:
		coordinateToTimeSeries.keySet().size() == 3
		equalsIgnoreUUID(coordinateToTimeSeries.get(PsdmWeatherTestData.COORDINATE_193186).entries, timeSeries193186.entries)
		equalsIgnoreUUID(coordinateToTimeSeries.get(PsdmWeatherTestData.COORDINATE_193187).entries, timeSeries193187.entries)
		equalsIgnoreUUID(coordinateToTimeSeries.get(PsdmWeatherTestData.COORDINATE_193188).entries, timeSeries193188.entries)
	}
}
