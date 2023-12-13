/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import edu.ie3.test.common.EnergyManagementTestData
import spock.lang.Specification

class EmInputTest extends Specification {

  def "The EmInput constructors work as expected"() {
    when:
    def emInput = new EmInput(
        UUID.fromString("977157f4-25e5-4c72-bf34-440edc778792"),
        "test_emInput",
        EnergyManagementTestData.emControlStrategy
        )

    then:
    emInput.with {
      assert uuid == UUID.fromString("977157f4-25e5-4c72-bf34-440edc778792")
      assert id == "test_emInput"
      assert controlStrategy == EnergyManagementTestData.emControlStrategy
    }
  }

  def "EmInputs are comparable"() {

    given:
    def emInputA = EnergyManagementTestData.emInput

    expect:
    (emInputA == emInputB) == isEqual

    where:
    emInputB                                                      || isEqual
    EnergyManagementTestData.emInput                              || true
    EnergyManagementTestData.emInput.copy().build()               || true
    EnergyManagementTestData.emInput.copy().id("otherId").build() || false
  }

  def "The EmInput to String method work as expected"() {

    given:
    def emInputToString = EnergyManagementTestData.emInput.toString()

    expect:
    emInputToString == "EmInput{" +
        "uuid=" +
        EnergyManagementTestData.emInput.uuid +
        ", id='" +
        EnergyManagementTestData.emInput.id +
        ", operator=" +
        EnergyManagementTestData.emInput.operator.uuid +
        ", operationTime=" +
        EnergyManagementTestData.emInput.operationTime +
        ", controlStrategy=" +
        EnergyManagementTestData.emInput.controlStrategy +
        '}'
  }

  def "A EmInput copy method should work as expected"() {
    given:
    def emInput = EnergyManagementTestData.emInput
    def newStrat = "new_strat"

    when:
    def alteredUnit = emInput.copy().controlStrategy(newStrat).build()

    then:
    alteredUnit.with {
      assert uuid == emInput.uuid
      assert operationTime == emInput.operationTime
      assert operator == emInput.operator
      assert id == emInput.id
      assert controlStrategy == newStrat
    }
  }
}
