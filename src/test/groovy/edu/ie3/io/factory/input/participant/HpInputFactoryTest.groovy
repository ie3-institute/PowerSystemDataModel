package edu.ie3.io.factory.input.participant

import edu.ie3.models.OperationTime
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.input.system.HpInput
import edu.ie3.models.input.system.type.HpTypeInput
import edu.ie3.models.input.thermal.ThermalBusInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class HpInputFactoryTest extends Specification implements FactoryTestHelper {
    def "A HpInputFactory should contain exactly the expected class for parsing"() {
        given:
        def inputFactory = new HpInputFactory()
        def expectedClasses = [HpInput]

        expect:
        inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A HpInputFactory should parse a valid operated HpInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new HpInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1"
        ]
        def inputClass = HpInput
        def nodeInput = Mock(NodeInput)
        def operatorInput = Mock(OperatorInput)
        def typeInput = Mock(HpTypeInput)
        def thermalBusInput = Mock(ThermalBusInput)

        when:
        Optional<HpInput> input = inputFactory.getEntity(
                new HpInputEntityData(parameter, operatorInput, nodeInput, typeInput, thermalBusInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((HpInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime.startDate.present
            assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
            assert operationTime.endDate.present
            assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
            assert operator == operatorInput
            assert id == parameter["id"]
            assert node == nodeInput
            assert qCharacteristics == parameter["qcharacteristics"]
            assert type == typeInput
            assert thermalBus == thermalBusInput
        }
    }

    def "A HpInputFactory should parse a valid non-operated HpInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new HpInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1"
        ]
        def inputClass = HpInput
        def nodeInput = Mock(NodeInput)
        def typeInput = Mock(HpTypeInput)
        def thermalBusInput = Mock(ThermalBusInput)

        when:
        Optional<HpInput> input = inputFactory.getEntity(
                new HpInputEntityData(parameter, nodeInput, typeInput, thermalBusInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((HpInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime == OperationTime.notLimited()
            assert operator == null
            assert id == parameter["id"]
            assert node == nodeInput
            assert qCharacteristics == parameter["qcharacteristics"]
            assert type == typeInput
            assert thermalBus == thermalBusInput
        }
    }
}
