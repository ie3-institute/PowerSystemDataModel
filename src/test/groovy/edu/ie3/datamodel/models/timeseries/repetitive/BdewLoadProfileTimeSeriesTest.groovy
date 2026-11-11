/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.timeseries.repetitive

import static tech.units.indriya.unit.Units.WATT

import edu.ie3.datamodel.io.source.LoadProfileSource
import edu.ie3.datamodel.io.source.PowerValueSource
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.util.quantities.QuantityUtil
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime

class BdewLoadProfileTimeSeriesTest extends Specification {

  def "A BdewLoadProfileTimeSeries should supply the value correct for the provided time"() {
    given:
    def lpts = LoadProfileSource.bdewLoadProfiles.get(BdewStandardLoadProfile.L0.key)

    expect:
    def inputValue = new PowerValueSource.TimeSeriesInputValue(ZonedDateTime.parse(time))
    def expectedQuantity = Quantities.getQuantity(expectedPower, WATT)
    def actualQuantity = lpts.getValueSupplier(inputValue).get().value().get().getP().get()
    QuantityUtil.isEquivalentAbs(actualQuantity, expectedQuantity, 1e-6d)

    where:
    time | expectedPower
    "2026-01-01T00:00:00+00:00" | 63.2
    "2026-01-01T01:00:00+01:00" | 63.2
    "2026-03-29T01:00:00+01:00" | 59.2
    "2026-03-29T02:00:00+01:00" | 54.6
    "2026-10-25T01:00:00+01:00" | 55.8
    "2026-10-25T02:00:00+01:00" | 55.8
  }
}
