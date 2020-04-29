/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.influxdb

import edu.ie3.datamodel.io.FileNamingStrategy
import edu.ie3.datamodel.io.connectors.InfluxDbConnector
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedEntryData
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedEntryFactory
import edu.ie3.datamodel.io.source.CoordinateSource
import edu.ie3.datamodel.io.source.csv.CsvCoordinateSource
import edu.ie3.datamodel.models.value.WeatherValue
import org.influxdb.dto.Query
import org.testcontainers.containers.InfluxDBContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Collectors

@Testcontainers
class InfluxDbWeatherSourceTest extends Specification {

	@Shared
	InfluxDBContainer influxDbContainer = new InfluxDBContainer()
	.withAuthEnabled(false)
	.withDatabase("test_weather")
	.withExposedPorts(8086)

	@Shared
	InfluxDbWeatherSource source

	@Shared
	CoordinateSource coordinateSource

	def setupSpec() {
		MountableFile influxWeatherImportFile = MountableFile.forClasspathResource("/testcontainersFiles/influxDb/weather.txt");
		influxDbContainer.copyFileToContainer(influxWeatherImportFile, "/home/weather.txt")
		def execResult = influxDbContainer.execInContainer("influx", "-import", "-path=/home/weather.txt", "-precision=ms")
		String coordinateFileFolder = new File(getClass().getResource('/testGridFiles/coordinates').toURI()).absolutePath
		coordinateSource = new CsvCoordinateSource(",", coordinateFileFolder, new FileNamingStrategy())

		def connector = new InfluxDbConnector(influxDbContainer.url,"test_weather", "test_scenario")
		source = new InfluxDbWeatherSource(connector, coordinateSource)
	}


	def "The test container can establish a valid connection"() {
		when:
		def pingSuccess = influxDbContainer.newInfluxDB.ping().good
		then:
		pingSuccess
	}

	def "Does some magic with it's values"() {
		when:
		def pingSuccess = influxDbContainer.newInfluxDB.ping().good
		def query = influxDbContainer.newInfluxDB.query(new Query("Select * from weather;"))
		def result = InfluxDbConnector.parseQueryResult(query)
		TimeBasedEntryFactory factory = new TimeBasedEntryFactory();
		def weather = result.get("weather").stream()
				.map({ map -> new TimeBasedEntryData(map, WeatherValue) })
				.map({data -> factory.getEntity(data)})
				.collect(Collectors.toList())
		then:
		weather.size() == 4 //because coordinates are not parsed yet
	}
}
