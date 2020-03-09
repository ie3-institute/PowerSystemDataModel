package edu.ie3.models.timeseries

import edu.ie3.models.value.TimeBasedValue
import edu.ie3.util.TimeTools
import spock.lang.Shared
import spock.lang.Specification

import java.time.ZonedDateTime

class IndividualTimeSeriesSpec extends Specification {
    @Shared
    IndividualTimeSeries<IntValue> timeSeries = new IndividualTimeSeries<>(
            UUID.randomUUID(),
            new HashMap<ZonedDateTime, IntValue>() {{
                    put(TimeTools.toZonedDateTime("01/01/1990 00:00:00"), new IntValue(3))
                    put(TimeTools.toZonedDateTime("01/01/1990 00:15:00"), new IntValue(4))
                    put(TimeTools.toZonedDateTime("01/01/1990 00:30:00"), new IntValue(1))
            }})

    def "Return empty optional value when queried for non existent time" () {
        expect:
        timeSeries.getValue(TimeTools.toZonedDateTime("01/01/1990 00:10:00")) == Optional.empty()
    }

    def "Return correct optional value when queried for existent time" () {
        given:
        IntValue expected = new IntValue(4)

        when:
        Optional<IntValue> actual = timeSeries.getValue(TimeTools.toZonedDateTime("01/01/1990 00:15:00"))

        then:
        actual.isPresent()
        actual.get().value == expected.value
    }

    def "Return correct optional time based value when queried for existent time" () {
        given:
        TimeBasedValue<IntValue> expected = new TimeBasedValue<>(TimeTools.toZonedDateTime("01/01/1990 00:15:00"), new IntValue(4))

        when:
        Optional<TimeBasedValue<IntValue>> actual = timeSeries.getTimeBasedValue(TimeTools.toZonedDateTime("01/01/1990 00:15:00"))

        then:
        actual.isPresent()
        actual.get().time == expected.time
        actual.get().value.value == expected.value.value
    }

    def "Return empty optional time based value when queried for non existent time" () {
        expect:
        timeSeries.getTimeBasedValue(TimeTools.toZonedDateTime("01/01/1990 00:10:00")) == Optional.empty()
    }

    def "The individual time series returns empty Optional, when queried time is before provided time frame" () {
        expect:
        timeSeries.getLastTimeBasedValue(TimeTools.toZonedDateTime("31/12/1989 00:00:00")) == Optional.empty()
    }

    def "The individual time series returns correct Optional, when queried for the last known information" () {
        given:
        Optional<TimeBasedValue<IntValue>> expected =  Optional.of(new TimeBasedValue<>(TimeTools.toZonedDateTime("01/01/1990 00:00:00"), new IntValue(3)))

        when:
        Optional<TimeBasedValue<IntValue>> actual = timeSeries.getLastTimeBasedValue(TimeTools.toZonedDateTime("01/01/1990 00:10:00"))

        then:
        expected.isPresent()
        expected.get().time == actual.get().time
        expected.get().value.value == actual.get().value.value
    }

    def "The individual time series returns correct Optional, when queried for the last known information on an existing value" () {
        given:
        Optional<TimeBasedValue<IntValue>> expected =  Optional.of(new TimeBasedValue<>(TimeTools.toZonedDateTime("01/01/1990 00:00:00"), new IntValue(3)))

        when:
        Optional<TimeBasedValue<IntValue>> actual = timeSeries.getLastTimeBasedValue(TimeTools.toZonedDateTime("01/01/1990 00:00:00"))

        then:
        expected.isPresent()
        expected.get().time == actual.get().time
        expected.get().value.value == actual.get().value.value
    }

    def "The individual time series returns empty Optional, when queried time is after provided time frame" () {
        expect:
        timeSeries.getNextTimeBasedValue(TimeTools.toZonedDateTime("01/01/1990 00:45:00")) == Optional.empty()
    }

    def "The individual time series returns correct Optional, when queried for the next known information" () {
        given:
        Optional<TimeBasedValue<IntValue>> expected =  Optional.of(new TimeBasedValue<>(TimeTools.toZonedDateTime("01/01/1990 00:15:00"), new IntValue(4)))

        when:
        Optional<TimeBasedValue<IntValue>> actual = timeSeries.getNextTimeBasedValue(TimeTools.toZonedDateTime("01/01/1990 00:10:00"))

        then:
        expected.isPresent()
        expected.get().time == actual.get().time
        expected.get().value.value == actual.get().value.value
    }

    def "The individual time series returns correct Optional, when queried for the next known information on an existing value" () {
        given:
        Optional<TimeBasedValue<IntValue>> expected =  Optional.of(new TimeBasedValue<>(TimeTools.toZonedDateTime("01/01/1990 00:15:00"), new IntValue(4)))

        when:
        Optional<TimeBasedValue<IntValue>> actual = timeSeries.getNextTimeBasedValue(TimeTools.toZonedDateTime("01/01/1990 00:15:00"))

        then:
        expected.isPresent()
        expected.get().time == actual.get().time
        expected.get().value.value == actual.get().value.value
    }
}
