/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.processor.timeseries

import edu.ie3.datamodel.exceptions.EntityProcessorException
import edu.ie3.datamodel.io.processor.Processor
import edu.ie3.datamodel.models.timeseries.IntValue
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput
import edu.ie3.datamodel.models.value.*
import edu.ie3.test.common.TimeSeriesTestData
import spock.lang.Specification

import java.lang.reflect.Method

class TimeSeriesProcessorTest extends Specification implements TimeSeriesTestData {
	def "A TimeSeriesProcessor is instantiated correctly"() {
		when:
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
		Map expectedSourceMapping = [
			"uuid": FieldSourceToMethod.FieldSource.ENTRY,
			"price": FieldSourceToMethod.FieldSource.VALUE,
			"time": FieldSourceToMethod.FieldSource.ENTRY]

		then:
		processor.with {
			/* Check for attributes in higher classes also they are ignored by the class itself. */
			assert processor.registeredClass == IndividualTimeSeries

			assert processor.registeredKey == new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
			assert processor.fieldToSource.size() == expectedSourceMapping.size()
			processor.fieldToSource.each { key, value ->
				assert expectedSourceMapping.containsKey(key)
				assert expectedSourceMapping.get(key) == value.source
			}
			/* Also test the logic of TimeSeriesProcessor#buildFieldToSource, because it is invoked during instantiation */
			assert processor.headerElements == [
				"uuid",
				"price",
				"time"] as String[]
		}
	}

	def "A TimeSeriesProcessor throws an Exception on instantiation, if the time series combination is not supported"() {
		when:
		new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, IntValue)

		then:
		EntityProcessorException thrown = thrown(EntityProcessorException)
		thrown.message.startsWith("Cannot register time series combination 'TimeSeriesProcessorKey{timeSeriesClass=class edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries, entryClass=class edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue, valueClass=class edu.ie3.datamodel.models.timeseries.IntValue}' with entity processor 'TimeSeriesProcessor'. Eligible combinations:")
	}

	def "A TimeSeriesProcessor throws an Exception, when the simple handle method is called"() {
		given:
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)

		when:
		processor.handleEntity(individualEnergyPriceTimeSeries)

		then:
		UnsupportedOperationException thrown = thrown(UnsupportedOperationException)
		thrown.message ==  "Don't invoke this simple method, but TimeSeriesProcessor#handleTimeSeries(TimeSeries)."
	}

	def "A TimeSeriesProcessor correctly extracts the field name to getter map"() {
		given:
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)

		when:
		Map<String, Method> actual = processor.extractFieldToMethod(source)

		then:
		actual.size() == expectedFieldNames.size()
		actual.each { entry -> expectedFieldNames.contains(entry.key) }

		where:
		source || expectedFieldNames
		FieldSourceToMethod.FieldSource.ENTRY || ["uuid", "time"]
		FieldSourceToMethod.FieldSource.VALUE || ["price"]
	}

	def "A TimeSeriesProcessor handles an entry correctly"() {
		given:
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
		Map<String, String> expected = Processor.putUuidFirst([
			"uuid" : "9e4dba1b-f3bb-4e40-bd7e-2de7e81b7704",
			"price": "5.0",
			"time" : "2020-04-02 10:00:00"
		]
		)

		when:
		Map<String, String> actual = processor.handleEntry(individualEnergyPriceTimeSeries, timeBasedEntry)

		then:
		actual.size() == expected.size()
		actual.each { key, value -> expected.get(key) == value }
	}

	def "A TimeSeriesProcessors handles a complete time series with EnergyPriceValues correctly"() {
		given:
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)

		when:
		Set<Map<String, String>> actual = processor.handleTimeSeries(individualEnergyPriceTimeSeries)

		then:
		actual == individualEnergyPriceTimeSeriesProcessed
	}

	def "A TimeSeriesProcessors handles a complete time series with TemperatureValues correctly"() {
		given:
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, TemperatureValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, TemperatureValue)

		when:
		Set<Map<String, String>> actual = processor.handleTimeSeries(individualTemperatureTimeSeries)

		then:
		actual == individualTemperatureTimeSeriesProcessed
	}

	def "A TimeSeriesProcessors handles a complete time series with WindValues correctly"() {
		given:
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, WindValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, WindValue)

		when:
		Set<Map<String, String>> actual = processor.handleTimeSeries(individualWindTimeSeries)

		then:
		actual == individualWindTimeSeriesProcessed
	}

	def "A TimeSeriesProcessors handles a complete time series with IrradiationValues correctly"() {
		given:
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, IrradiationValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, IrradiationValue)

		when:
		Set<Map<String, String>> actual = processor.handleTimeSeries(individualIrradiationTimeSeries)

		then:
		actual == individualIrradiationTimeSeriesProcessed
	}

	def "A TimeSeriesProcessors handles a complete time series with WeatherValues correctly"() {
		given:
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, WeatherValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, WeatherValue)

		when:
		Set<Map<String, String>> actual = processor.handleTimeSeries(individualWeatherTimeSeries)

		then:
		actual == individualWeatherTimeSeriesProcessed
	}

	def "A TimeSeriesProcessors handles a complete time series with HeatDemandValues correctly"() {
		given:
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, HeatDemandValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, HeatDemandValue)

		when:
		Set<Map<String, String>> actual = processor.handleTimeSeries(individualHeatDemandTimeSeries)

		then:
		actual == individualHeatDemandTimeSeriesProcessed
	}

	def "A TimeSeriesProcessors handles a complete time series with PValues correctly"() {
		given:
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, PValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, PValue)

		when:
		Set<Map<String, String>> actual = processor.handleTimeSeries(individualPTimeSeries)

		then:
		actual == individualPTimeSeriesProcessed
	}

	def "A TimeSeriesProcessors handles a complete time series with HeatAndPValues correctly"() {
		given:
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, HeatAndPValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, HeatAndPValue)

		when:
		Set<Map<String, String>> actual = processor.handleTimeSeries(individualHeatAndPTimeSeries)

		then:
		actual == individualHeatAndPTimeSeriesProcessed
	}

	def "A TimeSeriesProcessors handles a complete time series with SValue correctly"() {
		given:
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, SValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, SValue)

		when:
		Set<Map<String, String>> actual = processor.handleTimeSeries(individualSTimeSeries)

		then:
		actual == individualSTimeSeriesProcessed
	}

	def "A TimeSeriesProcessors handles a complete time series with HeatAndSValue correctly"() {
		given:
		TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, HeatAndSValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, HeatAndSValue)

		when:
		Set<Map<String, String>> actual = processor.handleTimeSeries(individualHeatAndSTimeSeries)

		then:
		actual == individualHeatAndSTimeSeriesProcessed
	}

	def "A TimeSeriesProcessors handles a complete LoadProfileInput correctly"() {
		given:
		TimeSeriesProcessor<LoadProfileInput, LoadProfileEntry, PValue> processor = new TimeSeriesProcessor<>(LoadProfileInput, LoadProfileEntry, PValue)

		when:
		Set<Map<String, String>> actual = processor.handleTimeSeries(loadProfileInput)

		then:
		actual == loadProfileInputProcessed
	}
}
