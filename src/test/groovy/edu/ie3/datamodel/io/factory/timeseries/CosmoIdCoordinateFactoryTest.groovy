/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import edu.ie3.datamodel.io.factory.SimpleFactoryData
import edu.ie3.datamodel.models.input.IdCoordinateInput
import edu.ie3.util.geo.GeoUtils
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
      "latRot",
      "longRot",
      "latGeo",
      "longGeo"
    ] as Set

    when:
    def actual = factory.getFields(IdCoordinateInput)

    then:
    actual.size() == 1
    actual.head() == expectedFields
  }

  def "A COSMO id to coordinate factory refuses to build from invalid data"() {
    given:
    def actualFields = CosmoIdCoordinateFactory.newSet("tid", "id", "latrot", "longrot")

    when:
    def actual = factory.validate(actualFields, IdCoordinateInput)

    then:
    actual.failure
    actual.exception.get().message == "The provided fields [id, latrot, longrot, tid] are invalid for instance of 'IdCoordinateInput'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'IdCoordinateInput' are possible (NOT case-sensitive!):\n" +
        "0: [id, latGeo, latRot, longGeo, longRot, tid] or [id, lat_geo, lat_rot, long_geo, long_rot, tid]\n"
  }

  def "A COSMO id to coordinate factory builds model from valid data"() {
    given:
    Map<String, String> parameter = [
      "tid": "1",
      "id": "106580",
      "latGeo": "39.602772",
      "longGeo": "1.279336",
      "latRot": "-10",
      "longRot": "-6.8125"
    ]

    def validSimpleFactoryData = new SimpleFactoryData(parameter, IdCoordinateInput)
    IdCoordinateInput expectedIdCoordinate = new IdCoordinateInput(106580, GeoUtils.buildPoint(39.602772, 1.279336))

    when:
    def actual = factory.get(validSimpleFactoryData)

    then:
    actual.success
    actual.data.get().with {
      assert it.id() == expectedIdCoordinate.id()
      assert it.point().equalsExact(expectedIdCoordinate.point(), 1E-6)
    }
  }
}
