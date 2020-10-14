/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.sink

import edu.ie3.datamodel.io.csv.FileNamingStrategy
import edu.ie3.datamodel.io.processor.ProcessorProvider
import edu.ie3.datamodel.io.processor.input.InputEntityProcessor
import edu.ie3.datamodel.io.processor.result.ResultEntityProcessor
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessor
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey
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
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput
import edu.ie3.datamodel.models.result.system.EvResult
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
import edu.ie3.test.common.ThermalUnitInputTestData
import edu.ie3.test.common.TimeSeriesTestData
import edu.ie3.util.TimeUtil
import edu.ie3.util.io.FileIOUtils
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.Quantity
import javax.measure.quantity.Power

import static edu.ie3.util.quantities.PowerSystemUnits.DEGREE_GEOM
import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLTAMPERE
import static tech.units.indriya.unit.Units.PERCENT

class CsvFileSinkTest extends Specification implements TimeSeriesTestData {

	@Shared
	String testBaseFolderPath = "test"

	// called automatically by spock (see http://spockframework.org/spock/docs/1.0/spock_primer.html - Fixture Methods)
	def cleanup() {
		// delete files after each test if they exist
		if (new File(testBaseFolderPath).exists()) {
			FileIOUtils.deleteRecursively(testBaseFolderPath)
		}
	}

	def "A valid CsvFileSink called by simple constructor should not initialize files by default and consist of several default values"() {
		given:
		CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath)
		csvFileSink.shutdown()

