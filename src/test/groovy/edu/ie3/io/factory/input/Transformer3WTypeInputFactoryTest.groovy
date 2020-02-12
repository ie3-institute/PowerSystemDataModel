package edu.ie3.io.factory.input


import edu.ie3.test.helper.FactoryTestHelper
import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.connector.type.Transformer3WTypeInput
import spock.lang.Specification

class Transformer3WTypeInputFactoryTest extends Specification implements FactoryTestHelper {

    def "A Transformer3WTypeInputFactory should contain exactly the expected class for parsing"() {
        given:
        def typeInputFactory = new Transformer3WTypeInputFactory()
        def expectedClasses = [Transformer3WTypeInput]

        expect:
        typeInputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A Transformer3WTypeInputFactory should parse a valid Transformer2WTypeInput correctly"() {
        given: "a system participant input type factory and model data"
        def typeInputFactory = new Transformer3WTypeInputFactory()
        Map<String, String> parameter = [
            "uuid":	    "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
            "id":   	"blablub",
            "srateda":	"3",
            "sratedb":	"4",
            "sratedc":	"5",
            "vrateda":	"6",
            "vratedb":	"7",
            "vratedc":	"8",
            "rsca":	    "9",
            "rscb":	    "10",
            "rscc":	    "11",
            "xsca":	    "12",
            "xscb":	    "13",
            "xscc":	    "14",
            "gm":	    "15",
            "bm":	    "16",
            "dv":   	"17",
            "dphi":	    "18",
            "tapneutr":	"19",
            "tapmin":	"20",
            "tapmax":	"21"
        ]
        def typeInputClass = Transformer3WTypeInput

        when:
        Optional<Transformer3WTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass

        typeInput.get().with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert id == parameter["id"]
            assert SRatedA == getQuant(parameter["srateda"], StandardUnits.S_RATED)
            assert SRatedB == getQuant(parameter["sratedb"], StandardUnits.S_RATED)
            assert SRatedC == getQuant(parameter["sratedc"], StandardUnits.S_RATED)
            assert VRatedA == getQuant(parameter["vrateda"], StandardUnits.V_RATED)
            assert VRatedB == getQuant(parameter["vratedb"], StandardUnits.V_RATED)
            assert VRatedC == getQuant(parameter["vratedc"], StandardUnits.V_RATED)
            assert RScA == getQuant(parameter["rsca"], StandardUnits.IMPEDANCE)
            assert RScB == getQuant(parameter["rscb"], StandardUnits.IMPEDANCE)
            assert RScC == getQuant(parameter["rscc"], StandardUnits.IMPEDANCE)
            assert XScA == getQuant(parameter["xsca"], StandardUnits.IMPEDANCE)
            assert XScB == getQuant(parameter["xscb"], StandardUnits.IMPEDANCE)
            assert XScC == getQuant(parameter["xscc"], StandardUnits.IMPEDANCE)
            assert GM == getQuant(parameter["gm"], StandardUnits.ADMITTANCE)
            assert BM == getQuant(parameter["bm"], StandardUnits.ADMITTANCE)
            assert DV == getQuant(parameter["dv"], StandardUnits.DV_TAP)
            assert DPhi == getQuant(parameter["dphi"], StandardUnits.DPHI_TAP)
            assert tapNeutr == Integer.parseInt(parameter["tapneutr"])
            assert tapMin == Integer.parseInt(parameter["tapmin"])
            assert tapMax == Integer.parseInt(parameter["tapmax"])
        }
    }
}