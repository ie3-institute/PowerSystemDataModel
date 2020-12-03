/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import static edu.ie3.util.quantities.PowerSystemUnits.*

import edu.ie3.test.common.GridTestData
import edu.ie3.util.geo.GeoUtils
import edu.ie3.util.quantities.PowerSystemUnits
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.LineString
import spock.lang.Specification
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Length

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

	def "TotalLengthOfLineString correctly calculates the total length of lineString correctly"() {
		given:
		LineString lineString = GeoUtils.DEFAULT_GEOMETRY_FACTORY.createLineString([
			new Coordinate(22.69962d,11.13038d,0),
			new Coordinate(20.84247d,28.14743d,0),
			new Coordinate(24.21942d,12.04265d,0)] as Coordinate[])

		when:
		ComparableQuantity<Length> y = GridAndGeoUtils.TotalLengthOfLineString(lineString)

		then:
		y.isGreaterThanOrEqualTo(Quantities.getQuantity(3463.37-10, KILOMETRE))
		y.isLessThanOrEqualTo(Quantities.getQuantity(3463.37+10, KILOMETRE))
		// Value from Google Maps, error range of +-10
	}

	def "TotalLengthOfLineString correctly calculates the total length of lineString correctly2"() {
		given:
		LineString lineString = GeoUtils.DEFAULT_GEOMETRY_FACTORY.createLineString([
			new Coordinate(51.48386110716543, 7.498165075275441,0),
			new Coordinate(3.4884439989616043, 137.87593281832065,0)] as Coordinate[])

		when:
		ComparableQuantity<Length> y = GridAndGeoUtils.TotalLengthOfLineString(lineString)

		then:
		System.out.println(y)
		y.isGreaterThanOrEqualTo(Quantities.getQuantity(12323.99-15, KILOMETRE))
		y.isLessThanOrEqualTo(Quantities.getQuantity(12323.99+15, KILOMETRE))
		// Value from Google Maps, error range of +-15
		// TODO NSteffan: Let check -> I use calcHaversine with (X1, Y1, X2, Y2), in other places it is used with (Y1, X1, Y2, X2)
		//  -> what is latitude, what is longitude?
		//  Luftlinie.org says my version is right, but with small error
		//  https://www.luftlinie.org/51.48386110716543,%207.498165075275441/3.4884439989616043,137.87593281832065
		//  Luftlinie.org says 12.323.99 km
		//  with (Y1, X1, Y2, X2) it says 12.675.25 -> wrong
		//  with (X1, Y1, X2, Xy2) it says 12.333.92 -> right
	}
}
