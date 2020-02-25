package edu.ie3.io.factory.input.participant

import edu.ie3.models.OperationTime
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.input.system.WecInput
import edu.ie3.models.input.system.type.WecTypeInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class WecInputFactoryTest extends Specification implements FactoryTestHelper {
    def "A WecInputFactoryTest should contain exactly the expected class for parsing"() {
        given:
        def inputFactory = new WecInputFactory()
        def expectedClasses = [WecInput]

        expect:
        inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A WecInputFactory should parse a valid operated WecInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new WecInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom"    : "",
                "operatesuntil"   : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1",
                "marketreaction"  : "true"
        ]
        def inputClass = WecInput
        def nodeInput = Mock(NodeInput)
        def operatorInput = Mock(OperatorInput)
        def typeInput = Mock(WecTypeInput)

        when:
        Optional<WecInput> input = inputFactory.getEntity(
                new SystemParticipantTypedEntityData<WecTypeInput>(parameter, inputClass, operatorInput, nodeInput, typeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((WecInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert !operationTime.startDate.present
            assert operationTime.endDate.presentinput
            assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
            assert operator == operatorInput
            assert id == parameter["id"]
            assert node == nodeInput
            assert qCharacteristics == parameter["qcharacteristics"]
            assert type == typeInput
            assert marketReaction
        }
    }

    def "A WecInputFactory should parse a valid non-operated WecInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new WecInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1",
                "marketreaction"  : "true"
        ]
        def inputClass = WecInput
        def nodeInput = Mock(NodeInput)
        def typeInput = Mock(WecTypeInput)

        when:
        Optional<WecInput> input = inputFactory.getEntity(
                new SystemParticipantTypedEntityData<WecTypeInput>(parameter, inputClass, nodeInput, typeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((WecInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime == OperationTime.notLimited()
            assert operator == null
            assert id == parameter["id"]
            assert node == nodeInput
            assert qCharacteristics == parameter["qcharacteristics"]
            assert type == typeInput
            assert marketReaction
        }
    }
}
