/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.source.csv.CsvDataSource
import edu.ie3.datamodel.models.UniqueEntity
import edu.ie3.datamodel.models.input.AssetInput
import edu.ie3.datamodel.models.input.EmInput
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.SystemParticipantTestData as sptd
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path
import java.util.function.Function
import java.util.stream.Collectors

class EntitySourceTest extends Specification {

  private final class DummyEntitySource extends EntitySource {
    DummyEntitySource(CsvDataSource dataSource) {
      super(dataSource);
    }
  }

  @Shared
  String csvSep = ","
  @Shared
  Path testBaseFolderPath = Path.of("testBaseFolderPath") // does not have to exist for this test
  @Shared
  FileNamingStrategy fileNamingStrategy = new FileNamingStrategy()

  CsvDataSource csvDataSource = new CsvDataSource(csvSep, testBaseFolderPath, fileNamingStrategy)

  DummyEntitySource dummyEntitySource = new DummyEntitySource(csvDataSource)

  def "An EntitySource should find a linked entity, if it was provided"() {
    given:
    Map<String, String> parameter = [
      "linked_entity" : sptd.emInput.uuid.toString(),
    ]
    def entityData = new EntityData(parameter, AssetInput.class)

    Map<UUID, EmInput> entityMap = [sptd.emInput].stream().collect(Collectors.toMap(UniqueEntity::getUuid, Function.identity()))

    when:
    def result = dummyEntitySource.getLinkedEntity(entityData, "linked_entity", entityMap)

    then:
    result == new Try.Success<EmInput, SourceException>(sptd.emInput)
  }

  def "An EntitySource trying to find a linked entity should fail, if no matching linked entity was provided"() {
    given:
    Map<String, String> parameter = [
      "linked_entity" : sptd.emInput.parentEm.uuid.toString(),
    ]
    def entityData = new EntityData(parameter, AssetInput.class)

    Map<UUID, EmInput> entityMap = [sptd.emInput].stream().collect(Collectors.toMap(UniqueEntity::getUuid, Function.identity()))

    when:
    def result = dummyEntitySource.getLinkedEntity(entityData, "linked_entity", entityMap)

    then:
    result.isFailure()
    result.getException().get().message == "Linked linked_entity with UUID 897bfc17-8e54-43d0-8d98-740786fd94dd was not found for entity EntityData{fieldsToAttributes={linked_entity=897bfc17-8e54-43d0-8d98-740786fd94dd}, targetClass=class edu.ie3.datamodel.models.input.AssetInput}"
  }

  def "An EntitySource should enrich entity data with a linked entity, if it was provided"() {
    given:
    Map<String, String> parameter = [
      "linked_entity" : GridTestData.nodeA.uuid.toString(),
    ]
    def entityData = new AssetInputEntityData(parameter, AssetInput.class)

    Map<UUID, NodeInput> entityMap = [GridTestData.nodeA].stream().collect(Collectors.toMap(UniqueEntity::getUuid, Function.identity()))

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

    Map<UUID, NodeInput> entityMap = [GridTestData.nodeA].stream().collect(Collectors.toMap(UniqueEntity::getUuid, Function.identity()))

    when:
    def result = dummyEntitySource.enrichEntityData(entityData, "linked_entity", entityMap, NodeAssetInputEntityData::new)

    then:
    result.isFailure()
    result.getException().get().message.startsWith("Linked linked_entity with UUID 47d29df0-ba2d-4d23-8e75-c82229c5c758 was not found for entity AssetInputEntityData")
  }

  // todo test enrich with two linked entities, optionallyEnrich, and various failures
}
