package edu.ie3.io.factory.input.participant

import edu.ie3.models.OperationTime
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.input.system.LoadInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class LoadInputFactoryTest extends Specification implements FactoryTestHelper {
    def "A LoadInputFactory should contain exactly the expected class for parsing"() {
        given:
        def inputFactory = new LoadInputFactory()
        def expectedClasses = [LoadInput]

        expect:
        inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A LoadInputFactory should parse a valid operated LoadInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new LoadInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1",
                "dsm"             : "true",
                "econsannual"     : "3",
                "srated"          : "4",
                "cosphi"          : "5"
        ]
        def inputClass = LoadInput
        def nodeInput = Mock(NodeInput)
        def operatorInput = Mock(OperatorInput)

        when:
        Optional<LoadInput> input = inputFactory.getEntity(
                new SystemParticipantEntityData(parameter, inputClass, operatorInput, nodeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((LoadInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime.startDate.present
            assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
            assert operationTime.endDate.present
            assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
            assert operator == operatorInput
            assert id == parameter["id"]
            assert node == nodeInput
            assert QCharacteristics == parameter["qcharacteristics"]
            assert dsm
            assert eConsAnnual == getQuant(parameter["econsannual"], StandardUnits.ENERGY_IN)
            assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
            assert cosphiRated == Double.parseDouble(parameter["cosphi"])
        }
    }

    def "A LoadInputFactory should parse a valid non-operated LoadInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new LoadInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1",
                "dsm"             : "true",
                "econsannual"     : "3",
                "srated"          : "4",
                "cosphi"          : "5"
        ]
        def inputClass = LoadInput
        def nodeInput = Mock(NodeInput)

        when:
        Optional<LoadInput> input = inputFactory.getEntity(
                new SystemParticipantEntityData(parameter, inputClass, nodeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((LoadInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime == OperationTime.notLimited()
            assert operator == null
            assert id == parameter["id"]
            assert node == nodeInput
            assert QCharacteristics == parameter["qcharacteristics"]
            assert dsm
            assert eConsAnnual == getQuant(parameter["econsannual"], StandardUnits.ENERGY_IN)
            assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
            assert cosphiRated == Double.parseDouble(parameter["cosphi"])
        }
    }
}
