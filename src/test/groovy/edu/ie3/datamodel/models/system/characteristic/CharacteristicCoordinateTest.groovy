/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.system.characteristic

import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATT
import static edu.ie3.util.quantities.PowerSystemUnits.PERCENT

import java.util.regex.Pattern

import java.util.regex.Matcher

import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicCoordinate
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Power

class CharacteristicCoordinateTest extends Specification {
	def "A set of CharacteristicCoordinates are sorted correctly"() {
		given: "A set of coordinates"
		CharacteristicCoordinate<Power, Dimensionless> a =
				new CharacteristicCoordinate<Power, Dimensionless>(
				Quantities.getQuantity(1d, KILOWATT),
				Quantities.getQuantity(1d, PERCENT))
		CharacteristicCoordinate<Power, Dimensionless> b =
				new CharacteristicCoordinate<Power, Dimensionless>(
				Quantities.getQuantity(2d, KILOWATT),
				Quantities.getQuantity(2d, PERCENT))
		CharacteristicCoordinate<Power, Dimensionless> c =
				new CharacteristicCoordinate<Power, Dimensionless>(
				Quantities.getQuantity(3d, KILOWATT),
				Quantities.getQuantity(3d, PERCENT))
		CharacteristicCoordinate<Power, Dimensionless> d =
				new CharacteristicCoordinate<Power, Dimensionless>(
				Quantities.getQuantity(3d, KILOWATT),
				Quantities.getQuantity(4d, PERCENT))
		CharacteristicCoordinate<Power, Dimensionless> e =
				new CharacteristicCoordinate<Power, Dimensionless>(
				Quantities.getQuantity(5d, KILOWATT),
				Quantities.getQuantity(5d, PERCENT))

		and: "an expected order"
		LinkedList<CharacteristicCoordinate<Power, Dimensionless>> expected = [a, b, c, d, e] as Queue

		when: "the coordinates are put to sorted set randomly"
		SortedSet<CharacteristicCoordinate<Power, Dimensionless>> actual =  [d, c, a, e, b] as SortedSet

		then: "they appear in the correct order"
		actual.size() == expected.size()
		Iterator<CharacteristicCoordinate<Power, Dimensionless>> expectedIterator = expected.iterator()
		Iterator<CharacteristicCoordinate<Power, Dimensionless>> actualIterator = actual.iterator()
		while (expectedIterator.hasNext()) {
			CharacteristicCoordinate<Power, Dimensionless> expectedCoordinate = expectedIterator.next()
			CharacteristicCoordinate<Power, Dimensionless> actualCoordinate = actualIterator.next()
			assert expectedCoordinate == actualCoordinate
		}
	}

	def "The CharacteristicCoordinate has the correct pattern to recognize a pair of double values"() {
		given: "An expected Pattern"
		String expected = "\\(([+-]?\\d+\\.?\\d*),([+-]?\\d+\\.?\\d*)\\)"

		when: "getting the actual pattern"
		Pattern actual = CharacteristicCoordinate.MATCHING_PATTERN

		then: "it has the same content"
		actual.pattern == expected
	}

	def "An CharacteristicCoordinate is de-serialized correctly"() {
		given: "A coordinate"
		CharacteristicCoordinate<Power, Dimensionless> coordinate =
				new CharacteristicCoordinate<Power, Dimensionless>(
				Quantities.getQuantity(3d, KILOWATT),
				Quantities.getQuantity(4d, PERCENT))

		when: "de-serialized"
		String twoPlaces = coordinate.deSerialize(2)
		String noPlace = coordinate.deSerialize(0)

		then: "the result is correct"
		twoPlaces == "(3.00,4.00)"
		noPlace == "(3,4)"
	}

	def "The CharacteristicCoordinate has a pattern, that matches it's own de-serialized output"() {
		given: "The matchers"
		Matcher firstMatcher = CharacteristicCoordinate.MATCHING_PATTERN.matcher("(3.00,4.00)")
		Matcher secondMatcher = CharacteristicCoordinate.MATCHING_PATTERN.matcher("(3,4)")

		expect: "that they recognize correct output correctly"
		assert firstMatcher.matches()
		assert secondMatcher.matches()
	}

	def "The CharacteristicCoordinate has a pattern, that recognizes wrong formatted strings"() {
		given: "The matchers"
		Matcher firstMatcher = CharacteristicCoordinate.MATCHING_PATTERN.matcher("(3.00, 4.00)")
		Matcher secondMatcher = CharacteristicCoordinate.MATCHING_PATTERN.matcher("(3,a)")

		expect: "that recognize correct output correctly"
		assert !firstMatcher.matches()
		assert !secondMatcher.matches()
	}

	def "The CharacteristicCoordinate is able to correctly extract the abscissa value from properly formatted string"() {
		expect:
		CharacteristicCoordinate.getXFromString("(3.00,4.00)") - 3.0 < 1E-12
		CharacteristicCoordinate.getXFromString("(3,4)") - 3.0 < 1E-12
	}

	def "The CharacteristicCoordinate throws an exception, when the String is malformed on extraction of abscissa value"() {
		when:
		CharacteristicCoordinate.getXFromString("3.0")

		then:
		IllegalArgumentException exception = thrown(IllegalArgumentException)
		exception.message == "The given input '3.0' is not a valid representation of a CharacteristicCoordinate."
	}

	def "The CharacteristicCoordinate is able to correctly extract the ordinate value from properly formatted string"() {
		expect:
		CharacteristicCoordinate.getYFromString("(3.00,4.00)") - 4.0 < 1E-12
		CharacteristicCoordinate.getYFromString("(3,4)") - 4.0 < 1E-12
	}

	def "The CharacteristicCoordinate throws an exception, when the String is malformed on extraction of ordinate value"() {
		when:
		CharacteristicCoordinate.getYFromString("3.0")

		then:
		IllegalArgumentException exception = thrown(IllegalArgumentException)
		exception.message == "The given input '3.0' is not a valid representation of a CharacteristicCoordinate."
	}
}
