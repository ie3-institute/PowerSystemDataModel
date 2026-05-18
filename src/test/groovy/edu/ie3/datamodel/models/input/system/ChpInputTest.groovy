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
        .type(SystemParticipantTestData.chpTypeInput).thermalStorage(SystemParticipantTestData.thermalStorage).build()

    then:
    alteredUnit.with {
      uuid == chpInput.uuid
      operationTime == chpInput.operationTime
      operator == chpInput.operator
      id == chpInput.id
      qCharacteristics == chpInput.qCharacteristics
      thermalBus == SystemParticipantTestData.thermalBus
      thermalStorage == SystemParticipantTestData.thermalStorage
      type == SystemParticipantTestData.chpTypeInput
      controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling a ChpInput via builder should work as expected"() {
    given:
    def chpInput = SystemParticipantTestData.chpInput

    when:
    def alteredUnit = chpInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      uuid == chpInput.uuid
      operationTime == chpInput.operationTime
      operator == chpInput.operator
      id == chpInput.id
      qCharacteristics == chpInput.qCharacteristics
      thermalBus == chpInput.thermalBus
      thermalStorage == chpInput.thermalStorage
      type.sRated == chpInput.type.sRated * 2d
      sRated() == chpInput.type.sRated * 2d
      type.pThermal == chpInput.type.pThermal * 2d
      type.pOwn == chpInput.type.pOwn * 2d
      controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
