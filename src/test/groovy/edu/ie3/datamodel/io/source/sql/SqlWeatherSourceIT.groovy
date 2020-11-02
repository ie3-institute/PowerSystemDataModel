/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.sql

import edu.ie3.datamodel.io.connectors.SqlConnector
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.test.common.WeatherTestData
import edu.ie3.test.helper.WeatherSourceTestHelper
import edu.ie3.util.interval.ClosedInterval
import org.locationtech.jts.geom.Point
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
class SqlWeatherSourceIT extends Specification implements WeatherSourceTestHelper {

	@Shared
	PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.9")
	.withExposedPorts(5432)

	@Shared
	SqlWeatherSource source

	static String schemaName = "public"
	static String weatherTableName = "weather"
	static String coordinateColumnName = "coordinate"
	static String timeColumnName = "time"

	def setupSpec() {
		// Copy sql import script into docker
		MountableFile sqlImportFile = MountableFile.forClasspathResource("/testcontainersFiles/sql/weather.sql")
		postgreSQLContainer.copyFileToContainer(sqlImportFile, "/home/weather.sql")
		// Execute import script
		postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/weather.sql")

		def connector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)
		source = new SqlWeatherSource(connector, WeatherTestData.coordinateSource, schemaName, weatherTableName, coordinateColumnName, timeColumnName)
	}

	def "A NativeSqlWeatherSource can read and correctly parse a single value for a specific date and coordinate"() {
		given:
		def expectedTimeBasedValue = new TimeBasedValue(WeatherTestData.TIME_15H, WeatherTestData.WEATHER_VALUE_193186_15H)
		when:
		def optTimeBasedValue = source.getWeather(WeatherTestData.TIME_15H, WeatherTestData.COORDINATE_193186)
		then:
		optTimeBasedValue.present
		equalsIgnoreUUID(optTimeBasedValue.get(), expectedTimeBasedValue )
	}

	def "A NativeSqlWeatherSource can read multiple timeseries values for multiple coordinates"() {
		given:
		def coordinates = [
			WeatherTestData.COORDINATE_193186,
			WeatherTestData.COORDINATE_193187
		]
		def timeInterval = new ClosedInterval(WeatherTestData.TIME_16H, WeatherTestData.TIME_17H)
		def timeseries_193186 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.TIME_16H, WeatherTestData.WEATHER_VALUE_193186_16H),
					new TimeBasedValue(WeatherTestData.TIME_17H, WeatherTestData.WEATHER_VALUE_193186_17H)]
				as Set<TimeBasedValue>)
		def timeseries_193187 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.TIME_16H, WeatherTestData.WEATHER_VALUE_193187_16H)] as Set<TimeBasedValue>)
		when:
		Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = source.getWeather(timeInterval, coordinates)
		then:
		coordinateToTimeSeries.keySet().size() == 2
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.COORDINATE_193186), timeseries_193186)
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.COORDINATE_193187), timeseries_193187)
	}



	def "A NativeSqlWeatherSource can read all weather data in a given time interval"() {
		given:
		def timeInterval = new ClosedInterval(WeatherTestData.TIME_15H, WeatherTestData.TIME_17H)
		def timeseries_193186 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.TIME_15H, WeatherTestData.WEATHER_VALUE_193186_15H),
					new TimeBasedValue(WeatherTestData.TIME_16H, WeatherTestData.WEATHER_VALUE_193186_16H),
					new TimeBasedValue(WeatherTestData.TIME_17H, WeatherTestData.WEATHER_VALUE_193186_17H)] as Set<TimeBasedValue>)
		def timeseries_193187 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.TIME_15H, WeatherTestData.WEATHER_VALUE_193187_15H),
					new TimeBasedValue(WeatherTestData.TIME_16H, WeatherTestData.WEATHER_VALUE_193187_16H)] as Set<TimeBasedValue>)
		def timeseries_193188 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.TIME_15H, WeatherTestData.WEATHER_VALUE_193187_16H)] as Set<TimeBasedValue>)
		when:
		Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = source.getWeather(timeInterval)
		then:
		coordinateToTimeSeries.keySet().size() == 3
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.COORDINATE_193186).entries, timeseries_193186.entries)
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.COORDINATE_193187).entries, timeseries_193187.entries)
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.COORDINATE_193188).entries, timeseries_193188.entries)
	}
}
