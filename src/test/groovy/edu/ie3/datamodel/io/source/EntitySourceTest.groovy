/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.test.helper.EntityMap.map

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData
import edu.ie3.datamodel.io.factory.input.participant.ChpInputEntityData
import edu.ie3.datamodel.io.factory.input.participant.SystemParticipantTypedEntityData
import edu.ie3.datamodel.io.source.csv.CsvDataSource
import edu.ie3.datamodel.models.input.AssetInput
import edu.ie3.datamodel.models.input.EmInput
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.ChpInput
import edu.ie3.datamodel.models.input.system.type.ChpTypeInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.SystemParticipantTestData as sptd
import spock.lang.Shared
import spock.lang.Specification

class EntitySourceTest extends Specification {

  private final class DummyEntitySource extends EntitySource {
    DummyEntitySource(CsvDataSource dataSource) {
      super(dataSource)
    }
  }

  @Shared
  DummyEntitySource dummyEntitySource = new DummyEntitySource(Mock(CsvDataSource))

  def "An EntitySource should enrich entity data with a linked entity, if it was provided"() {
    given:
    Map<String, String> parameter = [
      "linked_entity" : GridTestData.nodeA.uuid.toString(),
    ]
    def entityData = new AssetInputEntityData(parameter, AssetInput.class)

    Map<UUID, NodeInput> entityMap = map([GridTestData.nodeA])

    when:
    def result = dummyEntitySource.enrichEntityData(entityData, "linked_entity", entityMap, NodeAssetInputEntityData::new)

    then:
    result == new Try.Success<NodeAssetInputEntityData, SourceException>(new NodeAssetInputEntityData(entityData, GridTestData.nodeA))
  }

  def "An EntitySource trying to enrich entity data should fail, if no matching linked entity was provided"() {
    given:
    Map<String, String> parameter = [
      "linked_entity" : GridTestData.nodeB.uuid.toString(),
    ]
    def entityData = new AssetInputEntityData(parameter, AssetInput.class)

    Map<UUID, NodeInput> entityMap = map([GridTestData.nodeA])

    when:
    def result = dummyEntitySource.enrichEntityData(entityData, "linked_entity", entityMap, NodeAssetInputEntityData::new)

    then:
    result.isFailure()
    result.getException().get().message.startsWith("Linked linked_entity with UUID 47d29df0-ba2d-4d23-8e75-c82229c5c758 was not found for entity AssetInputEntityData")
  }

  def "An EntitySource should enrich entity data with two linked entities, if they are provided"() {
    given:
    Map<String, String> parameter = [
      "t_bus" : sptd.thermalBus.uuid.toString(),
      "t_storage" : sptd.thermalStorage.uuid.toString()
    ]
    def entityData = new SystemParticipantTypedEntityData<ChpTypeInput>(parameter, ChpInput.class, sptd.participantNode, null, sptd.chpTypeInput)

    Map<UUID, ThermalBusInput> busMap = map([sptd.thermalBus])
    Map<UUID, ThermalStorageInput> storageMap = map([sptd.thermalStorage])

    when:
    def result = dummyEntitySource.enrichEntityData(entityData, "t_bus", busMap, "t_storage", storageMap, ChpInputEntityData::new)

    then:
    result == new Try.Success<ChpInputEntityData, SourceException>(new ChpInputEntityData(entityData, sptd.thermalBus, sptd.thermalStorage))
  }

  def "An EntitySource trying to enrich entity data should fail, if one of two linked entities is not provided"() {
    given:
    Map<String, String> parameter = [
      "t_bus" : sptd.thermalBus.uuid.toString(),
      "t_storage" : "8851813b-3a7d-4fee-874b-4df9d724e4b4"
    ]
    def entityData = new SystemParticipantTypedEntityData<ChpTypeInput>(parameter, ChpInput.class, sptd.participantNode, null, sptd.chpTypeInput)

    Map<UUID, ThermalBusInput> busMap = map([sptd.thermalBus])
    Map<UUID, ThermalStorageInput> storageMap = map([sptd.thermalStorage])

    when:
    def result = dummyEntitySource.enrichEntityData(entityData, "t_bus", busMap, "t_storage", storageMap, ChpInputEntityData::new)

    then:
    result.isFailure()
    result.getException().get().message.startsWith("Linked t_storage with UUID 8851813b-3a7d-4fee-874b-4df9d724e4b4 was not found for entity SystemParticipantTypedEntityData")
  }

  def "An EntitySource should find a linked entity, if it was provided"() {
    given:
    Map<String, String> parameter = [
      "linked_entity" : sptd.emInput.uuid.toString(),
    ]
    def entityData = new EntityData(parameter, AssetInput.class)

    Map<UUID, EmInput> entityMap = map([sptd.emInput])

    when:
    def result = dummyEntitySource.getLinkedEntity(entityData, "linked_entity", entityMap)

    then:
    result == new Try.Success<EmInput, SourceException>(sptd.emInput)
  }

