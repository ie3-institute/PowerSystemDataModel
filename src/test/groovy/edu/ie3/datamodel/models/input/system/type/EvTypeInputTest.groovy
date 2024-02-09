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
      assert uuid == evTypeInput.uuid
      assert id == evTypeInput.id
      assert capex == evTypeInput.capex
      assert opex == evTypeInput.opex
      assert eStorage == Quantities.getQuantity(150, ENERGY_IN)
      assert eCons == evTypeInput.eCons
      assert sRated == evTypeInput.sRated
      assert cosPhiRated == evTypeInput.cosPhiRated
    }
  }

  def "Scaling a EvTypeInput via builder should work as expected"() {
    given:
    def evTypeInput = SystemParticipantTestData.evTypeInput

    when:
    def alteredUnit = evTypeInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      assert uuid == evTypeInput.uuid
      assert id == evTypeInput.id
      assert capex == evTypeInput.capex
      assert opex == evTypeInput.opex
      assert eStorage == evTypeInput.eStorage * 2d
      assert eCons == evTypeInput.eCons * 2d
      assert sRated == evTypeInput.sRated * 2d
      assert cosPhiRated == evTypeInput.cosPhiRated
    }
  }
}
