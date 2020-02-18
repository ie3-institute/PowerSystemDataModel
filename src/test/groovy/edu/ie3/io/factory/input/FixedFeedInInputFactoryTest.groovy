package edu.ie3.io.factory.input

import edu.ie3.exceptions.FactoryException
import edu.ie3.models.OperationTime
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.input.system.FixedFeedInInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class FixedFeedInInputFactoryTest extends Specification implements FactoryTestHelper {
    def "A FixedFeedInInputFactory should contain exactly the expected class for parsing"() {
        given:
        def inputFactory = new FixedFeedInInputFactory()
        def expectedClasses = [FixedFeedInInput]

        expect:
        inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A FixedFeedInInputFactory should parse a valid operated FixedFeedInInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new FixedFeedInInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operatesuntil"   : "",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1",
                "srated"          : "3",
                "cosphirated"     : "4"
        ]
        def inputClass = FixedFeedInInput
        def nodeInput = Mock(NodeInput)
        def operatorInput = Mock(OperatorInput)

        when:
        Optional<FixedFeedInInput> typeInput = inputFactory.getEntity(new SystemParticipantEntityData(parameter, inputClass, operatorInput, nodeInput))

        then:
        typeInput.present
        typeInput.get().getClass() == inputClass
        ((FixedFeedInInput) typeInput.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime.getStartDate().isPresent()
            assert operationTime.getStartDate().get() == ZonedDateTime.parse(parameter["operatesfrom"])
            assert !operationTime.getEndDate().isPresent()
            assert operator == operatorInput
            assert id == parameter["id"]
            assert node == nodeInput
            assert QCharacteristics == parameter["qcharacteristics"]
            assert SRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
            assert cosphiRated == Double.parseDouble(parameter["cosphirated"])
        }
    }

    def "A FixedFeedInInputFactory should parse a valid non-operated FixedFeedInInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new FixedFeedInInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1",
                "srated"          : "3",
                "cosphirated"     : "4"
        ]
        def inputClass = FixedFeedInInput
        def nodeInput = Mock(NodeInput)

        when:
        Optional<FixedFeedInInput> typeInput = inputFactory.getEntity(new SystemParticipantEntityData(parameter, inputClass, nodeInput))

        then:
        typeInput.present
        typeInput.get().getClass() == inputClass
        ((FixedFeedInInput) typeInput.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime == OperationTime.notLimited()
            assert operator == null
            assert id == parameter["id"]
            assert node == nodeInput
            assert QCharacteristics == parameter["qcharacteristics"]
            assert SRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
            assert cosphiRated == Double.parseDouble(parameter["cosphirated"])
        }
    }

    def "A FixedFeedInInputFactory should throw an exception on invalid or incomplete data (parameter missing)"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new FixedFeedInInputFactory()
        Map<String, String> parameter = [
                "uuid"       : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"         : "TestID",
                "srated"     : "3",
                "cosphirated": "4"
        ]
        def inputClass = FixedFeedInInput
        def nodeInput = Mock(NodeInput)

        when:
        inputFactory.getEntity(new SystemParticipantEntityData(parameter, inputClass, nodeInput))

        then:
        FactoryException ex = thrown()
        ex.message == "The provided fields [cosphirated, id, srated, uuid] with data {cosphirated -> 4,id -> TestID,srated -> 3,uuid -> 91ec3bcf-1777-4d38-af67-0bf7c9fa73c7} are invalid for instance of FixedFeedInInput. \n" +
                "The following fields to be passed to a constructor of FixedFeedInInput are possible:\n" +
                "0: [cosphirated, id, qcharacteristics, srated, uuid]\n" +
                "1: [cosphirated, id, operatesfrom, operatesuntil, qcharacteristics, srated, uuid]\n"
    }

    def "A FixedFeedInInputFactory should throw an exception on invalid or incomplete data (operator missing)"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new FixedFeedInInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operatesuntil"   : "",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1",
                "srated"          : "3",
                "cosphirated"     : "4"
        ]
        def inputClass = FixedFeedInInput
        def nodeInput = Mock(NodeInput)

        when:
        inputFactory.getEntity(new SystemParticipantEntityData(parameter, inputClass, nodeInput))

        then:
        FactoryException ex = thrown()
        ex.message == "Operation time (fields 'operatesfrom' and 'operatesuntil') are passed, but operator input is not."
    }

    def "A FixedFeedInInputFactory should throw an exception on invalid or incomplete data (operation time missing)"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new FixedFeedInInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1",
                "srated"          : "3",
                "cosphirated"     : "4"
        ]
        def inputClass = FixedFeedInInput
        def nodeInput = Mock(NodeInput)
        def operatorInput = Mock(OperatorInput)

        when:
        inputFactory.getEntity(new SystemParticipantEntityData(parameter, inputClass, operatorInput, nodeInput))

        then:
        FactoryException ex = thrown()
        ex.message == "Operator input is passed, but operation time (fields 'operatesfrom' and 'operatesuntil') is not."
    }
}
