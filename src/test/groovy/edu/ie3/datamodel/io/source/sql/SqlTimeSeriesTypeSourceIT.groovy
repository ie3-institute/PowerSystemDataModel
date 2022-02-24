/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.sql

import edu.ie3.datamodel.io.connectors.SqlConnector
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation
import org.testcontainers.containers.Container
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

@Testcontainers
class SqlTimeSeriesTypeSourceIT extends Specification {

	@Shared
	PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.2")

	@Shared
	SqlConnector connector

	@Shared
	SqlTimeSeriesTypeSource source

	def setupSpec() {
		URL url = getClass().getResource("timeseries/")
		assert url != null
		Path path = Paths.get(url.toURI())

		// Copy sql import script into docker
		MountableFile sqlImportFile = MountableFile.forHostPath(path)
		postgreSQLContainer.copyFileToContainer(sqlImportFile, "/home/")

		// Execute import script
		Iterable<String> importFiles = Arrays.asList(
				"time_series_c.sql",
				"time_series_h.sql",
				"time_series_p.sql",
				"time_series_ph.sql",
				"time_series_pq.sql",
				"time_series_pqh.sql",)
		for (String file: importFiles) {
			Container.ExecResult res = postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/" + file)
			assert res.stderr.empty
		}

		connector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)
		source = new SqlTimeSeriesTypeSource(connector, "public", new DatabaseNamingStrategy())
	}

	def "The sql time series type source returns a correct mapping of time series"() {
		when:
		def expectedTimeSeries= Set.of(
				new IndividualTimeSeriesMetaInformation(UUID.fromString("2fcb3e53-b94a-4b96-bea4-c469e499f1a1"), ColumnScheme.ENERGY_PRICE),
				new IndividualTimeSeriesMetaInformation(UUID.fromString("76c9d846-797c-4f07-b7ec-2245f679f5c7"), ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND),
				new IndividualTimeSeriesMetaInformation(UUID.fromString("c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0"), ColumnScheme.HEAT_DEMAND),
				new IndividualTimeSeriesMetaInformation(UUID.fromString("9185b8c1-86ba-4a16-8dea-5ac898e8caa5"), ColumnScheme.ACTIVE_POWER),
				new IndividualTimeSeriesMetaInformation(UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26"), ColumnScheme.APPARENT_POWER),
				new IndividualTimeSeriesMetaInformation(UUID.fromString("46be1e57-e4ed-4ef7-95f1-b2b321cb2047"), ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND),
				new IndividualTimeSeriesMetaInformation(UUID.fromString("b669e4bf-a351-4067-860d-d5f224b62247"), ColumnScheme.ACTIVE_POWER)
		)
		def result = source.getTimeSeriesMetaInformation()

		then:
		result.size() == 7

		result.every {
			it.key == it.value.uuid &&
					expectedTimeSeries.contains(it.value)
		}
	}
}
