package edu.ie3.io.factory.input

import edu.ie3.models.OperationTime
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.input.system.BmInput
import edu.ie3.models.input.system.type.BmTypeInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class BmInputFactoryTest extends Specification implements FactoryTestHelper {
    def "A BmInputFactory should contain exactly the expected class for parsing"() {
        given:
        def inputFactory = new BmInputFactory()
        def expectedClasses = [BmInput]

        expect:
        inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A BmInputFactory should parse a valid operated StorageInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new BmInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1",
                "marketreaction"  : "false",
                "costControlled"  : "true",
                "feedintariff"    : "3"
        ]
        def inputClass = BmInput
        def nodeInput = Mock(NodeInput)
        def operatorInput = Mock(OperatorInput)
        def typeInput = Mock(BmTypeInput)

        when:
        Optional<BmInput> input = inputFactory.getEntity(
                new SystemParticipantTypedEntityData<BmTypeInput>(parameter, inputClass, operatorInput, nodeInput, typeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((BmInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime.getStartDate().isPresent()
            assert operationTime.getStartDate().get() == ZonedDateTime.parse(parameter["operatesfrom"])
            assert operationTime.getEndDate().isPresent()
            assert operationTime.getEndDate().get() == ZonedDateTime.parse(parameter["operatesuntil"])
            assert operator == operatorInput
            assert id == parameter["id"]
            assert node == nodeInput
            assert QCharacteristics == parameter["qcharacteristics"]
            assert type == typeInput
            assert !marketReaction
            assert costControlled
            assert feedInTariff == getQuant(parameter["feedintariff"], StandardUnits.ENERGY_PRICE)
        }
    }

    def "A BmInputFactory should parse a valid non-operated StorageInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new BmInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1",
                "marketreaction"  : "false",
                "costControlled"  : "true",
                "feedintariff"    : "3"
        ]
        def inputClass = BmInput
        def nodeInput = Mock(NodeInput)
        def typeInput = Mock(BmTypeInput)

        when:
        Optional<BmInput> input = inputFactory.getEntity(
                new SystemParticipantTypedEntityData<BmTypeInput>(parameter, inputClass, nodeInput, typeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((BmInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime == OperationTime.notLimited()
            assert operator == null
            assert id == parameter["id"]
            assert node == nodeInput
            assert QCharacteristics == parameter["qcharacteristics"]
            assert type == typeInput
            assert !marketReaction
            assert costControlled
            assert feedInTariff == getQuant(parameter["feedintariff"], StandardUnits.ENERGY_PRICE)
        }
    }
}
