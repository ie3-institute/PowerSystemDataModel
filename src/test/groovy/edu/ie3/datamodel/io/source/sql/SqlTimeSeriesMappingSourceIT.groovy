/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.sql

import edu.ie3.datamodel.io.connectors.SqlConnector
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy
import edu.ie3.test.helper.TestContainerHelper
import org.testcontainers.containers.Container
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
class SqlTimeSeriesMappingSourceIT extends Specification implements TestContainerHelper {

  @Shared
  PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.2")

  @Shared
  SqlConnector connector

  @Shared
  SqlTimeSeriesMappingSource source

  def setupSpec() {
    // Copy sql import script into docker
    MountableFile sqlImportFile = getMountableFile("_timeseries/")
    postgreSQLContainer.copyFileToContainer(sqlImportFile, "/home/")

    // Execute import script
    Iterable<String> importFiles = Arrays.asList("time_series_mapping.sql")
    for (String file: importFiles) {
      Container.ExecResult res = postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/" + file)
      assert res.stderr.empty
    }

    connector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)
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
}
