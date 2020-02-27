package edu.ie3.io.factory.input

import edu.ie3.exceptions.FactoryException
import edu.ie3.models.OperationTime
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.input.connector.Transformer2WInput
import edu.ie3.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class Transformer2WInputFactoryTest extends Specification implements FactoryTestHelper {
    def "A Transformer2WInputFactory should contain exactly the expected class for parsing"() {
        given:
        def inputFactory = new Transformer2WInputFactory()
        def expectedClasses = [Transformer2WInput]

        expect:
        inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A Transformer2WInputFactory should parse a valid operated Transformer2WInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new Transformer2WInputFactory()
        Map<String, String> parameter = [
                "uuid"           : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom"   : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operatesuntil"  : "",
                "id"             : "TestID",
                "paralleldevices": "2",
                "tappos"         : "3",
                "autotap"        : "true"
        ]
        def inputClass = Transformer2WInput
        def operatorInput = Mock(OperatorInput)
        def nodeInputA = Mock(NodeInput)
        def nodeInputB = Mock(NodeInput)
        def typeInput = Mock(Transformer2WTypeInput)

        when:
        Optional<Transformer2WInput> input = inputFactory.getEntity(new Transformer2WInputEntityData(parameter, inputClass, operatorInput, nodeInputA, nodeInputB, typeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((Transformer2WInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime.startDate.present
            assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
            assert !operationTime.endDate.present
            assert operator == operatorInput
            assert id == parameter["id"]
            assert nodeA == nodeInputA
            assert nodeB == nodeInputB
            assert type == typeInput
            assert noOfParallelDevices == Integer.parseInt(parameter["paralleldevices"])
            assert tapPos == Integer.parseInt(parameter["tappos"])
            assert autoTap
        }
    }

    def "A Transformer2WInputFactory should parse a valid non-operated Transformer2WInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new Transformer2WInputFactory()
        Map<String, String> parameter = [
                "uuid"           : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"             : "TestID",
                "paralleldevices": "2",
                "tappos"         : "3",
                "autotap"        : "true"
        ]
        def inputClass = Transformer2WInput
        def nodeInputA = Mock(NodeInput)
        def nodeInputB = Mock(NodeInput)
        def typeInput = Mock(Transformer2WTypeInput)

        when:
        Optional<Transformer2WInput> input = inputFactory.getEntity(new Transformer2WInputEntityData(parameter, inputClass, nodeInputA, nodeInputB, typeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((Transformer2WInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime == OperationTime.notLimited()
            assert operator == null
            assert id == parameter["id"]
            assert nodeA == nodeInputA
            assert nodeB == nodeInputB
            assert type == typeInput
            assert noOfParallelDevices == Integer.parseInt(parameter["paralleldevices"])
            assert tapPos == Integer.parseInt(parameter["tappos"])
            assert autoTap
        }
    }

    def "A Transformer2WInputFactory should throw an exception on invalid or incomplete data (parameter missing)"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new Transformer2WInputFactory()
        Map<String, String> parameter = [
                "uuid"           : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"             : "TestID",
                "paralleldevices": "2",
                "autotap"        : "true"
        ]
        def inputClass = Transformer2WInput
        def nodeInputA = Mock(NodeInput)
        def nodeInputB = Mock(NodeInput)
        def typeInput = Mock(Transformer2WTypeInput)

        when:
        inputFactory.getEntity(new Transformer2WInputEntityData(parameter, inputClass, nodeInputA, nodeInputB, typeInput))

        then:
        FactoryException ex = thrown()
        ex.message == "The provided fields [autotap, id, paralleldevices, uuid] with data {autotap -> true,id -> TestID,paralleldevices -> 2,uuid -> 91ec3bcf-1777-4d38-af67-0bf7c9fa73c7} are invalid for instance of Transformer2WInput. \n" +
                "The following fields to be passed to a constructor of Transformer2WInput are possible:\n" +
                "0: [autotap, id, paralleldevices, tappos, uuid]\n" +
                "1: [autotap, id, operatesfrom, operatesuntil, paralleldevices, tappos, uuid]\n"
    }
}