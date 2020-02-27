package edu.ie3.io.factory.input

import edu.ie3.exceptions.FactoryException
import edu.ie3.models.OperationTime
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.input.thermal.CylindricalStorageInput
import edu.ie3.models.input.thermal.ThermalBusInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class CylindricalStorageInputFactoryTest  extends Specification implements FactoryTestHelper {
    def "A CylindricalStorageInputFactory should contain exactly the expected class for parsing"() {
        given:
        def inputFactory = new CylindricalStorageInputFactory()
        def expectedClasses = [CylindricalStorageInput]

        expect:
        inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A CylindricalStorageInputFactory should return empty when trying to parse an operated CylindricalStorageInput"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new CylindricalStorageInputFactory()
        Map<String, String> parameter = [
                "uuid"               : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom"       : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operatesuntil"      : "",
                "id"                 : "TestID",
                "storagevolumelvl"   : "3",
                "storagevolumelvlmin": "4",
                "inlettemp"          : "5",
                "returntemp"         : "6",
                "c"                  : "7"
        ]
        def inputClass = CylindricalStorageInput
        def operatorInput = Mock(OperatorInput)
        def thermalBusInput = Mock(ThermalBusInput)

        when:
        Optional<CylindricalStorageInput> input = inputFactory.getEntity(new ThermalUnitInputEntityData(parameter, inputClass, operatorInput, thermalBusInput))

        then:
        !input.present
    }

    def "A CylindricalStorageInputFactory should parse a valid non-operated CylindricalStorageInput correctlyt"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new CylindricalStorageInputFactory()
        Map<String, String> parameter = [
                "uuid"               : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"                 : "TestID",
                "storagevolumelvl"   : "3",
                "storagevolumelvlmin": "4",
                "inlettemp"          : "5",
                "returntemp"         : "6",
                "c"                  : "7"
        ]
        def inputClass = CylindricalStorageInput
        def thermalBusInput = Mock(ThermalBusInput)

        when:
        Optional<CylindricalStorageInput> input = inputFactory.getEntity(new ThermalUnitInputEntityData(parameter, inputClass, thermalBusInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((CylindricalStorageInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime == OperationTime.notLimited()
            assert operator == null
            assert id == parameter["id"]
            assert bus == thermalBusInput
            assert storageVolumeLvl == getQuant(parameter["storagevolumelvl"], StandardUnits.VOLUME)
            assert storageVolumeLvlMin == getQuant(parameter["storagevolumelvlmin"], StandardUnits.VOLUME)
            assert inletTemp == getQuant(parameter["inlettemp"], StandardUnits.TEMPERATURE)
            assert returnTemp == getQuant(parameter["returntemp"], StandardUnits.TEMPERATURE)
            assert c == getQuant(parameter["c"], StandardUnits.SPECIFIC_HEAT_CAPACITY)
        }
    }

    def "A CylindricalStorageInputFactory should throw an exception on invalid or incomplete data (parameter missing)"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new CylindricalStorageInputFactory()
        Map<String, String> parameter = [
                "uuid"               : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom"       : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operatesuntil"      : "",
                "id"                 : "TestID",
                "storagevolumelvl"   : "3",
                "storagevolumelvlmin": "4",
                "returntemp"         : "6",
                "c"                  : "7"
        ]
        def inputClass = CylindricalStorageInput
        def operatorInput = Mock(OperatorInput)
        def thermalBusInput = Mock(ThermalBusInput)

        when:
        inputFactory.getEntity(new ThermalUnitInputEntityData(parameter, inputClass, operatorInput, thermalBusInput))

        then:
        FactoryException ex = thrown()
        ex.message == "The provided fields [c, id, operatesfrom, operatesuntil, returntemp, storagevolumelvl, storagevolumelvlmin, uuid] with data {c -> 7,id -> TestID,operatesfrom -> 2019-01-01T00:00:00+01:00[Europe/Berlin],operatesuntil -> ,returntemp -> 6,storagevolumelvl -> 3,storagevolumelvlmin -> 4,uuid -> 91ec3bcf-1777-4d38-af67-0bf7c9fa73c7} are invalid for instance of CylindricalStorageInput. \n" +
                "The following fields to be passed to a constructor of CylindricalStorageInput are possible:\n" +
                "0: [c, id, inlettemp, returntemp, storagevolumelvl, storagevolumelvlmin, uuid]\n" +
                "1: [c, id, inlettemp, operatesfrom, operatesuntil, returntemp, storagevolumelvl, storagevolumelvlmin, uuid]\n"
    }
}
