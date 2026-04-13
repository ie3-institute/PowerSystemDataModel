/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification


class StorageInputTest extends Specification {

  def "A StorageInput copy method should work as expected"() {
    given:
    def storageInput = SystemParticipantTestData.storageInput

    when:
    def alteredUnit = storageInput.copy().type(SystemParticipantTestData.storageTypeInput).build()

    then:
    alteredUnit.with {
      uuid == storageInput.uuid
      operationTime == storageInput.operationTime
      operator == storageInput.operator
      id == storageInput.id
      qCharacteristics == storageInput.qCharacteristics
      type == SystemParticipantTestData.storageTypeInput
      controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling a StorageInput via builder should work as expected"() {
    given:
    def storageInput = SystemParticipantTestData.storageInput

    when:
    def alteredUnit = storageInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      uuid == storageInput.uuid
      operationTime == storageInput.operationTime
      operator == storageInput.operator
      id == storageInput.id
      qCharacteristics == storageInput.qCharacteristics
      type.sRated == storageInput.type.sRated * 2d
      sRated() == storageInput.type.sRated * 2d
      type.eStorage == storageInput.type.eStorage * 2d
      type.pMax == storageInput.type.pMax * 2d
      controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
