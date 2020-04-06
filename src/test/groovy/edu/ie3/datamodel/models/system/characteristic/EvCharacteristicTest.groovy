/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.system.characteristic

import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicCoordinate
import edu.ie3.datamodel.models.input.system.characteristic.EvCharacteristicInput
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput
import spock.lang.Shared
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Power

import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATT
import static edu.ie3.util.quantities.PowerSystemUnits.PU

class EvCharacteristicTest extends Specification {
	@Shared
	EvCharacteristicInput validInput

	def setupSpec() {
		SortedSet<CharacteristicCoordinate<Power, Dimensionless>> coordinates = new TreeSet<>()
		coordinates.add(
				new CharacteristicCoordinate<Power, Dimensionless>(Quantities.getQuantity(10, KILOWATT),
				Quantities.getQuantity(0.05, PU)))
		coordinates.add(
				new CharacteristicCoordinate<Power, Dimensionless>(Quantities.getQuantity(15, KILOWATT),
				Quantities.getQuantity(0.10, PU)))
		coordinates.add(
				new CharacteristicCoordinate<Power, Dimensionless>(Quantities.getQuantity(20, KILOWATT),
				Quantities.getQuantity(0.20, PU)))

		validInput = new EvCharacteristicInput(
				UUID.fromString("591db0f4-b1dc-4cd5-9a7f-9e6ed0f064b2"),
				coordinates
				)
	}

	def "A EvCharacteristicInput is correctly de-serialized"() {
		when:
		String actual = validInput.deSerialize()

		then:
		actual == "ev:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}"
	}

	def "A EvCharacteristicInput is correctly set up from a correctly formatted string"() {
		when:
		EvCharacteristicInput actual = new EvCharacteristicInput(UUID.fromString("6979beaa-fceb-4115-981f-b91154a34512"), "ev:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}")

		then:
		actual.getUuid() == UUID.fromString("6979beaa-fceb-4115-981f-b91154a34512")
		actual.getCoordinates() == validInput.getCoordinates()
	}
}
