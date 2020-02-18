package edu.ie3.io.factory.input

import edu.ie3.models.OperationTime
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.input.system.PvInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class PvInputFactoryTest extends Specification implements FactoryTestHelper {
    def "A PvInputFactory should contain exactly the expected class for parsing"() {
        given:
        def inputFactory = new PvInputFactory()
        def expectedClasses = [PvInput]

        expect:
        inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A PvInputFactory should parse a valid operated PvInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new PvInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1",
                "albedo"          : "3",
                "azimuth"         : "4",
                "etaconv"         : "5",
                "height"          : "6",
                "kg"              : "7",
                "kt"              : "8",
                "marketreaction"  : "true",
                "srated"          : "9",
                "cosphi"          : "10",
        ]
        def inputClass = PvInput
        def nodeInput = Mock(NodeInput)
        def operatorInput = Mock(OperatorInput)

        when:
        Optional<PvInput> input = inputFactory.getEntity(
                new SystemParticipantEntityData(parameter, inputClass, operatorInput, nodeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((PvInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime.startDate.isPresent()
            assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
            assert operationTime.endDate.isPresent()
            assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
            assert operator == operatorInput
            assert id == parameter["id"]
            assert node == nodeInput
            assert QCharacteristics == parameter["qcharacteristics"]
            assert albedo == Double.parseDouble(parameter["albedo"])
            assert azimuth == getQuant(parameter["azimuth"], StandardUnits.AZIMUTH)
            assert etaConv == getQuant(parameter["etaconv"], StandardUnits.EFFICIENCY)
            assert height == getQuant(parameter["height"], StandardUnits.SOLAR_HEIGHT)
            assert kG == Double.parseDouble(parameter["kg"])
            assert kT == Double.parseDouble(parameter["kt"])
            assert marketReaction
            assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
            assert cosphiRated == Double.parseDouble(parameter["cosphi"])
        }
    }

    def "A PvInputFactory should parse a valid non-operated PvInput correctly"() {
        given: "a system participant input type factory and model data"
        def inputFactory = new PvInputFactory()
        Map<String, String> parameter = [
                "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "id"              : "TestID",
                "qcharacteristics": "cosphi_fixed:1",
                "albedo"          : "3",
                "azimuth"         : "4",
                "etaconv"         : "5",
                "height"          : "6",
                "kg"              : "7",
                "kt"              : "8",
                "marketreaction"  : "true",
                "srated"          : "9",
                "cosphi"          : "10",
        ]
        def inputClass = PvInput
        def nodeInput = Mock(NodeInput)

        when:
        Optional<PvInput> input = inputFactory.getEntity(
                new SystemParticipantEntityData(parameter, inputClass, nodeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((PvInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime == OperationTime.notLimited()
            assert operator == null
            assert id == parameter["id"]
            assert node == nodeInput
            assert QCharacteristics == parameter["qcharacteristics"]
            assert albedo == Double.parseDouble(parameter["albedo"])
            assert azimuth == getQuant(parameter["azimuth"], StandardUnits.AZIMUTH)
            assert etaConv == getQuant(parameter["etaconv"], StandardUnits.EFFICIENCY)
            assert height == getQuant(parameter["height"], StandardUnits.SOLAR_HEIGHT)
            assert kG == Double.parseDouble(parameter["kg"])
            assert kT == Double.parseDouble(parameter["kt"])
            assert marketReaction
            assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
            assert cosphiRated == Double.parseDouble(parameter["cosphi"])
        }
    }
}
