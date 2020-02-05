package edu.ie3.io.factory.input

import edu.ie3.io.factory.FactorySpecification
import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.connector.type.Transformer2WTypeInput

class Transformer2WTypeInputFactoryTest extends FactorySpecification {

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
        Map<String, String> parameter = [:]
        parameter["uuid"] = "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"
        parameter["id"] = "blablub"
        parameter["rsc"] = "3"
        parameter["xsc"] = "4"
        parameter["srated"] = "5"
        parameter["vrateda"] = "6"
        parameter["vratedb"] = "7"
        parameter["gm"] = "8"
        parameter["bm"] = "9"
        parameter["dv"] = "10"
        parameter["dphi"] = "11"
        parameter["tapside"] = "1"
        parameter["tapneutr"] = "12"
        parameter["tapmin"] = "13"
        parameter["tapmax"] = "14"
        def typeInputClass = Transformer2WTypeInput

        when:
        Optional<Transformer2WTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass

        def typeInputVal = typeInput.get()
        typeInputVal.uuid == UUID.fromString(parameter["uuid"])
        typeInputVal.id == parameter["id"]
        typeInputVal.RSc == getQuant(parameter["rsc"], StandardUnits.IMPEDANCE)
        typeInputVal.XSc == getQuant(parameter["xsc"], StandardUnits.IMPEDANCE)
        typeInputVal.SRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
        typeInputVal.VRatedA == getQuant(parameter["vrateda"], StandardUnits.V_RATED)
        typeInputVal.VRatedB == getQuant(parameter["vratedb"], StandardUnits.V_RATED)
        typeInputVal.GM == getQuant(parameter["gm"], StandardUnits.ADMITTANCE)
        typeInputVal.BM == getQuant(parameter["bm"], StandardUnits.ADMITTANCE)
        typeInputVal.DV == getQuant(parameter["dv"], StandardUnits.DV_TAP)
        typeInputVal.DPhi == getQuant(parameter["dphi"], StandardUnits.DPHI_TAP)
        typeInputVal.tapSide == (parameter["tapside"].trim() == "1") || parameter["tapside"].trim() == "true"
        typeInputVal.tapNeutr == Integer.parseInt(parameter["tapneutr"])
        typeInputVal.tapMin == Integer.parseInt(parameter["tapmin"])
        typeInputVal.tapMax == Integer.parseInt(parameter["tapmax"])
    }
}
