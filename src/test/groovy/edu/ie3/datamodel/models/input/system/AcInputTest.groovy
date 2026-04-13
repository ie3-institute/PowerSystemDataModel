/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification


class AcInputTest extends Specification {

  def "A AcInput copy method should work as expected"() {
    given:
    def acInput = SystemParticipantTestData.acInput

    when:
    def alteredUnit = acInput.copy().thermalBus(SystemParticipantTestData.thermalBus)
        .type(SystemParticipantTestData.acTypeInput).build()

    then:
    alteredUnit.with {
      uuid == acInput.uuid
      operationTime == acInput.operationTime
      operator == acInput.operator
      id == acInput.id
      qCharacteristics == acInput.qCharacteristics
      thermalBus == SystemParticipantTestData.thermalBus
      type == SystemParticipantTestData.acTypeInput
      controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling a AcInput via builder should work as expected"() {
    given:
    def acInput = SystemParticipantTestData.acInput

    when:
    def alteredUnit = acInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      uuid == acInput.uuid
      operationTime == acInput.operationTime
      operator == acInput.operator
      id == acInput.id
      qCharacteristics == acInput.qCharacteristics
      thermalBus == acInput.thermalBus
      type.sRated == acInput.type.sRated * 2d
      type.pThermal == acInput.type.pThermal * 2d
      controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
