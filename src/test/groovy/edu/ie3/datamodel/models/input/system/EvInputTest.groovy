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
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling an EvInput via builder should work as expected"() {
    given:
    def ev = SystemParticipantTestData.evInput

    when:
    def alteredUnit = ev.copy().scale(2d).build()

    then:
    alteredUnit.with {
      assert uuid == ev.uuid
      assert operationTime == ev.operationTime
      assert operator == ev.operator
      assert id == ev.id
      assert qCharacteristics == ev.qCharacteristics
      assert type.sRated == ev.type.sRated * 2d
      assert type.sRatedDC == ev.type.sRatedDC * 2d
      assert type.eStorage == ev.type.eStorage * 2d
      assert type.eCons == ev.type.eCons * 2d
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
