/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.timeseries

import edu.ie3.datamodel.io.source.LoadProfileSource
import edu.ie3.util.TimeUtil
import spock.lang.Specification

class RandomLoadProfileTimeSeriesTest extends Specification {


  def "A RandomLoadProfileTimeSeries should supply a random value correctly"() {
    given:
    def lpts = LoadProfileSource.randomLoadProfile.getTimeSeries()
    def time = TimeUtil.withDefaults.toZonedDateTime("2020-01-01T00:00:00Z")

    def supplier = lpts.supplyValue(time)

    when:
    def values = [
      supplier.get(),
      supplier.get(),
      supplier.get()
    ].collect { it.get()}
    then:
    values.size() > 1
  }
}
