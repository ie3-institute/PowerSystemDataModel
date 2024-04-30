/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification

class ChpInputTest extends Specification {

  def "A ChpInput copy method should work as expected"() {
    given:
    def chpInput = SystemParticipantTestData.chpInput

    when:
    def alteredUnit = chpInput.copy().thermalBus(SystemParticipantTestData.thermalBus)
        .type(SystemParticipantTestData.chpTypeInput).thermalStorage(SystemParticipantTestData.thermalStorage).marketReaction(true).build()

    then:
    alteredUnit.with {
      assert uuid == chpInput.uuid
      assert operationTime == chpInput.operationTime
      assert operator == chpInput.operator
      assert id == chpInput.id
      assert qCharacteristics == chpInput.qCharacteristics
      assert thermalBus == SystemParticipantTestData.thermalBus
      assert thermalStorage == SystemParticipantTestData.thermalStorage
      assert marketReaction
      assert type == SystemParticipantTestData.chpTypeInput
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling a ChpInput via builder should work as expected"() {
    given:
    def chpInput = SystemParticipantTestData.chpInput

    when:
    def alteredUnit = chpInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      assert uuid == chpInput.uuid
      assert operationTime == chpInput.operationTime
      assert operator == chpInput.operator
      assert id == chpInput.id
      assert qCharacteristics == chpInput.qCharacteristics
      assert thermalBus == chpInput.thermalBus
      assert thermalStorage == chpInput.thermalStorage
      assert marketReaction == chpInput.marketReaction
      assert type.sRated == chpInput.type.sRated * 2d
      assert type.pThermal == chpInput.type.pThermal * 2d
      assert type.pOwn == chpInput.type.pOwn * 2d
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
