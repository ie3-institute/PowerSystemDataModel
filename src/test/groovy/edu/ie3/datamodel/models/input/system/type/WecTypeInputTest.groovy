/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system.type

import static edu.ie3.datamodel.models.StandardUnits.HUB_HEIGHT
import static edu.ie3.datamodel.models.StandardUnits.ROTOR_AREA

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class WecTypeInputTest extends Specification {

  def "A WecTypeInput copy method should work as expected"() {
    given:
    def wecType = SystemParticipantTestData.wecType

    when:
    def alteredUnit = wecType.copy()
        .setRotorArea(Quantities.getQuantity(25, ROTOR_AREA))
        .setHubHeight(Quantities.getQuantity(180, HUB_HEIGHT))
        .build()

    then:
    alteredUnit.with {
      uuid == wecType.uuid
      id == wecType.id
      capex == wecType.capex
      opex == wecType.opex
      sRated == wecType.sRated
      cosPhiRated == wecType.cosPhiRated
      cpCharacteristic == wecType.cpCharacteristic
      etaConv == wecType.etaConv
      rotorArea == Quantities.getQuantity(25, ROTOR_AREA)
      hubHeight == Quantities.getQuantity(180, HUB_HEIGHT)
    }
  }

  def "Scaling a WecTypeInput via builder should work as expected"() {
    given:
    def wecType = SystemParticipantTestData.wecType

    when:
    def alteredUnit = wecType.copy().scale(2d).build()

    then:
    alteredUnit.with {
      uuid == wecType.uuid
      id == wecType.id
      capex == wecType.capex * 2d
      opex == wecType.opex
      sRated == wecType.sRated * 2d
      cosPhiRated == wecType.cosPhiRated
      cpCharacteristic == wecType.cpCharacteristic
      etaConv == wecType.etaConv
      rotorArea == wecType.rotorArea * 2d
      hubHeight == wecType.hubHeight
    }
  }
}
