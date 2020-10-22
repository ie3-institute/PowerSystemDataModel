/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import edu.ie3.datamodel.models.BdewLoadProfile
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLTAMPERE
import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATTHOUR


class LoadInputTest extends Specification {

	def "A LoadInput copy method should work as expected"() {
		given:
		def loadInput = SystemParticipantTestData.loadInput

		when:
		def alteredUnit = loadInput.copy().standardLoadProfile(BdewLoadProfile.G0).dsm(true)
				.eConsAnnual(Quantities.getQuantity(6000, KILOWATTHOUR)).sRated(Quantities.getQuantity(0d, KILOVOLTAMPERE))
				.cosPhiRated(0.8).node(GridTestData.nodeG)
				.qCharacteristics(CosPhiFixed.CONSTANT_CHARACTERISTIC).build()

		then:
		alteredUnit.with {
			assert uuid == loadInput.uuid
			assert operationTime == loadInput.operationTime
			assert operator == loadInput.operator
			assert id == loadInput.id
			assert standardLoadProfile == BdewLoadProfile.G0
			assert dsm
			assert node == GridTestData.nodeG
			assert qCharacteristics == CosPhiFixed.CONSTANT_CHARACTERISTIC
			assert eConsAnnual == Quantities.getQuantity(6000, KILOWATTHOUR)
			assert sRated == Quantities.getQuantity(0d, KILOVOLTAMPERE)
			assert cosPhiRated == 0.8d
		}
	}
}
