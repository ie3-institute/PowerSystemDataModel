/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import edu.ie3.datamodel.models.input.EmInput
import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification

class EmInputTest extends Specification {

  def "The EmInput constructors work as expected"() {
    when:
    def emInput = new EmInput(
        UUID.fromString("977157f4-25e5-4c72-bf34-440edc778792"),
        "test_emInput",
        SystemParticipantTestData.emControlStrategy,
        SystemParticipantTestData.parentEm
        )

    then:
    emInput.with {
      assert uuid == UUID.fromString("977157f4-25e5-4c72-bf34-440edc778792")
      assert id == "test_emInput"
      assert controlStrategy == SystemParticipantTestData.emControlStrategy
    }
  }

  def "EmInputs are comparable"() {

    given:
    def emInputA = SystemParticipantTestData.emInput

    expect:
    (emInputA == emInputB) == isEqual

    where:
    emInputB                                                       || isEqual
    SystemParticipantTestData.emInput                              || true
    SystemParticipantTestData.emInput.copy().build()               || true
    SystemParticipantTestData.emInput.copy().id("otherId").build() || false
  }

  def "The EmInput to String method work as expected"() {

    given:
    def emInputToString = SystemParticipantTestData.emInput.toString()

    expect:
    emInputToString == "EmInput{" +
        "uuid=" +
        SystemParticipantTestData.emInput.uuid +
        ", id='" +
        SystemParticipantTestData.emInput.id +
        "', operator=" +
        SystemParticipantTestData.emInput.operator.uuid +
        ", operationTime=" +
        SystemParticipantTestData.emInput.operationTime +
        ", controlStrategy=" +
        SystemParticipantTestData.emInput.controlStrategy +
        ", parentEm=" +
        SystemParticipantTestData.parentEm.uuid +
        "}"
  }

  def "A EmInput copy method should work as expected"() {
    given:
    def emInput = SystemParticipantTestData.emInput
    def newStrat = "new_strat"
    def givenParentEm = new EmInput(
        UUID.fromString("cfc0639b-65bc-47e5-a8e5-82703de3c650"),
        "testParent",
        "controlStrat",
        null
        )

    when:
    def alteredUnit = emInput.copy().controlStrategy(newStrat).parentEm(givenParentEm).build()

    then:
    alteredUnit.with {
      assert uuid == emInput.uuid
      assert operationTime == emInput.operationTime
      assert operator == emInput.operator
      assert id == emInput.id
      assert controlStrategy == newStrat
      assert parentEm == Optional.of(givenParentEm)
    }
  }
}
