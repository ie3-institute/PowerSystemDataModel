/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.sql

import edu.ie3.datamodel.io.connectors.SqlConnector
import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme
import edu.ie3.datamodel.io.csv.timeseries.IndividualTimeSeriesMetaInformation
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy
import org.testcontainers.containers.Container
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

@Testcontainers
class SqlTimeSeriesMappingSourceIT extends Specification {

	@Shared
	PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.14")

	@Shared
	SqlTimeSeriesMappingSource source

	def setupSpec() {
		URL url = getClass().getResource("timeseries/")
		assert url != null
		Path path = Paths.get(url.toURI())

		// Copy sql import script into docker
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
		for(String file: importFiles) {
			Container.ExecResult res = postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/" + file)
			assert res.stderr.isEmpty()
		}

		def connector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)
		source = new SqlTimeSeriesMappingSource(connector, "public", new EntityPersistenceNamingStrategy())
	}

	def "The sql time series mapping source returns empty optional on not covered model"() {
		given:
		def modelUuid = UUID.fromString("60b9a3da-e56c-40ff-ace7-8060cea84baf")

		when:
		def actual = source.getTimeSeriesUuid(modelUuid)

		then:
		!actual.present
	}

	def "The sql time series mapping source is able to return the correct time series uuid"() {
		given:
		def modelUuid = UUID.fromString("c7ebcc6c-55fc-479b-aa6b-6fa82ccac6b8")
		def expectedUuid = UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26")

		when:
		def actual = source.getTimeSeriesUuid(modelUuid)

		then:
		actual.present
		actual.get() == expectedUuid
	}

	def "A sql time series mapping source returns empty optional on meta information for non existing time series"() {
		given:
		def timeSeriesUuid = UUID.fromString("f5eb3be5-98db-40de-85b0-243507636cd5")

		when:
		def actual = source.getTimeSeriesMetaInformation(timeSeriesUuid)

		then:
		!actual.present
	}

	def "A sql time series mapping source returns correct meta information for an existing time series"() {
		given:
		def timeSeriesUuid = UUID.fromString("9185b8c1-86ba-4a16-8dea-5ac898e8caa5")
		def expected = new IndividualTimeSeriesMetaInformation(
				timeSeriesUuid,
				ColumnScheme.ACTIVE_POWER)

		when:
		def actual = source.getTimeSeriesMetaInformation(timeSeriesUuid)

		then:
		actual.present
		actual.get() == expected
	}
}