		expect:
		!new File(testBaseFolderPath).exists()
		csvFileSink.csvSep == ","
	}

	def "A valid CsvFileSink with 'initFiles' enabled should create files as expected"() {
		given:
		CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath,
				new ProcessorProvider([
					new ResultEntityProcessor(PvResult),
					new ResultEntityProcessor(EvResult)
				], [] as Map),
				new FileNamingStrategy(),
				true,
				",")
		csvFileSink.shutdown()

		expect:
		new File(testBaseFolderPath).exists()
		new File(testBaseFolderPath + File.separator + "ev_res.csv").exists()
		new File(testBaseFolderPath + File.separator + "pv_res.csv").exists()
	}

	def "A valid CsvFileSink is able to convert an entity data map correctly to RFC 4180 compliant strings"() {
		given:
		def csvFileSink = new CsvFileSink(testBaseFolderPath)
		def input = [
			"hello, whats up?": "nothing",
			"okay": "that's fine"
		]

		when:
		def actual = csvFileSink.csvEntityFieldData(input)

		then:
		actual == [
			"\"hello, whats up?\"": "nothing",
			"okay": "that's fine"
		]

		cleanup:
		csvFileSink.shutdown()
	}

	def "A valid CsvFileSink throws an IllegalStateException, if processing entity data map to RFC 4180 compliant strings generates duplicated keys"() {
		given:
		def csvFileSink = new CsvFileSink(testBaseFolderPath)
		def input = [
			"what is \"this\"?": "nothing",
			"\"what is \"this\"?\"": "something"
		]

		when:
		def bla = csvFileSink.csvEntityFieldData(input)
		println(bla)

		then:
		def exception = thrown(IllegalStateException)
		exception.message == "Converting entity data to RFC 4180 compliant strings has lead to duplicate keys. Initial input:\n\twhat is \"this\"? = nothing,\n\t\"what is \"this\"?\" = something"

		cleanup:
		csvFileSink.shutdown()
	}

	def "A valid CsvFileSink without 'initFiles' should only persist provided elements correctly but not all files"() {
		given:
		CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath,
				new ProcessorProvider([
					new ResultEntityProcessor(PvResult),
					new ResultEntityProcessor(WecResult),
					new ResultEntityProcessor(EvResult),
					new InputEntityProcessor(Transformer2WInput),
					new InputEntityProcessor(NodeInput),
					new InputEntityProcessor(Transformer2WTypeInput),
					new InputEntityProcessor(LineGraphicInput),
					new InputEntityProcessor(NodeGraphicInput),
					new InputEntityProcessor(CylindricalStorageInput),
					new InputEntityProcessor(ThermalHouseInput),
					new InputEntityProcessor(OperatorInput),
					new InputEntityProcessor(LineInput),
					new InputEntityProcessor(ThermalBusInput),
					new InputEntityProcessor(LineTypeInput)
				], [] as Map),
				new FileNamingStrategy(),
				false,
				",")

		UUID uuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
		UUID inputModel = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
		Quantity<Power> p = Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN)
		Quantity<Power> q = Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
		PvResult pvResult = new PvResult(uuid, TimeUtil.withDefaults.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)
		WecResult wecResult = new WecResult(uuid, TimeUtil.withDefaults.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)

		when:
		csvFileSink.persistAll([
			pvResult,
			wecResult,
			GridTestData.transformerCtoG,
			GridTestData.lineGraphicCtoD,
			GridTestData.nodeGraphicC,
			ThermalUnitInputTestData.cylindricStorageInput,
			ThermalUnitInputTestData.thermalHouseInput
		])
		csvFileSink.shutdown()

		then:
		new File(testBaseFolderPath).exists()
		new File(testBaseFolderPath + File.separator + "wec_res.csv").exists()
		new File(testBaseFolderPath + File.separator + "pv_res.csv").exists()
		new File(testBaseFolderPath + File.separator + "transformer2w_type_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "node_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "transformer2w_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "operator_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "cylindrical_storage_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "line_graphic_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "line_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "operator_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "node_graphic_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "thermal_bus_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "thermal_house_input.csv").exists()

		!new File(testBaseFolderPath + File.separator + "ev_res.csv").exists()
	}

	def "A valid CsvFileSink should persist a time series correctly"() {
		given:
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue> timeSeriesProcessor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
		TimeSeriesProcessorKey timeSeriesProcessorKey = new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
		HashMap<TimeSeriesProcessorKey, TimeSeriesProcessor> timeSeriesProcessorMap = new HashMap<>()
		timeSeriesProcessorMap.put(timeSeriesProcessorKey, timeSeriesProcessor)

		IndividualTimeSeries<EnergyPriceValue> individualTimeSeries = individualEnergyPriceTimeSeries

		CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath,
				new ProcessorProvider([], timeSeriesProcessorMap),
				new FileNamingStrategy(),
				false,
				",")

		when:
		csvFileSink.persist(individualTimeSeries)
		csvFileSink.shutdown()

		then:
		new File(testBaseFolderPath).exists()
		new File(testBaseFolderPath + File.separator + "individual_time_series_a4bbcb77-b9d0-4b88-92be-b9a14a3e332b.csv").exists()
	}

	def "A valid CsvFileSink persists a bunch of time series correctly"() {
		given:
		CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath)

		when:
		csvFileSink.persistAll(allTimeSeries)
		csvFileSink.shutdown()

		then:
		new File(testBaseFolderPath).exists()
		new File(testBaseFolderPath + File.separator + "individual_time_series_3c0ebc06-9bd7-44ea-a347-0c52d3dec854.csv").exists()
		new File(testBaseFolderPath + File.separator + "individual_time_series_3dbfb74f-1fba-4150-95e7-24d22bfca4ac.csv").exists()
		new File(testBaseFolderPath + File.separator + "individual_time_series_4fcbdfcd-4ff0-46dd-b0df-f3af7ae3ed98.csv").exists()
		new File(testBaseFolderPath + File.separator + "individual_time_series_7d085fc9-be29-4218-b768-00f885be066b.csv").exists()
		new File(testBaseFolderPath + File.separator + "individual_time_series_56c20b88-c001-4225-8dac-cd13a75c6b48.csv").exists()
		new File(testBaseFolderPath + File.separator + "individual_time_series_83b577cc-06b1-47a1-bfff-ad648a00784b.csv").exists()
		new File(testBaseFolderPath + File.separator + "individual_time_series_90da7b7d-2148-4510-a730-31f01a554ace.csv").exists()
		new File(testBaseFolderPath + File.separator + "individual_time_series_a4bbcb77-b9d0-4b88-92be-b9a14a3e332b.csv").exists()
		new File(testBaseFolderPath + File.separator + "load_profile_time_series_g2_b56853fe-b800-4c18-b324-db1878b22a28.csv").exists()
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
		new File(testBaseFolderPath).exists()
		new File(testBaseFolderPath + File.separator + "pv_input.csv").exists()
		!(new File(testBaseFolderPath + File.separator + "node_input.csv").exists())

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
				new HashMap<TimeSeriesProcessorKey, TimeSeriesProcessor<TimeSeries<TimeSeriesEntry<Value>, Value>, TimeSeriesEntry<Value>, Value>>()),
				new FileNamingStrategy(),
				false,
				",")

		when:
		csvFileSink.persist(individualEnergyPriceTimeSeries)

		then:
		!(new File(testBaseFolderPath + File.separator + "individual_time_series_a4bbcb77-b9d0-4b88-92be-b9a14a3e332b.csv").exists())

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
				false,
				",")

		when:
		csvFileSink.persistJointGrid(SampleJointGrid.grid())

		then:
		new File(testBaseFolderPath + File.separator + "line_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "line_type_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "load_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "node_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "operator_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "pv_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "storage_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "storage_type_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "transformer2w_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "transformer2w_type_input.csv").exists()

		cleanup:
		csvFileSink.shutdown()
	}
}
