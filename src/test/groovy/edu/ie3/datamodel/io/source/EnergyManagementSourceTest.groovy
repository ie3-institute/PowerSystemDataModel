/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.test.helper.EntityMap.map

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.models.Entity
import edu.ie3.datamodel.models.input.EmInput
import spock.lang.Specification

import java.util.stream.Stream

class EnergyManagementSourceTest extends Specification {

  def "An EnergyManagementSource should construct hierarchical EmInputs with two branches as expected"() {
    given:
    def dataSource = new DataSource() {
          @Override
          Optional<Set<String>> getSourceFields(Class<? extends Entity> entityClass) throws SourceException {
            return null
          }

          @Override
          Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass) throws SourceException {
            return Stream.of(
                ["uuid": "0-0-0-0-0", "id": "root", "controllingem": "", "controlstrategy": ""],
                ["uuid": "0-0-0-0-1", "id": "child 1", "controllingem": "0-0-0-0-0", "controlstrategy": ""],
                ["uuid": "0-0-0-0-11", "id": "child 1-1", "controllingem": "0-0-0-0-1", "controlstrategy": ""],
                ["uuid": "0-0-0-0-2", "id": "child 2", "controllingem" : "0-0-0-0-0", "controlstrategy" : ""],
                ["uuid": "0-0-0-0-21", "id": "child 2-1", "controllingem" : "0-0-0-0-2", "controlstrategy" : ""]
                )
          }
        }

    def source = new EnergyManagementSource(null, dataSource)

    expect:
    def emUnits = source.getEmUnits([:])
    def expectedRootEm = new EmInput(UUID.fromString("0-0-0-0-0"), "root", "", null)
    def expectedEm1 = new EmInput(UUID.fromString("0-0-0-0-1"), "child 1", "", expectedRootEm)
    def expectedEm11 = new EmInput(UUID.fromString("0-0-0-0-11"), "child 1-1", "", expectedEm1)
    def expectedEm2 = new EmInput(UUID.fromString("0-0-0-0-2"), "child 2", "", expectedRootEm)
    def expectedEm21 = new EmInput(UUID.fromString("0-0-0-0-21"), "child 2-1", "", expectedEm2)
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
    def dataSource = new DataSource() {
          @Override
          Optional<Set<String>> getSourceFields(Class<? extends Entity> entityClass) throws SourceException {
            return null
          }

          @Override
          Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass) throws SourceException {
            return Stream.of(
                ["uuid": "0-0-0-0-1", "id": "em 1", "controllingem" : "", "controlstrategy" : ""],
                ["uuid": "0-0-0-0-2", "id": "em 2", "controllingem" : "", "controlstrategy" : "strat_b"],
                ["uuid": "0-0-0-0-3", "id": "em 3", "controllingem" : "", "controlstrategy" : "other"]
                )
          }
        }

    def source = new EnergyManagementSource(null, dataSource)

    expect:
    def emUnits = source.getEmUnits([:])
    def expectedEm1 = new EmInput(UUID.fromString("0-0-0-0-1"), "em 1", "", null)
    def expectedEm2 = new EmInput(UUID.fromString("0-0-0-0-2"), "em 2", "strat_b", null)
    def expectedEm3 = new EmInput(UUID.fromString("0-0-0-0-3"), "em 3", "other", null)
    emUnits == map([
      expectedEm1,
      expectedEm2,
      expectedEm3
    ])
  }

  def "An EnergyManagementSource should fail if a parent EM UUID is malformed"() {
    given:
    def dataSource = new DataSource() {
          @Override
          Optional<Set<String>> getSourceFields(Class<? extends Entity> entityClass) throws SourceException {
            return null
          }

          @Override
          Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass) throws SourceException {
            return Stream.of(
                ["uuid": "0-0-0-0-1", "id": "em 1", "controllingem": "", "controlstrategy": ""],
                ["uuid": "0-0-0-0-2", "id": "em 2", "controllingem": "not-a-uuid", "controlstrategy": ""]
                )
          }
        }

    def source = new EnergyManagementSource(null, dataSource)

    when:
    source.getEmUnits([:])

    then:
    def exc = thrown(SourceException)
    exc.message.contains("Exception while trying to parse UUID of field \"controllingEm\" with value \"not-a-uuid\"")
  }

  def "An EnergyManagementSource should fail if the it fails for one EM"() {
    given:
    def dataSource = new DataSource() {
          @Override
          Optional<Set<String>> getSourceFields(Class<? extends Entity> entityClass) throws SourceException {
            return null
          }

          @Override
          Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass) throws SourceException {
            return Stream.of(
                ["uuid"           : "0-0-0-0-1",
                  "id"             : "em 1",
                  "controllingem"  : "",
                  "controlstrategy": ""],
                ["uuid"           : "0-0-0-0-2", // id is missing
                  "controllingem"  : "",
                  "controlstrategy": ""]
                )
          }
        }

    def source = new EnergyManagementSource(null, dataSource)

    when:
    source.getEmUnits([:])

    then:
    def exc = thrown(SourceException)
    exc.message == "1 exception(s) occurred within \"EmInput\" data: \n" +
        "        Field \"id\" not found in EntityData"
  }

  def "An EnergyManagementSource should fail if a parent em is not provided"() {
    given:
    def dataSource = new DataSource() {
          @Override
          Optional<Set<String>> getSourceFields(Class<? extends Entity> entityClass) throws SourceException {
            return null
          }

          @Override
          Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass) throws SourceException {
            return Stream.of(
                ["uuid"           : "0-0-0-0-1",
                  "id"             : "em 1",
                  "controllingem"  : "",
                  "controlstrategy": ""],
                ["uuid"           : "0-0-0-0-2",
                  "id"             : "em 2",
                  "controllingem"  : "1-2-3-4-5", // does not exist
                  "controlstrategy": ""]
                )
          }
        }

    def source = new EnergyManagementSource(null, dataSource)

    when:
    source.getEmUnits([:])

    then:
    def exc = thrown(SourceException)
    exc.message.contains("were assigned a parent EM that does not exist.")
  }

  def "An EnergyManagementSource should fail if no parent ems are provided"() {
    given:
    def dataSource = new DataSource() {
          @Override
          Optional<Set<String>> getSourceFields(Class<? extends Entity> entityClass) throws SourceException {
            return null
          }

          @Override
          Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass) throws SourceException {
            return Stream.of(
                ["uuid"           : "0-0-0-0-1",
                  "id"             : "em 1",
                  "controllingem"  : "1-2-3-4-5", // does not exist
                  "controlstrategy": ""],
                ["uuid"           : "0-0-0-0-2",
                  "id"             : "em 2",
                  "controllingem"  : "1-2-3-4-5", // does not exist
                  "controlstrategy": ""],
                )
          }
        }

    def source = new EnergyManagementSource(null, dataSource)

    when:
    source.getEmUnits([:])

    then:
    def exc = thrown(SourceException)
    exc.message.contains("were assigned a parent EM that does not exist.")
  }
}
