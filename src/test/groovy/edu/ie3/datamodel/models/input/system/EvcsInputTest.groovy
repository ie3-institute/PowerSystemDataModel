/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointTypeUtils
import edu.ie3.datamodel.models.input.system.type.evcslocation.EvcsLocationType
import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification

class EvcsInputTest extends Specification {

  def "An EvcsInput copy method should work as expected"() {
    given:
    def evcsInput = SystemParticipantTestData.evcsInput

    when:
    def alteredEntity = evcsInput.copy()
        .type(ChargingPointTypeUtils.TeslaSuperChargerV3)
        .cosPhiRated(0.7d).chargingPoints(1)
        .locationType(EvcsLocationType.CHARGING_HUB_HIGHWAY)
        .v2gSupport(true)
        .build()

    then:
    alteredEntity.with {
      assert uuid == evcsInput.uuid
      assert operationTime == evcsInput.operationTime
      assert operator == evcsInput.operator
      assert id == evcsInput.id
      assert qCharacteristics == evcsInput.qCharacteristics
      assert type == ChargingPointTypeUtils.TeslaSuperChargerV3
      assert cosPhiRated == 0.7d
      assert chargingPoints == 1
      assert locationType == EvcsLocationType.CHARGING_HUB_HIGHWAY
      assert v2gSupport
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling an EvcsInput via builder should work as expected"() {
    given:
    def evcsInput = SystemParticipantTestData.evcsInput

    when:
    def alteredUnit = evcsInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      assert uuid == evcsInput.uuid
      assert operationTime == evcsInput.operationTime
      assert operator == evcsInput.operator
      assert id == evcsInput.id
      assert qCharacteristics == evcsInput.qCharacteristics
      assert type.sRated == evcsInput.type.sRated * 2d
      assert cosPhiRated == evcsInput.cosPhiRated
      assert chargingPoints == evcsInput.chargingPoints
      assert locationType == evcsInput.locationType
      assert v2gSupport == evcsInput.v2gSupport
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
