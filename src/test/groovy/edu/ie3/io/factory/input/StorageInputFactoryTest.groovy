package edu.ie3.io.factory.input

import edu.ie3.models.OperationTime
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.input.system.StorageInput
import edu.ie3.models.input.system.StorageStrategy
import edu.ie3.models.input.system.type.StorageTypeInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class StorageInputFactoryTest extends Specification implements FactoryTestHelper {
    def "A StorageInputFactory should contain exactly the expected class for parsing"() {
        given:
        def inputFactory = new StorageInputFactory()
        def expectedClasses = [StorageInput]

        expect:
        inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A StorageInputFactory should parse a valid operated StorageInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new StorageInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1",
                "behaviour"       : "market"
        ]
        def inputClass = StorageInput
        def nodeInput = Mock(NodeInput)
        def operatorInput = Mock(OperatorInput)
        def typeInput = Mock(StorageTypeInput)

        when:
        Optional<StorageInput> input = inputFactory.getEntity(
                new SystemParticipantTypedEntityData<StorageTypeInput>(parameter, inputClass, operatorInput, nodeInput, typeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((StorageInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime.startDate.isPresent()
            assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
            assert operationTime.endDate.isPresent()
            assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
            assert operator == operatorInput
            assert id == parameter["id"]
            assert node == nodeInput
            assert QCharacteristics == parameter["qcharacteristics"]
            assert type == typeInput
            assert behaviour == StorageStrategy.get(parameter["behaviour"])
        }
    }

    def "A StorageInputFactory should parse a valid non-operated StorageInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new StorageInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1",
                "behaviour"       : "market"
        ]
        def inputClass = StorageInput
        def nodeInput = Mock(NodeInput)
        def typeInput = Mock(StorageTypeInput)

        when:
        Optional<StorageInput> input = inputFactory.getEntity(
                new SystemParticipantTypedEntityData<StorageTypeInput>(parameter, inputClass, nodeInput, typeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((StorageInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime == OperationTime.notLimited()
            assert operator == null
            assert id == parameter["id"]
            assert node == nodeInput
            assert QCharacteristics == parameter["qcharacteristics"]
            assert type == typeInput
            assert behaviour == StorageStrategy.get(parameter["behaviour"])
        }
    }
}
