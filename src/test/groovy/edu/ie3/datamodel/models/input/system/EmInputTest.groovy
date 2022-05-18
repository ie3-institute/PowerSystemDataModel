package edu.ie3.datamodel.models.input.system

import edu.ie3.datamodel.models.ControlStrategy
import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification

import static edu.ie3.datamodel.models.ControlStrategy.DefaultControlStrategies.NO_CONTROL_STRATEGY


class EmInputTest extends Specification {

    def "The EmInput constructors work as expected"() {
        when:
        def emInput = new EmInput(
                UUID.fromString("977157f4-25e5-4c72-bf34-440edc778792"),
                "test_emInput",
                SystemParticipantTestData.participantNode,
                SystemParticipantTestData.cosPhiFixed,
                SystemParticipantTestData.connectedAssets,
                SystemParticipantTestData.emControlStrategy
        )

        then:
        emInput.with {
            assert uuid == UUID.fromString("977157f4-25e5-4c72-bf34-440edc778792")
            assert id == "test_emInput"
            assert qCharacteristics == SystemParticipantTestData.cosPhiFixed
            assert connectedAssets ==  SystemParticipantTestData.connectedAssets
            assert controlStrategy.getKey() == SystemParticipantTestData.emControlStrategy

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

    }

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
