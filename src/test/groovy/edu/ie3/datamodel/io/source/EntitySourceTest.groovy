/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.datamodel.io.source.EntitySource.getBuildInSource
import static edu.ie3.datamodel.io.source.EntitySource.getEntities

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.factory.input.OperatorInputFactory
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.test.common.GridTestData
import spock.lang.Specification

import static edu.ie3.datamodel.io.source.EntitySource.getEntityMap

class EntitySourceTest extends Specification {

  def "An EntitySource validates required built-in resources"() {
    when:
    def source = getBuildInSource(EntitySource, "/type")

    then:
    source
  }

  def "An EntitySource can build a map of entities correctly"() {
    given:
    Map<String, String> parameter = ["uuid": GridTestData.profBroccoli.uuid.toString(), "id": GridTestData.profBroccoli.id]
    def source = DummyDataSource.of(parameter)

    when:
    def actual = getEntityMap(OperatorInput, source, new OperatorInputFactory())

    then:
    actual.size() == 1
    OperatorInput input = actual.get(GridTestData.profBroccoli.uuid)
    input.id == GridTestData.profBroccoli.id
  }

  def "An EntitySource throws a SourceException if an entity can not be build"() {
    given:
    Map<String, String> parameter = ["uuid": GridTestData.profBroccoli.uuid.toString()]
    def source = DummyDataSource.of(parameter)

    when:
    getEntities(OperatorInput, source, new OperatorInputFactory())

    then:
    SourceException ex = thrown()
    ex.message == "1 exception(s) occurred within \"OperatorInput\" data: \n" +
        "        An error occurred in OperatorInputFactory. Caused by: Field \"id\" not found in EntityData"
  }
}
