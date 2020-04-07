/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.system.characteristic

import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATT
import static edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicCoordinate
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiP
import edu.ie3.datamodel.models.input.system.characteristic.QV
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import spock.lang.Shared
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Power

class ReactivePowerCharacteristicTest extends Specification {
	@Shared
	CosPhiFixed validCosPhiFixed

	@Shared
	String validCosPhiFixedDeSerialized = "cosPhiFixed:{(0.00,0.95)}"

	@Shared
	CosPhiP validCosPhiP

	@Shared
	String validCosPhiPDeSerialized = "cosPhiP:{(0.00,1.00),(0.90,1.00),(1.20,-0.30)}"

	@Shared
	QV validQV

	@Shared
	String validQVDeSerialized = "qV:{(0.90,-0.30),(0.95,0.00),(1.05,0.00),(1.10,0.30)}"

	def setupSpec() {
		validCosPhiFixed = new CosPhiFixed(
				[
					new CharacteristicCoordinate<Power, Dimensionless>(
					Quantities.getQuantity(0d, KILOWATT), Quantities.getQuantity(0.95, PU))
				] as SortedSet<CharacteristicCoordinate<Power, Dimensionless>>
				)

		validCosPhiP = new CosPhiP(
				[
					new CharacteristicCoordinate<Power, Dimensionless>(
					Quantities.getQuantity(0d, KILOWATT), Quantities.getQuantity(1.0, PU)),
					new CharacteristicCoordinate<Power, Dimensionless>(
					Quantities.getQuantity(0.9, KILOWATT), Quantities.getQuantity(1.0, PU)),
					new CharacteristicCoordinate<Power, Dimensionless>(
					Quantities.getQuantity(1.2, KILOWATT), Quantities.getQuantity(-0.3, PU))
				] as SortedSet<CharacteristicCoordinate<Power, Dimensionless>>
				)

		validQV = new QV(
				[
					new CharacteristicCoordinate<Dimensionless, Dimensionless>(
					Quantities.getQuantity(0.9, PU), Quantities.getQuantity(-0.3, PU)),
					new CharacteristicCoordinate<Dimensionless, Dimensionless>(
					Quantities.getQuantity(0.95, PU), Quantities.getQuantity(0.0, PU)),
					new CharacteristicCoordinate<Dimensionless, Dimensionless>(
					Quantities.getQuantity(1.05, PU), Quantities.getQuantity(0.0, PU)),
					new CharacteristicCoordinate<Dimensionless, Dimensionless>(
					Quantities.getQuantity(1.1, PU), Quantities.getQuantity(0.3, PU))
				] as SortedSet<CharacteristicCoordinate<Dimensionless, Dimensionless>>
				)
	}

	def "A valid CosPhiFixed is correctly de-serialized"() {
		when: "De-serializing a valid input"
		String actual = validCosPhiFixed.deSerialize()

		then: "it returns the correct string"
		actual == validCosPhiFixedDeSerialized
	}

	def "A CosPhiFixed is correctly set up from a correctly formatted string"() {
		when:
		CosPhiFixed actual = new CosPhiFixed(validCosPhiFixedDeSerialized)

		then:
		actual.coordinates == validCosPhiFixed.coordinates
	}

	def "A CosPhiFixed throws an exception if it should be set up from a malformed string"() {
		when:
		new CosPhiFixed("cosPhiFixed:{(10.00)}")

		then:
		IllegalArgumentException exception = thrown(IllegalArgumentException)
		exception.message == "The given input 'cosPhiFixed:{(10.00)}' is not a valid representation."
	}

	def "A valid CosPhiP is correctly de-serialized"() {
		when: "De-serializing a valid input"
		String actual = validCosPhiP.deSerialize()

		then: "it returns the correct string"
		actual == validCosPhiPDeSerialized
	}

	def "A CosPhiP is correctly set up from a correctly formatted string"() {
		when:
		CosPhiP actual = new CosPhiP(validCosPhiPDeSerialized)

		then:
		actual.coordinates == validCosPhiP.coordinates
	}

	def "A CosPhiP throws an exception if it should be set up from a malformed string"() {
		when:
		new CosPhiFixed("cosPhiP:{(0.00),(0.90),(1.20)}")

		then:
		IllegalArgumentException exception = thrown(IllegalArgumentException)
		exception.message == "The given input 'cosPhiP:{(0.00),(0.90),(1.20)}' is not a valid representation."
	}

	def "A valid QV is correctly de-serialized"() {
		when: "De-serializing a valid input"
		String actual = validQV.deSerialize()

		then: "it returns the correct string"
		actual == validQVDeSerialized
	}

	def "A QV is correctly set up from a correctly formatted string"() {
		when:
		QV actual = new QV(validQVDeSerialized)

		then:
		actual.coordinates == validQV.coordinates
	}

	def "A QV throws an exception if it should be set up from a malformed string"() {
		when:
		new CosPhiFixed("qV:{(0.90),(0.95),(1.05),(1.10)}")

		then:
		IllegalArgumentException exception = thrown(IllegalArgumentException)
		exception.message == "The given input 'qV:{(0.90),(0.95),(1.05),(1.10)}' is not a valid representation."
	}

	def "The ReactivePowerCharacteristic is able to parse a fixed power factor correctly from string"() {
		when:
		ReactivePowerCharacteristic actual = ReactivePowerCharacteristic.parse(validCosPhiFixedDeSerialized)

		then:
		actual instanceof CosPhiFixed
		actual.coordinates == validCosPhiFixed.coordinates
	}

	def "The ReactivePowerCharacteristic is able to parse a power dependent power factor correctly from string"() {
		when:
		ReactivePowerCharacteristic actual = ReactivePowerCharacteristic.parse(validCosPhiPDeSerialized)

		then:
		actual instanceof CosPhiP
		actual.coordinates == validCosPhiP.coordinates
	}

	def "The ReactivePowerCharacteristic is able to parse voltage dependent reactive power correctly from string"() {
		when:
		ReactivePowerCharacteristic actual = ReactivePowerCharacteristic.parse(validQVDeSerialized)

		then:
		actual instanceof QV
		actual.coordinates == validQV.coordinates
	}

	def "The ReactivePowerCharacteristic throws an Exception, if no valid characteristic format can be recognized"() {
		when:
		ReactivePowerCharacteristic.parse("nonSense:{bli,bla,blob}")

		then:
		IllegalArgumentException exception = thrown(IllegalArgumentException)
		exception.message == "Cannot parse 'nonSense:{bli,bla,blob}' to a reactive power characteristic, as it does " +
				"not meet the specifications of any of the available classes."
	}
}
