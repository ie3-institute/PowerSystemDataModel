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
      assert uuid == wec.uuid
      assert operationTime == wec.operationTime
      assert operator == wec.operator
      assert id == wec.id
      assert qCharacteristics == wec.qCharacteristics
      assert type == SystemParticipantTestData.wecType
      assert marketReaction
    }
  }
}
