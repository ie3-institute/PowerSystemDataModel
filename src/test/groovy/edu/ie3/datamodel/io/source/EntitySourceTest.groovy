/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.datamodel.io.source.EntitySource.extractEntity
import static edu.ie3.datamodel.io.source.EntitySource.extractFunction
import static edu.ie3.datamodel.io.source.EntitySource.getEntities
import static edu.ie3.datamodel.io.source.EntitySource.toMap
import static edu.ie3.test.helper.EntityMap.map

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import spock.lang.Specification

class EntitySourceTest extends Specification {

  def "An EntitySource can build a map of entities correctly"() {
    given:
    Map<String, String> parameter = ["uuid": GridTestData.profBroccoli.uuid.toString(), "id": GridTestData.profBroccoli.id]
    def source = DummyDataSource.of(parameter)

    when:
    def actual = getEntities(OperatorInput, source, TypeSource.operatorBuildFunction).collect(toMap())

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
    getEntities(OperatorInput, source, TypeSource.operatorBuildFunction)

    then:
    SourceException ex = thrown()
    ex.message == "1 exception(s) occurred within \"OperatorInput\" data: \n" +
        "        Field \"id\" not found in EntityData"
  }

  def "An EntitySource can extract an Entity from a map correctly if a field name is given"() {
    given:
    def entityData = new EntityData(["operator": GridTestData.profBroccoli.uuid.toString()], NodeInput)
    def entityMap = map([GridTestData.profBroccoli])

    when:
    def actual = extractFunction(entityData, "operator", entityMap)

    then:
    actual == GridTestData.profBroccoli
  }

  def "An EntitySource returns a failure if an entity can not be extracted from a given map"() {
    given:
    def entityData = new EntityData(fieldsToAttributes, NodeInput)

    when:
    def actual = extractEntity(entityData, "operator", entityMap)

    then:
    actual.failure
    actual.exception.get().class == SourceException
    actual.exception.get().message == expectedMessage

    where:
    fieldsToAttributes                                      | entityMap                                 | expectedMessage
    ["operator": "no uuid"]                                 | map([
      OperatorInput.NO_OPERATOR_ASSIGNED
    ]) | "Extracting UUID for field 'operator' failed. Caused by: Exception while trying to parse UUID of field \"operator\" with value \"no uuid\""
    ["operator": GridTestData.profBroccoli.uuid.toString()] | map([
      OperatorInput.NO_OPERATOR_ASSIGNED
    ]) | "Extracting UUID for field 'operator' failed. Caused by: Entity with uuid f15105c4-a2de-4ab8-a621-4bc98e372d92 was not provided."
  }

  def "An EntitySource returns a failure if a given map does not contain the given uuid"() {
    given:
    def uuid = GridTestData.profBroccoli.uuid
    def entityMap = map([
      OperatorInput.NO_OPERATOR_ASSIGNED
    ])

    when:
    def actual = extractEntity(uuid, entityMap)

    then:
    actual.failure
    actual.exception.get().message == "Entity with uuid f15105c4-a2de-4ab8-a621-4bc98e372d92 was not provided."
  }
}
