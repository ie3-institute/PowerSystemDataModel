/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.sink

import edu.ie3.datamodel.exceptions.SinkException
import edu.ie3.datamodel.io.CsvFileDefinition
import edu.ie3.datamodel.io.FileNamingStrategy
import edu.ie3.datamodel.io.processor.ProcessorProvider
import edu.ie3.datamodel.io.processor.input.InputEntityProcessor
import edu.ie3.datamodel.io.processor.result.ResultEntityProcessor
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessor
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
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
import edu.ie3.test.common.TimeSeriesTestData
import edu.ie3.util.TimeTools
import edu.ie3.util.io.FileIOUtils
import spock.lang.Shared
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import javax.measure.Quantity
import javax.measure.quantity.Power
import java.time.ZoneId
import java.time.ZonedDateTime

import static edu.ie3.util.quantities.PowerSystemUnits.EURO_PER_MEGAWATTHOUR
import static edu.ie3.util.quantities.PowerSystemUnits.EURO_PER_MEGAWATTHOUR

class CsvFileSinkTest extends Specification implements TimeSeriesTestData {

	@Shared
	String testBaseFolderPath = "test"

	def cleanup() {
		// delete files after each test if they exist
		if (new File(testBaseFolderPath).exists()) {
			FileIOUtils.deleteRecursively(testBaseFolderPath)
		}
	}

	def "A valid CsvFileSink called by simple constructor should not initialize files by default and consist of several default values"() {
		given:
		CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath)
		csvFileSink.dataConnector.shutdown()

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
		csvFileSink.dataConnector.shutdown()

		expect:
		new File(testBaseFolderPath).exists()
		new File(testBaseFolderPath + File.separator + "ev_res.csv").exists()
		new File(testBaseFolderPath + File.separator + "pv_res.csv").exists()
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
					new InputEntityProcessor(Transformer2WTypeInput)
				], [] as Map),
				new FileNamingStrategy(),
				false,
				",")

		UUID uuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
		UUID inputModel = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
		Quantity<Power> p = Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN)
		Quantity<Power> q = Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
		PvResult pvResult = new PvResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)
		WecResult wecResult = new WecResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)

		when:
		csvFileSink.persistAll([
			pvResult,
			wecResult,
			GridTestData.transformerCtoG
		])
		csvFileSink.dataConnector.shutdown()

		then:
		new File(testBaseFolderPath).exists()
		new File(testBaseFolderPath + File.separator + "wec_res.csv").exists()
		new File(testBaseFolderPath + File.separator + "pv_res.csv").exists()
		new File(testBaseFolderPath + File.separator + "transformer2w_type_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "node_input.csv").exists()
		new File(testBaseFolderPath + File.separator + "transformer2w_input.csv").exists()

		!new File(testBaseFolderPath + File.separator + "ev_res.csv").exists()
	}

	def "A valid CsvFileSink should throw an exception if the provided entity cannot be handled"() {
		given:
		CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath,
				new ProcessorProvider([
					new ResultEntityProcessor(PvResult)
				], [] as Map),
				new FileNamingStrategy(),
				false,
				",")

		UUID uuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
		UUID inputModel = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
		Quantity<Power> p = Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN)
		Quantity<Power> q = Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
		WecResult wecResult = new WecResult(uuid, TimeTools.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)

		when:
		csvFileSink.persist(wecResult)
		csvFileSink.dataConnector.shutdown()

		then:
		thrown(SinkException)
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
		csvFileSink.dataConnector.shutdown()

		then:
		new File(testBaseFolderPath).exists()
		new File(testBaseFolderPath + File.separator + "individual_time_series_a4bbcb77-b9d0-4b88-92be-b9a14a3e332b.csv").exists()
	}
}
