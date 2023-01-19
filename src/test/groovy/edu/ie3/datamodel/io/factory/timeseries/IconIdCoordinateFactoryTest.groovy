/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.FactoryData
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
    Map<String, String> parameter = [
      "id":"477295",
      "latitude":"52.312",
      "longitude":"12.812",
      "coordinatetype":"ICON"]

    def validSimpleFactoryData = new SimpleFactoryData(new FactoryData.MapWithRowIndex("-1", parameter), Pair)

    when:
    def actual = factory.getFields(validSimpleFactoryData)

    then:
    actual.size() == 1
    actual.head() == expectedFields
  }

  def "A COSMO id to coordinate factory refuses to build from invalid data"() {
    given:
    Map<String, String> parameter = [
      "id":"477295",
      "latitude":"52.312",
      "coordinatetype":"ICON"]

    def invalidSimpleFactoryData = new SimpleFactoryData(new FactoryData.MapWithRowIndex("-1", parameter), Pair)

    when:
    def actual = factory.get(invalidSimpleFactoryData)

    then:
    actual.failure
    actual.exception.cause.message.startsWith("The provided fields [coordinatetype, id, latitude] with data \n{coordinatetype -> " +
        "ICON,\nid -> 477295,\nlatitude -> 52.312} are invalid for instance of Pair. ")
  }

  def "A COSMO id to coordinate factory builds model from valid data"() {
    given:
    Map<String, String> parameter = [
      "id":"477295",
      "latitude":"52.312",
      "longitude":"12.812",
      "coordinatetype":"ICON"]
    def validSimpleFactoryData = new SimpleFactoryData(new FactoryData.MapWithRowIndex("-1", parameter), Pair)
    Pair<Integer, Point> expectedPair = Pair.of(477295, GeoUtils.buildPoint(52.312, 12.812))

    when:
    def actual = factory.get(validSimpleFactoryData)

    then:
    actual.success
    actual.data.with {
      assert it.key == expectedPair.key
      assert it.value.equalsExact(expectedPair.value, 1E-6)
    }
  }
}
