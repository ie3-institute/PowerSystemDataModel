package edu.ie3.io.factory.input

import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.connector.type.Transformer2WTypeInput
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

class Transformer2WTypeInputFactoryTest extends Specification {

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
        typeInput.get().getUuid() == UUID.fromString(parameter["uuid"])
        typeInput.get().getId() == parameter["id"]
        typeInput.get().getRSc() == Quantities.getQuantity(Double.parseDouble(parameter["rsc"]), StandardUnits.IMPEDANCE)
        typeInput.get().getXSc() == Quantities.getQuantity(Double.parseDouble(parameter["xsc"]), StandardUnits.IMPEDANCE)
        typeInput.get().getSRated() == Quantities.getQuantity(Double.parseDouble(parameter["srated"]), StandardUnits.S_RATED)
        typeInput.get().getVRatedA() == Quantities.getQuantity(Double.parseDouble(parameter["vrateda"]), StandardUnits.V_RATED)
        typeInput.get().getVRatedB() == Quantities.getQuantity(Double.parseDouble(parameter["vratedb"]), StandardUnits.V_RATED)
        typeInput.get().getGM() == Quantities.getQuantity(Double.parseDouble(parameter["gm"]), StandardUnits.ADMITTANCE)
        typeInput.get().getBM() == Quantities.getQuantity(Double.parseDouble(parameter["bm"]), StandardUnits.ADMITTANCE)
        typeInput.get().getDV() == Quantities.getQuantity(Double.parseDouble(parameter["dv"]), StandardUnits.DV_TAP)
        typeInput.get().getDPhi() == Quantities.getQuantity(Double.parseDouble(parameter["dphi"]), StandardUnits.DPHI_TAP)
        typeInput.get().getTapSide() == (parameter["tapside"].trim() == "1") || parameter["tapside"].trim() == "true"
        typeInput.get().getTapNeutr() == Integer.parseInt(parameter["tapneutr"])
        typeInput.get().getTapMin() == Integer.parseInt(parameter["tapmin"])
        typeInput.get().getTapMax() == Integer.parseInt(parameter["tapmax"])
    }
}
