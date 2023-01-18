package edu.ie3.datamodel.io.source.sql

import edu.ie3.datamodel.io.connectors.SqlConnector
import edu.ie3.datamodel.io.factory.timeseries.IconTimeBasedWeatherValueFactory
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.test.common.GridTestData
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
    SqlConnector connector

    @Shared
    SqlTypeSource source

    @Shared
    DatabaseNamingStrategy namingStrategy

    static String schemaName = "types"

    def setupSpec() {
        // Copy sql import script into docker

        MountableFile sqlImportFile = getMountableFile("_types/types.sql")
        postgreSQLContainer.copyFileToContainer(sqlImportFile, "/home/types.sql")
        // Execute import script
        Container.ExecResult res = postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/types.sql")
        assert res.stderr.empty

        def connector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)
        source = new SqlTypeSource(connector, schemaName, new DatabaseNamingStrategy())


    }

    def "A SqlTypeSource should read and handle valid 2W Transformer type file as expected"() {
        given:
        def typeSource = new SqlTypeSource(connector, schemaName, new DatabaseNamingStrategy())

        expect:
        def transformer2WTypes = typeSource.transformer2WTypes
        def transformerToBeFound = transformer2WTypes.find {trafoType ->
            trafoType.uuid == GridTestData.transformerTypeBtoD.uuid
        }
        transformerToBeFound.id == GridTestData.transformerTypeBtoD.id
        transformerToBeFound.rSc == GridTestData.transformerTypeBtoD.rSc
        transformerToBeFound.xSc == GridTestData.transformerTypeBtoD.xSc
        transformerToBeFound.sRated == GridTestData.transformerTypeBtoD.sRated
        transformerToBeFound.vRatedA == GridTestData.transformerTypeBtoD.vRatedA
        transformerToBeFound.vRatedB == GridTestData.transformerTypeBtoD.vRatedB
        transformerToBeFound.gM == GridTestData.transformerTypeBtoD.gM
        transformerToBeFound.bM == GridTestData.transformerTypeBtoD.bM
        transformerToBeFound.dV == GridTestData.transformerTypeBtoD.dV
        transformerToBeFound.dPhi == GridTestData.transformerTypeBtoD.dPhi
        transformerToBeFound.tapSide == GridTestData.transformerTypeBtoD.tapSide
        transformerToBeFound.tapNeutr == GridTestData.transformerTypeBtoD.tapNeutr
        transformerToBeFound.tapMin == GridTestData.transformerTypeBtoD.tapMin
        transformerToBeFound.tapMax == GridTestData.transformerTypeBtoD.tapMax
    }



}
