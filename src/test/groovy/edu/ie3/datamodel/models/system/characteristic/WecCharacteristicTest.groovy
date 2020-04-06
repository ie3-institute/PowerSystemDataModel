/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.system.characteristic

import static edu.ie3.util.quantities.PowerSystemUnits.METRE_PER_SECOND
import static edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicCoordinate
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput
import spock.lang.Shared
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Speed

class WecCharacteristicTest extends Specification {
	@Shared
	WecCharacteristicInput validInput

	def setupSpec() {
		SortedSet<CharacteristicCoordinate<Speed, Dimensionless>> coordinates = [
			new CharacteristicCoordinate<Speed, Dimensionless>(Quantities.getQuantity(10, METRE_PER_SECOND),
			Quantities.getQuantity(0.05, PU)),
			new CharacteristicCoordinate<Speed, Dimensionless>(Quantities.getQuantity(15, METRE_PER_SECOND),
			Quantities.getQuantity(0.10, PU)),
			new CharacteristicCoordinate<Speed, Dimensionless>(Quantities.getQuantity(20, METRE_PER_SECOND),
			Quantities.getQuantity(0.20, PU))
		] as SortedSet

		validInput = new WecCharacteristicInput(
				UUID.fromString("9fee3c6c-4b00-4807-8eb1-1e1e4bbe52ac"),
				coordinates
				)
	}

	def "A WecCharacteristicInput is correctly de-serialized"() {
		when:
		String actual = validInput.deSerialize()

		then:
		actual == "cP:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}"
	}

	def "A WecCharacteristicInput is correctly set up from a correctly formatted string"() {
		when:
		WecCharacteristicInput actual = new WecCharacteristicInput(UUID.fromString("6979beaa-fceb-4115-981f-b91154a34512"), "cP:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}")

		then:
		actual.uuid == UUID.fromString("6979beaa-fceb-4115-981f-b91154a34512")
		actual.coordinates == validInput.coordinates
	}
}
