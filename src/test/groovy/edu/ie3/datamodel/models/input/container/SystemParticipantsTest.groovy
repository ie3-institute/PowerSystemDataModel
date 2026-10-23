/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.container

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification

class SystemParticipantsTest extends Specification {

  def "A valid collection of EV entities can be used to build SystemParticipants"() {
    given:
    def systemParticipants =
        new SystemParticipants(Set.of(SystemParticipantTestData.evInput))

    when:
    def recreated =
        new SystemParticipants(systemParticipants.allEntitiesAsList())

    then:
    recreated == systemParticipants
    recreated.evs == Set.of(SystemParticipantTestData.evInput)
  }

  def "A SystemParticipants copy method should work as expected"() {
    given:
    def systemParticipants =
        new SystemParticipants(Set.of(SystemParticipantTestData.evInput))

    def modifiedEvInput =
        SystemParticipantTestData.evInput.copy().id("modified").build()

    when:
    def modifiedSystemParticipants = systemParticipants.copy()
        .evs(Set.of(modifiedEvInput))
        .build()

    then:
    modifiedSystemParticipants.evs == Set.of(modifiedEvInput)
  }
}