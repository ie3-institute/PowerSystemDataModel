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
        Map<String, String> parameter = [:]
        parameter["uuid"] = "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"
        parameter["id"] = "blablub"
        parameter["b"] = "3"
        parameter["g"] = "4"
        parameter["r"] = "5"
        parameter["x"] = "6"
        parameter["imax"] = "7"
        parameter["vrated"] = "8"
        def typeInputClass = LineTypeInput

        when:
        Optional<LineTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        typeInput.get().getUuid() == UUID.fromString(parameter["uuid"])
        typeInput.get().getId() == parameter["id"]
        typeInput.get().getB() == Quantities.getQuantity(Double.parseDouble(parameter["b"]), StandardUnits.SPECIFIC_ADMITTANCE)
        typeInput.get().getG() == Quantities.getQuantity(Double.parseDouble(parameter["g"]), StandardUnits.SPECIFIC_ADMITTANCE)
        typeInput.get().getR() == Quantities.getQuantity(Double.parseDouble(parameter["r"]), StandardUnits.SPECIFIC_IMPEDANCE)
        typeInput.get().getX() == Quantities.getQuantity(Double.parseDouble(parameter["x"]), StandardUnits.SPECIFIC_IMPEDANCE)
        typeInput.get().getIMax() == Quantities.getQuantity(Double.parseDouble(parameter["imax"]), StandardUnits.CURRENT)
        typeInput.get().getVRated() == Quantities.getQuantity(Double.parseDouble(parameter["vrated"]), StandardUnits.V_RATED)
    }
}
