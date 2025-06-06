/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.processor.timeseries

import edu.ie3.datamodel.exceptions.EntityProcessorException
import edu.ie3.datamodel.io.processor.Processor
import edu.ie3.datamodel.models.timeseries.IntValue
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.timeseries.repetitive.BdewLoadProfileTimeSeries
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry
import edu.ie3.datamodel.models.value.*
import edu.ie3.datamodel.models.value.load.BdewLoadValues
import edu.ie3.test.common.TimeSeriesTestData
import spock.lang.Specification

import java.lang.reflect.Method

class TimeSeriesProcessorTest extends Specification implements TimeSeriesTestData {
  def "A TimeSeriesProcessor is instantiated correctly"() {
    when:
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue, EnergyPriceValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
    Map expectedSourceMapping = [
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
        assert expectedSourceMapping.get(key) == value.source()
      }
      /* Also test the logic of TimeSeriesProcessor#buildFieldToSource, because it is invoked during instantiation */
      assert processor.headerElements == [
        "price",
        "time"
      ] as String[]
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
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue, EnergyPriceValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)

    when:
    processor.handleEntity(individualEnergyPriceTimeSeries)

    then:
    UnsupportedOperationException thrown = thrown(UnsupportedOperationException)
    thrown.message ==  "Don't invoke this simple method, but TimeSeriesProcessor#handleTimeSeries(TimeSeries)."
  }

  def "A TimeSeriesProcessor correctly extracts the field name to getter map"() {
    given:
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue, EnergyPriceValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)

    when:
    Map<String, Method> actual = processor.extractFieldToMethod(source)

    then:
    actual.size() == expectedFieldNames.size()
    actual.each { entry -> expectedFieldNames.contains(entry.key) }

    where:
    source || expectedFieldNames
    FieldSourceToMethod.FieldSource.ENTRY || ["time"]
    FieldSourceToMethod.FieldSource.VALUE || ["price"]
  }

  def "A TimeSeriesProcessor handles an entry correctly"() {
    given:
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue, EnergyPriceValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
    Map<String, String> expected = Processor.putUuidFirst([
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
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue, EnergyPriceValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)

    when:
    Set<Map<String, String>> actual = processor.handleTimeSeries(individualEnergyPriceTimeSeries)

    then:
    actual == individualEnergyPriceTimeSeriesProcessed
  }

  def "A TimeSeriesProcessors handles a complete time series with TemperatureValues correctly"() {
    given:
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, TemperatureValue, TemperatureValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, TemperatureValue)

    when:
    Set<Map<String, String>> actual = processor.handleTimeSeries(individualTemperatureTimeSeries)

    then:
    actual == individualTemperatureTimeSeriesProcessed
  }

  def "A TimeSeriesProcessors handles a complete time series with WindValues correctly"() {
    given:
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, WindValue, WindValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, WindValue)

    when:
    Set<Map<String, String>> actual = processor.handleTimeSeries(individualWindTimeSeries)

    then:
    actual == individualWindTimeSeriesProcessed
  }

  def "A TimeSeriesProcessors handles a complete time series with IrradianceValues correctly"() {
    given:
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, SolarIrradianceValue, SolarIrradianceValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, SolarIrradianceValue)

    when:
    Set<Map<String, String>> actual = processor.handleTimeSeries(individualIrradianceTimeSeries)

    then:
    actual == individualIrradianceTimeSeriesProcessed
  }

  def "A TimeSeriesProcessors handles a complete time series with WeatherValues correctly"() {
    given:
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, WeatherValue, WeatherValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, WeatherValue)

    when:
    Set<Map<String, String>> actual = processor.handleTimeSeries(individualWeatherTimeSeries)

    then:
    actual == individualWeatherTimeSeriesProcessed
  }

  def "A TimeSeriesProcessors handles a complete time series with HeatDemandValues correctly"() {
    given:
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, HeatDemandValue, HeatDemandValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, HeatDemandValue)

    when:
    Set<Map<String, String>> actual = processor.handleTimeSeries(individualHeatDemandTimeSeries)

    then:
    actual == individualHeatDemandTimeSeriesProcessed
  }

  def "A TimeSeriesProcessors handles a complete time series with PValues correctly"() {
    given:
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, PValue, PValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, PValue)

    when:
    Set<Map<String, String>> actual = processor.handleTimeSeries(individualPTimeSeries)

    then:
    actual == individualPTimeSeriesProcessed
  }

  def "A TimeSeriesProcessors handles a complete time series with HeatAndPValues correctly"() {
    given:
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, HeatAndPValue, HeatAndPValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, HeatAndPValue)

    when:
    Set<Map<String, String>> actual = processor.handleTimeSeries(individualHeatAndPTimeSeries)

    then:
    actual == individualHeatAndPTimeSeriesProcessed
  }

  def "A TimeSeriesProcessors handles a complete time series with SValue correctly"() {
    given:
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, SValue, SValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, SValue)

    when:
    Set<Map<String, String>> actual = processor.handleTimeSeries(individualSTimeSeries)

    then:
    actual == individualSTimeSeriesProcessed
  }

  def "A TimeSeriesProcessors handles a complete time series with HeatAndSValue correctly"() {
    given:
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, HeatAndSValue, HeatAndSValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, HeatAndSValue)

    when:
    Set<Map<String, String>> actual = processor.handleTimeSeries(individualHeatAndSTimeSeries)

    then:
    actual == individualHeatAndSTimeSeriesProcessed
  }

  def "A TimeSeriesProcessors handles a complete LoadProfileTimeSeries correctly"() {
    given:
    TimeSeriesProcessor<BdewLoadProfileTimeSeries, LoadProfileEntry, BdewLoadValues, PValue> processor = new TimeSeriesProcessor<>(BdewLoadProfileTimeSeries, LoadProfileEntry, BdewLoadValues)

    when:
    Set<Map<String, String>> actual = processor.handleTimeSeries(loadProfileTimeSeries)

    then:
    actual == loadProfileTimeSeriesProcessed
  }
}
