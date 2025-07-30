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
      assert uuid == acInput.uuid
      assert operationTime == acInput.operationTime
      assert operator == acInput.operator
      assert id == acInput.id
      assert qCharacteristics == acInput.qCharacteristics
      assert thermalBus == SystemParticipantTestData.thermalBus
      assert type == SystemParticipantTestData.acTypeInput
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling a AcInput via builder should work as expected"() {
    given:
    def acInput = SystemParticipantTestData.acInput

    when:
    def alteredUnit = acInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      assert uuid == acInput.uuid
      assert operationTime == acInput.operationTime
      assert operator == acInput.operator
      assert id == acInput.id
      assert qCharacteristics == acInput.qCharacteristics
      assert thermalBus == acInput.thermalBus
      assert type.sRated == acInput.type.sRated * 2d
      assert type.pThermal == acInput.type.pThermal * 2d
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
