/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.system.characteristic

import static edu.ie3.util.quantities.PowerSystemUnits.*

import edu.ie3.datamodel.exceptions.ParsingException
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Power

class CharacteristicPointTest extends Specification {
	def "A set of CharacteristicCoordinates are sorted correctly"() {
		given: "A set of points"
		CharacteristicPoint<Power, Dimensionless> a =
				new CharacteristicPoint<Power, Dimensionless>(
				Quantities.getQuantity(1d, KILOWATT),
				Quantities.getQuantity(1d, PERCENT))
		CharacteristicPoint<Power, Dimensionless> b =
				new CharacteristicPoint<Power, Dimensionless>(
				Quantities.getQuantity(2d, KILOWATT),
				Quantities.getQuantity(2d, PERCENT))
		CharacteristicPoint<Power, Dimensionless> c =
				new CharacteristicPoint<Power, Dimensionless>(
				Quantities.getQuantity(3d, KILOWATT),
				Quantities.getQuantity(3d, PERCENT))
		CharacteristicPoint<Power, Dimensionless> d =
				new CharacteristicPoint<Power, Dimensionless>(
				Quantities.getQuantity(3d, KILOWATT),
				Quantities.getQuantity(4d, PERCENT))
		CharacteristicPoint<Power, Dimensionless> e =
				new CharacteristicPoint<Power, Dimensionless>(
				Quantities.getQuantity(5d, KILOWATT),
				Quantities.getQuantity(5d, PERCENT))

		and: "an expected order"
		LinkedList<CharacteristicPoint<Power, Dimensionless>> expected = [a, b, c, d, e] as Queue

		when: "the points are put to sorted set randomly"
		SortedSet<CharacteristicPoint<Power, Dimensionless>> actual =  [d, c, a, e, b] as SortedSet

		then: "they appear in the correct order"
		actual.size() == expected.size()
		Iterator<CharacteristicPoint<Power, Dimensionless>> expectedIterator = expected.iterator()
		Iterator<CharacteristicPoint<Power, Dimensionless>> actualIterator = actual.iterator()
		while (expectedIterator.hasNext()) {
			CharacteristicPoint<Power, Dimensionless> expectedCoordinate = expectedIterator.next()
			CharacteristicPoint<Power, Dimensionless> actualCoordinate = actualIterator.next()
			assert expectedCoordinate == actualCoordinate
		}
	}

	def "An CharacteristicCoordinate is de-serialized correctly"() {
		given: "A point"
		CharacteristicPoint<Power, Dimensionless> point =
				new CharacteristicPoint<Power, Dimensionless>(
				Quantities.getQuantity(3d, KILOWATT),
				Quantities.getQuantity(4d, PERCENT))

		when: "de-serialized"
		String twoPlaces = point.deSerialize(2)
		String noPlace = point.deSerialize(0)

		then: "the result is correct"
		twoPlaces == "(3.00,4.00)"
		noPlace == "(3,4)"
	}

	def "The CharacteristicCoordinate is able to parse a String to itself"(String input, double x, double y) {
		when: "Parsing the input"
		CharacteristicPoint<Dimensionless, Dimensionless> actual = new CharacteristicPoint<>(input, PU, PU)

		then: "it has correct values"
		actual.x.value.doubleValue() == x
		actual.y.value.doubleValue() == y

		where: "different inputs are tested"
		input 			|| x 	|| y
		"(3.00,2.00)"	|| 3.0 	|| 2.0
		"(3.00,2)" 		|| 3.0 	|| 2.0
		"(3,2.00)" 		|| 3.0 	|| 2.0
		"(3.00,-2.00)" 	|| 3.0 	|| -2.0
	}

	def "The CharacteristicCoordinate throws a parsing exception, if the input is malformed"() {
		when: "Parsing the input"
		new CharacteristicPoint<>("bla", PU, PU)

		then: "it throws an exception"
		ParsingException exception = thrown(ParsingException)
		exception.message == "Cannot parse bla to CharacteristicCoordinate. It doesn't comply with the required format '(%d,%d)'."
	}

	def "The CharacteristicCoordinate throws a parsing exception, if abscissa cannot be parsed to double"() {
		when: "Parsing the input"
		new CharacteristicPoint<>("(bla,2.0)", PU, PU)

		then: "it throws an exception"
		ParsingException exception = thrown(ParsingException)
		exception.message == "Cannot parse (bla,2.0) to CharacteristicCoordinate. Abscissa value cannot be parsed to double."
	}

	def "The CharacteristicCoordinate throws a parsing exception, if ordinate cannot be parsed to double"() {
		when: "Parsing the input"
		new CharacteristicPoint<>("(1.0,bla)", PU, PU)

		then: "it throws an exception"
		ParsingException exception = thrown(ParsingException)
		exception.message == "Cannot parse (1.0,bla) to CharacteristicCoordinate. Abscissa value cannot be parsed to double."
	}
}
