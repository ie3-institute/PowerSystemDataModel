/*
 * © 2020. TU Dortmund University,
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

class CosmoIdCoordinateFactoryTest extends Specification {
	@Shared
	CosmoIdCoordinateFactory factory

	def setupSpec() {
		factory = new CosmoIdCoordinateFactory()
	}

	def "A COSMO id to coordinate factory returns correct fields"() {
		given:
		def expectedFields = [
			"tid",
			"id",
			"latrot",
			"longrot",
			"latgeo",
			"longgeo"] as Set
		def validSimpleFactoryData = new SimpleFactoryData([
			"tid": "1",
			"id": "106580",
			"latgeo": "39.602772",
			"longgeo": "1.279336",
			"latrot": "-10",
			"longrot": "-6.8125"
		] as Map<String, String>, Pair.class)


		when:
		def actual = factory.getFields(validSimpleFactoryData)

		then:
		actual.size() == 1
		actual.head() == expectedFields
	}

	def "A COSMO id to coordinate factory refuses to build from invalid data"() {
		given:
		def invalidSimpleFactoryData = new SimpleFactoryData([
			"tid": "1",
			"id": "106580",
			"latrot": "-10",
			"longrot": "-6.8125"
		] as Map<String, String>, Pair.class)

		when:
		factory.get(invalidSimpleFactoryData)

		then:
		def e = thrown(FactoryException)
		e.message.startsWith("The provided fields [id, latrot, longrot, tid] with data \n{id -> 106580,\nlatrot" +
				" -> -10,\nlongrot -> -6.8125,\ntid -> 1} are invalid for instance of Pair.")
	}

	def "A COSMO id to coordinate factory builds model from valid data"() {
		given:
		def validSimpleFactoryData = new SimpleFactoryData([
			"tid": "1",
			"id": "106580",
			"latgeo": "39.602772",
			"longgeo": "1.279336",
			"latrot": "-10",
			"longrot": "-6.8125"
		] as Map<String, String>, Pair.class)
		Pair<Integer, Point> expectedPair = Pair.of(106580, GeoUtils.xyToPoint(1.279336, 39.602772))

		when:
		def actual = factory.get(validSimpleFactoryData)

		then:
		actual.isPresent()
		actual.get().with {
			assert it.key == expectedPair.key
			assert it.value.equalsExact(expectedPair.value, 1E-6)
		}
	}
}
