/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.system.characteristic

import static edu.ie3.util.quantities.PowerSystemUnits.METRE_PER_SECOND
import static edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.datamodel.exceptions.ParsingException
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicCoordinate
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput
import spock.lang.Shared
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Speed

class OlmCharacteristicTest extends Specification {
	@Shared
	OlmCharacteristicInput validInput

	def setupSpec() {
		SortedSet<CharacteristicCoordinate<Speed, Dimensionless>> coordinates = [
			new CharacteristicCoordinate<Speed, Dimensionless>(Quantities.getQuantity(10, METRE_PER_SECOND),
			Quantities.getQuantity(0.05, PU)),
			new CharacteristicCoordinate<Speed, Dimensionless>(Quantities.getQuantity(15, METRE_PER_SECOND),
			Quantities.getQuantity(0.10, PU)),
			new CharacteristicCoordinate<Speed, Dimensionless>(Quantities.getQuantity(20, METRE_PER_SECOND),
			Quantities.getQuantity(0.20, PU))
		] as SortedSet

		validInput = new OlmCharacteristicInput(coordinates)
	}

	def "A OlmCharacteristicInput is correctly de-serialized"() {
		when:
		String actual = validInput.deSerialize()

		then:
		actual == "olm:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}"
	}

	def "A OlmCharacteristicInput is correctly set up from a correctly formatted string"() {
		when:
		OlmCharacteristicInput actual = new OlmCharacteristicInput("olm:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}")

		then:
		actual.coordinates == validInput.coordinates
	}

	def "A OlmCharacteristicInput throws an exception if it should be set up from a malformed string"() {
		when:
		new OlmCharacteristicInput("olm:{(10.00),(15.00),(20.00)}")

		then:
		ParsingException exception = thrown(ParsingException)
		exception.message == "Cannot parse '(10.00),(15.00),(20.00)' to Set of coordinates as it contains a malformed coordinate."
	}
}
