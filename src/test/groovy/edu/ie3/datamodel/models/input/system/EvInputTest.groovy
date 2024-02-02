/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification


class EvInputTest extends Specification {

  def "An EvInput copy method should work as expected"() {
    given:
    def ev = SystemParticipantTestData.evInput

    when:
    def alteredUnit = ev.copy().type(SystemParticipantTestData.evTypeInput).build()

    then:
    alteredUnit.with {
      assert uuid == ev.uuid
      assert operationTime == ev.operationTime
      assert operator == ev.operator
      assert id == ev.id
      assert qCharacteristics == ev.qCharacteristics
      assert type == SystemParticipantTestData.evTypeInput
      assert em == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
