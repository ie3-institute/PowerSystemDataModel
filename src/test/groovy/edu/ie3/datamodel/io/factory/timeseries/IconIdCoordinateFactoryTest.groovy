/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.SimpleFactoryData
import edu.ie3.datamodel.utils.Try
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
      "coordinateType"
    ] as Set

    when:
    def actual = factory.getFields(Pair)

    then:
    actual.size() == 1
    actual.head() == expectedFields
  }

  def "A COSMO id to coordinate factory refuses to build from invalid data"() {
    given:
    def actualFields = IconIdCoordinateFactory.newSet("id", "latitude", "coordinatetype")

    when:
    def actual = factory.validate(actualFields, Pair)

    then:
    actual.failure
    actual.exception.get().message == "The provided fields [coordinatetype, id, latitude] are invalid for instance of 'Pair'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'Pair' are possible (NOT case-sensitive!):\n" +
        "0: [coordinateType, id, latitude, longitude] or [coordinate_type, id, latitude, longitude]\n"
  }

  def "A COSMO id to coordinate factory builds model from valid data"() {
    given:
    Map<String, String> parameter = [
      "id":"477295",
      "latitude":"52.312",
      "longitude":"12.812",
      "coordinateType":"ICON"]
    def validSimpleFactoryData = new SimpleFactoryData(parameter, Pair)
    Pair<Integer, Point> expectedPair = Pair.of(477295, GeoUtils.buildPoint(52.312, 12.812))

    when:
    def actual = factory.get(validSimpleFactoryData)

    then:
    actual.success
    actual.data.get().with {
      assert it.key == expectedPair.key
      assert it.value.equalsExact(expectedPair.value, 1E-6)
    }
  }
}
