package edu.ie3.datamodel.models.input.system

import edu.ie3.datamodel.models.ControlStrategy
import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification

import static edu.ie3.datamodel.models.ControlStrategy.DefaultControlStrategies.NO_CONTROL_STRATEGY


class EmInputTest extends Specification {

    def "A EmInput copy method should work as expected"() {
        given:
        def emInput = SystemParticipantTestData.emInput
        def newConnectedAssets = [UUID.randomUUID(), UUID.randomUUID()] as UUID[]


        when:
        def alteredUnit = emInput.copy().connectedAssets(newConnectedAssets).controlStrategy(ControlStrategy.parse("")).build()

        then:
        alteredUnit.with {
            assert uuid == emInput.uuid
            assert operationTime == emInput.operationTime
            assert operator == emInput.operator
            assert id == emInput.id
            assert qCharacteristics == emInput.qCharacteristics
            assert connectedAssets == newConnectedAssets
            assert controlStrategy == NO_CONTROL_STRATEGY
        }
    }
}
