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

  def "A SystemParticipants' copy method should work as expected"() {
    given:
    def systemParticipants = new SystemParticipants(
        Collections.singleton(SystemParticipantTestData.bmInput),
        Collections.singleton(SystemParticipantTestData.chpInput),
        Collections.singleton(SystemParticipantTestData.evcsInput),
        Collections.singleton(SystemParticipantTestData.evInput),
        Collections.singleton(SystemParticipantTestData.fixedFeedInInput),
        Collections.singleton(SystemParticipantTestData.hpInput),
        Collections.singleton(SystemParticipantTestData.loadInput),
        Collections.singleton(SystemParticipantTestData.pvInput),
        Collections.singleton(SystemParticipantTestData.storageInput),
        Collections.singleton(SystemParticipantTestData.wecInput)
        )

    def modifiedBmInput = SystemParticipantTestData.bmInput.copy().id("modified").build()
    def modifiedChpInput = SystemParticipantTestData.chpInput.copy().id("modified").build()
    def modifiedEvcsInput = SystemParticipantTestData.evcsInput.copy().id("modified").build()
    def modifiedEvInput = SystemParticipantTestData.evInput.copy().id("modified").build()
    def modifiedFixedFeedInInput = SystemParticipantTestData.fixedFeedInInput.copy().id("modified").build()
    def modifiedHpInput = SystemParticipantTestData.hpInput.copy().id("modified").build()
    def modifiedLoadInput = SystemParticipantTestData.loadInput.copy().id("modified").build()
    def modifiedPvInput = SystemParticipantTestData.pvInput.copy().id("modified").build()
    def modifiedStorageInput = SystemParticipantTestData.storageInput.copy().id("modified").build()
    def modifiedWecInput = SystemParticipantTestData.wecInput.copy().id("modified").build()

    when:
    def modifiedSystemParticipants = systemParticipants.copy()
        .bmPlants(Set.of(modifiedBmInput))
        .chpPlants(Set.of(modifiedChpInput))
        .evcs(Set.of(modifiedEvcsInput))
        .evs(Set.of(modifiedEvInput))
        .fixedFeedIn(Set.of(modifiedFixedFeedInInput))
        .heatPumps(Set.of(modifiedHpInput))
        .loads(Set.of(modifiedLoadInput))
        .pvPlants(Set.of(modifiedPvInput))
        .storages(Set.of(modifiedStorageInput))
        .wecPlants(Set.of(modifiedWecInput))
        .build()

    then:
    modifiedSystemParticipants.bmPlants.first() == modifiedBmInput
    modifiedSystemParticipants.chpPlants.first() == modifiedChpInput
    modifiedSystemParticipants.evcs.first() == modifiedEvcsInput
    modifiedSystemParticipants.evs.first() == modifiedEvInput
    modifiedSystemParticipants.fixedFeedIns.first() == modifiedFixedFeedInInput
    modifiedSystemParticipants.heatPumps.first() == modifiedHpInput
    modifiedSystemParticipants.loads.first() == modifiedLoadInput
    modifiedSystemParticipants.pvPlants.first() == modifiedPvInput
    modifiedSystemParticipants.storages.first() == modifiedStorageInput
    modifiedSystemParticipants.wecPlants.first() == modifiedWecInput
  }
}
