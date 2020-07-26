/*
 * © 2020. TU Dortmund University,
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
		def csInput = SystemParticipantTestData.evcsInput

		when:
		def alteredEntity = EvcsInput.copy()
				.type(ChargingPointTypeUtils.TeslaSuperChargerV3)
				.cosPhiRated(0.7d).chargingPoints(1).build()

		then:
		alteredEntity.with {
			assert uuid == csInput.uuid
			assert operationTime == csInput.operationTime
			assert operator == csInput.operator
			assert id == csInput.id
			assert qCharacteristics == csInput.qCharacteristics
			assert type == ChargingPointTypeUtils.TeslaSuperChargerV3
			assert cosPhiRated == 0.7d
			assert chargingPoints == 1
		}
	}
}
