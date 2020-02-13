package edu.ie3.io.factory.input

import edu.ie3.test.helper.FactoryTestHelper
import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.connector.type.Transformer2WTypeInput
import spock.lang.Specification

class Transformer2WTypeInputFactoryTest extends Specification implements FactoryTestHelper {

    def "A Transformer2WTypeInputFactory should contain exactly the expected class for parsing"() {
        given:
        def typeInputFactory = new Transformer2WTypeInputFactory()
        def expectedClasses = [Transformer2WTypeInput]

        expect:
        typeInputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A Transformer2WTypeInputFactory should parse a valid Transformer2WTypeInput correctly"() {
        given: "a system participant input type factory and model data"
        def typeInputFactory = new Transformer2WTypeInputFactory()
        Map<String, String> parameter = [
            "uuid":     "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
            "id":       "blablub",
            "rsc":      "3",
            "xsc":      "4",
            "srated":   "5",
            "vrateda":  "6",
            "vratedb":  "7",
            "gm":       "8",
            "bm":       "9",
            "dv":       "10",
            "dphi":     "11",
            "tapside":  "1",
            "tapneutr": "12",
            "tapmin":   "13",
            "tapmax":   "14"
        ]
        def typeInputClass = Transformer2WTypeInput

        when:
        Optional<Transformer2WTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass

        typeInput.get().with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert id == parameter["id"]
            assert RSc == getQuant(parameter["rsc"], StandardUnits.IMPEDANCE)
            assert XSc == getQuant(parameter["xsc"], StandardUnits.IMPEDANCE)
            assert SRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
            assert VRatedA == getQuant(parameter["vrateda"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
            assert VRatedB == getQuant(parameter["vratedb"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
            assert GM == getQuant(parameter["gm"], StandardUnits.ADMITTANCE)
            assert BM == getQuant(parameter["bm"], StandardUnits.ADMITTANCE)
            assert DV == getQuant(parameter["dv"], StandardUnits.DV_TAP)
            assert DPhi == getQuant(parameter["dphi"], StandardUnits.DPHI_TAP)
            assert tapSide == (parameter["tapside"].trim() == "1") || parameter["tapside"].trim() == "true"
            assert tapNeutr == Integer.parseInt(parameter["tapneutr"])
            assert tapMin == Integer.parseInt(parameter["tapmin"])
            assert tapMax == Integer.parseInt(parameter["tapmax"])
        }
    }
}
