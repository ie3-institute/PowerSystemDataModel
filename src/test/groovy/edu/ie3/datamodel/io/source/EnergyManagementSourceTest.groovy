/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.test.helper.EntityMap.map

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.factory.input.EmInputFactory
import edu.ie3.datamodel.models.input.EmInput
import spock.lang.Specification

import java.util.stream.Stream

class EnergyManagementSourceTest extends Specification {

  def "An EnergyManagementSource should construct hierarchical EmInputs with two branches as expected"() {
    given:
    def emUnits = new HashMap<>()
    def emFactory = new EmInputFactory(Collections.emptyMap(), emUnits)

    def assetData = [
      ["uuid": "0-0-0-0-0",
        "id": "root",
        "controllingEm" : "",
        "controlStrategy" : ""],
      ["uuid": "0-0-0-0-1",
        "id": "child 1",
        "controllingEm" : "0-0-0-0-0",
        "controlStrategy" : ""],
      ["uuid": "0-0-0-0-11",
        "id": "child 1-1",
        "controllingEm" : "0-0-0-0-1",
        "controlStrategy" : ""],
      ["uuid": "0-0-0-0-2",
        "id": "child 2",
        "controllingEm" : "0-0-0-0-0",
        "controlStrategy" : ""],
      ["uuid": "0-0-0-0-21",
        "id": "child 2-1",
        "controllingEm" : "0-0-0-0-2",
        "controlStrategy" : ""]
    ]

    expect:
    EnergyManagementSource.createEmsRecursively(assetData, emUnits, emFactory)

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

    expectedRootEm.additionalInformation.isEmpty()
    expectedEm1.additionalInformation.isEmpty()
    expectedEm11.additionalInformation.isEmpty()
    expectedEm2.additionalInformation.isEmpty()
    expectedEm21.additionalInformation.isEmpty()

    emUnits == map([
      expectedRootEm,
      expectedEm1,
      expectedEm11,
      expectedEm2,
      expectedEm21
    ])
  }

  def "An EnergyManagementSource should construct flat EmInputs without hierarchy as expected"() {
    given:
    def emUnits = new HashMap<>()
    def emFactory = new EmInputFactory(Collections.emptyMap(), emUnits)

    def assetData = [
      ["uuid": "0-0-0-0-1",
        "id": "em 1",
        "controllingEm" : "",
        "controlStrategy" : ""],
      ["uuid": "0-0-0-0-2",
        "id": "em 2",
        "controllingEm" : "",
        "controlStrategy" : "strat_b"],
      ["uuid": "0-0-0-0-3",
        "id": "em 3",
        "controllingEm" : "",
        "controlStrategy" : "other"]
    ]


    expect:
    EnergyManagementSource.createEmsRecursively(assetData, emUnits, emFactory)

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

    emUnits == map([
      expectedEm1,
      expectedEm2,
      expectedEm3
    ])
  }

  def "An EnergyManagementSource should fail if a parent EM UUID is malformed"() {
    given:
    def emUnits = new HashMap<>()
    def emFactory = new EmInputFactory(Collections.emptyMap(), emUnits)

    def assetData = [
      ["uuid": "0-0-0-0-1",
        "id": "em 1",
        "controllingEm" : "",
        "controlStrategy" : ""],
      ["uuid": "0-0-0-0-2",
        "id": "em 2",
        "controllingEm" : "not-a-uuid",
        "controlStrategy" : ""]
    ]

    when:
    EnergyManagementSource.createEmsRecursively(assetData, emUnits, emFactory)

    then:
    def exc = thrown(SourceException)
    exc.message.contains("Exception while trying to parse UUID of field \"controllingEm\" with value \"not-a-uuid\"")
  }

  def "An EnergyManagementSource should fail if the factory fails for one EM"() {
    given:
    def emUnits = new HashMap<>()
    def emFactory = new EmInputFactory(Collections.emptyMap(), emUnits)

    def assetData = [
      ["uuid": "0-0-0-0-1",
        "id": "em 1",
        "controllingEm" : "",
        "controlStrategy" : ""],
      ["uuid": "0-0-0-0-2", // id is missing
        "controllingEm" : "",
        "controlStrategy" : ""]
    ]

    when:
    EnergyManagementSource.createEmsRecursively(assetData, emUnits, emFactory)

    then:
    def exc = thrown(SourceException)
    exc.message == "1 exception(s) occurred within \"EmInput\" data: \n" +
        "        An error occurred in EmInputFactory. Caused by: Field \"id\" not found in EntityData"
  }

  def "An EnergyManagementSource should fail if a parent em is not provided"() {
    given:
    def emUnits = new HashMap<>()
    def emFactory = new EmInputFactory(Collections.emptyMap(), emUnits)

    def assetData = [
      ["uuid": "0-0-0-0-1",
        "id": "em 1",
        "controllingEm" : "",
        "controlStrategy" : ""],
      ["uuid": "0-0-0-0-2",
        "id": "em 2",
        "controllingEm" : "1-2-3-4-5", // does not exist
        "controlStrategy" : ""]
    ]

    when:
    EnergyManagementSource.createEmsRecursively(assetData, emUnits, emFactory)

    then:
    def exc = thrown(SourceException)
    exc.message == "There are em inputs, where controlling ems are assigned that don't exist."
  }

  def "An EnergyManagementSource should fail if no parent ems are provided"() {
    given:
    def emUnits = new HashMap<>()
    def emFactory = new EmInputFactory(Collections.emptyMap(), emUnits)

    def assetData = [
      ["uuid": "0-0-0-0-1",
        "id": "em 1",
        "controllingEm" : "1-2-3-4-5", // does not exist
        "controlStrategy" : ""],
      ["uuid": "0-0-0-0-2",
        "id": "em 2",
        "controllingEm" : "1-2-3-4-5", // does not exist
        "controlStrategy" : ""]
    ]

    when:
    EnergyManagementSource.createEmsRecursively(assetData, emUnits, emFactory)

    then:
    def exc = thrown(SourceException)
    exc.message == "There are em inputs, where controlling ems are assigned that don't exist."
  }
}
