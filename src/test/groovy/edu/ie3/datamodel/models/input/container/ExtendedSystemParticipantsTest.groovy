/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.container

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification

class ExtendedSystemParticipantsTest extends Specification {

  def "A valid collection of non-EV entities can be used to build ExtendedSystemParticipants"() {
    given:
    def extendedSystemParticipants = new ExtendedSystemParticipants(
        Set.of(SystemParticipantTestData.bmInput),
        Set.of(SystemParticipantTestData.chpInput),
        Set.of(SystemParticipantTestData.evcsInput),
        Set.of(SystemParticipantTestData.fixedFeedInInput),
        Set.of(SystemParticipantTestData.hpInput),
        Set.of(SystemParticipantTestData.acInput),
        Set.of(SystemParticipantTestData.loadInput),
        Set.of(SystemParticipantTestData.pvInput),
        Set.of(SystemParticipantTestData.storageInput),
        Set.of(SystemParticipantTestData.wecInput)
    )

    when:
    def recreated =
        new ExtendedSystemParticipants(extendedSystemParticipants.allEntitiesAsList())

    then:
    recreated == extendedSystemParticipants
    recreated.allEntitiesAsList().size() == 10
  }

  def "An ExtendedSystemParticipants copy method should work as expected"() {
    given:
    def extendedSystemParticipants = new ExtendedSystemParticipants(
        Set.of(SystemParticipantTestData.bmInput),
        Set.of(SystemParticipantTestData.chpInput),
        Set.of(SystemParticipantTestData.evcsInput),
        Set.of(SystemParticipantTestData.fixedFeedInInput),
        Set.of(SystemParticipantTestData.hpInput),
        Set.of(SystemParticipantTestData.acInput),
        Set.of(SystemParticipantTestData.loadInput),
        Set.of(SystemParticipantTestData.pvInput),
        Set.of(SystemParticipantTestData.storageInput),
        Set.of(SystemParticipantTestData.wecInput)
    )

    def modifiedBmInput =
        SystemParticipantTestData.bmInput.copy().id("modified").build()
    def modifiedChpInput =
        SystemParticipantTestData.chpInput.copy().id("modified").build()
    def modifiedEvcsInput =
        SystemParticipantTestData.evcsInput.copy().id("modified").build()
    def modifiedFixedFeedInInput =
        SystemParticipantTestData.fixedFeedInInput.copy().id("modified").build()
    def modifiedHpInput =
        SystemParticipantTestData.hpInput.copy().id("modified").build()
    def modifiedAcInput =
        SystemParticipantTestData.acInput.copy().id("modified").build()
    def modifiedLoadInput =
        SystemParticipantTestData.loadInput.copy().id("modified").build()
    def modifiedPvInput =
        SystemParticipantTestData.pvInput.copy().id("modified").build()
    def modifiedStorageInput =
        SystemParticipantTestData.storageInput.copy().id("modified").build()
    def modifiedWecInput =
        SystemParticipantTestData.wecInput.copy().id("modified").build()

    when:
    def modifiedSystemParticipants = extendedSystemParticipants.copy()
        .bmPlants(Set.of(modifiedBmInput))
        .chpPlants(Set.of(modifiedChpInput))
        .evcs(Set.of(modifiedEvcsInput))
        .fixedFeedIns(Set.of(modifiedFixedFeedInInput))
        .heatPumps(Set.of(modifiedHpInput))
        .airConditions(Set.of(modifiedAcInput))
        .loads(Set.of(modifiedLoadInput))
        .pvPlants(Set.of(modifiedPvInput))
        .storages(Set.of(modifiedStorageInput))
        .wecPlants(Set.of(modifiedWecInput))
        .build()

    then:
    modifiedSystemParticipants.bmPlants == Set.of(modifiedBmInput)
    modifiedSystemParticipants.chpPlants == Set.of(modifiedChpInput)
    modifiedSystemParticipants.evcs == Set.of(modifiedEvcsInput)
    modifiedSystemParticipants.fixedFeedIns == Set.of(modifiedFixedFeedInInput)
    modifiedSystemParticipants.heatPumps == Set.of(modifiedHpInput)
    modifiedSystemParticipants.airConditions == Set.of(modifiedAcInput)
    modifiedSystemParticipants.loads == Set.of(modifiedLoadInput)
    modifiedSystemParticipants.pvPlants == Set.of(modifiedPvInput)
    modifiedSystemParticipants.storages == Set.of(modifiedStorageInput)
    modifiedSystemParticipants.wecPlants == Set.of(modifiedWecInput)
  }
}