/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import edu.ie3.test.common.GridTestData
import edu.ie3.util.geo.GeoUtils
import edu.ie3.util.quantities.PowerSystemUnits
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.LineString
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.util.stream.Collector
import java.util.stream.Collectors

class GridAndGeoUtilsTest extends Specification {


	def "The GridAndGeoUtils should build a line string with two exact equal geo coordinates correctly avoiding the known bug in jts geometry"() {
		given:
		def line = GridTestData.geoJsonReader.read(lineString) as LineString

		expect:
		def safeLineString = GridAndGeoUtils.buildSafeLineString(line)
		safeLineString.getCoordinates() as List == coordinates

		where:
		lineString                                                                                                                            | coordinates
		"{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228]]}"                                           | [
			new Coordinate(7.4111110000001, 51.4922800000001),
			new Coordinate(7.411111, 51.49228)
		]
		"{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228]]}" | [
			new Coordinate(7.4111110000001, 51.4922800000001),
			new Coordinate(7.411111, 51.49228)
		]
		"{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.311111, 51.49228],[7.511111, 51.49228]]}" | [
			new Coordinate(7.311111, 51.49228),
			new Coordinate(7.411111, 51.49228),
			new Coordinate(7.511111, 51.49228)
		]
	}

	def "The GridAndGeoUtils should only modify a provided Coordinate as least as possible"() {
		given:
		def coord = new Coordinate(1, 1, 0)

		expect:
		GridAndGeoUtils.buildSafeCoord(coord) == new Coordinate(1.0000000000001, 1.0000000000001, 1.0E-13)
	}

	def "The GridAndGeoUtils should build a safe instance of a LineString between two provided coordinates correctly"() {

		expect:
		GridAndGeoUtils.buildSafeLineStringBetweenCoords(coordA, coordB) == resLineString
		// do not change or remove the following line, it is NOT equal to the line above in this case!
		GridAndGeoUtils.buildSafeLineStringBetweenCoords(coordA, coordB).equals(resLineString)

		where:
		coordA                  | coordB                  || resLineString
		new Coordinate(1, 1, 0) | new Coordinate(1, 1, 0) || GeoUtils.DEFAULT_GEOMETRY_FACTORY.createLineString([
			new Coordinate(1.0000000000001, 1.0000000000001, 1.0E-13),
			new Coordinate(1, 1, 0)] as Coordinate[])
		new Coordinate(1, 1, 0) | new Coordinate(2, 2, 0) || GeoUtils.DEFAULT_GEOMETRY_FACTORY.createLineString([
			new Coordinate(1, 1, 0),
			new Coordinate(2, 2, 0)] as Coordinate[])

	}

	def "The GridAndGeoUtils should calculate distance between two nodes correctly"() {

		given:
		def nodeA = GridTestData.nodeA
		def nodeB = GridTestData.nodeB

		expect:
		GridAndGeoUtils.distanceBetweenNodes(nodeA, nodeB) == Quantities.getQuantity(0.91356787076109815268517, PowerSystemUnits.KILOMETRE)
	}

	def "The GridAndGeoUtils should get the CoordinateDistances between a base point and a collection of other points correctly"() {
		given:
		def basePoint = GeoUtils.xyToPoint(49d, 7d)
		def points = [
			GeoUtils.xyToPoint(50d, 7d),
			GeoUtils.xyToPoint(50d, 7.1d),
			GeoUtils.xyToPoint(49d, 7.1d),
			GeoUtils.xyToPoint(52d, 9d)
		]
		def coordinateDistances = [
			new CoordinateDistance(basePoint, points[0]),
			new CoordinateDistance(basePoint, points[1]),
			new CoordinateDistance(basePoint, points[2]),
			new CoordinateDistance(basePoint, points[3])
		]
		expect:
		GridAndGeoUtils.getCoordinateDistances(basePoint, points) == new TreeSet(coordinateDistances)
	}
}
