/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification


class HpInputTest extends Specification {

  def "A HpInput copy method should work as expected"() {
    given:
    def hpInput = SystemParticipantTestData.hpInput

    when:
    def alteredUnit = hpInput.copy().thermalBus(SystemParticipantTestData.thermalBus)
        .type(SystemParticipantTestData.hpTypeInput).build()

    then:
    alteredUnit.with {
      assert uuid == hpInput.uuid
      assert operationTime == hpInput.operationTime
      assert operator == hpInput.operator
      assert id == hpInput.id
      assert qCharacteristics == hpInput.qCharacteristics
      assert thermalBus == SystemParticipantTestData.thermalBus
      assert type == SystemParticipantTestData.hpTypeInput
      assert em == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling a HpInput via builder should work as expected"() {
    given:
    def hpInput = SystemParticipantTestData.hpInput

    when:
    def alteredUnit = hpInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      assert uuid == hpInput.uuid
      assert operationTime == hpInput.operationTime
      assert operator == hpInput.operator
      assert id == hpInput.id
      assert qCharacteristics == hpInput.qCharacteristics
      assert thermalBus == hpInput.thermalBus
      assert type.sRated == hpInput.type.sRated * 2d
      assert type.pThermal == hpInput.type.pThermal * 2d
      assert em == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
