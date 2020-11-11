/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.utils.GridAndGeoUtils
import edu.ie3.test.common.GridTestData
import edu.ie3.util.TimeTools
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Coordinate
import spock.lang.Specification
import tech.units.indriya.ComparableQuantity

import javax.measure.quantity.Length
import java.time.ZoneId

class ConnectorValidationUtilsTest extends Specification {

	// TODO NSteffan: Where does this test belong?
	def "Util method calculateTotalLengthOfLineString in GridAndGeoUtils calculates total line length correctly"() {
		given:
		def line = GridTestData.lineAtoB
		def a = GridTestData.nodeA
		def b = GridTestData.nodeB.copy().geoPosition(GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(new Coordinate(7.414116, 51.484136))).build()
		// GridTestData is not correct here

		when:
		ComparableQuantity<Length> y = GridAndGeoUtils.calculateTotalLengthOfLineString(line.geoPosition)

		then:
		y == GridAndGeoUtils.distanceBetweenNodes(a, b)
		System.out.println(y)
		System.out.println(GridAndGeoUtils.distanceBetweenNodes(a, b))
		System.out.println(line.getLength()) // GridTestData incorrect
	}

	def "The check method in ValidationUtils delegates the check to ConnectorValidationUtils for a connector"() {
		given:
		def line = GridTestData.lineCtoD

		when:
		ValidationUtils.check(line)

		then:
		0 * ConnectorValidationUtils.check(line)
		// TODO NSteffan: Why is the method invoked 0 times?
	}
}