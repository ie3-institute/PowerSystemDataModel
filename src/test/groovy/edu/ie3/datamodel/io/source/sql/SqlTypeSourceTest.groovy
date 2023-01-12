package edu.ie3.datamodel.io.source.sql

import edu.ie3.datamodel.io.connectors.SqlConnector
import edu.ie3.datamodel.io.factory.timeseries.IconTimeBasedWeatherValueFactory
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.test.common.IconWeatherTestData
import edu.ie3.test.helper.TestContainerHelper
import edu.ie3.test.helper.WeatherSourceTestHelper
import edu.ie3.util.TimeUtil
import edu.ie3.util.interval.ClosedInterval
import org.locationtech.jts.geom.Point
import org.testcontainers.containers.Container
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
class SqlTypeSourceTest extends Specification implements TestContainerHelper {

    @Shared
    PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.2")

    @Shared
    SqlWeatherSource source

    static String schemaName = "types"

    def setupSpec() {
        // Copy sql import script into docker
        MountableFile sqlImportFile = getMountableFile("_grid/types.sql")
        postgreSQLContainer.copyFileToContainer(sqlImportFile, "/home/types.sql")
        // Execute import script
        Container.ExecResult res = postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/types.sql")
        assert res.stderr.empty

        def connector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)
        source = new SqlTypeSource(connector, schemaName, new DatabaseNamingStrategy()
    }
}
