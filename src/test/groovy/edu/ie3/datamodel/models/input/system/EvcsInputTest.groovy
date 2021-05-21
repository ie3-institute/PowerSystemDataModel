/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointTypeUtils
import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification

class EvcsInputTest extends Specification {

	def "A EvCsInput copy method should work as expected"() {
		given:
		def evcsInput = SystemParticipantTestData.evcsInput

		when:
		def alteredEntity = evcsInput.copy()
				.type(ChargingPointTypeUtils.TeslaSuperChargerV3)
				.cosPhiRated(0.7d).chargingPoints(1).build()

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
		}
	}
}
