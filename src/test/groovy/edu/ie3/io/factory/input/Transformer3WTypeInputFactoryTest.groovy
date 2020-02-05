package edu.ie3.io.factory.input

import edu.ie3.io.factory.FactorySpecification
import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.connector.type.Transformer3WTypeInput

class Transformer3WTypeInputFactoryTest extends FactorySpecification {

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
        Map<String, String> parameter = [:]
        parameter["uuid"] = "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"
        parameter["id"] = "blablub"
        parameter["srateda"] = "3"
        parameter["sratedb"] = "4"
        parameter["sratedc"] = "5"
        parameter["vrateda"] = "6"
        parameter["vratedb"] = "7"
        parameter["vratedc"] = "8"
        parameter["rsca"] = "9"
        parameter["rscb"] = "10"
        parameter["rscc"] = "11"
        parameter["xsca"] = "12"
        parameter["xscb"] = "13"
        parameter["xscc"] = "14"
        parameter["gm"] = "15"
        parameter["bm"] = "16"
        parameter["dv"] = "17"
        parameter["dphi"] = "18"
        parameter["tapneutr"] = "19"
        parameter["tapmin"] = "20"
        parameter["tapmax"] = "21"
        def typeInputClass = Transformer3WTypeInput

        when:
        Optional<Transformer3WTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass

        def typeInputVal = typeInput.get()
        typeInputVal.uuid == UUID.fromString(parameter["uuid"])
        typeInputVal.id == parameter["id"]
        typeInputVal.SRatedA == getQuant(parameter["srateda"], StandardUnits.S_RATED)
        typeInputVal.SRatedB == getQuant(parameter["sratedb"], StandardUnits.S_RATED)
        typeInputVal.SRatedC == getQuant(parameter["sratedc"], StandardUnits.S_RATED)
        typeInputVal.VRatedA == getQuant(parameter["vrateda"], StandardUnits.V_RATED)
        typeInputVal.VRatedB == getQuant(parameter["vratedb"], StandardUnits.V_RATED)
        typeInputVal.VRatedC == getQuant(parameter["vratedc"], StandardUnits.V_RATED)
        typeInputVal.RScA == getQuant(parameter["rsca"], StandardUnits.IMPEDANCE)
        typeInputVal.RScB == getQuant(parameter["rscb"], StandardUnits.IMPEDANCE)
        typeInputVal.RScC == getQuant(parameter["rscc"], StandardUnits.IMPEDANCE)
        typeInputVal.XScA == getQuant(parameter["xsca"], StandardUnits.IMPEDANCE)
        typeInputVal.XScB == getQuant(parameter["xscb"], StandardUnits.IMPEDANCE)
        typeInputVal.XScC == getQuant(parameter["xscc"], StandardUnits.IMPEDANCE)
        typeInputVal.GM == getQuant(parameter["gm"], StandardUnits.ADMITTANCE)
        typeInputVal.BM == getQuant(parameter["bm"], StandardUnits.ADMITTANCE)
        typeInputVal.DV == getQuant(parameter["dv"], StandardUnits.DV_TAP)
        typeInputVal.DPhi == getQuant(parameter["dphi"], StandardUnits.DPHI_TAP)
        typeInputVal.tapNeutr == Integer.parseInt(parameter["tapneutr"])
        typeInputVal.tapMin == Integer.parseInt(parameter["tapmin"])
        typeInputVal.tapMax == Integer.parseInt(parameter["tapmax"])
    }
}