/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system.type

import static edu.ie3.datamodel.models.StandardUnits.ACTIVE_POWER_IN
import static edu.ie3.datamodel.models.StandardUnits.EFFICIENCY

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class ChpTypeInputTest extends Specification {

  def "A ChpTypeInput copy method should work as expected"() {
    given:
    def chpTypeInput = SystemParticipantTestData.chpTypeInput

    when:
    def alteredUnit = chpTypeInput.copy()
        .setsRated(Quantities.getQuantity(50d, ACTIVE_POWER_IN))
        .setEtaEl(Quantities.getQuantity(20, EFFICIENCY))
        .setpThermal(Quantities.getQuantity(15, ACTIVE_POWER_IN))
        .build()

    then:
    alteredUnit.with {
      assert uuid == chpTypeInput.uuid
      assert id == chpTypeInput.id
      assert capex == chpTypeInput.capex
      assert opex == chpTypeInput.opex
      assert etaEl == Quantities.getQuantity(20, EFFICIENCY)
      assert etaThermal == chpTypeInput.etaThermal
      assert sRated == Quantities.getQuantity(50d, ACTIVE_POWER_IN)
      assert cosPhiRated == chpTypeInput.cosPhiRated
      assert pThermal == Quantities.getQuantity(15, ACTIVE_POWER_IN)
      assert pOwn == chpTypeInput.pOwn
    }
  }

  def "Scaling a ChpTypeInput via builder should work as expected"() {
    given:
    def chpTypeInput = SystemParticipantTestData.chpTypeInput

    when:
    def alteredUnit = chpTypeInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      assert uuid == chpTypeInput.uuid
      assert id == chpTypeInput.id
      assert capex == chpTypeInput.capex * 2d
      assert opex == chpTypeInput.opex
      assert etaEl == chpTypeInput.etaEl
      assert etaThermal == chpTypeInput.etaThermal
      assert sRated == chpTypeInput.sRated * 2d
      assert cosPhiRated == chpTypeInput.cosPhiRated
      assert pThermal == chpTypeInput.pThermal * 2d
      assert pOwn == chpTypeInput.pOwn * 2d
    }
  }
}
