/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.sink

import static edu.ie3.util.quantities.PowerSystemUnits.DEGREE_GEOM
import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLTAMPERE
import static tech.units.indriya.unit.Units.PERCENT

import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.processor.ProcessorProvider
import edu.ie3.datamodel.io.processor.input.InputEntityProcessor
import edu.ie3.datamodel.io.processor.result.ResultEntityProcessor
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessor
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.EmInput
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput
import edu.ie3.datamodel.models.input.system.EvcsInput
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.DomesticHotWaterStorageInput
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
import edu.ie3.datamodel.models.value.EnergyPriceValue
import edu.ie3.datamodel.models.value.Value
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.SampleJointGrid
import edu.ie3.test.common.SystemParticipantTestData
import edu.ie3.test.common.ThermalUnitInputTestData
import edu.ie3.test.common.TimeSeriesTestData
import edu.ie3.util.TimeUtil
import edu.ie3.util.io.FileIOUtils
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.nio.file.Path
import javax.measure.Quantity
import javax.measure.quantity.Power

class CsvFileSinkTest extends Specification implements TimeSeriesTestData {

  @Shared
  Path testBaseFolderPath = Path.of("test")

  // called automatically by spock (see http://spockframework.org/spock/docs/1.0/spock_primer.html - Fixture Methods)
  def cleanup() {
    // delete files after each test if they exist
    if (testBaseFolderPath.toFile().exists()) {
      FileIOUtils.deleteRecursively(testBaseFolderPath)
    }
  }


  def "A valid CsvFileSink is able to convert an entity data map correctly to RFC 4180 compliant strings"() {
    given:
    def csvFileSink = new CsvFileSink(testBaseFolderPath)
    def input = [
      "hello, whats up?": "nothing",
      "okay"            : "that's fine"
    ]

    when:
    def actual = csvFileSink.csvEntityFieldData(input)

    then:
    actual == [
      "\"hello, whats up?\"": "nothing",
      "okay"                : "that's fine"
    ]

    cleanup:
    csvFileSink.shutdown()
  }

  def "A valid CsvFileSink throws an IllegalStateException, if processing entity data map to RFC 4180 compliant strings generates duplicated keys"() {
    given:
    def csvFileSink = new CsvFileSink(testBaseFolderPath)
    def input = [
      "what is \"this\"?"    : "nothing",
      "\"what is \"this\"?\"": "something"
    ]

    when:
    csvFileSink.csvEntityFieldData(input)

    then:
    def exception = thrown(IllegalStateException)
    exception.message == "Converting entity data to RFC 4180 compliant strings has lead to duplicate keys. Initial input:\n\twhat is \"this\"? = nothing,\n\t\"what is \"this\"?\" = something"

    cleanup:
    csvFileSink.shutdown()
  }

