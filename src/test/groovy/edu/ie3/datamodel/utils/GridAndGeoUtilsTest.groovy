/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import edu.ie3.util.geo.GeoUtils
import edu.ie3.util.quantities.QuantityUtil
import spock.lang.Shared

import static edu.ie3.util.quantities.PowerSystemUnits.*

import edu.ie3.test.common.GridTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class GridAndGeoUtilsTest extends Specification {
	@Shared
	double testingTolerance = 1E-12

	def "Grid and geo utils refuse instantiation"() {
		when:
		new GridAndGeoUtils()

		then:
		def e = thrown(IllegalStateException)
		e.message == "Utility classes cannot be instantiated"
	}

	def "The grid and geo utils should calculate distance between two nodes correctly"() {
		given:
		def nodeA = GridTestData.nodeA
		def nodeB = GridTestData.nodeB
		def expectedDistance = Quantities.getQuantity(0.91356787076109815268517, KILOMETRE)

		when:
		def actualDistance = GridAndGeoUtils.distanceBetweenNodes(nodeA, nodeB)

		then:
		QuantityUtil.isEquivalentAbs(expectedDistance, actualDistance, testingTolerance)
	}

	def "The grid and geo utils build a correct line string between nodes with disjoint coordinates"() {
		given:
		def expectedLineString = GeoUtils.buildSafeLineStringBetweenPoints(GridTestData.nodeA.geoPosition, GridTestData.nodeB.geoPosition)

		when:
		def actualLineString = GridAndGeoUtils.buildSafeLineStringBetweenNodes(GridTestData.nodeA, GridTestData.nodeB)

		then:
		actualLineString == expectedLineString
	}

	def "The grid and geo utils build a correct line string between nodes with same coordinates"() {
		given:
		def expectedLineString = GeoUtils.buildSafeLineStringBetweenPoints(GridTestData.nodeA.geoPosition, GridTestData.nodeA.geoPosition)

		when:
		def actualLineString = GridAndGeoUtils.buildSafeLineStringBetweenNodes(GridTestData.nodeA, GridTestData.nodeA)

		then:
		actualLineString == expectedLineString
	}
}
