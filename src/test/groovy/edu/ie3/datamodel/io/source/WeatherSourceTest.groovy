/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import edu.ie3.datamodel.exceptions.SourceException
import spock.lang.Specification

import java.time.ZonedDateTime

class WeatherSourceTest extends Specification {

  def base = ZonedDateTime.parse("2021-01-01T00:00:00+01:00")

  def "isFallbackAcceptable returns true when stepReference is null (step size unknown)"() {
    given:
    def requested = base.plusHours(2)
    def fallback = base.plusHours(1)

    expect:
    WeatherSource.isFallbackAcceptable(requested, fallback, null)
  }

  def "isFallbackAcceptable throws SourceException when stepReference is after fallback"() {
    given:
    def requested = base.plusHours(3)
    def fallback = base.plusHours(1)
    def stepReference = base.plusHours(2) // after fallback, inconsistent

    when:
    WeatherSource.isFallbackAcceptable(requested, fallback, stepReference)

    then:
    thrown(SourceException)
  }

  def "isFallbackAcceptable returns true when stepReference equals fallback (duplicate timestamps)"() {
    given:
    def requested = base.plusHours(2)
    def fallback = base.plusHours(1)
    def stepReference = base.plusHours(1) // same as fallback

    expect:
    WeatherSource.isFallbackAcceptable(requested, fallback, stepReference)
  }

  def "isFallbackAcceptable returns true when gap is within MAX_FALLBACK_STEPS"() {
    given: "step size of 1 hour, gap of exactly MAX_FALLBACK_STEPS hours"
    def stepReference = base
    def fallback = base.plusHours(1)
    def requested = base.plusHours(1 + WeatherSource.MAX_FALLBACK_STEPS)

    expect:
    WeatherSource.isFallbackAcceptable(requested, fallback, stepReference)
  }

  def "isFallbackAcceptable returns false when gap exceeds MAX_FALLBACK_STEPS"() {
    given: "step size of 1 hour, gap of MAX_FALLBACK_STEPS + 1 hours"
    def stepReference = base
    def fallback = base.plusHours(1)
    def requested = base.plusHours(1 + WeatherSource.MAX_FALLBACK_STEPS + 1)

    expect:
    !WeatherSource.isFallbackAcceptable(requested, fallback, stepReference)
  }
}
