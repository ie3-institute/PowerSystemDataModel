/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system.type

import static edu.ie3.datamodel.models.StandardUnits.ACTIVE_POWER_IN
import static edu.ie3.datamodel.models.StandardUnits.CAPEX
import static edu.ie3.datamodel.models.StandardUnits.ENERGY_PRICE

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class BmTypeInputTest extends Specification {

  def "A BmTypeInput copy method should work as expected"() {
    given:
    def bmTypeInput = SystemParticipantTestData.bmTypeInput

    when:
    def alteredUnit = bmTypeInput.copy()
        .setsRated(Quantities.getQuantity(50d, ACTIVE_POWER_IN))
        .setCapex(Quantities.getQuantity(110d, CAPEX))
        .setOpex(Quantities.getQuantity(20d, ENERGY_PRICE))
        .build()

    then:
    alteredUnit.with {
      assert uuid == bmTypeInput.uuid
      assert id == bmTypeInput.id
      assert capex == Quantities.getQuantity(110d, CAPEX)
      assert opex == Quantities.getQuantity(20d, ENERGY_PRICE)
      assert sRated == Quantities.getQuantity(50d, ACTIVE_POWER_IN)
      assert cosPhiRated == bmTypeInput.cosPhiRated
      assert etaConv == bmTypeInput.etaConv
    }
  }

  def "Scaling a BmTypeInput via builder should work as expected"() {
    given:
    def bmTypeInput = SystemParticipantTestData.bmTypeInput

    when:
    def alteredUnit = bmTypeInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      assert uuid == bmTypeInput.uuid
      assert id == bmTypeInput.id
      assert capex == bmTypeInput.capex * 2d
      assert opex == bmTypeInput.opex
      assert sRated == bmTypeInput.sRated * 2d
      assert cosPhiRated == bmTypeInput.cosPhiRated
      assert etaConv == bmTypeInput.etaConv
    }
  }
}
