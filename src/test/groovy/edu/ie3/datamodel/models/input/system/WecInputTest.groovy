/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification


class WecInputTest extends Specification {

  def "A WecInput copy method should work as expected"() {
    given:
    def wec = SystemParticipantTestData.wecInput

    when:
    def alteredUnit = wec.copy().type(SystemParticipantTestData.wecType).marketReaction(true).build()

    then:
    alteredUnit.with {
      uuid == wec.uuid
      operationTime == wec.operationTime
      operator == wec.operator
      id == wec.id
      qCharacteristics == wec.qCharacteristics
      type == SystemParticipantTestData.wecType
      marketReaction
      controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling a WecInput via builder should work as expected"() {
    given:
    def wec = SystemParticipantTestData.wecInput

    when:
    def alteredUnit = wec.copy().scale(2d).build()

    then:
    alteredUnit.with {
      uuid == wec.uuid
      operationTime == wec.operationTime
      operator == wec.operator
      id == wec.id
      qCharacteristics == wec.qCharacteristics
      type.sRated == wec.type.sRated * 2d
      sRated() == wec.type.sRated * 2d
      type.rotorArea == wec.type.rotorArea * 2d
      type.hubHeight == wec.type.hubHeight
      marketReaction == wec.marketReaction
      controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
