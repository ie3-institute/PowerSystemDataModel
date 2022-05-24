/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.SimpleFactoryData
import edu.ie3.util.geo.GeoUtils
import org.apache.commons.lang3.tuple.Pair
import org.locationtech.jts.geom.Point
import spock.lang.Shared
import spock.lang.Specification

class IconIdCoordinateFactoryTest extends Specification {
	@Shared
	IconIdCoordinateFactory factory

	def setupSpec() {
		factory = new IconIdCoordinateFactory()
	}

	def "A COSMO id to coordinate factory returns correct fields"() {
		given:
		def expectedFields = [
			"id",
			"latitude",
			"longitude",
			"coordinatetype"
		] as Set
		def validSimpleFactoryData = new SimpleFactoryData([
			"id":"477295",
			"latitude":"52.312",
			"longitude":"12.812",
			"coordinatetype":"ICON"] as Map<String, String>, Pair)

		when:
		def actual = factory.getFields(validSimpleFactoryData)

		then:
		actual.size() == 1
		actual.head() == expectedFields
	}

	def "A COSMO id to coordinate factory refuses to build from invalid data"() {
		given:
		def invalidSimpleFactoryData = new SimpleFactoryData([
			"id":"477295",
			"latitude":"52.312",
			"coordinatetype":"ICON"] as Map<String, String>, Pair)

		when:
		factory.get(invalidSimpleFactoryData)

		then:
		def e = thrown(FactoryException)
		e.message.startsWith("The provided fields [coordinatetype, id, latitude] with data \n{coordinatetype -> " +
				"ICON,\nid -> 477295,\nlatitude -> 52.312} are invalid for instance of Pair. ")
	}

	def "A COSMO id to coordinate factory builds model from valid data"() {
		given:
		def validSimpleFactoryData = new SimpleFactoryData([
			"id":"477295",
			"latitude":"52.312",
			"longitude":"12.812",
			"coordinatetype":"ICON"] as Map<String, String>, Pair)
		Pair<Integer, Point> expectedPair = Pair.of(477295, GeoUtils.buildPoint(52.312, 12.812))

		when:
		def actual = factory.get(validSimpleFactoryData)

		then:
		actual.present
		actual.get().with {
			assert it.key == expectedPair.key
			assert it.value.equalsExact(expectedPair.value, 1E-6)
		}
	}
}
