/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.sql


import static edu.ie3.test.common.TimeSeriesSourceTestData.G3_VALUE_00MIN
import static edu.ie3.test.common.TimeSeriesSourceTestData.TIME_00MIN

import edu.ie3.datamodel.io.connectors.SqlConnector
import edu.ie3.datamodel.io.factory.timeseries.BdewLoadProfileFactory
import edu.ie3.datamodel.io.factory.timeseries.LoadProfileFactory
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileTimeSeriesMetaInformation
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.datamodel.models.value.load.BdewLoadValues
import edu.ie3.test.helper.TestContainerHelper
import edu.ie3.util.TimeUtil
import org.testcontainers.containers.Container
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
class SqlLoadProfileSourceIT extends Specification implements TestContainerHelper {

  @Shared
  PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.2")

  @Shared
  SqlConnector connector

  @Shared
  SqlLoadProfileSource<BdewStandardLoadProfile, BdewLoadValues> loadSource

  @Shared
  DatabaseNamingStrategy namingStrategy

  static String schemaName = "public"

  static UUID timeSeriesUuid = UUID.fromString("9b880468-309c-43c1-a3f4-26dd26266216")

  def setupSpec() {
    // Copy sql import scripts into docker
    MountableFile sqlImportFile = getMountableFile("_timeseries/")
    postgreSQLContainer.copyFileToContainer(sqlImportFile, "/home/")

    // Execute import script
    Iterable<String> importFiles = Arrays.asList("time_series_load_profiles.sql")
    for (String file: importFiles) {
      Container.ExecResult res = postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/" + file)
      assert res.stderr.empty
    }

    connector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)
    def metaInformation = new LoadProfileTimeSeriesMetaInformation(timeSeriesUuid, "g3")

    namingStrategy = new DatabaseNamingStrategy()

    loadSource = new SqlLoadProfileSource<>(connector, schemaName, namingStrategy, metaInformation, BdewLoadValues, new BdewLoadProfileFactory())
  }

  def "A SqlTimeSeriesSource can read and correctly parse a single value for a specific date"() {
    when:
    def value = loadSource.getValue(TIME_00MIN)

    then:
    value.present
    value.get().p.get() == G3_VALUE_00MIN.p.get()
  }

  def "A SqlTimeSeriesSource can read all value data"() {
    when:
    def timeSeries = loadSource.timeSeries

    then:
    timeSeries.uuid == timeSeriesUuid
    timeSeries.entries.size() == 3
  }

  def "The SqlTimeSeriesSource returns the time keys after a given key correctly"() {
    given:
    def time = TimeUtil.withDefaults.toZonedDateTime("2020-01-01T00:00:00Z")

    when:
    def actual = loadSource.getTimeKeysAfter(time)

    then:
    actual == [
      TimeUtil.withDefaults.toZonedDateTime("2020-01-01T00:15:00Z")
    ]
  }
}
