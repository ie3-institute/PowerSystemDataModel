/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.sql

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.connectors.SqlConnector
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy
import edu.ie3.test.helper.TestContainerHelper
import org.testcontainers.containers.Container
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

import java.sql.SQLException

@Testcontainers
class SqlDataSourceIT extends Specification implements TestContainerHelper {

  @Shared
  PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.2")

  @Shared
  SqlDataSource source

  def setupSpec() {
    // Copy sql import script into docker
    MountableFile sqlImportFile = getMountableFile("_timeseries/")
    postgreSQLContainer.copyFileToContainer(sqlImportFile, "/home/")

    // Execute import script
    Container.ExecResult res = postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/time_series_mapping.sql")
    assert res.stderr.empty

    def connector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)
    source = new SqlDataSource(connector, "public", new DatabaseNamingStrategy())
  }

  def "A SqlDataSource returns the fields of an existing table"() {
    expect:
    source.getSourceFields("time_series_mapping") == Optional.of(["asset", "timeSeries"] as Set)
  }

  def "A SqlDataSource returns an empty optional for a table that does not exist"() {
    expect:
    source.getSourceFields("not_existing") == Optional.empty()
  }

  def "A SqlDataSource throws a SourceException if the fields of a table cannot be read"() {
    given:
    def connector = Mock(SqlConnector)
    connector.getConnection() >> { throw new SQLException("Connection failed") }
    def failingSource = new SqlDataSource(connector, "public", new DatabaseNamingStrategy())

    when:
    failingSource.getSourceFields("time_series_mapping")

    then:
    thrown(SourceException)
  }
}
