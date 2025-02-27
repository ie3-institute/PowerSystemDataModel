/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.processor

import edu.ie3.datamodel.exceptions.ProcessorProviderException
import edu.ie3.datamodel.io.processor.result.ResultEntityProcessor
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessor
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.*
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
import edu.ie3.datamodel.models.input.thermal.DomesticHotWaterStorageInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput
import edu.ie3.datamodel.models.result.CongestionResult
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.result.connector.LineResult
import edu.ie3.datamodel.models.result.connector.SwitchResult
import edu.ie3.datamodel.models.result.connector.Transformer2WResult
import edu.ie3.datamodel.models.result.connector.Transformer3WResult
import edu.ie3.datamodel.models.result.system.*
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult
import edu.ie3.datamodel.models.result.thermal.DomesticHotWaterStorageResult
import edu.ie3.datamodel.models.result.thermal.ThermalHouseResult
import edu.ie3.datamodel.models.timeseries.IntValue
import edu.ie3.datamodel.models.timeseries.TimeSeries
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.timeseries.repetitive.BdewLoadProfileTimeSeries
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry
import edu.ie3.datamodel.models.timeseries.repetitive.RandomLoadProfileTimeSeries
import edu.ie3.datamodel.models.value.*
import edu.ie3.datamodel.models.value.load.BdewLoadValues
import edu.ie3.datamodel.models.value.load.RandomLoadValues
import edu.ie3.datamodel.utils.Try
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
      TimeSeriesMappingSource.MappingEntry,
      IdCoordinateInput,
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
      EmInput,
      /* -- ThermalUnitInput */
      ThermalHouseInput,
      CylindricalStorageInput,
      DomesticHotWaterStorageInput,
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
      EmResult,
      FlexOptionsResult,
      Transformer2WResult,
      Transformer3WResult,
      LineResult,
      LoadResult,
      SwitchResult,
      NodeResult,
      CongestionResult,
      ThermalHouseResult,
      CylindricalStorageResult,
      DomesticHotWaterStorageResult
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
      new TimeSeriesProcessorKey(BdewLoadProfileTimeSeries, LoadProfileEntry, BdewLoadValues),
      new TimeSeriesProcessorKey(RandomLoadProfileTimeSeries, LoadProfileEntry, RandomLoadValues)
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
    ], [] as Map<TimeSeriesProcessorKey, TimeSeriesProcessor<TimeSeries<TimeSeriesEntry<Value>, Value, Value>, TimeSeriesEntry<Value>, Value, Value>>)

    when:
    String[] headerResults = provider.getHeaderElements(PvResult)

    then:
    headerResults == [
      "inputModel",
      "p",
      "q",
      "time"
    ] as String[]

    when:
    provider.getHeaderElements(WecResult)

    then:
    ProcessorProviderException exception = thrown(ProcessorProviderException)
    exception.message == "Error during determination of header elements for entity class 'WecResult'."
  }

  def "A ProcessorProvider should return the header elements for a time series key known by one of its processors and do nothing otherwise"() {
    given:
    TimeSeriesProcessorKey availableKey = new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
    Map<TimeSeriesProcessorKey, TimeSeriesProcessor<TimeSeries<TimeSeriesEntry<Value>, Value, Value>, TimeSeriesEntry<Value>, Value, Value>> timeSeriesProcessors = new HashMap<>()
    timeSeriesProcessors.put(availableKey, new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue))
    ProcessorProvider provider = new ProcessorProvider([], timeSeriesProcessors)

    when:
    String[] headerResults = provider.getHeaderElements(availableKey)

    then:
    headerResults == [
      "price",
      "time"
    ] as String[]

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
    ], [] as Map<TimeSeriesProcessorKey, TimeSeriesProcessor<TimeSeries<TimeSeriesEntry<Value>, Value, Value>, TimeSeriesEntry<Value>, Value, Value>>)

    Map expectedMap = [
      "inputModel": "22bea5fc-2cb2-4c61-beb9-b476e0107f52",
      "p"         : "0.01",
      "q"         : "0.01",
      "time"      : "2020-01-30T17:26:44Z"]

    when:
    UUID inputModel = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
    Quantity<Power> p = Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN)
    Quantity<Power> q = Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
    PvResult pvResult = new PvResult(TimeUtil.withDefaults.toZonedDateTime("2020-01-30T17:26:44Z"), inputModel, p, q)

    and:
    Try<Map<String, String>, ProcessorProviderException> result = provider.handleEntity(pvResult)

    then:
    result.success
    Map<String, String> resultMap = result.data.get()

    resultMap.size() == 4
    resultMap == expectedMap

    when:
    Try<Map<String, String>, ProcessorProviderException> entityTry = provider.handleEntity(new WecResult(TimeUtil.withDefaults.toZonedDateTime("2020-01-30T17:26:44Z"), inputModel, p, q))

    then:
    entityTry.failure
    ProcessorProviderException ex = entityTry.exception.get()
    [
      "Cannot find a suitable processor for provided class with name 'WecResult'. This provider's processors can process: ",
      "PvResult",
      "EvResult"
    ]
    .every { str -> ex.message.contains(str) }
  }

  def "A ProcessorProvider returns an empty Optional, if none of the assigned processors is able to handle a time series"() {
    given:
    TimeSeriesProcessorKey key = new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue, EnergyPriceValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
    Map<TimeSeriesProcessorKey, TimeSeriesProcessor> timeSeriesProcessorMap = new HashMap<>()
    timeSeriesProcessorMap.put(key, processor)
    ProcessorProvider provider = new ProcessorProvider([], timeSeriesProcessorMap)

    when:
    provider.handleTimeSeries(individualIntTimeSeries)

    then:
    Exception ex = thrown()
    ex.class == ProcessorProviderException
    ex.message == "Cannot find processor for time series combination 'TimeSeriesProcessorKey{timeSeriesClass=class edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries, entryClass=class edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue, valueClass=class edu.ie3.datamodel.models.timeseries.IntValue}'. Either your provider is not properly initialized or there is no implementation to process this entity class!)"
  }

  def "A ProcessorProvider handles a time series correctly"() {
    given:
    TimeSeriesProcessorKey key = new TimeSeriesProcessorKey(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
    TimeSeriesProcessor<IndividualTimeSeries, TimeBasedValue, EnergyPriceValue, EnergyPriceValue> processor = new TimeSeriesProcessor<>(IndividualTimeSeries, TimeBasedValue, EnergyPriceValue)
    Map<TimeSeriesProcessorKey, TimeSeriesProcessor> timeSeriesProcessorMap = new HashMap<>()
    timeSeriesProcessorMap.put(key, processor)
    ProcessorProvider provider = new ProcessorProvider([], timeSeriesProcessorMap)

    when:
    Set<Map<String, String>> actual = provider.handleTimeSeries(individualEnergyPriceTimeSeries)

    then:
    actual == individualEnergyPriceTimeSeriesProcessed
  }
}
