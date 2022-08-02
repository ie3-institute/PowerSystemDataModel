/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.container

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification

class SystemParticipantsTest extends Specification {

  def "A valid collection of asset entities can be used to build a valid instance of SystemParticipants"() {
    given:
    def systemParticipants = new SystemParticipants(
        Collections.singleton(SystemParticipantTestData.bmInput),
        Collections.singleton(SystemParticipantTestData.chpInput),
        Collections.emptySet(),
        Collections.singleton(SystemParticipantTestData.evInput),
        Collections.singleton(SystemParticipantTestData.fixedFeedInInput),
        Collections.singleton(SystemParticipantTestData.hpInput),
        Collections.singleton(SystemParticipantTestData.loadInput),
        Collections.singleton(SystemParticipantTestData.pvInput),
        Collections.singleton(SystemParticipantTestData.storageInput),
        Collections.singleton(SystemParticipantTestData.wecInput)
        )

    when:
    def newlyCreatedSystemParticipants = new SystemParticipants(systemParticipants.allEntitiesAsList())

    then:
    newlyCreatedSystemParticipants == systemParticipants
  }
}
