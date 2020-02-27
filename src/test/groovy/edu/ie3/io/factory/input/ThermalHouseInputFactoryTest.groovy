package edu.ie3.io.factory.input

import edu.ie3.exceptions.FactoryException
import edu.ie3.models.OperationTime
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.input.thermal.ThermalBusInput
import edu.ie3.models.input.thermal.ThermalHouseInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class ThermalHouseInputFactoryTest extends Specification implements FactoryTestHelper {
    def "A ThermalHouseInputFactory should contain exactly the expected class for parsing"() {
        given:
        def inputFactory = new ThermalHouseInputFactory()
        def expectedClasses = [ThermalHouseInput]

        expect:
        inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A ThermalHouseInputFactory should return empty when trying to parse an operated ThermalHouseInput"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new ThermalHouseInputFactory()
        Map<String, String> parameter = [
                "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operatesuntil": "",
                "id"           : "TestID",
                "ethlosses"    : "3",
                "ethcapa"      : "4"
        ]
        def inputClass = ThermalHouseInput
        def operatorInput = Mock(OperatorInput)
        def thermalBusInput = Mock(ThermalBusInput)

        when:
        Optional<ThermalHouseInput> input = inputFactory.getEntity(new ThermalUnitInputEntityData(parameter, inputClass, operatorInput, thermalBusInput))

        then:
        !input.present
    }

    def "A ThermalHouseInputFactory should parse a valid non-operated ThermalHouseInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new ThermalHouseInputFactory()
        Map<String, String> parameter = [
                "uuid"     : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"       : "TestID",
                "ethlosses": "3",
                "ethcapa"  : "4"
        ]
        def inputClass = ThermalHouseInput
        def thermalBusInput = Mock(ThermalBusInput)

        when:
        Optional<ThermalHouseInput> input = inputFactory.getEntity(new ThermalUnitInputEntityData(parameter, inputClass, thermalBusInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((ThermalHouseInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime == OperationTime.notLimited()
            assert operator == null
            assert id == parameter["id"]
            assert bus == thermalBusInput
            assert ethLosses == getQuant(parameter["ethlosses"], StandardUnits.THERMAL_TRANSMISSION)
            assert ethCapa == getQuant(parameter["ethcapa"], StandardUnits.HEAT_CAPACITY)
        }
    }

    def "A ThermalHouseInputFactory should throw an exception on invalid or incomplete data (parameter missing)"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new ThermalHouseInputFactory()
        Map<String, String> parameter = [
                "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom" : "",
                "operatesuntil": "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "id"           : "TestID",
                "ethcapa"      : "4"
        ]
        def inputClass = ThermalHouseInput
        def operatorInput = Mock(OperatorInput)
        def thermalBusInput = Mock(ThermalBusInput)

        when:
        inputFactory.getEntity(new ThermalUnitInputEntityData(parameter, inputClass, operatorInput, thermalBusInput))

        then:
        FactoryException ex = thrown()
        ex.message == "The provided fields [ethcapa, id, operatesfrom, operatesuntil, uuid] with data {ethcapa -> 4,id -> TestID,operatesfrom -> ,operatesuntil -> 2019-01-01T00:00:00+01:00[Europe/Berlin],uuid -> 91ec3bcf-1777-4d38-af67-0bf7c9fa73c7} are invalid for instance of ThermalHouseInput. \n" +
                "The following fields to be passed to a constructor of ThermalHouseInput are possible:\n" +
                "0: [ethcapa, ethlosses, id, uuid]\n" +
                "1: [ethcapa, ethlosses, id, operatesfrom, operatesuntil, uuid]\n"
    }
}
