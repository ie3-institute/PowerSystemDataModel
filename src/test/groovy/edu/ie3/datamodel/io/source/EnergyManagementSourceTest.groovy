/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.test.helper.EntityMap.map

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData
import edu.ie3.datamodel.models.input.EmInput
import edu.ie3.datamodel.utils.Try
import spock.lang.Specification

import java.util.stream.Stream

class EnergyManagementSourceTest extends Specification {

  def "An EnergyManagementSource should construct hierarchical EmInputs with two branches as expected"() {
    given:
    def assetEntityDataStream = Stream.of(
    new AssetInputEntityData(
    ["uuid": "0-0-0-0-0",
      "id": "root",
      "parentem" : "",
      "controlstrategy" : ""],
    EmInput
    ),
    new AssetInputEntityData(
    ["uuid": "0-0-0-0-1",
      "id": "child 1",
      "parentem" : "0-0-0-0-0",
      "controlstrategy" : ""],
    EmInput
    ),
    new AssetInputEntityData(
    ["uuid": "0-0-0-0-11",
      "id": "child 1-1",
      "parentem" : "0-0-0-0-1",
      "controlstrategy" : ""],
    EmInput
    ),
    new AssetInputEntityData(
    ["uuid": "0-0-0-0-2",
      "id": "child 2",
      "parentem" : "0-0-0-0-0",
      "controlstrategy" : ""],
    EmInput
    ),
    new AssetInputEntityData(
    ["uuid": "0-0-0-0-21",
      "id": "child 2-1",
      "parentem" : "0-0-0-0-2",
      "controlstrategy" : ""],
    EmInput
    ),
    ).map(data -> Try.of(() -> data, SourceException))

    expect:
    def emUnits = EnergyManagementSource.createEmInputs(assetEntityDataStream)

    def expectedRootEm = new EmInput(
    UUID.fromString("0-0-0-0-0"),
    "root",
    "",
    null
    )
    def expectedEm1 = new EmInput(
    UUID.fromString("0-0-0-0-1"),
    "child 1",
    "",
    expectedRootEm
    )
    def expectedEm11 = new EmInput(
    UUID.fromString("0-0-0-0-11"),
    "child 1-1",
    "",
    expectedEm1
    )
    def expectedEm2 = new EmInput(
    UUID.fromString("0-0-0-0-2"),
    "child 2",
    "",
    expectedRootEm
    )
    def expectedEm21 = new EmInput(
    UUID.fromString("0-0-0-0-21"),
    "child 2-1",
    "",
    expectedEm2
    )

    emUnits == map([expectedRootEm, expectedEm1, expectedEm11, expectedEm2, expectedEm21])
  }

  def "An EnergyManagementSource should construct flat EmInputs without hierarchy as expected"() {
    given:
    def assetEntityDataStream = Stream.of(
    new AssetInputEntityData(
    ["uuid": "0-0-0-0-1",
      "id": "em 1",
      "parentem" : "",
      "controlstrategy" : ""],
    EmInput
    ),
    new AssetInputEntityData(
    ["uuid": "0-0-0-0-2",
      "id": "em 2",
      "parentem" : "",
      "controlstrategy" : "strat_b"],
    EmInput
    ),
    new AssetInputEntityData(
    ["uuid": "0-0-0-0-3",
      "id": "em 3",
      "parentem" : "",
      "controlstrategy" : "other"],
    EmInput
    ),
    ).map(data -> Try.of(() -> data, SourceException))

    expect:
    def emUnits = EnergyManagementSource.createEmInputs(assetEntityDataStream)

    def expectedEm1 = new EmInput(
    UUID.fromString("0-0-0-0-1"),
    "em 1",
    "",
    null
    )
    def expectedEm2 = new EmInput(
    UUID.fromString("0-0-0-0-2"),
    "em 2",
    "strat_b",
    null
    )
    def expectedEm3 = new EmInput(
    UUID.fromString("0-0-0-0-3"),
    "em 3",
    "other",
    null
    )

    emUnits == map([expectedEm1, expectedEm2, expectedEm3])
  }

