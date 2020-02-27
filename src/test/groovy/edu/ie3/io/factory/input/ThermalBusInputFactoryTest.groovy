package edu.ie3.io.factory.input

import edu.ie3.exceptions.FactoryException
import edu.ie3.models.OperationTime
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.input.thermal.ThermalBusInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class ThermalBusInputFactoryTest extends Specification implements FactoryTestHelper {
    def "A ThermalBusInputFactory should contain exactly the expected class for parsing"() {
        given:
        def inputFactory = new ThermalBusInputFactory()
        def expectedClasses = [ThermalBusInput]

        expect:
        inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A ThermalBusInputFactory should parse a valid operated SwitchInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new ThermalBusInputFactory()
        Map<String, String> parameter = [
                "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operatesuntil": "",
                "id"           : "TestID"
        ]
        def inputClass = ThermalBusInput
        def operatorInput = Mock(OperatorInput)

        when:
        Optional<ThermalBusInput> input = inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass, operatorInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((ThermalBusInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime.startDate.present
            assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
            assert !operationTime.endDate.present
            assert operator == operatorInput
            assert id == parameter["id"]
        }
    }

    def "A ThermalBusInputFactory should parse a valid non-operated SwitchInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new ThermalBusInputFactory()
        Map<String, String> parameter = [
                "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"           : "TestID"
        ]
        def inputClass = ThermalBusInput

        when:
        Optional<ThermalBusInput> input = inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass))

        then:
        input.present
        input.get().getClass() == inputClass
        ((ThermalBusInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime == OperationTime.notLimited()
            assert operator == null
            assert id == parameter["id"]
        }
    }

    def "A ThermalBusInputFactory should throw an exception on invalid or incomplete data (parameter missing)"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new ThermalBusInputFactory()
        Map<String, String> parameter = [
                "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"
        ]
        def inputClass = ThermalBusInput

        when:
        inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass))

        then:
        FactoryException ex = thrown()
        ex.message == "The provided fields [uuid] with data {uuid -> 91ec3bcf-1777-4d38-af67-0bf7c9fa73c7} are invalid for instance of ThermalBusInput. \n" +
                "The following fields to be passed to a constructor of ThermalBusInput are possible:\n" +
                "0: [id, uuid]\n" +
                "1: [id, operatesfrom, operatesuntil, uuid]\n"
    }
}
