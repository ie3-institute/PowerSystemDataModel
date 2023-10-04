package edu.ie3.datamodel.io.sink

import edu.ie3.datamodel.io.DatabaseIdentifier
import edu.ie3.datamodel.io.SqlUtils
import edu.ie3.datamodel.io.connectors.SqlConnector
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation
import edu.ie3.datamodel.io.processor.ProcessorProvider
import edu.ie3.datamodel.io.processor.input.InputEntityProcessor
import edu.ie3.datamodel.io.processor.result.ResultEntityProcessor
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessor
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey
import edu.ie3.datamodel.io.source.csv.CsvTimeSeriesSource
import edu.ie3.datamodel.io.source.sql.SqlDataSource
import edu.ie3.datamodel.io.source.sql.SqlTimeSeriesSource
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput
import edu.ie3.datamodel.models.input.system.EmInput
import edu.ie3.datamodel.models.input.system.EvcsInput
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.StorageInput
import edu.ie3.datamodel.models.input.system.WecInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.system.type.StorageTypeInput
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput
import edu.ie3.datamodel.models.result.system.EmResult
import edu.ie3.datamodel.models.result.system.EvResult
import edu.ie3.datamodel.models.result.system.EvcsResult
import edu.ie3.datamodel.models.result.system.FlexOptionsResult
import edu.ie3.datamodel.models.result.system.PvResult
import edu.ie3.datamodel.models.result.system.WecResult
import edu.ie3.datamodel.models.timeseries.TimeSeries
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput
import edu.ie3.datamodel.models.value.EnergyPriceValue
import edu.ie3.datamodel.models.value.Value
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.SampleJointGrid
import edu.ie3.test.common.ThermalUnitInputTestData
import edu.ie3.test.common.TimeSeriesTestData
import edu.ie3.test.helper.TestContainerHelper
import edu.ie3.util.TimeUtil
import edu.ie3.util.io.FileIOUtils
import org.jetbrains.annotations.NotNull
import org.testcontainers.containers.Container
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification
import org.testcontainers.spock.Testcontainers
import tech.units.indriya.quantity.Quantities

import javax.measure.Quantity
import javax.measure.quantity.Power
import java.nio.file.Path

import edu.ie3.test.common.SystemParticipantTestData

import java.sql.SQLException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import static edu.ie3.datamodel.models.StandardUnits.ENERGY_PRICE
import static edu.ie3.util.quantities.PowerSystemUnits.DEGREE_GEOM
import static edu.ie3.util.quantities.PowerSystemUnits.DEGREE_GEOM
import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLTAMPERE
import static tech.units.indriya.unit.Units.PERCENT

@Testcontainers
class SqlSinkTest extends Specification implements TestContainerHelper, TimeSeriesTestData {

    @Shared
    PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.2")

    @Shared
    SqlConnector connector

    @Shared
    SqlDataSource sqlSource

    @Shared
    DatabaseNamingStrategy namingStrategy

    @Shared
    DatabaseIdentifier identifier

    static String schemaName = "public"

    def setupSpec() {
        // Copy sql import scripts into docker
        MountableFile sqlImportFile = getMountableFile("_sql/")
        postgreSQLContainer.copyFileToContainer(sqlImportFile, "/home/")
        postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/" + "setup.sql")

        connector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)

        namingStrategy = new DatabaseNamingStrategy()

        identifier = new DatabaseIdentifier("vn_simona", UUID.fromString("8e6bd444-4580-11ee-be56-0242ac120002"))

