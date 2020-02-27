package edu.ie3.io.factory.input

import edu.ie3.exceptions.FactoryException
import edu.ie3.models.OperationTime
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.input.connector.Transformer3WInput
import edu.ie3.models.input.connector.type.Transformer3WTypeInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class Transformer3WInputFactoryTest  extends Specification implements FactoryTestHelper {
    def "A Transformer3WInputFactory should contain exactly the expected class for parsing"() {
        given:
        def inputFactory = new Transformer3WInputFactory()
        def expectedClasses = [Transformer3WInput]

        expect:
        inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A Transformer3WInputFactory should parse a valid operated Transformer3WInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new Transformer3WInputFactory()
        Map<String, String> parameter = [
                "uuid"           : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom"   : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operatesuntil"  : "",
                "id"             : "TestID",
                "paralleldevices": "2",
                "tappos"         : "3",
                "autotap"        : "true"
        ]
        def inputClass = Transformer3WInput
        def operatorInput = Mock(OperatorInput)
        def nodeInputA = Mock(NodeInput)
        def nodeInputB = Mock(NodeInput)
        def nodeInputC = Mock(NodeInput)
        def typeInput = Mock(Transformer3WTypeInput)

        when:
        Optional<Transformer3WInput> input = inputFactory.getEntity(new Transformer3WInputEntityData(parameter, inputClass, operatorInput, nodeInputA, nodeInputB, nodeInputC, typeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((Transformer3WInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime.startDate.present
            assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
            assert !operationTime.endDate.present
            assert operator == operatorInput
            assert id == parameter["id"]
            assert nodeA == nodeInputA
            assert nodeB == nodeInputB
            assert nodeC == nodeInputC
            assert type == typeInput
            assert noOfParallelDevices == Integer.parseInt(parameter["paralleldevices"])
            assert tapPos == Integer.parseInt(parameter["tappos"])
            assert autoTap
        }
    }

    def "A Transformer3WInputFactory should parse a valid non-operated Transformer3WInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new Transformer3WInputFactory()
        Map<String, String> parameter = [
                "uuid"           : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"             : "TestID",
                "paralleldevices": "2",
                "tappos"         : "3",
                "autotap"        : "true"
        ]
        def inputClass = Transformer3WInput
        def nodeInputA = Mock(NodeInput)
        def nodeInputB = Mock(NodeInput)
        def nodeInputC = Mock(NodeInput)
        def typeInput = Mock(Transformer3WTypeInput)

        when:
        Optional<Transformer3WInput> input = inputFactory.getEntity(new Transformer3WInputEntityData(parameter, inputClass, nodeInputA, nodeInputB, nodeInputC, typeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((Transformer3WInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime == OperationTime.notLimited()
            assert operator == null
            assert id == parameter["id"]
            assert nodeA == nodeInputA
            assert nodeB == nodeInputB
            assert nodeC == nodeInputC
            assert type == typeInput
            assert noOfParallelDevices == Integer.parseInt(parameter["paralleldevices"])
            assert tapPos == Integer.parseInt(parameter["tappos"])
            assert autoTap
        }
    }


    def "A Transformer3WInputFactory should throw an exception on invalid or incomplete data (parameter missing)"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new Transformer3WInputFactory()
        Map<String, String> parameter = [
                "uuid"           : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"             : "TestID",
                "paralleldevices": "2",
                "tappos"         : "3"
        ]
        def inputClass = Transformer3WInput
        def nodeInputA = Mock(NodeInput)
        def nodeInputB = Mock(NodeInput)
        def nodeInputC = Mock(NodeInput)
        def typeInput = Mock(Transformer3WTypeInput)

        when:
        inputFactory.getEntity(new Transformer3WInputEntityData(parameter, inputClass, nodeInputA, nodeInputB, nodeInputC, typeInput))

        then:
        FactoryException ex = thrown()
        ex.message == "The provided fields [id, paralleldevices, tappos, uuid] with data {id -> TestID,paralleldevices -> 2,tappos -> 3,uuid -> 91ec3bcf-1777-4d38-af67-0bf7c9fa73c7} are invalid for instance of Transformer3WInput. \n" +
                "The following fields to be passed to a constructor of Transformer3WInput are possible:\n" +
                "0: [autotap, id, paralleldevices, tappos, uuid]\n" +
                "1: [autotap, id, operatesfrom, operatesuntil, paralleldevices, tappos, uuid]\n"
    }
}
