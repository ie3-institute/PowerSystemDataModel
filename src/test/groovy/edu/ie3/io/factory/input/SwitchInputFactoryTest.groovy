package edu.ie3.io.factory.input

import edu.ie3.exceptions.FactoryException
import edu.ie3.models.OperationTime
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.input.connector.SwitchInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class SwitchInputFactoryTest extends Specification implements FactoryTestHelper {
    def "A SwitchInputFactory should contain exactly the expected class for parsing"() {
        given:
        def inputFactory = new SwitchInputFactory()
        def expectedClasses = [SwitchInput]

        expect:
        inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A SwitchInputFactory should parse a valid operated SwitchInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new SwitchInputFactory()
        Map<String, String> parameter = [
                "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operatesuntil": "",
                "id"           : "TestID",
                "closed"       : "true"
        ]
        def inputClass = SwitchInput
        def operatorInput = Mock(OperatorInput)
        def nodeInputA = Mock(NodeInput)
        def nodeInputB = Mock(NodeInput)

        when:
        Optional<SwitchInput> input = inputFactory.getEntity(new ConnectorInputEntityData(parameter, inputClass, operatorInput, nodeInputA, nodeInputB))

        then:
        input.present
        input.get().getClass() == inputClass
        ((SwitchInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime.startDate.present
            assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
            assert !operationTime.endDate.present
            assert operator == operatorInput
            assert id == parameter["id"]
            assert nodeA == nodeInputA
            assert nodeB == nodeInputB
            assert closed
        }
    }

    def "A SwitchInputFactory should parse a valid non-operated SwitchInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new SwitchInputFactory()
        Map<String, String> parameter = [
                "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"           : "TestID",
                "closed"       : "true"
        ]
        def inputClass = SwitchInput
        def nodeInputA = Mock(NodeInput)
        def nodeInputB = Mock(NodeInput)

        when:
        Optional<SwitchInput> input = inputFactory.getEntity(new ConnectorInputEntityData(parameter, inputClass, nodeInputA, nodeInputB))

        then:
        input.present
        input.get().getClass() == inputClass
        ((SwitchInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime == OperationTime.notLimited()
            assert operator == null
            assert id == parameter["id"]
            assert nodeA == nodeInputA
            assert nodeB == nodeInputB
            assert closed
        }
    }

    def "A SwitchInputFactory should throw an exception on invalid or incomplete data (parameter missing)"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new SwitchInputFactory()
        Map<String, String> parameter = [
                "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"           : "TestID"
        ]
        def inputClass = SwitchInput
        def nodeInputA = Mock(NodeInput)
        def nodeInputB = Mock(NodeInput)

        when:
        inputFactory.getEntity(new ConnectorInputEntityData(parameter, inputClass, nodeInputA, nodeInputB))

        then:
        FactoryException ex = thrown()
        ex.message == "The provided fields [id, uuid] with data {id -> TestID,uuid -> 91ec3bcf-1777-4d38-af67-0bf7c9fa73c7} are invalid for instance of SwitchInput. \n" +
                "The following fields to be passed to a constructor of SwitchInput are possible:\n" +
                "0: [closed, id, uuid]\n" +
                "1: [closed, id, operatesfrom, operatesuntil, uuid]\n"
    }
}
