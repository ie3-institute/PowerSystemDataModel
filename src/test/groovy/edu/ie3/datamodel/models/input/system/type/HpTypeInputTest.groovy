/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system.type

import static edu.ie3.datamodel.models.StandardUnits.ACTIVE_POWER_IN

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class HpTypeInputTest extends Specification {

  def "A HpTypeInput copy method should work as expected"() {
    given:
    def hpTypeInput = SystemParticipantTestData.hpTypeInput

    when:
    def alteredUnit = hpTypeInput.copy()
        .setpThermal(Quantities.getQuantity(10, ACTIVE_POWER_IN))
        .setCosPhiRated(0.97d)
        .build()

    then:
    alteredUnit.with {
      assert uuid == hpTypeInput.uuid
      assert id == hpTypeInput.id
      assert capex == hpTypeInput.capex
      assert opex == hpTypeInput.opex
      assert sRated == hpTypeInput.sRated
      assert cosPhiRated == 0.97d
      assert pThermal == Quantities.getQuantity(10, ACTIVE_POWER_IN)
    }
  }

  def "Scaling a HpTypeInput via builder should work as expected"() {
    given:
    def hpTypeInput = SystemParticipantTestData.hpTypeInput

    when:
    def alteredUnit = hpTypeInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      assert uuid == hpTypeInput.uuid
      assert id == hpTypeInput.id
      assert capex == hpTypeInput.capex * 2d
      assert opex == hpTypeInput.opex
      assert sRated == hpTypeInput.sRated * 2d
      assert cosPhiRated == hpTypeInput.cosPhiRated
      assert pThermal == hpTypeInput.pThermal * 2d
    }
  }
}
