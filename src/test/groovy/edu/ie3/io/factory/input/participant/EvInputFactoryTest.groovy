package edu.ie3.io.factory.input.participant

import edu.ie3.models.OperationTime
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.input.system.EvInput
import edu.ie3.models.input.system.type.EvTypeInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class EvInputFactoryTest extends Specification implements FactoryTestHelper {
    def "A EvInputFactory should contain exactly the expected class for parsing"() {
        given:
        def inputFactory = new EvInputFactory()
        def expectedClasses = [EvInput]

        expect:
        inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A EvInputFactory should parse a valid operated EvInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new EvInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1"
        ]
        def inputClass = EvInput
        def nodeInput = Mock(NodeInput)
        def operatorInput = Mock(OperatorInput)
        def typeInput = Mock(EvTypeInput)

        when:
        Optional<EvInput> input = inputFactory.getEntity(
                new SystemParticipantTypedEntityData<EvTypeInput>(parameter, inputClass, operatorInput, nodeInput, typeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((EvInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime.startDate.present
            assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
            assert operationTime.endDate.present
            assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
            assert operator == operatorInput
            assert id == parameter["id"]
            assert node == nodeInput
            assert QCharacteristics == parameter["qcharacteristics"]
            assert type == typeInput
        }
    }

    def "A EvInputFactory should parse a valid non-operated EvInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new EvInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1"
        ]
        def inputClass = EvInput
        def nodeInput = Mock(NodeInput)
        def typeInput = Mock(EvTypeInput)

        when:
        Optional<EvInput> input = inputFactory.getEntity(
                new SystemParticipantTypedEntityData<EvTypeInput>(parameter, inputClass, nodeInput, typeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((EvInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime == OperationTime.notLimited()
            assert operator == null
            assert id == parameter["id"]
            assert node == nodeInput
            assert QCharacteristics == parameter["qcharacteristics"]
            assert type == typeInput
        }
    }
}
