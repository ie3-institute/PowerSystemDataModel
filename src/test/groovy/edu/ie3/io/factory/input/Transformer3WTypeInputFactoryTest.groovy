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
        Map<String, String> parameterMap = new HashMap<>()
        parameterMap.put("uuid", "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7")
        parameterMap.put("id", "blablub")
        parameterMap.put("srateda", "3")
        parameterMap.put("sratedb", "4")
        parameterMap.put("sratedc", "5")
        parameterMap.put("vrateda", "6")
        parameterMap.put("vratedb", "7")
        parameterMap.put("vratedc", "8")
        parameterMap.put("rsca", "9")
        parameterMap.put("rscb", "10")
        parameterMap.put("rscc", "11")
        parameterMap.put("xsca", "12")
        parameterMap.put("xscb", "13")
        parameterMap.put("xscc", "14")
        parameterMap.put("gm", "15")
        parameterMap.put("bm", "16")
        parameterMap.put("dv", "17")
        parameterMap.put("dphi", "18")
        parameterMap.put("tapneutr", "19")
        parameterMap.put("tapmin", "20")
        parameterMap.put("tapmax", "21")
        def typeInputClass = Transformer3WTypeInput

        when:
        Optional<Transformer3WTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameterMap, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        typeInput.get().getUuid() == UUID.fromString(parameterMap.get("uuid"))
        typeInput.get().getId() == parameterMap.get("id")
        typeInput.get().getSRatedA() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("srateda")), StandardUnits.S_RATED)
        typeInput.get().getSRatedB() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("sratedb")), StandardUnits.S_RATED)
        typeInput.get().getSRatedC() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("sratedc")), StandardUnits.S_RATED)
        typeInput.get().getVRatedA() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("vrateda")), StandardUnits.V_RATED)
        typeInput.get().getVRatedB() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("vratedb")), StandardUnits.V_RATED)
        typeInput.get().getVRatedC() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("vratedc")), StandardUnits.V_RATED)
        typeInput.get().getRScA() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("rsca")), StandardUnits.IMPEDANCE)
        typeInput.get().getRScB() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("rscb")), StandardUnits.IMPEDANCE)
        typeInput.get().getRScC() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("rscc")), StandardUnits.IMPEDANCE)
        typeInput.get().getXScA() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("xsca")), StandardUnits.IMPEDANCE)
        typeInput.get().getXScB() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("xscb")), StandardUnits.IMPEDANCE)
        typeInput.get().getXScC() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("xscc")), StandardUnits.IMPEDANCE)
        typeInput.get().getGM() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("gm")), StandardUnits.ADMITTANCE)
        typeInput.get().getBM() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("bm")), StandardUnits.ADMITTANCE)
        typeInput.get().getDV() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("dv")), StandardUnits.DV_TAP)
        typeInput.get().getDPhi() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("dphi")), StandardUnits.DPHI_TAP)
        typeInput.get().getTapNeutr() == Integer.parseInt(parameterMap.get("tapneutr"))
        typeInput.get().getTapMin() == Integer.parseInt(parameterMap.get("tapmin"))
        typeInput.get().getTapMax() == Integer.parseInt(parameterMap.get("tapmax"))
    }
}