/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import edu.ie3.util.geo.GeoUtils
import spock.lang.Specification

class CoordinateDistanceTest extends Specification {

	def "The constructor without a distance parameter calculates the distance as expected" () {
		given:
		def pointA = GeoUtils.xyToPoint(49d, 7d)
		def pointB = GeoUtils.xyToPoint(50d, 7d)
		def expectedDistance = GeoUtils.calcHaversine(pointA.y, pointA.x, pointB.y, pointB.x)
		when:
		CoordinateDistance coordinateDistance = new CoordinateDistance(pointA, pointB)
		CoordinateDistance expectedCoordinateDistance = new CoordinateDistance(coordinateDistance.coordinateA, coordinateDistance.coordinateB, expectedDistance)
		then:
		coordinateDistance == expectedCoordinateDistance
	}

	def "CoordinateDistances are sortable using their distance field" () {
		given:
		def basePoint = GeoUtils.xyToPoint(49d, 7d)
		def distA = new CoordinateDistance(basePoint, GeoUtils.xyToPoint(50d, 7d))
		def distB = new CoordinateDistance(basePoint, GeoUtils.xyToPoint(50d, 7.1d))
		def distC = new CoordinateDistance(basePoint, GeoUtils.xyToPoint(49d, 7.1d))
		def distD = new CoordinateDistance(basePoint, GeoUtils.xyToPoint(52d, 9d))
		def coordinateDistances = [distA, distB, distC, distD]
		when:
		def sortedDistances = coordinateDistances.toSorted()
		then:
		sortedDistances == [distC, distA, distB, distD].toList()
	}
}
