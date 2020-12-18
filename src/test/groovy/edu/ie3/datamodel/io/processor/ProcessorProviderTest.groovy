/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.processor

import edu.ie3.datamodel.exceptions.ProcessorProviderException
import edu.ie3.datamodel.io.processor.result.ResultEntityProcessor
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessor
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.MeasurementUnitInput
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.RandomLoadParameters
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.SwitchInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.Transformer3WInput
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput
import edu.ie3.datamodel.models.input.system.*
import edu.ie3.datamodel.models.input.system.type.*
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.result.connector.LineResult
import edu.ie3.datamodel.models.result.connector.SwitchResult
import edu.ie3.datamodel.models.result.connector.Transformer2WResult
import edu.ie3.datamodel.models.result.connector.Transformer3WResult
import edu.ie3.datamodel.models.result.system.*
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult
import edu.ie3.datamodel.models.result.thermal.ThermalHouseResult
import edu.ie3.datamodel.models.timeseries.IntValue
import edu.ie3.datamodel.models.timeseries.TimeSeries
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.timeseries.mapping.TimeSeriesMapping
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput
import edu.ie3.datamodel.models.value.*
import edu.ie3.test.common.TimeSeriesTestData
import edu.ie3.util.TimeUtil
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.Quantity
import javax.measure.quantity.Power

class ProcessorProviderTest extends Specification implements TimeSeriesTestData {

	def "A ProcessorProvider should initialize all known EntityProcessors by default"() {
		given:
		ProcessorProvider provider = new ProcessorProvider()
		List knownEntityProcessors = [
			/* InputEntity */
			OperatorInput,
			RandomLoadParameters,
			TimeSeriesMapping.Entry,
			/* - AssetInput */
			NodeInput,
			LineInput,
			Transformer2WInput,
			Transformer3WInput,
			SwitchInput,
			MeasurementUnitInput,
			ThermalBusInput,
			/* -- SystemParticipantInput */
			ChpInput,
			BmInput,
			EvInput,
			EvcsInput,
			FixedFeedInInput,
			HpInput,
			LoadInput,
			PvInput,
			StorageInput,
			WecInput,
			/* -- ThermalUnitInput */
			ThermalHouseInput,
			CylindricalStorageInput,
			/* - GraphicInput */
			NodeGraphicInput,
			LineGraphicInput,
			/* - AssetTypeInput */
			BmTypeInput,
			ChpTypeInput,
			EvTypeInput,
			HpTypeInput,
			LineTypeInput,
			Transformer2WTypeInput,
			Transformer3WTypeInput,
			StorageTypeInput,
			WecTypeInput,
			/* ResultEntity */
			FixedFeedInResult,
			HpResult,
			BmResult,
			PvResult,
			ChpResult,
			WecResult,
			StorageResult,
			EvcsResult,
			EvResult,
			Transformer2WResult,
			Transformer3WResult,
			LineResult,
			LoadResult,
			SwitchResult,
			NodeResult,
			ThermalHouseResult,
			CylindricalStorageResult
		]
		// currently known processors

		expect:
		provider.registeredClasses.size() == knownEntityProcessors.size()
		provider.registeredClasses.sort() == knownEntityProcessors.sort()
	}

