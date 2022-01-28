/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.sql

import static edu.ie3.test.common.TimeSeriesSourceTestData.*

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.connectors.SqlConnector
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme
import edu.ie3.datamodel.models.value.*
import edu.ie3.util.interval.ClosedInterval
import org.testcontainers.containers.Container
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

@Testcontainers
class SqlTimeSeriesSourceIT extends Specification {

	@Shared
	PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.14")

	@Shared
	SqlConnector connector

	@Shared
	SqlTimeSeriesSource pSource

	static String schemaName = "public"
	static String pTableName = "its_p_9185b8c1-86ba-4a16-8dea-5ac898e8caa5"

	static UUID timeSeriesUuid = UUID.fromString("9185b8c1-86ba-4a16-8dea-5ac898e8caa5")

	def setupSpec() {
		URL url = getClass().getResource("timeseries/")
		assert url != null
		Path path = Paths.get(url.toURI())

		// Copy sql import scripts into docker
		MountableFile sqlImportFile = MountableFile.forHostPath(path)
		postgreSQLContainer.copyFileToContainer(sqlImportFile, "/home/")

		// Execute import script
		Iterable<String> importFiles = Arrays.asList(
				"its_c_2fcb3e53-b94a-4b96-bea4-c469e499f1a1.sql",
				"its_h_c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0.sql",
				"its_p_9185b8c1-86ba-4a16-8dea-5ac898e8caa5.sql",
				"its_ph_76c9d846-797c-4f07-b7ec-2245f679f5c7.sql",
				"its_pq_3fbfaa97-cff4-46d4-95ba-a95665e87c26.sql",
				"its_pqh_46be1e57-e4ed-4ef7-95f1-b2b321cb2047.sql",
				"time_series_mapping.sql")
		for (String file: importFiles) {
			Container.ExecResult res = postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/" + file)
			assert res.stderr.empty
		}

		connector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)
		def metaInformation = new SqlConnector.SqlIndividualTimeSeriesMetaInformation(
				timeSeriesUuid,
				ColumnScheme.ACTIVE_POWER,
				pTableName
				)

		pSource = SqlTimeSeriesSource.getSource(connector, schemaName, metaInformation, "yyyy-MM-dd HH:mm:ss")
	}

	def "The factory method in SqlTimeSeriesSource builds a time series source for all supported column types"() {
		given:
		def metaInformation = new SqlConnector.SqlIndividualTimeSeriesMetaInformation(uuid, columnScheme, tableName)
		def timePattern = "yyyy-MM-dd HH:mm:ss"

		when:
		def actual = SqlTimeSeriesSource.getSource(connector, schemaName, metaInformation, timePattern)

		then:
		actual.timeSeries.entries.size() == amountOfEntries
		actual.timeSeries.entries[0].value.class == valueClass

		where:
		uuid                                                    | columnScheme                                | tableName                                      || amountOfEntries | valueClass
		UUID.fromString("2fcb3e53-b94a-4b96-bea4-c469e499f1a1") | ColumnScheme.ENERGY_PRICE                   | "its_c_2fcb3e53-b94a-4b96-bea4-c469e499f1a1"   || 2               | EnergyPriceValue
		UUID.fromString("c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0") | ColumnScheme.HEAT_DEMAND                    | "its_h_c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0"   || 2               | HeatDemandValue
		UUID.fromString("9185b8c1-86ba-4a16-8dea-5ac898e8caa5") | ColumnScheme.ACTIVE_POWER                   | "its_p_9185b8c1-86ba-4a16-8dea-5ac898e8caa5"   || 2               | PValue
		UUID.fromString("76c9d846-797c-4f07-b7ec-2245f679f5c7") | ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND   | "its_ph_76c9d846-797c-4f07-b7ec-2245f679f5c7"  || 2               | HeatAndPValue
		UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26") | ColumnScheme.APPARENT_POWER                 | "its_pq_3fbfaa97-cff4-46d4-95ba-a95665e87c26"  || 2               | SValue
		UUID.fromString("46be1e57-e4ed-4ef7-95f1-b2b321cb2047") | ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND | "its_pqh_46be1e57-e4ed-4ef7-95f1-b2b321cb2047" || 2               | HeatAndSValue
	}

	def "The factory method in SqlTimeSeriesSource refuses to build time series with unsupported column type"() {
		given:
		def metaInformation = new SqlConnector.SqlIndividualTimeSeriesMetaInformation(
				UUID.fromString("8bc9120d-fb9b-4484-b4e3-0cdadf0feea9"),
				ColumnScheme.WEATHER,
				"weather"
				)
		def timePattern = "yyyy-MM-dd HH:mm:ss"

		when:
		SqlTimeSeriesSource.getSource(connector, schemaName, metaInformation, timePattern)

		then:
		def e = thrown(SourceException)
		e.message == "Unsupported column scheme '" + ColumnScheme.WEATHER + "'."
	}

	def "A SqlTimeSeriesSource can read and correctly parse a single value for a specific date"() {
		when:
		def optTimeBasedValue = pSource.getValue(TIME_00MIN)

		then:
		optTimeBasedValue.present
		optTimeBasedValue.get() == P_VALUE_00MIN
	}

	def "A SqlTimeSeriesSource can read multiple time series values for a time interval"() {
		given:
		def timeInterval = new ClosedInterval(TIME_00MIN, TIME_15MIN)

		when:
		def actualTimeSeries = pSource.getTimeSeries(timeInterval)

		then:
		actualTimeSeries.uuid == timeSeriesUuid
		actualTimeSeries.entries.size() == 2
	}

	def "A SqlTimeSeriesSource can read all value data"() {
		when:
		def actualTimeSeries = pSource.timeSeries

		then:
		actualTimeSeries.uuid == timeSeriesUuid
		actualTimeSeries.entries.size() == 2
	}
}
