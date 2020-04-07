/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.system.characteristic

import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATT
import static edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicCoordinate
import edu.ie3.datamodel.models.input.system.characteristic.EvCharacteristicInput
import spock.lang.Shared
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Power

class EvCharacteristicTest extends Specification {
	@Shared
	EvCharacteristicInput validInput

	def setupSpec() {
		SortedSet<CharacteristicCoordinate<Power, Dimensionless>> coordinates = [
			new CharacteristicCoordinate<Power, Dimensionless>(Quantities.getQuantity(10, KILOWATT),
			Quantities.getQuantity(0.05, PU)),
			new CharacteristicCoordinate<Power, Dimensionless>(Quantities.getQuantity(15, KILOWATT),
			Quantities.getQuantity(0.10, PU)),
			new CharacteristicCoordinate<Power, Dimensionless>(Quantities.getQuantity(20, KILOWATT),
			Quantities.getQuantity(0.20, PU))
		] as SortedSet

		validInput = new EvCharacteristicInput(coordinates)
	}

	def "A EvCharacteristicInput is correctly de-serialized"() {
		when:
		String actual = validInput.deSerialize()

		then:
		actual == "ev:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}"
	}

	def "A EvCharacteristicInput is correctly set up from a correctly formatted string"() {
		when:
		EvCharacteristicInput actual = new EvCharacteristicInput("ev:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}")

		then:
		actual.coordinates == validInput.coordinates
	}

	def "A EvCharacteristicInput throws an exception if it should be set up from a malformed string"() {
		when:
		new EvCharacteristicInput("ev:{(10.00),(15.00),(20.00)}")

		then:
		IllegalArgumentException exception = thrown(IllegalArgumentException)
		exception.message == "The given input 'ev:{(10.00),(15.00),(20.00)}' is not a valid representation."
	}
}
