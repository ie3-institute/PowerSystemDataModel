/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.sql

import static edu.ie3.datamodel.models.StandardUnits.TEMPERATURE
import static edu.ie3.util.quantities.PowerSystemUnits.KELVIN_METRE_PER_WATT
import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATTHOUR_PER_KELVIN_TIMES_CUBICMETRE
import static tech.units.indriya.unit.Units.METRE

import edu.ie3.datamodel.io.connectors.SqlConnector
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy
import edu.ie3.datamodel.io.source.SoilDataSource
import edu.ie3.test.helper.TestContainerHelper
import org.testcontainers.containers.Container
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
class SqlSoilDataSourceIT extends Specification implements TestContainerHelper {
  @Shared
  PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.2")

  @Shared
  SqlConnector connector

  @Shared
  SoilDataSource source

  def setupSpec() {
    MountableFile sqlImportFile = getMountableFile("_soil/")
    postgreSQLContainer.copyFileToContainer(sqlImportFile, "/home/")

    Container.ExecResult result =
        postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/soil_source.sql")
    assert result.stderr.empty

    connector =
        new SqlConnector(
        postgreSQLContainer.jdbcUrl,
        postgreSQLContainer.username,
        postgreSQLContainer.password)
    source =
        new SoilDataSource(
        new SqlDataSource(connector, "public", new DatabaseNamingStrategy(new EntityPersistenceNamingStrategy())))
  }

  def cleanupSpec() {
    connector?.shutdown()
  }

  def "reads soil types and layers from SQL"() {
    when:
    def types = source.getSoilTypes()
    def layers = source.getSoilLayers()

    then:
    types.size() == 1
    types.values().first().id == "loam"
    types.values().first().thermalResistivityWet.to(KELVIN_METRE_PER_WATT).value.doubleValue() == 0.3d
    types.values().first().thermalResistivityDry.to(KELVIN_METRE_PER_WATT).value.doubleValue() == 0.4d
    types.values().first().specificHeatCapacity.to(KILOWATTHOUR_PER_KELVIN_TIMES_CUBICMETRE).value.doubleValue() == 0.0015d
    types.values().first().criticalTemperatureDifference.to(TEMPERATURE).value.doubleValue() == 15.0d

    layers.size() == 1
    layers.values().first().geometry.area == 1.0d
    layers.values().first().soilType == types.values().first().uuid
    layers.values().first().zFrom.to(METRE).value.doubleValue() == 0.0d
    layers.values().first().zTo.to(METRE).value.doubleValue() == -2.0d
    layers.values().first().thickness().to(METRE).value.doubleValue() == 2.0d
  }
}
