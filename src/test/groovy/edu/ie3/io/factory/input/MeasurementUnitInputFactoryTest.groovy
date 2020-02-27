package edu.ie3.io.factory.input

import edu.ie3.exceptions.FactoryException
import edu.ie3.models.OperationTime
import edu.ie3.models.input.MeasurementUnitInput
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.OperatorInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class MeasurementUnitInputFactoryTest extends Specification implements FactoryTestHelper {
    def "A MeasurementUnitInputFactory should contain exactly the expected class for parsing"() {
        given:
        def inputFactory = new MeasurementUnitInputFactory()
        def expectedClasses = [MeasurementUnitInput]

        expect:
        inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A MeasurementUnitInputFactory should parse a valid operated MeasurementUnitInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new MeasurementUnitInputFactory()
        Map<String, String> parameter = [
                "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operatesuntil": "",
                "id"           : "TestID",
                "vmag"         : "true",
                "vang"         : "false",
                "p"            : "true",
                "q"            : "true"
        ]
        def inputClass = MeasurementUnitInput
        def nodeInput = Mock(NodeInput)
        def operatorInput = Mock(OperatorInput)

        when:
        Optional<MeasurementUnitInput> input = inputFactory.getEntity(new MeasurementUnitInputEntityData(parameter, inputClass, operatorInput, nodeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((MeasurementUnitInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime.startDate.present
            assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
            assert !operationTime.endDate.present
            assert operator == operatorInput
            assert id == parameter["id"]
            assert node == nodeInput
            assert VMag
            assert !VAng
            assert p
            assert q
        }
    }

    def "A MeasurementUnitInputFactory should parse a valid non-operated MeasurementUnitInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new MeasurementUnitInputFactory()
        Map<String, String> parameter = [
                "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"           : "TestID",
                "vmag"         : "true",
                "vang"         : "false",
                "p"            : "true",
                "q"            : "true"
        ]
        def inputClass = MeasurementUnitInput
        def nodeInput = Mock(NodeInput)

        when:
        Optional<MeasurementUnitInput> input = inputFactory.getEntity(new MeasurementUnitInputEntityData(parameter, inputClass, nodeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((MeasurementUnitInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime == OperationTime.notLimited()
            assert operator == null
            assert id == parameter["id"]
            assert node == nodeInput
            assert VMag
            assert !VAng
            assert p
            assert q
        }
    }

    def "A MeasurementUnitInputFactory should throw an exception on invalid or incomplete data (parameter missing)"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new MeasurementUnitInputFactory()
        Map<String, String> parameter = [
                "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"           : "TestID",
                "vmag"         : "true",
                "vang"         : "false",
                "q"            : "true"
        ]
        def inputClass = MeasurementUnitInput
        def nodeInput = Mock(NodeInput)

        when:
        inputFactory.getEntity(new MeasurementUnitInputEntityData(parameter, inputClass, nodeInput))

        then:
        FactoryException ex = thrown()
        ex.message == "The provided fields [id, q, uuid, vang, vmag] with data {id -> TestID,q -> true,uuid -> 91ec3bcf-1777-4d38-af67-0bf7c9fa73c7,vang -> false,vmag -> true} are invalid for instance of MeasurementUnitInput. \n" +
                "The following fields to be passed to a constructor of MeasurementUnitInput are possible:\n" +
                "0: [id, p, q, uuid, vang, vmag]\n" +
                "1: [id, operatesfrom, operatesuntil, p, q, uuid, vang, vmag]\n"
    }
}
