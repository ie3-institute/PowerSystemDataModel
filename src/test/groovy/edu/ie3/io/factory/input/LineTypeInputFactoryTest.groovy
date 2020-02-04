package edu.ie3.io.factory.input

import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.connector.type.LineTypeInput
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

class LineTypeInputFactoryTest extends Specification {

    def "A LineTypeInputFactory should contain exactly the expected class for parsing"() {
        given:
        def typeInputFactory = new LineTypeInputFactory()
        def expectedClasses = [LineTypeInput]

        expect:
        typeInputFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A LineTypeInputFactory should parse a valid LineTypeInput correctly"() {
        given: "a system participant input type factory and model data"
        def typeInputFactory = new LineTypeInputFactory()
        Map<String, String> parameterMap = new HashMap<>()
        parameterMap.put("uuid", "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7")
        parameterMap.put("id", "blablub")
        parameterMap.put("b", "3")
        parameterMap.put("g", "4")
        parameterMap.put("r", "5")
        parameterMap.put("x", "6")
        parameterMap.put("imax", "7")
        parameterMap.put("vrated", "8")
        def typeInputClass = LineTypeInput

        when:
        Optional<LineTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameterMap, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        typeInput.get().getUuid() == UUID.fromString(parameterMap.get("uuid"))
        typeInput.get().getId() == parameterMap.get("id")
        typeInput.get().getB() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("b")), StandardUnits.SPECIFIC_ADMITTANCE)
        typeInput.get().getG() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("g")), StandardUnits.SPECIFIC_ADMITTANCE)
        typeInput.get().getR() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("r")), StandardUnits.SPECIFIC_IMPEDANCE)
        typeInput.get().getX() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("x")), StandardUnits.SPECIFIC_IMPEDANCE)
        typeInput.get().getIMax() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("imax")), StandardUnits.CURRENT)
        typeInput.get().getVRated() == Quantities.getQuantity(Double.parseDouble(parameterMap.get("vrated")), StandardUnits.V_RATED)
    }
}
