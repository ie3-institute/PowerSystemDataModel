/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.exceptions.InvalidColumnNameException
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
      "longgeo"
    ] as Set

    Map<String, String> parameter = [
      "tid": "1",
      "id": "106580",
      "latgeo": "39.602772",
      "longgeo": "1.279336",
      "latrot": "-10",
      "longrot": "-6.8125"
    ]


    def validSimpleFactoryData = new SimpleFactoryData(parameter, Pair)


    when:
    def actual = factory.getFields(validSimpleFactoryData)

    then:
    actual.size() == 1
    actual.head() == expectedFields
  }

  def "A COSMO id to coordinate factory refuses to build from invalid data"() {
    given:
    Map<String, String> parameter  =  [
      "tid": "1",
      "id": "106580",
      "latrot": "-10",
      "longrot": "-6.8125"
    ]

    def invalidSimpleFactoryData = new SimpleFactoryData(parameter, Pair)

    when:
    def actual = factory.get(invalidSimpleFactoryData)

    then:
    actual.failure
    actual.exception.get().cause.message.startsWith("The provided fields [id, latrot, longrot, tid] with data \n{id -> 106580,\nlatrot" +
        " -> -10,\nlongrot -> -6.8125,\ntid -> 1} are invalid for instance of Pair.")
  }

  def "A COSMO id to coordinate factory builds model from valid data"() {
    given:
    Map<String, String> parameter = [
      "tid": "1",
      "id": "106580",
      "latgeo": "39.602772",
      "longgeo": "1.279336",
      "latrot": "-10",
      "longrot": "-6.8125"
    ]

    def validSimpleFactoryData = new SimpleFactoryData(parameter, Pair)
    Pair<Integer, Point> expectedPair = Pair.of(106580, GeoUtils.buildPoint(39.602772, 1.279336))

    when:
    def actual = factory.get(validSimpleFactoryData)

    then:
    actual.success
    actual.data.get().with {
      assert it.key == expectedPair.key
      assert it.value.equalsExact(expectedPair.value, 1E-6)
    }
  }

  def "Throws an exception if an invalid column name is given" () {
    given:
    def invalidColumnNames = ["id", "lat", "lon"] as Set<String>
    when:
    factory.checkForInvalidColumnNames(invalidColumnNames)

    then:
    Exception ex = thrown()
    ex.class == InvalidColumnNameException
    ex.message == "The provided column names [id, lat, lon] does not match the expected column names [tid, id, long_geo, lat_geo, long_rot, lat_rot]!"
  }
}
