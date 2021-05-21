/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import static edu.ie3.util.quantities.PowerSystemUnits.EURO_PER_MEGAWATTHOUR

class BmInputTest extends Specification {

	def "A BmInput copy method should work as expected"() {
		given:
		def bmInput = SystemParticipantTestData.bmInput

		when:
		def alteredUnit = bmInput.copy().type(SystemParticipantTestData.bmTypeInput)
				.marketReaction(true)
				.costControlled(true).feedInTariff(Quantities.getQuantity(15, EURO_PER_MEGAWATTHOUR)).build()

		then:
		alteredUnit.with {
			assert uuid == bmInput.uuid
			assert operationTime == bmInput.operationTime
			assert operator == bmInput.operator
			assert id == bmInput.id
			assert marketReaction
			assert costControlled
			assert qCharacteristics == bmInput.qCharacteristics
			assert feedInTariff == Quantities.getQuantity(15, EURO_PER_MEGAWATTHOUR)
			assert type == SystemParticipantTestData.bmTypeInput
		}
	}
}
