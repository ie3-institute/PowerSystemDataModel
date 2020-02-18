package edu.ie3.io.factory.typeinput

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
        Map<String, String> parameter = [
            "uuid":     "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
            "id":       "blablub",
            "b":        "3",
            "g":        "4",
            "r":        "5",
            "x":        "6",
            "imax":     "7",
            "vrated":   "8"
        ]
        def typeInputClass = LineTypeInput

        when:
        Optional<LineTypeInput> typeInput = typeInputFactory.getEntity(new SimpleEntityData(parameter, typeInputClass))

        then:
        typeInput.present
        typeInput.get().getClass() == typeInputClass
        ((LineTypeInput) typeInput.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert id == parameter["id"]
            assert b == getQuant(parameter["b"], StandardUnits.ADMITTANCE_PER_LENGTH)
            assert g == getQuant(parameter["g"], StandardUnits.ADMITTANCE_PER_LENGTH)
            assert r == getQuant(parameter["r"], StandardUnits.IMPEDANCE_PER_LENGTH)
            assert x == getQuant(parameter["x"], StandardUnits.IMPEDANCE_PER_LENGTH)
            assert IMax == getQuant(parameter["imax"], StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
            assert vRated == getQuant(parameter["vrated"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
        }
    }
}
