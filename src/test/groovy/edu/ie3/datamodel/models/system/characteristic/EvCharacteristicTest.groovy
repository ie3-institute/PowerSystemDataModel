/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.system.characteristic

import edu.ie3.datamodel.exceptions.ParsingException
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.models.input.system.characteristic.EvCharacteristicInput
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Power

import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATT
import static edu.ie3.util.quantities.PowerSystemUnits.PU

class EvCharacteristicTest extends Specification {
	@Shared
	EvCharacteristicInput validInput

	def setupSpec() {
		SortedSet<CharacteristicPoint<Power, Dimensionless>> points = [
			new CharacteristicPoint<Power, Dimensionless>(Quantities.getQuantity(10, KILOWATT),
			Quantities.getQuantity(0.05, PU)),
			new CharacteristicPoint<Power, Dimensionless>(Quantities.getQuantity(15, KILOWATT),
			Quantities.getQuantity(0.10, PU)),
			new CharacteristicPoint<Power, Dimensionless>(Quantities.getQuantity(20, KILOWATT),
			Quantities.getQuantity(0.20, PU))
		] as SortedSet

		validInput = new EvCharacteristicInput(points)
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
		actual.points == validInput.points
	}

	def "A EvCharacteristicInput throws an exception if it should be set up from a malformed string"() {
		when:
		new EvCharacteristicInput("ev:{(10.00),(15.00),(20.00)}")

		then:
		ParsingException exception = thrown(ParsingException)
		exception.message == "Cannot parse '(10.00),(15.00),(20.00)' to Set of points as it contains a malformed point."
	}
}
