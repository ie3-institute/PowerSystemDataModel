/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.timeseries

import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.test.common.TimeSeriesTestData
import spock.lang.Specification

import java.time.ZoneId
import java.time.ZonedDateTime

class IndividualTimeSeriesSpec extends Specification implements TimeSeriesTestData {
	def "Return empty optional value when queried for non existent time" () {
		expect:
		individualIntTimeSeries.getValue(ZonedDateTime.of(1990, 1, 1, 0, 10, 0, 0, ZoneId.of("UTC"))) == Optional.empty()
	}

	def "Return correct optional value when queried for existent time" () {
		given:
		IntValue expected = new IntValue(4)

		when:
		Optional<IntValue> actual = individualIntTimeSeries.getValue(ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")))

		then:
		actual.present
		actual.get().value == expected.value
	}

	def "Return correct optional time based value when queried for existent time" () {
		given:
		TimeBasedValue<IntValue> expected = new TimeBasedValue<>(ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")), new IntValue(4))

		when:
		Optional<TimeBasedValue<IntValue>> actual = individualIntTimeSeries.getTimeBasedValue(ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")))

		then:
		actual.present
		actual.get().time == expected.time
		actual.get().value.value == expected.value.value
	}

	def "Return empty optional time based value when queried for non existent time" () {
		expect:
		individualIntTimeSeries.getTimeBasedValue(ZonedDateTime.of(1990, 1, 1, 0, 10, 0, 0, ZoneId.of("UTC"))) == Optional.empty()
	}

	def "The individual time series returns empty Optional, when queried time is before provided time frame" () {
		expect:
		individualIntTimeSeries.getPreviousTimeBasedValue(ZonedDateTime.of(1989, 12, 31, 0, 0, 0, 0, ZoneId.of("UTC"))) == Optional.empty()
	}

	def "The individual time series returns correct Optional, when queried for the last known information" () {
		given:
		Optional<TimeBasedValue<IntValue>> expected =  Optional.of(new TimeBasedValue<>(ZonedDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")), new IntValue(3)))

		when:
		Optional<TimeBasedValue<IntValue>> actual = individualIntTimeSeries.getPreviousTimeBasedValue(ZonedDateTime.of(1990, 1, 1, 0, 10, 0, 0, ZoneId.of("UTC")))

		then:
		expected.isPresent()
		expected.get().time == actual.get().time
		expected.get().value.value == actual.get().value.value
	}

	def "The individual time series returns correct Optional, when queried for the last known information on an existing value" () {
		given:
		Optional<TimeBasedValue<IntValue>> expected =  Optional.of(new TimeBasedValue<>(ZonedDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")), new IntValue(3)))

		when:
		Optional<TimeBasedValue<IntValue>> actual = individualIntTimeSeries.getPreviousTimeBasedValue(ZonedDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))

		then:
		expected.isPresent()
		expected.get().time == actual.get().time
		expected.get().value.value == actual.get().value.value
	}

	def "The individual time series returns empty Optional, when queried time is after provided time frame" () {
		expect:
		individualIntTimeSeries.getNextTimeBasedValue(ZonedDateTime.of(1990, 1, 1, 0, 45, 0, 0, ZoneId.of("UTC"))) == Optional.empty()
	}

	def "The individual time series returns correct Optional, when queried for the next known information" () {
		given:
		Optional<TimeBasedValue<IntValue>> expected =  Optional.of(new TimeBasedValue<>(ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")), new IntValue(4)))

		when:
		Optional<TimeBasedValue<IntValue>> actual = individualIntTimeSeries.getNextTimeBasedValue(ZonedDateTime.of(1990, 1, 1, 0, 10, 0, 0, ZoneId.of("UTC")))

		then:
		expected.isPresent()
		expected.get().time == actual.get().time
		expected.get().value.value == actual.get().value.value
	}

	def "The individual time series returns correct Optional, when queried for the next known information on an existing value" () {
		given:
		Optional<TimeBasedValue<IntValue>> expected =  Optional.of(new TimeBasedValue<>(ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")), new IntValue(4)))

		when:
		Optional<TimeBasedValue<IntValue>> actual = individualIntTimeSeries.getNextTimeBasedValue(ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")))

		then:
		expected.isPresent()
		expected.get().time == actual.get().time
		expected.get().value.value == actual.get().value.value
	}
}
