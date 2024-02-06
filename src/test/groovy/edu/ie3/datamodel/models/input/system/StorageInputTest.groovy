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
      assert uuid == storageInput.uuid
      assert operationTime == storageInput.operationTime
      assert operator == storageInput.operator
      assert id == storageInput.id
      assert qCharacteristics == storageInput.qCharacteristics
      assert type == SystemParticipantTestData.storageTypeInput
      assert em == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
