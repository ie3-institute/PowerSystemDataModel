package edu.ie3.io.factory.input

import edu.ie3.test.helper.FactoryTestHelper
import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.connector.type.LineTypeInput
import spock.lang.Specification

class LineTypeInputFactoryTest extends Specification implements FactoryTestHelper {

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
        typeInput.get().uuid == UUID.fromString(parameter["uuid"])
        typeInput.get().id == parameter["id"]
        typeInput.get().b == getQuant(parameter["b"], StandardUnits.SPECIFIC_ADMITTANCE)
        typeInput.get().g == getQuant(parameter["g"], StandardUnits.SPECIFIC_ADMITTANCE)
        typeInput.get().r == getQuant(parameter["r"], StandardUnits.SPECIFIC_IMPEDANCE)
        typeInput.get().x == getQuant(parameter["x"], StandardUnits.SPECIFIC_IMPEDANCE)
        typeInput.get().IMax == getQuant(parameter["imax"], StandardUnits.CURRENT)
        typeInput.get().vRated == getQuant(parameter["vrated"], StandardUnits.V_RATED)
    }
}
