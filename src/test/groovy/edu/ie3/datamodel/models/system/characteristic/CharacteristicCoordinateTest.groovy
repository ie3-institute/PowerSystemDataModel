/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.system.characteristic

import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATT
import static edu.ie3.util.quantities.PowerSystemUnits.PERCENT

import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicCoordinate
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicInput
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Power

class CharacteristicCoordinateTest extends Specification {
	def "A set of CharacteristicCoordinates are sorted correctly"() {
		given: "A set of coordinates"
		CharacteristicCoordinate<Power, Dimensionless> a = new CharacteristicCoordinate<Power, Dimensionless>(Quantities.getQuantity(1d, KILOWATT), Quantities.getQuantity(1d, PERCENT))
		CharacteristicCoordinate<Power, Dimensionless> b = new CharacteristicCoordinate<Power, Dimensionless>(Quantities.getQuantity(2d, KILOWATT), Quantities.getQuantity(2d, PERCENT))
		CharacteristicCoordinate<Power, Dimensionless> c = new CharacteristicCoordinate<Power, Dimensionless>(Quantities.getQuantity(3d, KILOWATT), Quantities.getQuantity(3d, PERCENT))
		CharacteristicCoordinate<Power, Dimensionless> d = new CharacteristicCoordinate<Power, Dimensionless>(Quantities.getQuantity(3d, KILOWATT), Quantities.getQuantity(4d, PERCENT))
		CharacteristicCoordinate<Power, Dimensionless> e = new CharacteristicCoordinate<Power, Dimensionless>(Quantities.getQuantity(5d, KILOWATT), Quantities.getQuantity(5d, PERCENT))

		and: "an expected order"
		LinkedList<CharacteristicCoordinate<Power, Dimensionless>> expected = new LinkedList<>()
		expected.add(a)
		expected.add(b)
		expected.add(c)
		expected.add(d)
		expected.add(e)

		when: "the coordinates are put to sorted set randomly"
		SortedSet<CharacteristicCoordinate<Power, Dimensionless>> actual = new TreeSet<>()
		actual.add(d)
		actual.add(c)
		actual.add(a)
		actual.add(e)
		actual.add(b)

		then: "they appear in the correct order"
		actual.size() == expected.size()
		Iterator<CharacteristicCoordinate<Power, Dimensionless>> expectedIterator = expected.iterator()
		Iterator<CharacteristicCoordinate<Power, Dimensionless>> actualIterator = actual.iterator()
		while(expectedIterator.hasNext()) {
			CharacteristicCoordinate<Power, Dimensionless> expectedCoordinate = expectedIterator.next()
			CharacteristicCoordinate<Power, Dimensionless> actualCoordinate = actualIterator.next()
			assert expectedCoordinate == actualCoordinate
		}
	}
}
