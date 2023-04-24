/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.connectors

import edu.ie3.test.helper.TestContainerHelper
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

import java.sql.SQLException
import java.time.format.DateTimeFormatter

@Testcontainers
class SqlConnectorIT extends Specification implements TestContainerHelper {
  @Shared
  PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.2")

  @Shared
  SqlConnector connector

  def setupSpec() {
    // Copy sql import script into docker
    MountableFile sqlImportFile = getMountableFile("_sql/connectorTest.sql")
    postgreSQLContainer.copyFileToContainer(sqlImportFile, "/home/connectorTest.sql")
    // Execute import script
    postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/connectorTest.sql")

    connector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)
  }

  def cleanupSpec() {
    connector.shutdown()
  }

  def "A SQL connector is instantiated correctly"() {
    expect:
    connector.with {
      assert it.jdbcUrl == postgreSQLContainer.jdbcUrl
      assert it.connectionProps.getProperty("user") == postgreSQLContainer.username
      assert it.connectionProps.getProperty("password") == postgreSQLContainer.password
      /* SQL connection should be null, but we cannot test it here, as it would fire the default getter, which
       * initializes a connection, if it is not yet initialized. */
    }
  }
  def "A SQL connector refuses to connect, if one of the credentials is wrong"() {
    given:
    def testConnector = new SqlConnector("jdbc://somewhere/else", postgreSQLContainer.username, postgreSQLContainer.password)

    when:
    testConnector.getConnection()

    then:
    def thrown = thrown(SQLException)
    thrown.message == "Could not establish connection: "
  }

  def "A SQL connector establishes a connection even, if it shall reuse a connection and none is apparent"() {
    when:
    def actual = connector.getConnection(false)

    then:
    Objects.nonNull(actual)
    !actual.closed
  }

  def "A SQL connector does not reuse the existing connection, if asked to do so."() {
    given:
    /* Create a new connection, to be safe */
    def connection = connector.getConnection(false)

    when:
    /* Attempt to reuse the connection */
    def actual = connector.getConnection(false)

    then:
    actual != connection
  }

  def "A SQL connector reuses the existing connection, if asked to do so."() {
    given:
    /* Create a new connection, to be safe */
    def connection = connector.getConnection(false)

    when:
    /* Attempt to reuse the connection */
    def actual = connector.getConnection(true)

    then:
    actual == connection
  }

  def "A SQL connector is able to extract one field to value map from result set"() {
    given:
    def preparedStatement = connector.getConnection(false).prepareStatement("SELECT * FROM public.test;")
    def resultSet = preparedStatement.executeQuery()
    resultSet.next()

    def expected = [
      "a": "hello",
      "b": "1",
      "id": "1"
    ]

    when:
    def actual = connector.extractFieldMap(resultSet)

    then:
    actual.size() == expected.size()
    actual.get("id") == expected.get("id")
    actual.get("a") == expected.get("a")
    actual.get("b") == expected.get("b")

    cleanup:
    resultSet.close()
  }

  def "A SQL connector is able to extract all field to value maps from result set"() {
    given:
    def preparedStatement = connector.getConnection(false).prepareStatement("SELECT * FROM public.test;")
    def resultSet = preparedStatement.executeQuery()

    when:
    def actual = connector.extractFieldMaps(resultSet)

    then:
    actual.size() == 2

    cleanup:
    resultSet.close()
  }

  def "A SQL connector shuts down correctly, if no connection was opened"() {
    given:
    def testConnector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)

    when:
    testConnector.shutdown()

    then:
    noExceptionThrown()
  }

  def "A SQL connector shuts down correctly, if a connection was opened"() {
    given:
    def testConnector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)
    testConnector.connection

    when:
    testConnector.shutdown()

    then:
    noExceptionThrown()
  }
}
