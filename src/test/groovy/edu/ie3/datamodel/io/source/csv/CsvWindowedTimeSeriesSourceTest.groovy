/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.value.EnergyPriceValue
import edu.ie3.util.interval.ClosedInterval
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime

class CsvWindowedTimeSeriesSourceTest extends Specification implements CsvTestDataMeta {
	def "The windowed time series source is able to query an instance in time"() {
		given:
		def factory = new TimeBasedSimpleValueFactory(EnergyPriceValue)
		def source = new CsvWindowedTimeSeriesSource(
				";",
				timeSeriesFolderPath,
				"its_c_2fcb3e53-b94a-4b96-bea4-c469e499f1a1",
				new FileNamingStrategy(),
				Duration.ofHours(2L),
				EnergyPriceValue,
				factory)
		def expected = new EnergyPriceValue(Quantities.getQuantity(125.0, StandardUnits.ENERGY_PRICE))

		when:
		def actual = source.getValue(ZonedDateTime.of(2020, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")))

		then:
		actual.isPresent()
		actual.get() == expected

		source.close()
	}

	def "The windowed time series source is able to query multiple instances in time"() {
		given:
		def factory = new TimeBasedSimpleValueFactory(EnergyPriceValue)
		def source = new CsvWindowedTimeSeriesSource(
				";",
				timeSeriesFolderPath,
				"its_c_2fcb3e53-b94a-4b96-bea4-c469e499f1a1",
				new FileNamingStrategy(),
				Duration.ofHours(2L),
				EnergyPriceValue,
				factory)

		when:
		source.getValue(ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
		source.getValue(ZonedDateTime.of(2020, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")))

		then:
		noExceptionThrown()

		source.close()
	}

	def "The windowed time series source throws an exception, if the queried time is before the currently covered interval"() {
		given:
		def factory = new TimeBasedSimpleValueFactory(EnergyPriceValue)
		def source = new CsvWindowedTimeSeriesSource(
				";",
				timeSeriesFolderPath,
				"its_c_2fcb3e53-b94a-4b96-bea4-c469e499f1a1",
				new FileNamingStrategy(),
				Duration.ofHours(2L),
				EnergyPriceValue,
				factory)

		when:
		source.getValue(ZonedDateTime.of(2020, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")))
		source.getValue(ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))

		then:
		def thrown = thrown(RuntimeException)
		thrown.message == "The buffer window already passed your desired time instance '2020-01-01T00:00Z[UTC]'."

		source.close()
	}

	def "The windowed time series source throws an exception, if the queried time frame starts before the currently covered interval"() {
		given:
		def factory = new TimeBasedSimpleValueFactory(EnergyPriceValue)
		def source = new CsvWindowedTimeSeriesSource(
				";",
				timeSeriesFolderPath,
				"its_c_2fcb3e53-b94a-4b96-bea4-c469e499f1a1",
				new FileNamingStrategy(),
				Duration.ofHours(2L),
				EnergyPriceValue,
				factory)
		def start = ZonedDateTime.of(2020, 1, 1, 0, 00, 0, 0, ZoneId.of("UTC"))

		when:
		source.getValue(ZonedDateTime.of(2020, 1, 1, 0, 0, 15, 0, ZoneId.of("UTC")))
		source.getTimeSeries(new ClosedInterval<ZonedDateTime>(start, start.plusHours(2L)))

		then:
		def thrown = thrown(RuntimeException)
		thrown.message == "The buffer window already passed the start  '2020-01-01T00:00Z[UTC]' of your desired time frame."

		source.close()
	}

	def "The windowed time series source is able to load a time series for a given interval"() {
		given:
		def factory = new TimeBasedSimpleValueFactory(EnergyPriceValue)
		def source = new CsvWindowedTimeSeriesSource(
				";",
				timeSeriesFolderPath,
				"its_c_2fcb3e53-b94a-4b96-bea4-c469e499f1a1",
				new FileNamingStrategy(),
				Duration.ofHours(2L),
				EnergyPriceValue,
				factory)
		def start = ZonedDateTime.of(2020, 1, 1, 0, 00, 0, 0, ZoneId.of("UTC"))
		def end = ZonedDateTime.of(2020, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC"))

		when:
		def actual = source.getTimeSeries(new ClosedInterval<ZonedDateTime>(start, end))

		then:
		actual.entries.size() == 2

		source.close()
	}
}