  def "A valid CsvFileSink should persist provided elements correctly"() {
    given:
    CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath,
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
          new InputEntityProcessor(DomesticHotWaterStorageInput),
          new InputEntityProcessor(ThermalHouseInput),
          new InputEntityProcessor(OperatorInput),
          new InputEntityProcessor(LineInput),
          new InputEntityProcessor(ThermalBusInput),
          new InputEntityProcessor(LineTypeInput),
          new InputEntityProcessor(LoadInput),
          new InputEntityProcessor(EmInput)
        ], [] as Map),
        new FileNamingStrategy(),
        ",")

    UUID inputModel = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
    Quantity<Power> p = Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN)
    Quantity<Power> q = Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
    PvResult pvResult = new PvResult(TimeUtil.withDefaults.toZonedDateTime("2020-01-30T17:26:44Z"), inputModel, p, q)
    WecResult wecResult = new WecResult(TimeUtil.withDefaults.toZonedDateTime("2020-01-30T17:26:44Z"), inputModel, p, q)
    EvcsResult evcsResult = new EvcsResult(TimeUtil.withDefaults.toZonedDateTime("2020-01-30T17:26:44Z"), inputModel, p, q)
    EmResult emResult = new EmResult(TimeUtil.withDefaults.toZonedDateTime("2020-01-30T17:26:44Z"), inputModel, p, q)

    Quantity<Power> pRef = Quantities.getQuantity(5.1, StandardUnits.ACTIVE_POWER_RESULT)
    Quantity<Power> pMin = Quantities.getQuantity(-6, StandardUnits.ACTIVE_POWER_RESULT)
    Quantity<Power> pMax = Quantities.getQuantity(6, StandardUnits.ACTIVE_POWER_RESULT)
    FlexOptionsResult flexOptionsResult = new FlexOptionsResult(TimeUtil.withDefaults.toZonedDateTime("2020-01-30T17:26:44Z"), inputModel, pRef, pMin, pMax)

    when:
    csvFileSink.persistAll([
      pvResult,
      wecResult,
      evcsResult,
      emResult,
      flexOptionsResult,
      GridTestData.transformerCtoG,
      GridTestData.lineGraphicCtoD,
      GridTestData.nodeGraphicC,
      ThermalUnitInputTestData.cylindricalStorageInput,
      ThermalUnitInputTestData.domesticHotWaterStorageInput,
      ThermalUnitInputTestData.thermalHouseInput,
      SystemParticipantTestData.evcsInput,
      SystemParticipantTestData.loadInput,
      SystemParticipantTestData.emInput
    ])
    csvFileSink.shutdown()

    then:
    testBaseFolderPath.toFile().exists()
    testBaseFolderPath.resolve("wec_res.csv").toFile().exists()
    testBaseFolderPath.resolve("pv_res.csv").toFile().exists()
    testBaseFolderPath.resolve("evcs_res.csv").toFile().exists()
    testBaseFolderPath.resolve("em_res.csv").toFile().exists()
    testBaseFolderPath.resolve("flex_options_res.csv").toFile().exists()
    testBaseFolderPath.resolve("transformer_2_w_type_input.csv").toFile().exists()
    testBaseFolderPath.resolve("node_input.csv").toFile().exists()
    testBaseFolderPath.resolve("transformer_2_w_input.csv").toFile().exists()
    testBaseFolderPath.resolve("operator_input.csv").toFile().exists()
    testBaseFolderPath.resolve("cylindrical_storage_input.csv").toFile().exists()
    testBaseFolderPath.resolve("domestic_hot_water_storage_input.csv").toFile().exists()
    testBaseFolderPath.resolve("line_graphic_input.csv").toFile().exists()
    testBaseFolderPath.resolve("line_input.csv").toFile().exists()
    testBaseFolderPath.resolve("operator_input.csv").toFile().exists()
    testBaseFolderPath.resolve("node_graphic_input.csv").toFile().exists()
    testBaseFolderPath.resolve("thermal_bus_input.csv").toFile().exists()
    testBaseFolderPath.resolve("thermal_house_input.csv").toFile().exists()
    testBaseFolderPath.resolve("load_input.csv").toFile().exists()
    testBaseFolderPath.resolve("em_input.csv").toFile().exists()

    !testBaseFolderPath.resolve("ev_res.csv").toFile().exists()
  }

  def "A valid CsvFileSink should persist a time series correctly"() {
    given:
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue, EnergyPriceValue> timeSeriesProcessor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
    TimeSeriesProcessorKey timeSeriesProcessorKey = new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
    HashMap<TimeSeriesProcessorKey, TimeSeriesProcessor> timeSeriesProcessorMap = new HashMap<>()
    timeSeriesProcessorMap.put(timeSeriesProcessorKey, timeSeriesProcessor)

    IndividualTimeSeries<EnergyPriceValue> individualTimeSeries = individualEnergyPriceTimeSeries

    CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath,
        new ProcessorProvider([], timeSeriesProcessorMap),
        new FileNamingStrategy(),
        ",")

    when:
    csvFileSink.persist(individualTimeSeries)
    csvFileSink.shutdown()

    then:
    testBaseFolderPath.toFile().exists()
    testBaseFolderPath.resolve("its_c_a4bbcb77-b9d0-4b88-92be-b9a14a3e332b.csv").toFile().exists()
  }

  def "A valid CsvFileSink persists a bunch of time series correctly"() {
    given:
    CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath)

    when:
    csvFileSink.persistAll(allTimeSeries)
    csvFileSink.shutdown()

    then:
    testBaseFolderPath.toFile().exists()
    testBaseFolderPath.resolve("its_h_3c0ebc06-9bd7-44ea-a347-0c52d3dec854.csv").toFile().exists()
    testBaseFolderPath.resolve("its_p_b3d93b08-4985-41a6-b063-00f934a10b28.csv").toFile().exists()
    testBaseFolderPath.resolve("its_pq_7d085fc9-be29-4218-b768-00f885be066b.csv").toFile().exists()
    testBaseFolderPath.resolve("its_ph_56c20b88-c001-4225-8dac-cd13a75c6b48.csv").toFile().exists()
    testBaseFolderPath.resolve("its_pqh_83b577cc-06b1-47a1-bfff-ad648a00784b.csv").toFile().exists()
    testBaseFolderPath.resolve("its_c_a4bbcb77-b9d0-4b88-92be-b9a14a3e332b.csv").toFile().exists()
    testBaseFolderPath.resolve("lpts_g2.csv").toFile().exists()
    testBaseFolderPath.resolve("its_weather_4fcbdfcd-4ff0-46dd-b0df-f3af7ae3ed98.csv").toFile().exists()
  }

  def "A valid CsvFileSink is able to persist an InputEntity with multiple nested entities."() {
    given:
    def csvFileSink = new CsvFileSink(testBaseFolderPath)
    def nestedInput = new PvInput(
        UUID.fromString("d56f15b7-8293-4b98-b5bd-58f6273ce229"),
        "test_pvInput",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        GridTestData.nodeA,
        new CosPhiFixed("cosPhiFixed:{(0.0,0.95)}"),
        SystemParticipantTestData.emInput,
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
    csvFileSink.persist(nestedInput)

    then:
    testBaseFolderPath.toFile().exists()
    testBaseFolderPath.resolve("pv_input.csv").toFile().exists()
    testBaseFolderPath.resolve("node_input.csv").toFile().exists()
    testBaseFolderPath.resolve("em_input.csv").toFile().exists()

    cleanup:
    csvFileSink.shutdown()
  }

  def "A valid CsvFileSink is able to persist an InputEntity without persisting the nested elements"() {
    given:
    def csvFileSink = new CsvFileSink(testBaseFolderPath)
    def nestedInput = new PvInput(
        UUID.fromString("d56f15b7-8293-4b98-b5bd-58f6273ce229"),
        "test_pvInput",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        Mock(NodeInput),
        new CosPhiFixed("cosPhiFixed:{(0.0,0.95)}"),
        Mock(EmInput),
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
    csvFileSink.persistIgnoreNested(nestedInput)

    then:
    testBaseFolderPath.toFile().exists()
    testBaseFolderPath.resolve("pv_input.csv").toFile().exists()
    !testBaseFolderPath.resolve("node_input.csv").toFile().exists()

    cleanup:
    csvFileSink.shutdown()
  }

  def "A valid CsvFileSink refuses to persist an entity, if no processor can be found for a specific input"() {
    given:
    /* A csv file sink, that is NOT able to handle time series */
    def csvFileSink = new CsvFileSink(
        testBaseFolderPath,
        new ProcessorProvider(
        ProcessorProvider.allEntityProcessors(),
        new HashMap<TimeSeriesProcessorKey, TimeSeriesProcessor<TimeSeries<TimeSeriesEntry<Value>, Value, Value>, TimeSeriesEntry<Value>, Value, Value>>()),
        new FileNamingStrategy(),
        ",")

    when:
    csvFileSink.persist(individualEnergyPriceTimeSeries)

    then:
    !testBaseFolderPath.resolve("its_a4bbcb77-b9d0-4b88-92be-b9a14a3e332b.csv").toFile().exists()

    cleanup:
    csvFileSink.shutdown()
  }

  def "A valid CsvFileSink should persist a valid joint grid container correctly"() {
    given:
    /* A csv file sink, that is NOT able to handle time series */
    def csvFileSink = new CsvFileSink(
        testBaseFolderPath,
        new ProcessorProvider(),
        new FileNamingStrategy(),
        ",")

    when:
    csvFileSink.persistJointGrid(SampleJointGrid.grid())

    then:
    testBaseFolderPath.resolve("line_input.csv").toFile().exists()
    testBaseFolderPath.resolve("line_type_input.csv").toFile().exists()
    testBaseFolderPath.resolve("load_input.csv").toFile().exists()
    testBaseFolderPath.resolve("node_input.csv").toFile().exists()
    testBaseFolderPath.resolve("operator_input.csv").toFile().exists()
    testBaseFolderPath.resolve("pv_input.csv").toFile().exists()
    testBaseFolderPath.resolve("storage_input.csv").toFile().exists()
    testBaseFolderPath.resolve("storage_type_input.csv").toFile().exists()
    testBaseFolderPath.resolve("transformer_2_w_input.csv").toFile().exists()
    testBaseFolderPath.resolve("transformer_2_w_type_input.csv").toFile().exists()

    cleanup:
    csvFileSink.shutdown()
  }
}
