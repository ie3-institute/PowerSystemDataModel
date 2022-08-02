/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import edu.ie3.test.common.TimeSeriesTestData
import edu.ie3.util.interval.ClosedInterval
import spock.lang.Specification

import java.time.ZoneId
import java.time.ZonedDateTime

class TimeSeriesUtilsTest extends Specification implements TimeSeriesTestData {
  def "A time series util is able to trim an individual time series to a given interval"() {
    given:
    def interval = new ClosedInterval(ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")), ZonedDateTime.of(1990, 1, 1, 0, 30, 0, 0, ZoneId.of("UTC")))

    when:
    def actual = TimeSeriesUtils.trimTimeSeriesToInterval(individualIntTimeSeries, interval)

    then:
    actual.entries.size() == 2
  }

  def "A time series util returns an empty time series, if the interval is not covered"() {
    given:
    def interval = new ClosedInterval(ZonedDateTime.of(1990, 12, 1, 0, 15, 0, 0, ZoneId.of("UTC")), ZonedDateTime.of(1990, 12, 1, 0, 30, 0, 0, ZoneId.of("UTC")))

    when:
    def actual = TimeSeriesUtils.trimTimeSeriesToInterval(individualIntTimeSeries, interval)

    then:
    actual.entries.size() == 0
  }

  def "A time series util returns only that parts of the time series, that are covered by the interval"() {
    given:
    def interval = new ClosedInterval(ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")), ZonedDateTime.of(1990, 1, 1, 1, 45, 0, 0, ZoneId.of("UTC")))

    when:
    def actual = TimeSeriesUtils.trimTimeSeriesToInterval(individualIntTimeSeries, interval)

    then:
    actual.entries.size() == 2
  }
}
