/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import static edu.ie3.datamodel.utils.validation.DummyAssetInput.valid

import edu.ie3.datamodel.models.UniqueEntity
import edu.ie3.datamodel.models.input.AssetInput
import spock.lang.Specification

class CollectionUtilsTest extends Specification {

  def "A collection of unique entities can be mapped to their uuid"() {
    given:
    UUID uuid1 = UUID.randomUUID()
    UUID uuid2 = UUID.randomUUID()
    UUID uuid3 = UUID.randomUUID()

    UniqueEntity entity1 = valid(uuid1, "1")
    UniqueEntity entity2 = valid(uuid2, "2")
    UniqueEntity entity3 = valid(uuid3, "3")

    when:
    def map = CollectionUtils.toMap([entity1, entity2, entity3])

    then:
    map.get(uuid1) == entity1
    map.get(uuid2) == entity2
    map.get(uuid3) == entity3
  }

  def "A collection of entities can be mapped to a given field"() {
    given:
    UUID uuid1 = UUID.randomUUID()
    UUID uuid2 = UUID.randomUUID()
    UUID uuid3 = UUID.randomUUID()

    AssetInput entity1 = valid(uuid1, "1")
    AssetInput entity2 = valid(uuid2, "2")
    AssetInput entity3 = valid(uuid3, "3")

    when:
    def map = CollectionUtils.toMap([entity1, entity2, entity3], AssetInput::getId)

    then:
    map.get("1") == entity1
    map.get("2") == entity2
    map.get("3") == entity3
  }

  def "A collection of entities can be grouped to a given field"() {
    given:
    UUID uuid1 = UUID.randomUUID()
    UUID uuid2 = UUID.randomUUID()
    UUID uuid3 = UUID.randomUUID()

    AssetInput entity1 = valid(uuid1, "1")
    AssetInput entity2 = valid(uuid2, "2")
    AssetInput entity3 = valid(uuid3, "3")

    when:
    def map = CollectionUtils.groupBy([entity1, entity2, entity3], AssetInput::getId)

    then:
    map.get("1") == [entity1] as Set
    map.get("2") == [entity2] as Set
    map.get("3") == [entity3] as Set
  }
}
