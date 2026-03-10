/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system.type

import static edu.ie3.datamodel.models.StandardUnits.ENERGY_IN

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class EvTypeInputTest extends Specification {

  def "A EvTypeInput copy method should work as expected"() {
    given:
    def evTypeInput = SystemParticipantTestData.evTypeInput

    when:
    def alteredUnit = evTypeInput.copy()
        .seteStorage(Quantities.getQuantity(150, ENERGY_IN))
        .build()

    then:
    alteredUnit.with {
      uuid == evTypeInput.uuid
      id == evTypeInput.id
      capex == evTypeInput.capex
      opex == evTypeInput.opex
      eStorage == Quantities.getQuantity(150, ENERGY_IN)
      eCons == evTypeInput.eCons
      sRated == evTypeInput.sRated
      sRatedDC == evTypeInput.sRatedDC
      cosPhiRated == evTypeInput.cosPhiRated
    }
  }

  def "Scaling a EvTypeInput via builder should work as expected"() {
    given:
    def evTypeInput = SystemParticipantTestData.evTypeInput

    when:
    def alteredUnit = evTypeInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      uuid == evTypeInput.uuid
      id == evTypeInput.id
      capex == evTypeInput.capex * 2d
      opex == evTypeInput.opex
      eStorage == evTypeInput.eStorage * 2d
      eCons == evTypeInput.eCons * 2d
      sRated == evTypeInput.sRated * 2d
      sRatedDC == evTypeInput.sRatedDC * 2d
      cosPhiRated == evTypeInput.cosPhiRated
    }
  }
}
