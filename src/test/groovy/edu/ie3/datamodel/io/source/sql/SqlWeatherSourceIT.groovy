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

	def setupSpec() {
		// Copy sql import script into docker
		MountableFile sqlImportFile = MountableFile.forClasspathResource("/testcontainersFiles/nativeSql/weather.sql")
		postgreSQLContainer.copyFileToContainer(sqlImportFile, "/home/weather.sql")
		// Execute import script
		def execResult = postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/weather.sql")
		println "Command \"psql -U test -f /home/weather.sql\" returned:"
		if(!execResult.stderr.isEmpty()) println execResult.getStderr()
		if(!execResult.stdout.isEmpty()) println execResult.getStdout()

		def connector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)
		source = new SqlWeatherSource(connector, WeatherTestData.coordinateSource)
	}

	def "A NativeSqlWeatherSource can read and correctly parse a single value for a specific date and coordinate"() {
		given:
		def expectedTimeBasedValue = new TimeBasedValue(WeatherTestData.time_15h, WeatherTestData.weatherVal_coordinate_193186_15h)
		when:
		def optTimeBasedValue = source.getWeather(WeatherTestData.time_15h, WeatherTestData.coordinate_193186)
		then:
		optTimeBasedValue.isPresent()
		equalsIgnoreUUID(optTimeBasedValue.get(), expectedTimeBasedValue )
	}

	def "A NativeSqlWeatherSource can read multiple timeseries values for multiple coordinates"() {
		given:
		def coordinates = [
			WeatherTestData.coordinate_193186,
			WeatherTestData.coordinate_193187
		]
		def timeInterval = new ClosedInterval(WeatherTestData.time_16h, WeatherTestData.time_17h)
		def timeseries_193186 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.time_16h, WeatherTestData.weatherVal_coordinate_193186_16h),
					new TimeBasedValue(WeatherTestData.time_17h, WeatherTestData.weatherVal_coordinate_193186_17h)]
				as Set<TimeBasedValue>)
		def timeseries_193187 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.time_16h, WeatherTestData.weatherVal_coordinate_193187_16h)] as Set<TimeBasedValue>)
		when:
		Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = source.getWeather(timeInterval, coordinates)
		then:
		coordinateToTimeSeries.keySet().size() == 2
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.coordinate_193186), timeseries_193186)
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.coordinate_193187), timeseries_193187)
	}



	def "A NativeSqlWeatherSource can read all weather data in a given time interval"() {
		given:
		def timeInterval = new ClosedInterval(WeatherTestData.time_15h, WeatherTestData.time_17h)
		def timeseries_193186 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.time_15h,WeatherTestData.weatherVal_coordinate_193186_15h),
					new TimeBasedValue(WeatherTestData.time_16h,WeatherTestData.weatherVal_coordinate_193186_16h),
					new TimeBasedValue(WeatherTestData.time_17h,WeatherTestData.weatherVal_coordinate_193186_17h)] as Set<TimeBasedValue>)
		def timeseries_193187 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.time_15h,WeatherTestData.weatherVal_coordinate_193187_15h),
					new TimeBasedValue(WeatherTestData.time_16h,WeatherTestData.weatherVal_coordinate_193187_16h)] as Set<TimeBasedValue>)
		def timeseries_193188 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.time_15h,WeatherTestData.weatherVal_coordinate_193188_15h)] as Set<TimeBasedValue>)
		when:
		Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = source.getWeather(timeInterval)
		then:
		coordinateToTimeSeries.keySet().size() == 3
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.coordinate_193186).getEntries(), timeseries_193186.getEntries())
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.coordinate_193187).getEntries(), timeseries_193187.getEntries())
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.coordinate_193188).getEntries(), timeseries_193188.getEntries())
	}
}
