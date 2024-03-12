/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.datamodel.io.source.EntitySource.*
import static edu.ie3.test.helper.EntityMap.map

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData
import edu.ie3.datamodel.io.factory.input.ConnectorInputEntityData
import edu.ie3.datamodel.io.factory.input.OperatorInputFactory
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.datamodel.utils.validation.DummyAssetInput
import edu.ie3.test.common.GridTestData
import org.apache.commons.lang3.tuple.Pair
import spock.lang.Specification

class EntitySourceTest extends Specification {

  def "An EntitySource can build a map of entities correctly"() {
    given:
    Map<String, String> parameter = ["uuid": GridTestData.profBroccoli.uuid.toString(), "id": GridTestData.profBroccoli.id]
    def source = DummyDataSource.of(parameter)

    when:
    def actual = getEntities(OperatorInput, source, new OperatorInputFactory())

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
    ex.message == "edu.ie3.datamodel.exceptions.FailureException: 1 exception(s) occurred within \"OperatorInput\" data, one is: edu.ie3.datamodel.exceptions.FactoryException: An error occurred when creating instance of OperatorInput.class."
  }

  def "An EntitySource can build EntityData correctly"() {
    given:
    Map<String, String> parameter = ["operator": GridTestData.profBroccoli.uuid.toString()]
    def source = DummyDataSource.of(parameter)

    when:
    def actual = buildEntityData(DummyAssetInput, source).toList()

    then:
    actual.size() == 1
    actual.get(0).success
  }

  def "An EntitySource can enrich and build EntityData correctly"() {
    given:
    Map<String, String> parameter = ["operator": GridTestData.profBroccoli.uuid.toString()]
    def entityMap = map([GridTestData.profBroccoli])
    def source = DummyDataSource.of(parameter)
    def fcn = enrich("operator", entityMap, AssetInputEntityData::new)

    when:
    def actual = buildEntityData(DummyAssetInput, source, fcn).toList()

    then:
    actual.size() == 1
    actual.get(0).success
    def data = actual.get(0).data.get()

    data.targetClass == DummyAssetInput
    data.fieldsToValues.size() == 0
  }

  def "An EntitySource can enrich EntityData with default fallback"() {
    given:
    def entityMap = map([GridTestData.profBroccoli])
    def entityData1 = new EntityData(["operator": GridTestData.profBroccoli.uuid.toString()], NodeInput)
    def entityData2 = new EntityData(["operator": ""], NodeInput)
    def fcn = enrichWithDefault("operator", entityMap, OperatorInput.NO_OPERATOR_ASSIGNED, AssetInputEntityData::new)

    when:
    def enrichedWithEntity = fcn.apply(new Try.Success<>(entityData1))
    def enrichedWithDefault = fcn.apply(new Try.Success<>(entityData2))

    then:
    enrichedWithEntity.success
    enrichedWithEntity.data.get().operatorInput == GridTestData.profBroccoli

    enrichedWithDefault.success
    enrichedWithDefault.data.get().operatorInput == OperatorInput.NO_OPERATOR_ASSIGNED
  }

  def "An EntitySource can enrich EntityData"() {
    given:
    def entityMap = map([GridTestData.profBroccoli])
    def entityData = new EntityData(["operator": GridTestData.profBroccoli.uuid.toString()], NodeInput)
    def fcn = enrich("operator", entityMap, AssetInputEntityData::new)

    when:
    def enrichedData = fcn.apply(new Try.Success<>(entityData))

    then:
    enrichedData.success
    enrichedData.data.get().operatorInput == GridTestData.profBroccoli
  }

  def "An EntitySource can enrich EntityData with two entities"() {
    given:
    def entityMap = map([GridTestData.nodeA, GridTestData.nodeB])
    def entityData = new AssetInputEntityData(["nodeA": GridTestData.nodeA.uuid.toString(), "nodeB": GridTestData.nodeB.uuid.toString()], LineInput)
    def fcn = biEnrich("nodeA", entityMap, "nodeB", entityMap, ConnectorInputEntityData::new)

    when:
    def enrichedData = fcn.apply(new Try.Success<>(entityData))

    then:
    enrichedData.success
    enrichedData.data.get().nodeA == GridTestData.nodeA
    enrichedData.data.get().nodeB == GridTestData.nodeB
  }

  def "An EntitySource's builder function should work as expected"() {
    given:
    def entityData = new EntityData(["operator": ""], NodeInput)
    def pair = Pair.of(entityData, GridTestData.profBroccoli)
    def fcn = enrich(["operator"], AssetInputEntityData::new)

    when:
    def result = fcn.apply(pair)

    then:
    result.fieldsToValues.isEmpty()
    result.operatorInput == GridTestData.profBroccoli
  }

  def "An EntitySource can extract an Entity from a map correctly if a field name is given"() {
    given:
    def entityData = new EntityData(["operator": GridTestData.profBroccoli.uuid.toString()], NodeInput)
    def entityMap = map([GridTestData.profBroccoli])

    when:
    def actual = extract(new Try.Success<>(entityData), "operator", entityMap)

    then:
    actual.success
    actual.data.get() == GridTestData.profBroccoli
  }

  def "An EntitySource returns a failure if an entity can not be extracted from a given map"() {
    given:
    def entityData = new EntityData(fieldsToAttributes, NodeInput)

    when:
    def actual = extract(new Try.Success<>(entityData), "operator", entityMap)

    then:
    actual.failure
    actual.exception.get().class == SourceException
    actual.exception.get().message.contains(expectedMessage)

    where:
    fieldsToAttributes                                      | entityMap                                 | expectedMessage
    ["operator": "no uuid"]                                 | map([OperatorInput.NO_OPERATOR_ASSIGNED]) | "Extracting UUID field operator from entity data"
    ["operator": GridTestData.profBroccoli.uuid.toString()] | map([OperatorInput.NO_OPERATOR_ASSIGNED]) | "Entity with uuid f15105c4-a2de-4ab8-a621-4bc98e372d92 was not provided."
  }

  def "An EntitySource returns a failure if a given map does not contain the given uuid"() {
    given:
    def uuid = GridTestData.profBroccoli.uuid
    def entityMap = map([
      OperatorInput.NO_OPERATOR_ASSIGNED
    ])

    when:
    def actual = extract(uuid, entityMap)

    then:
    actual.failure
    actual.exception.get().message == "Entity with uuid f15105c4-a2de-4ab8-a621-4bc98e372d92 was not provided."
  }
}
