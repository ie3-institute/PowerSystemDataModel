/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.couchbase

import edu.ie3.datamodel.io.connectors.CouchbaseConnector
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.test.common.WeatherTestData
import edu.ie3.test.helper.WeatherSourceTestHelper
import edu.ie3.util.interval.ClosedInterval
import org.locationtech.jts.geom.Point
import org.testcontainers.couchbase.BucketDefinition
import org.testcontainers.couchbase.CouchbaseContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
class CouchbaseWeatherSourceIT extends Specification implements WeatherSourceTestHelper{

	@Shared
	BucketDefinition bucketDefinition = new BucketDefinition("ie3_in")

	@Shared
	CouchbaseContainer couchbaseContainer = new CouchbaseContainer().withBucket(bucketDefinition)
	.withExposedPorts(8091, 8092, 8093, 8094, 11210)

	@Shared
	CouchbaseWeatherSource source

	def setupSpec() {

		// Copy import file with json array of documents into docker
		MountableFile couchbaseWeatherJsonsFile = MountableFile.forClasspathResource("/testcontainersFiles/couchbase/weather.json")
		couchbaseContainer.copyFileToContainer(couchbaseWeatherJsonsFile, "/home/weather.json")

		// create an index for the document keys
		def execResult = couchbaseContainer.execInContainer("cbq",
				"-e", "http://localhost:8093",
				"-u", couchbaseContainer.getUsername(),
				"-p", couchbaseContainer.getPassword(),
				"-s", "CREATE index id_idx ON `" + bucketDefinition.getName() + "` (META().id);")

		//import the json documents from the copied file
		execResult = couchbaseContainer.execInContainer("cbimport", "json",
				"-cluster", "http://localhost:8091",
				"--bucket", "ie3_in",
				"--username", couchbaseContainer.getUsername(),
				"--password", couchbaseContainer.getPassword(),
				"--format", "list",
				"--generate-key", "weather::%" + CouchbaseWeatherSource.COORDINATE_ID_COLUMN_NAME + "%::%time%",
				"--dataset", "file:///home/weather.json")

		def connector = new CouchbaseConnector(couchbaseContainer.connectionString, bucketDefinition.getName(), couchbaseContainer.getUsername(), couchbaseContainer.getPassword())
		source = new CouchbaseWeatherSource(connector, WeatherTestData.coordinateSource)
	}

	def "The test container can establish a valid connection"() {
		when:
		def connector = new CouchbaseConnector(couchbaseContainer.connectionString, bucketDefinition.getName(), couchbaseContainer.getUsername(), couchbaseContainer.getPassword())
		then:
		connector.connectionValid
	}

	def "A CouchbaseWeatherSource can read and correctly parse a single value for a specific date and coordinate"() {
		given:
		def expectedTimeBasedValue = new TimeBasedValue(WeatherTestData.time_15h, WeatherTestData.weatherVal_coordinate_193186_15h)
		when:
		def optTimeBasedValue = source.getWeather(WeatherTestData.time_15h, WeatherTestData.coordinate_193186)
		then:
		optTimeBasedValue.present
		equalsIgnoreUUID(optTimeBasedValue.get(), expectedTimeBasedValue )
	}

	def "A CouchbaseWeatherSource can read multiple time series values for multiple coordinates"() {
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



	def "A CouchbaseWeatherSource can read all weather data in a given time interval"() {
		given:
		def timeInterval = new ClosedInterval(WeatherTestData.time_15h, WeatherTestData.time_17h)
		def timeseries_193186 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.time_15h, WeatherTestData.weatherVal_coordinate_193186_15h),
					new TimeBasedValue(WeatherTestData.time_16h, WeatherTestData.weatherVal_coordinate_193186_16h),
					new TimeBasedValue(WeatherTestData.time_17h, WeatherTestData.weatherVal_coordinate_193186_17h)] as Set<TimeBasedValue>)
		def timeseries_193187 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.time_15h, WeatherTestData.weatherVal_coordinate_193187_15h),
					new TimeBasedValue(WeatherTestData.time_16h, WeatherTestData.weatherVal_coordinate_193187_16h)] as Set<TimeBasedValue>)
		def timeseries_193188 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.time_15h, WeatherTestData.weatherVal_coordinate_193188_15h)] as Set<TimeBasedValue>)
		when:
		Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = source.getWeather(timeInterval)
		then:
		coordinateToTimeSeries.keySet().size() == 3
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.coordinate_193186).entries, timeseries_193186.entries)
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.coordinate_193187).entries, timeseries_193187.entries)
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.coordinate_193188).entries, timeseries_193188.entries)
	}
}