        sqlSource = new SqlDataSource(connector, schemaName, namingStrategy)
    }

    def setup() {
        // Execute import script
        Iterable<String> importFiles = Arrays.asList(
                "types.sql",
                "result_entities.sql",
                "input_entities.sql",
                "time_series.sql",
                "load_profile.sql"
        )
        for (String file: importFiles) {
            Container.ExecResult res = postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/" + file)
            assert res.stderr.empty
        }
    }

    def cleanup() {
        postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/" + "cleanup.sql")
    }

    def "SQL sink can persist provided elements correctly"() {
        given:
            TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue> timeSeriesProcessor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
            TimeSeriesProcessorKey timeSeriesProcessorKey = new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
            HashMap<TimeSeriesProcessorKey, TimeSeriesProcessor> timeSeriesProcessorMap = new HashMap<>()
            timeSeriesProcessorMap.put(timeSeriesProcessorKey, timeSeriesProcessor)
            IndividualTimeSeries<EnergyPriceValue> individualTimeSeries = individualEnergyPriceTimeSeries

            SqlSink sink = new SqlSink(schemaName,
                    new ProcessorProvider([
                            new ResultEntityProcessor(PvResult),
                            new ResultEntityProcessor(WecResult),
                            new ResultEntityProcessor(EvResult),
                            new ResultEntityProcessor(EvcsResult),
                            new ResultEntityProcessor(EmResult),
                            new ResultEntityProcessor(FlexOptionsResult),
                            new InputEntityProcessor(Transformer2WInput),
                            new InputEntityProcessor(NodeInput),
                            new InputEntityProcessor(EvcsInput),
                            new InputEntityProcessor(Transformer2WTypeInput),
                            new InputEntityProcessor(LineGraphicInput),
                            new InputEntityProcessor(NodeGraphicInput),
                            new InputEntityProcessor(CylindricalStorageInput),
                            new InputEntityProcessor(ThermalHouseInput),
                            new InputEntityProcessor(OperatorInput),
                            new InputEntityProcessor(LineInput),
                            new InputEntityProcessor(ThermalBusInput),
                            new InputEntityProcessor(LineTypeInput),
                            new InputEntityProcessor(LoadInput),
                            new InputEntityProcessor(EmInput)
                    ], timeSeriesProcessorMap),
                    namingStrategy,
                    connector)
            UUID uuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
            UUID inputModel = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
            Quantity<Power> p = Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN)
            Quantity<Power> q = Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
            PvResult pvResult = new PvResult(uuid, TimeUtil.withDefaults.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)
            WecResult wecResult = new WecResult(uuid, TimeUtil.withDefaults.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)
            EvcsResult evcsResult = new EvcsResult(uuid, TimeUtil.withDefaults.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)
            EmResult emResult = new EmResult(uuid, TimeUtil.withDefaults.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)

            Quantity<Power> pRef = Quantities.getQuantity(5.1, StandardUnits.ACTIVE_POWER_RESULT)
            Quantity<Power> pMin = Quantities.getQuantity(-6, StandardUnits.ACTIVE_POWER_RESULT)
            Quantity<Power> pMax = Quantities.getQuantity(6, StandardUnits.ACTIVE_POWER_RESULT)
            FlexOptionsResult flexOptionsResult = new FlexOptionsResult(uuid, TimeUtil.withDefaults.toZonedDateTime("2020-01-30 17:26:44"), inputModel, pRef, pMin, pMax)

            when:
                sink.persistAll([
                        pvResult,
                        wecResult,
                        evcsResult,
                        emResult,
                        flexOptionsResult,
                        GridTestData.transformerCtoG,
                        GridTestData.lineGraphicCtoD,
                        GridTestData.nodeGraphicC,
                        ThermalUnitInputTestData.cylindricStorageInput,
                        ThermalUnitInputTestData.thermalHouseInput,
                        SystemParticipantTestData.evcsInput,
                        SystemParticipantTestData.loadInput,
                        SystemParticipantTestData.emInput,
                        individualTimeSeries
                ], identifier)

            then:
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "pv_res", ps -> {}).count() == 1
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "wec_res", ps -> {}).count() == 1
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "evcs_res", ps -> {}).count() == 1
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "em_res", ps -> {}).count() == 1
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "flex_options_res", ps -> {}).count() == 1
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "transformer_2_w_type_input", ps -> {}).count() == 1
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "node_input", ps -> {}).count() == 4
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "transformer_2_w_input", ps -> {}).count() == 1
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "operator_input", ps -> {}).count() == 2
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "cylindrical_storage_input", ps -> {}).count() == 1
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "line_graphic_input", ps -> {}).count() == 1
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "line_input", ps -> {}).count() == 1
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "node_graphic_input", ps -> {}).count() == 1
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "thermal_bus_input", ps -> {}).count() == 1
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "thermal_house_input", ps -> {}).count() == 1
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "load_input", ps -> {}).count() == 1
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "em_input", ps -> {}).count() == 1
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "ev_res", ps -> {}).count() == 0
                sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "time_series_c", ps -> {}).count() == 3

        cleanup:
            sink.shutdown()
    }


    def "A SqlSink can persist a time series."() {
        given:
            TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue> timeSeriesProcessor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
            TimeSeriesProcessorKey timeSeriesProcessorKey = new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
            HashMap<TimeSeriesProcessorKey, TimeSeriesProcessor> timeSeriesProcessorMap = new HashMap<>()
            timeSeriesProcessorMap.put(timeSeriesProcessorKey, timeSeriesProcessor)
            IndividualTimeSeries<EnergyPriceValue> individualTimeSeries = individualEnergyPriceTimeSeries
            SqlSink sink = new SqlSink(schemaName, namingStrategy, connector)

        when:
            sink.persist(individualTimeSeries, identifier)

        then:
            sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "time_series_c", ps -> {}).count() == 3

        cleanup:
            sink.shutdown()
    }

    def "A valid SqlSink persists a bunch of time series correctly"() {
        given:
            SqlSink sink = new SqlSink(schemaName, namingStrategy, connector)
            SqlDataSource source = new SqlDataSource(connector, schemaName, namingStrategy)

        when:
            sink.persistAll(allTimeSeries, identifier)

        then:
            source.executeQuery("SELECT * FROM " + schemaName + "." + "time_series_c", ps -> {}).count() == 3
            source.executeQuery("SELECT * FROM " + schemaName + "." + "time_series_p", ps -> {}).count() == 3
            source.executeQuery("SELECT * FROM " + schemaName + "." + "time_series_pq", ps -> {}).count() == 3
            source.executeQuery("SELECT * FROM " + schemaName + "." + "time_series_ph", ps -> {}).count() == 3
            source.executeQuery("SELECT * FROM " + schemaName + "." + "time_series_pqh", ps -> {}).count() == 3
            source.executeQuery("SELECT * FROM " + schemaName + "." + "time_series_weather", ps -> {}).count() == 3

        cleanup:
            sink.shutdown()
    }

    def "A valid SqlSink throws an exception if an entity has null for a not null attribute."() {
        given:
            def sink = new SqlSink(schemaName, namingStrategy, connector)
            def nestedInput = new PvInput(
                UUID.fromString("d56f15b7-8293-4b98-b5bd-58f6273ce229"),
                "test_pvInput",
                OperatorInput.NO_OPERATOR_ASSIGNED,
                OperationTime.notLimited(),
                Mock(NodeInput),
                new CosPhiFixed("cosPhiFixed:{(0.0,0.95)}"),
                0.2,
                Quantities.getQuantity(-8.926613807678223, DEGREE_GEOM),
                Quantities.getQuantity(95d, PERCENT),
                Quantities.getQuantity(41.01871871948242, DEGREE_GEOM),
                0.8999999761581421,
                1,
                false,
                Quantities.getQuantity(25d, KILOVOLTAMPERE),
                0.95
        )

        when:
            sink.persistIgnoreNested(nestedInput, identifier)

        then:
            def exception = thrown(SQLException)
            exception.message.contains("ERROR: invalid input syntax for type uuid: \"null\"\n")

        cleanup:
            sink.shutdown()
    }


    def "A valid SqlSink refuses to persist an entity, if no processor can be found for a specific input"() {
        given:
        def sink = new SqlSink(
                schemaName,
                new ProcessorProvider(
                        ProcessorProvider.allEntityProcessors(),
                        new HashMap<TimeSeriesProcessorKey, TimeSeriesProcessor<TimeSeries<TimeSeriesEntry<Value>, Value>, TimeSeriesEntry<Value>, Value>>()),
                namingStrategy,
                connector)

        when:
            sink.persist(individualEnergyPriceTimeSeries, identifier)

        then:
            sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "time_series_c", ps -> {}).count() == 0

        cleanup:
            sink.shutdown()
    }

    def "A valid SqlSink throws an exception if a nested entity hasn't all of its nested entity."() {
        given:
            def sink = new SqlSink(schemaName, namingStrategy, connector)

        when:
            sink.persistIgnoreNested(SystemParticipantTestData.loadInput, identifier)

        then:
            def exception = thrown(SQLException)
            exception.message == "ERROR: insert or update on table \"load_input\" violates foreign key constraint \"load_input_node_fkey\"\n" +
                "  Detail: Key (node)=(4ca90220-74c2-4369-9afa-a18bf068840d) is not present in table \"node_input\"."

        cleanup:
            sink.shutdown()
    }


    def "A valid SqlSink should persist a valid joint grid container correctly"() {
        given:
            def sink = new SqlSink(schemaName, namingStrategy, connector)

        when:
            sink.persistJointGrid(SampleJointGrid.grid())

        then:
            sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "line_input", ps -> {}).count() == 6
            sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "line_type_input", ps -> {}).count() == 2
            sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "load_input", ps -> {}).count() == 2
            sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "node_input", ps -> {}).count() == 7
            sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "operator_input", ps -> {}).count() == 1
            sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "pv_input", ps -> {}).count() == 1
            sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "storage_input", ps -> {}).count() == 1
            sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "storage_type_input", ps -> {}).count() == 1
            sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "transformer_2_w_type_input", ps -> {}).count() == 2
            sqlSource.executeQuery("SELECT * FROM " + schemaName + "." + "transformer_2_w_input", ps -> {}).count() == 2

        cleanup:
            sink.shutdown()
    }

    /*
    def "Ausgabe"() {
        given:
            System.out.println(SqlUtils.getDataTypes(StorageTypeInput.class))
            //System.out.println(SqlUtils.getDataTypes(StorageInput.class))
        //System.out.println(SqlUtils.getDataTypes(NodeInput.class))

        System.out.println(SqlUtils.getDataTypes(PvResult.class))
        System.out.println(SqlUtils.getDataTypes(WecResult.class))
        System.out.println(SqlUtils.getDataTypes(EvResult.class))
        System.out.println(SqlUtils.getDataTypes(EvcsResult.class))
        System.out.println(SqlUtils.getDataTypes(EmResult.class))
        System.out.println(SqlUtils.getDataTypes(FlexOptionsResult.class))


        System.out.println(SqlUtils.getDataTypes(Transformer2WInput.class))
        System.out.println(SqlUtils.getDataTypes(NodeInput.class))
        System.out.println(SqlUtils.getDataTypes(EvcsInput.class))
        System.out.println(SqlUtils.getDataTypes(LineGraphicInput.class))
        System.out.println(SqlUtils.getDataTypes(NodeGraphicInput.class))
        System.out.println(SqlUtils.getDataTypes(CylindricalStorageInput.class))
        System.out.println(SqlUtils.getDataTypes(ThermalHouseInput.class))
        System.out.println(SqlUtils.getDataTypes(OperatorInput.class))
        System.out.println(SqlUtils.getDataTypes(LineInput.class))
        System.out.println(SqlUtils.getDataTypes(ThermalBusInput.class))
        System.out.println(SqlUtils.getDataTypes(LineTypeInput.class))
        System.out.println(SqlUtils.getDataTypes(LoadInput.class))
        System.out.println(SqlUtils.getDataTypes(EmInput.class))

        System.out.println(SqlUtils.getDataTypes(Transformer2WTypeInput.class))


        when:
        def nummer = 1

        then:
        nummer == 1
    }
    */
}
