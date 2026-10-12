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
      uuid == ev.uuid
      operationTime == ev.operationTime
      operator == ev.operator
      id == ev.id
      type == SystemParticipantTestData.evTypeInput
    }
  }

  def "Scaling an EvInput via builder should work as expected"() {
    given:
    def ev = SystemParticipantTestData.evInput

    when:
    def alteredUnit = ev.copy().scale(2d).build()

    then:
    alteredUnit.with {
      uuid == ev.uuid
      operationTime == ev.operationTime
      operator == ev.operator
      id == ev.id
      type.sRated == ev.type.sRated * 2d
      sRated() == ev.type.sRated * 2d
      type.sRatedDC == ev.type.sRatedDC * 2d
      type.eStorage == ev.type.eStorage * 2d
      type.eCons == ev.type.eCons * 2d
    }
  }

  def "An EvInput always-on constructor should work as expected"() {
    given:
    def uuid = UUID.randomUUID()
    def ev = new EvInput(uuid, "ev_always_on", SystemParticipantTestData.evTypeInput)

    expect:
    ev.uuid == uuid
    ev.id == "ev_always_on"
    ev.type == SystemParticipantTestData.evTypeInput
    ev.operator != null
    ev.operationTime != null
  }

  def "An EvInput equals and hashCode should work as expected"() {
    given:
    def ev1 = SystemParticipantTestData.evInput
    def ev2 = SystemParticipantTestData.evInput.copy().build()
    def evDifferentType = SystemParticipantTestData.evInput.copy()
        .type(SystemParticipantTestData.evTypeInput.copy().id("other").build())
        .build()
    def evDifferentUuid = new EvInput(
        UUID.randomUUID(),
        SystemParticipantTestData.evInput.id,
        SystemParticipantTestData.evInput.operator,
        SystemParticipantTestData.evInput.operationTime,
        SystemParticipantTestData.evTypeInput
        )

    expect:
    ev1 == ev1
    ev1 == ev2
    ev1.hashCode() == ev2.hashCode()
    ev1 != null
    ev1 != "string"
    ev1 != evDifferentType
    ev1 != evDifferentUuid
  }
}
