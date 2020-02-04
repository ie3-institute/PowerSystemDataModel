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
        Map<String, String> parameterMap = new HashMap<>()
        parameterMap.put("uuid", "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7")
        parameterMap.put("id", "blablub")
        parameterMap.put("rsc", "3")
        parameterMap.put("xsc", "4")
        parameterMap.put("srated", "5")
        parameterMap.put("vrateda", "6")
        parameterMap.put("vratedb", "7")
        parameterMap.put("gm", "8")
        parameterMap.put("bm", "9")
        parameterMap.put("dv", "10")
        parameterMap.put("dphi", "11")
        parameterMap.put("tapside", "1")
        parameterMap.put("tapneutr", "12")
        parameterMap.put("tapmin", "13")
        parameterMap.put("tapmax", "14")
        def typeInputClass = Transformer2WTypeInput

        when:
        Optional<Transformer2WTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameterMap, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        typeInput.get().getUuid() == UUID.fromString(parameterMap.get("uuid"))
        typeInput.get().getId() == parameterMap.get("id")
        typeInput.get().getRSc() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("rsc")), StandardUnits.IMPEDANCE)
        typeInput.get().getXSc() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("xsc")), StandardUnits.IMPEDANCE)
        typeInput.get().getSRated() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("srated")), StandardUnits.S_RATED)
        typeInput.get().getVRatedA() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("vrateda")), StandardUnits.V_RATED)
        typeInput.get().getVRatedB() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("vratedb")), StandardUnits.V_RATED)
        typeInput.get().getGM() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("gm")), StandardUnits.ADMITTANCE)
        typeInput.get().getBM() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("bm")), StandardUnits.ADMITTANCE)
        typeInput.get().getDV() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("dv")), StandardUnits.DV_TAP)
        typeInput.get().getDPhi() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("dphi")), StandardUnits.DPHI_TAP)
        typeInput.get().getTapSide() == (parameterMap.get("tapside").trim() == "1") || parameterMap.get("tapside").trim() == "true"
        typeInput.get().getTapNeutr() == Integer.parseInt(parameterMap.get("tapneutr"))
        typeInput.get().getTapMin() == Integer.parseInt(parameterMap.get("tapmin"))
        typeInput.get().getTapMax() == Integer.parseInt(parameterMap.get("tapmax"))
    }
}