  def "An EntitySource trying to find a linked entity should fail, if no matching linked entity was provided"() {
    given:
    Map<String, String> parameter = [
      "linked_entity" : sptd.parentEm.uuid.toString(),
    ]
    def entityData = new EntityData(parameter, AssetInput.class)

    Map<UUID, EmInput> entityMap = map([sptd.emInput])

    when:
    def result = dummyEntitySource.getLinkedEntity(entityData, "linked_entity", entityMap)

    then:
    result.isFailure()
    result.getException().get().message == "Linked linked_entity with UUID 897bfc17-8e54-43d0-8d98-740786fd94dd was not found for entity EntityData{fieldsToAttributes={linked_entity=897bfc17-8e54-43d0-8d98-740786fd94dd}, targetClass=class edu.ie3.datamodel.models.input.AssetInput}"
  }

  def "An EntitySource trying to find a linked entity should fail, if corresponding UUID is malformed"() {
    given:
    Map<String, String> parameter = [
      "linked_entity" : "not-a-uuid",
    ]
    def entityData = new EntityData(parameter, AssetInput.class)

    Map<UUID, EmInput> entityMap = map([sptd.emInput])

    when:
    def result = dummyEntitySource.getLinkedEntity(entityData, "linked_entity", entityMap)

    then:
    result.isFailure()
    result.getException().get().message == "Extracting UUID field linked_entity from entity data EntityData{fieldsToAttributes={linked_entity=not-a-uuid}, targetClass=class edu.ie3.datamodel.models.input.AssetInput} failed."
  }

  def "An EntitySource should optionally enrich entity data with a linked entity, if it was provided"() {
    given:
    Map<String, String> parameter = [
      "linked_entity" : GridTestData.nodeA.uuid.toString(),
    ]
    def entityData = new AssetInputEntityData(parameter, AssetInput.class)

    Map<UUID, NodeInput> entityMap = map([GridTestData.nodeA])

    when:
    def result = dummyEntitySource.optionallyEnrichEntityData(entityData, "linked_entity", entityMap, GridTestData.nodeB, NodeAssetInputEntityData::new)

    then:
    result == new Try.Success<NodeAssetInputEntityData, SourceException>(new NodeAssetInputEntityData(entityData, GridTestData.nodeA))
  }

  def "An EntitySource should (optionally) enrich entity data with the default entity, if no linked entity is specified"() {
    given:
    Map<String, String> parameter = [
      "linked_entity" : "",
    ]
    def entityData = new AssetInputEntityData(parameter, AssetInput.class)

    Map<UUID, NodeInput> entityMap = map([GridTestData.nodeA])

    when:
    def result = dummyEntitySource.optionallyEnrichEntityData(entityData, "linked_entity", entityMap, GridTestData.nodeB, NodeAssetInputEntityData::new)

    then:
    result == new Try.Success<NodeAssetInputEntityData, SourceException>(new NodeAssetInputEntityData(entityData, GridTestData.nodeB))
  }

  def "An EntitySource trying to optionally find a linked entity should fail, if no matching linked entity was provided"() {
    given:
    Map<String, String> parameter = [
      "linked_entity" : "4ca90220-74c2-4369-9afa-a18bf068840e",
    ]
    def entityData = new AssetInputEntityData(parameter, AssetInput.class)

    Map<UUID, NodeInput> entityMap = map([GridTestData.nodeA])

    when:
    def result = dummyEntitySource.optionallyEnrichEntityData(entityData, "linked_entity", entityMap, GridTestData.nodeB, NodeAssetInputEntityData::new)

    then:
    result.isFailure()
    result.getException().get().message.startsWith("Linked linked_entity with UUID 4ca90220-74c2-4369-9afa-a18bf068840e was not found for entity AssetInputEntityData{fieldsToValues={linked_entity=4ca90220-74c2-4369-9afa-a18bf068840e}, targetClass=class edu.ie3.datamodel.models.input.AssetInput")
  }


  def "An EntitySource trying to optionally find a linked entity should fail, if corresponding UUID is malformed"() {
    given:
    Map<String, String> parameter = [
      "linked_entity" : "not-a-uuid",
    ]
    def entityData = new AssetInputEntityData(parameter, AssetInput.class)

    Map<UUID, NodeInput> entityMap = map([GridTestData.nodeA])

    when:
    def result = dummyEntitySource.optionallyEnrichEntityData(entityData, "linked_entity", entityMap, GridTestData.nodeB, NodeAssetInputEntityData::new)

    then:
    result.isFailure()
    result.getException().get().message == "Exception while trying to parse UUID of field \"linked_entity\" with value \"not-a-uuid\""
  }
}
