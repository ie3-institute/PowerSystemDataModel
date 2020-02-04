package edu.ie3.io.factory.input

import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.connector.type.Transformer3WTypeInput
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

class Transformer3WTypeInputFactoryTest extends Specification {

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
        typeInput.get().uuid == UUID.fromString(parameter["uuid"])
        typeInput.get().id == parameter["id"]
        typeInput.get().SRatedA == Quantities.getQuantity(Double.parseDouble(parameter["srateda"]), StandardUnits.S_RATED)
        typeInput.get().SRatedB == Quantities.getQuantity(Double.parseDouble(parameter["sratedb"]), StandardUnits.S_RATED)
        typeInput.get().SRatedC == Quantities.getQuantity(Double.parseDouble(parameter["sratedc"]), StandardUnits.S_RATED)
        typeInput.get().VRatedA == Quantities.getQuantity(Double.parseDouble(parameter["vrateda"]), StandardUnits.V_RATED)
        typeInput.get().VRatedB == Quantities.getQuantity(Double.parseDouble(parameter["vratedb"]), StandardUnits.V_RATED)
        typeInput.get().VRatedC == Quantities.getQuantity(Double.parseDouble(parameter["vratedc"]), StandardUnits.V_RATED)
        typeInput.get().RScA == Quantities.getQuantity(Double.parseDouble(parameter["rsca"]), StandardUnits.IMPEDANCE)
        typeInput.get().RScB == Quantities.getQuantity(Double.parseDouble(parameter["rscb"]), StandardUnits.IMPEDANCE)
        typeInput.get().RScC == Quantities.getQuantity(Double.parseDouble(parameter["rscc"]), StandardUnits.IMPEDANCE)
        typeInput.get().XScA == Quantities.getQuantity(Double.parseDouble(parameter["xsca"]), StandardUnits.IMPEDANCE)
        typeInput.get().XScB == Quantities.getQuantity(Double.parseDouble(parameter["xscb"]), StandardUnits.IMPEDANCE)
        typeInput.get().XScC == Quantities.getQuantity(Double.parseDouble(parameter["xscc"]), StandardUnits.IMPEDANCE)
        typeInput.get().GM == Quantities.getQuantity(Double.parseDouble(parameter["gm"]), StandardUnits.ADMITTANCE)
        typeInput.get().BM == Quantities.getQuantity(Double.parseDouble(parameter["bm"]), StandardUnits.ADMITTANCE)
        typeInput.get().DV == Quantities.getQuantity(Double.parseDouble(parameter["dv"]), StandardUnits.DV_TAP)
        typeInput.get().DPhi == Quantities.getQuantity(Double.parseDouble(parameter["dphi"]), StandardUnits.DPHI_TAP)
        typeInput.get().tapNeutr == Integer.parseInt(parameter["tapneutr"])
        typeInput.get().tapMin == Integer.parseInt(parameter["tapmin"])
        typeInput.get().tapMax == Integer.parseInt(parameter["tapmax"])
    }
}