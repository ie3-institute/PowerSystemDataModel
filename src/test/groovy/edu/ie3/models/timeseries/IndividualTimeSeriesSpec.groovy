package edu.ie3.models.timeseries

import edu.ie3.models.value.TimeBasedValue
import spock.lang.Shared
import spock.lang.Specification

import java.time.ZoneId
import java.time.ZonedDateTime

class IndividualTimeSeriesSpec extends Specification {
    @Shared
    IndividualTimeSeries<IntValue> timeSeries = new IndividualTimeSeries<>(
            UUID.randomUUID(),
            new HashMap<ZonedDateTime, IntValue>() { {
                    put(ZonedDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")), new IntValue(3))
                    put(ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")), new IntValue(4))
                    put(ZonedDateTime.of(1990, 1, 1, 0, 30, 0, 0, ZoneId.of("UTC")), new IntValue(1))
            } })

    def "Return empty optional value when queried for non existent time" () {
        expect:
        timeSeries.getValue(ZonedDateTime.of(1990, 1, 1, 0, 10, 0, 0, ZoneId.of("UTC"))) == Optional.empty()
    }

    def "Return correct optional value when queried for existent time" () {
        given:
        IntValue expected = new IntValue(4)

        when:
        Optional<IntValue> actual = timeSeries.getValue(ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")))

        then:
        actual.present
        actual.get().value == expected.value
    }

    def "Return correct optional time based value when queried for existent time" () {
        given:
        TimeBasedValue<IntValue> expected = new TimeBasedValue<>(ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")), new IntValue(4))

        when:
        Optional<TimeBasedValue<IntValue>> actual = timeSeries.getTimeBasedValue(ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")))

        then:
        actual.present
        actual.get().time == expected.time
        actual.get().value.value == expected.value.value
    }

    def "Return empty optional time based value when queried for non existent time" () {
        expect:
        timeSeries.getTimeBasedValue(ZonedDateTime.of(1990, 1, 1, 0, 10, 0, 0, ZoneId.of("UTC"))) == Optional.empty()
    }

    def "The individual time series returns empty Optional, when queried time is before provided time frame" () {
        expect:
        timeSeries.getPreviousTimeBasedValue(ZonedDateTime.of(1989, 12, 31, 0, 0, 0, 0, ZoneId.of("UTC"))) == Optional.empty()
    }

    def "The individual time series returns correct Optional, when queried for the last known information" () {
        given:
        Optional<TimeBasedValue<IntValue>> expected =  Optional.of(new TimeBasedValue<>(ZonedDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")), new IntValue(3)))

        when:
        Optional<TimeBasedValue<IntValue>> actual = timeSeries.getPreviousTimeBasedValue(ZonedDateTime.of(1990, 1, 1, 0, 10, 0, 0, ZoneId.of("UTC")))

        then:
        expected.isPresent()
        expected.get().time == actual.get().time
        expected.get().value.value == actual.get().value.value
    }

    def "The individual time series returns correct Optional, when queried for the last known information on an existing value" () {
        given:
        Optional<TimeBasedValue<IntValue>> expected =  Optional.of(new TimeBasedValue<>(ZonedDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")), new IntValue(3)))

        when:
        Optional<TimeBasedValue<IntValue>> actual = timeSeries.getPreviousTimeBasedValue(ZonedDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))

        then:
        expected.isPresent()
        expected.get().time == actual.get().time
        expected.get().value.value == actual.get().value.value
    }

    def "The individual time series returns empty Optional, when queried time is after provided time frame" () {
        expect:
        timeSeries.getNextTimeBasedValue(ZonedDateTime.of(1990, 1, 1, 0, 45, 0, 0, ZoneId.of("UTC"))) == Optional.empty()
    }

    def "The individual time series returns correct Optional, when queried for the next known information" () {
        given:
        Optional<TimeBasedValue<IntValue>> expected =  Optional.of(new TimeBasedValue<>(ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")), new IntValue(4)))

        when:
        Optional<TimeBasedValue<IntValue>> actual = timeSeries.getNextTimeBasedValue(ZonedDateTime.of(1990, 1, 1, 0, 10, 0, 0, ZoneId.of("UTC")))

        then:
        expected.isPresent()
        expected.get().time == actual.get().time
        expected.get().value.value == actual.get().value.value
    }

    def "The individual time series returns correct Optional, when queried for the next known information on an existing value" () {
        given:
        Optional<TimeBasedValue<IntValue>> expected =  Optional.of(new TimeBasedValue<>(ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")), new IntValue(4)))

        when:
        Optional<TimeBasedValue<IntValue>> actual = timeSeries.getNextTimeBasedValue(ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")))

        then:
        expected.isPresent()
        expected.get().time == actual.get().time
        expected.get().value.value == actual.get().value.value
    }
}