  def "An EnergyManagementSource should fail if any entity data already failed before"() {
    given:
    def assetEntityDataStream = Stream.of(
    new Try.Success<AssetInputEntityData, SourceException>(new AssetInputEntityData(
    ["uuid": "0-0-0-0-1",
      "id": "em 1",
      "parentem" : "",
      "controlstrategy" : ""],
    EmInput
    )),
    new Try.Success<AssetInputEntityData, SourceException>(new AssetInputEntityData(
    ["uuid": "0-0-0-0-2",
      "id": "em 2",
      "parentem" : "",
      "controlstrategy" : ""],
    EmInput
    )),
    new Try.Failure<AssetInputEntityData, SourceException>(new SourceException("test failure abc"))
    )

    when:
    EnergyManagementSource.createEmInputs(assetEntityDataStream)

    then:
    def exc = thrown(SourceException)
    exc.cause.message.contains("test failure abc")
  }

  def "An EnergyManagementSource should fail if a parent EM UUID is malformed"() {
    given:
    def assetEntityDataStream = Stream.of(
    new AssetInputEntityData(
    ["uuid": "0-0-0-0-1",
      "id": "em 1",
      "parentem" : "",
      "controlstrategy" : ""],
    EmInput
    ),
    new AssetInputEntityData(
    ["uuid": "0-0-0-0-2",
      "id": "em 2",
      "parentem" : "not-a-uuid",
      "controlstrategy" : ""],
    EmInput
    ),
    ).map(data -> Try.of(() -> data, SourceException))

    when:
    EnergyManagementSource.createEmInputs(assetEntityDataStream)

    then:
    def exc = thrown(SourceException)
    exc.cause.message.contains("Exception while trying to parse UUID of field \"parentem\" with value \"not-a-uuid\"")
  }

  def "An EnergyManagementSource should fail if the factory fails for one EM"() {
    given:
    def assetEntityDataStream = Stream.of(
    new AssetInputEntityData(
    ["uuid": "0-0-0-0-1",
      "id": "em 1",
      "parentem" : "",
      "controlstrategy" : ""],
    EmInput
    ),
    new AssetInputEntityData(
    ["uuid": "0-0-0-0-2", // id is missing
      "parentem" : "",
      "controlstrategy" : ""],
    EmInput
    ),
    ).map(data -> Try.of(() -> data, SourceException))

    when:
    EnergyManagementSource.createEmInputs(assetEntityDataStream)

    then:
    def exc = thrown(SourceException)
    exc.cause.message.contains("An error occurred when creating instance of EmInput")
    exc.cause.cause == FactoryException
  }

  def "An EnergyManagementSource should fail if a parent em is not provided"() {
    given:
    def assetEntityDataStream = Stream.of(
    new AssetInputEntityData(
    ["uuid": "0-0-0-0-1",
      "id": "em 1",
      "parentem" : "",
      "controlstrategy" : ""],
    EmInput
    ),
    new AssetInputEntityData(
    ["uuid": "0-0-0-0-2",
      "id": "em 2",
      "parentem" : "1-2-3-4-5", // does not exist
      "controlstrategy" : ""],
    EmInput
    ),
    ).map(data -> Try.of(() -> data, SourceException))

    when:
    EnergyManagementSource.createEmInputs(assetEntityDataStream)

    then:
    def exc = thrown(SourceException)
    exc.message.contains("were assigned a parent EM that does not exist.")
  }

  def "An EnergyManagementSource should fail if no parent ems are provided"() {
    given:
    def assetEntityDataStream = Stream.of(
    new AssetInputEntityData(
    ["uuid": "0-0-0-0-1",
      "id": "em 1",
      "parentem" : "1-2-3-4-5", // does not exist
      "controlstrategy" : ""],
    EmInput
    ),
    new AssetInputEntityData(
    ["uuid": "0-0-0-0-2",
      "id": "em 2",
      "parentem" : "1-2-3-4-5", // does not exist
      "controlstrategy" : ""],
    EmInput
    ),
    ).map(data -> Try.of(() -> data, SourceException))

    when:
    EnergyManagementSource.createEmInputs(assetEntityDataStream)

    then:
    def exc = thrown(SourceException)
    exc.message.contains("were assigned a parent EM that does not exist.")
  }
}
