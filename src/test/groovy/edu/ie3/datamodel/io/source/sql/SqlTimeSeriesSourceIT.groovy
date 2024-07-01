/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.sql

import static edu.ie3.test.common.TimeSeriesSourceTestData.*

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.connectors.SqlConnector
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation
import edu.ie3.datamodel.models.value.*
import edu.ie3.test.helper.TestContainerHelper
import edu.ie3.util.TimeUtil
import edu.ie3.util.interval.ClosedInterval
import org.testcontainers.containers.Container
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

import java.time.format.DateTimeFormatter

@Testcontainers
class SqlTimeSeriesSourceIT extends Specification implements TestContainerHelper {

  @Shared
  PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.2")

  @Shared
  SqlConnector connector

  @Shared
  SqlTimeSeriesSource pSource

  @Shared
  DatabaseNamingStrategy namingStrategy

  @Shared
  DateTimeFormatter dateTimeFormatter

  static String schemaName = "public"

  static UUID pTimeSeriesUuid = UUID.fromString("9185b8c1-86ba-4a16-8dea-5ac898e8caa5")

  def setupSpec() {
    // Copy sql import scripts into docker
    MountableFile sqlImportFile = getMountableFile("_timeseries/")
    postgreSQLContainer.copyFileToContainer(sqlImportFile, "/home/")

    // Execute import script
    Iterable<String> importFiles = Arrays.asList(
        "time_series_c.sql",
        "time_series_h.sql",
        "time_series_p.sql",
        "time_series_ph.sql",
        "time_series_pq.sql",
        "time_series_pqh.sql")
    for (String file: importFiles) {
      Container.ExecResult res = postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/" + file)
      assert res.stderr.empty
    }

    connector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)
    def metaInformation = new IndividualTimeSeriesMetaInformation(
        pTimeSeriesUuid,
        ColumnScheme.ACTIVE_POWER
        )

    namingStrategy = new DatabaseNamingStrategy()
    dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    pSource = SqlTimeSeriesSource.createSource(connector, schemaName, namingStrategy, metaInformation, dateTimeFormatter)
  }

  def "The factory method in SqlTimeSeriesSource builds a time series source for all supported column types"() {
    given:
    def metaInformation = new IndividualTimeSeriesMetaInformation(uuid, columnScheme)

    when:
    def source = SqlTimeSeriesSource.createSource(connector, schemaName, namingStrategy, metaInformation, dateTimeFormatter)
    def timeSeries = source.timeSeries

    then:
    timeSeries.entries.size() == amountOfEntries
    timeSeries.entries[0].value.class == valueClass

    where:
    uuid                                                    | columnScheme                                || amountOfEntries | valueClass
    UUID.fromString("2fcb3e53-b94a-4b96-bea4-c469e499f1a1") | ColumnScheme.ENERGY_PRICE                   || 2               | EnergyPriceValue
    UUID.fromString("c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0") | ColumnScheme.HEAT_DEMAND                    || 2               | HeatDemandValue
    UUID.fromString("9185b8c1-86ba-4a16-8dea-5ac898e8caa5") | ColumnScheme.ACTIVE_POWER                   || 2               | PValue
    UUID.fromString("76c9d846-797c-4f07-b7ec-2245f679f5c7") | ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND   || 2               | HeatAndPValue
    UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26") | ColumnScheme.APPARENT_POWER                 || 2               | SValue
    UUID.fromString("46be1e57-e4ed-4ef7-95f1-b2b321cb2047") | ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND || 2               | HeatAndSValue
  }

  def "The factory method in SqlTimeSeriesSource refuses to build time series with unsupported column type"() {
    given:
    def metaInformation = new IndividualTimeSeriesMetaInformation(
        UUID.fromString("8bc9120d-fb9b-4484-b4e3-0cdadf0feea9"),
        ColumnScheme.WEATHER
        )

    when:
    SqlTimeSeriesSource.createSource(connector, schemaName, namingStrategy, metaInformation, dateTimeFormatter)

    then:
    def e = thrown(SourceException)
    e.message == "Unsupported column scheme '" + ColumnScheme.WEATHER + "'."
  }

  def "A SqlTimeSeriesSource can read and correctly parse a single value for a specific date"() {
    when:
    def value = pSource.getValue(TIME_00MIN)

    then:
    value.present
    value.get() == P_VALUE_00MIN
  }

  def "A SqlTimeSeriesSource can read multiple time series values for a time interval"() {
    given:
    def timeInterval = new ClosedInterval(TIME_00MIN, TIME_15MIN)

    when:
    def timeSeries = pSource.getTimeSeries(timeInterval)

    then:
    timeSeries.uuid == pTimeSeriesUuid
    timeSeries.entries.size() == 2
  }

  def "A SqlTimeSeriesSource can read all value data"() {
    when:
    def timeSeries = pSource.timeSeries

    then:
    timeSeries.uuid == pTimeSeriesUuid
    timeSeries.entries.size() == 2
  }

  def "The SqlTimeSeriesSource returns the time keys after a given key correctly"() {
    given:
    def time = TimeUtil.withDefaults.toZonedDateTime("2019-12-31T23:59:59Z")

    when:
    def actual = pSource.getTimeKeysAfter(time)

    then:
    actual == [
      TimeUtil.withDefaults.toZonedDateTime("2020-01-01T00:00:00Z"),
      TimeUtil.withDefaults.toZonedDateTime("2020-01-01T00:15:00Z")
    ]
  }
}