	def "A ProcessorProvider should initialize all known TimeSeriesProcessors by default"() {
		given:
		ProcessorProvider provider = new ProcessorProvider()
		Set expected = [
			new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue),
			new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, SolarIrradianceValue),
			new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, TemperatureValue),
			new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, WindValue),
			new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, WeatherValue),
			new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, HeatDemandValue),
			new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, PValue),
			new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, HeatAndPValue),
			new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, SValue),
			new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, HeatAndSValue),
			new TimeSeriesProcessorKey(LoadProfileInput, LoadProfileEntry, PValue)
		] as Set

		when:
		Set<TimeSeriesProcessorKey> actual = provider.getRegisteredTimeSeriesCombinations()

		then:
		actual == expected
	}

	def "A ProcessorProvider should return the header elements for a entity class known by one of its processors and do nothing otherwise"() {
		given:
		ProcessorProvider provider = new ProcessorProvider([
			new ResultEntityProcessor(PvResult),
			new ResultEntityProcessor(EvResult)
		], [] as Map<TimeSeriesProcessorKey, TimeSeriesProcessor<TimeSeries<TimeSeriesEntry<Value>, Value>, TimeSeriesEntry<Value>, Value>>)

		when:
		String[] headerResults = provider.getHeaderElements(PvResult)

		then:
		headerResults == [
			"uuid",
			"inputModel",
			"p",
			"q",
			"time"] as String[]

		when:
		provider.getHeaderElements(WecResult)

		then:
		ProcessorProviderException exception = thrown(ProcessorProviderException)
		exception.message == "Error during determination of header elements for entity class 'WecResult'."
	}

	def "A ProcessorProvider should return the header elements for a time series key known by one of its processors and do nothing otherwise"() {
		given:
		TimeSeriesProcessorKey availableKey = new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
		Map<TimeSeriesProcessorKey, TimeSeriesProcessor<TimeSeries<TimeSeriesEntry<Value>, Value>, TimeSeriesEntry<Value>, Value>> timeSeriesProcessors = new HashMap<>()
		timeSeriesProcessors.put(availableKey, new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue))
		ProcessorProvider provider = new ProcessorProvider([], timeSeriesProcessors)

		when:
		String[] headerResults = provider.getHeaderElements(availableKey)

		then:
		headerResults == [
			"uuid",
			"price",
			"time"] as String[]

		when:
		provider.getHeaderElements(new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, IntValue))

		then:
		ProcessorProviderException exception = thrown(ProcessorProviderException)
		exception.message == "Error during determination of header elements for time series combination 'TimeSeriesProcessorKey{timeSeriesClass=class edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries, entryClass=class edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue, valueClass=class edu.ie3.datamodel.models.timeseries.IntValue}'."
	}

	def "A ProcessorProvider should process an entity known by its underlying processors correctly and do nothing otherwise"() {
		given:
		ProcessorProvider provider = new ProcessorProvider([
			new ResultEntityProcessor(PvResult),
			new ResultEntityProcessor(EvResult)
		], [] as Map<TimeSeriesProcessorKey, TimeSeriesProcessor<TimeSeries<TimeSeriesEntry<Value>, Value>, TimeSeriesEntry<Value>, Value>>)

		Map expectedMap = ["uuid"      : "22bea5fc-2cb2-4c61-beb9-b476e0107f52",
			"inputModel": "22bea5fc-2cb2-4c61-beb9-b476e0107f52",
			"p"         : "0.01",
			"q"         : "0.01",
			"time"      : "2020-01-30T17:26:44Z[UTC]"]

		when:
		UUID uuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
		UUID inputModel = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
		Quantity<Power> p = Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN)
		Quantity<Power> q = Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
		PvResult pvResult = new PvResult(uuid, TimeUtil.withDefaults.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q)

		and:
		Optional processorResult = provider.handleEntity(pvResult)

		then:
		processorResult.present
		Map resultMap = processorResult.get()
		resultMap.size() == 5
		resultMap == expectedMap

		when:
		Optional result = provider.handleEntity(new WecResult(uuid, TimeUtil.withDefaults.toZonedDateTime("2020-01-30 17:26:44"), inputModel, p, q))

		then:
		!result.present
	}

	def "A ProcessorProvider returns an empty Optional, if none of the assigned processors is able to handle a time series"() {
		given:
		TimeSeriesProcessorKey key = new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
		Map<TimeSeriesProcessorKey, TimeSeriesProcessor> timeSeriesProcessorMap = new HashMap<>()
		timeSeriesProcessorMap.put(key, processor)
		ProcessorProvider provider = new ProcessorProvider([], timeSeriesProcessorMap)

		when:
		Optional<Set<LinkedHashMap<String, String>>> actual = provider.handleTimeSeries(individualIntTimeSeries)

		then:
		!actual.present
	}

	def "A ProcessorProvider handles a time series correctly"() {
		given:
		TimeSeriesProcessorKey key = new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
		Map<TimeSeriesProcessorKey, TimeSeriesProcessor> timeSeriesProcessorMap = new HashMap<>()
		timeSeriesProcessorMap.put(key, processor)
		ProcessorProvider provider = new ProcessorProvider([], timeSeriesProcessorMap)

		when:
		Optional<Set<LinkedHashMap<String, String>>> actual = provider.handleTimeSeries(individualEnergyPriceTimeSeries)

		then:
		actual.present
		actual.get() == individualEnergyPriceTimeSeriesProcessed
	}
}
