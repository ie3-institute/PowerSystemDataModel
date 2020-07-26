/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointTypeUtils
import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification

class CsInputTest extends Specification {

	def "A CsInput copy method should work as expected"() {
		given:
		def csInput = SystemParticipantTestData.csInput

		when:
		def alteredEntity = csInput.copy()
				.type(ChargingPointTypeUtils.TeslaSuperChargerV3)
				.cosPhiRated(0.7d).noChargingPoints(1).build()

		then:
		alteredEntity.with {
			assert uuid == csInput.uuid
			assert operationTime == csInput.operationTime
			assert operator == csInput.operator
			assert id == csInput.id
			assert qCharacteristics == csInput.qCharacteristics
			assert type == ChargingPointTypeUtils.TeslaSuperChargerV3
			assert cosPhiRated == 0.7d
			assert noChargingPoints == 1
		}
	}
}
