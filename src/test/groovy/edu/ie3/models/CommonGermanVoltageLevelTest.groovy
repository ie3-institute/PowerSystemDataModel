package edu.ie3.models

import edu.ie3.exceptions.VoltageLevelException
import spock.lang.Specification
import tec.uom.se.quantity.Quantities
import static tec.uom.se.unit.Units.VOLT

import static edu.ie3.models.CommonGermanVoltageLevel.*
import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT

class CommonGermanVoltageLevelTest extends Specification {
    def "A common german voltage level should correctly check, if a valid rated voltage is covered"() {
        expect:
        LV.covers(Quantities.getQuantity(500d, VOLT))   // May be true
    }

    def "A common german voltage level should correctly check, if a outlying rated voltage is covered"() {
        expect:
        !LV.covers(Quantities.getQuantity(10d, KILOVOLT)) // May be false
    }

    def "A common german voltage level should correctly check, if the upper boundary of rated voltages is covered"() {
        expect:
        !LV.covers(Quantities.getQuantity(10d, KILOVOLT)) // May be false, because the interval is right open
    }

    def "A common german voltage level should correctly check, if a valid id / rated voltage combination is covered"() {
        expect:
        LV.covers("NS", Quantities.getQuantity(500d, VOLT))   // May be true
    }

    def "A common german voltage level should correctly check, if an invalid id / rated voltage combination is covered"() {
        expect:
        !LV.covers("HS", Quantities.getQuantity(110d, KILOVOLT))   // May be false
    }

    def "A common german voltage level should thrown an exception, if an inconsistent id / rated voltage combination is checked"() {
        when:
        LV.covers("HS", Quantities.getQuantity(500d, VOLT))   // May be true

        then:
        VoltageLevelException ex = thrown()
        ex.getMessage() == "The provided id \"HS\" and rated voltage \"500.0 V\" could possibly meet the voltage level \"Niederspannung\" (Interval [0.0 kV, 10.0 kV)), but are inconsistent."
    }

    def "The common german voltage level enum should be able to correctly parse different valid inputs"() {
        given:
        CommonVoltageLevel actual = parse(id, vRated)

        expect:
        actual == expected

        where:
        id      || vRated                                   || expected
        "NS"    || Quantities.getQuantity(0.4d, KILOVOLT)   || LV
        "MS"    || Quantities.getQuantity(15d, KILOVOLT)    || MV_10KV
        "MS"    || Quantities.getQuantity(20d, KILOVOLT)    || MV_20KV
        "MS"    || Quantities.getQuantity(35d, KILOVOLT)    || MV_30KV
        "HS"    || Quantities.getQuantity(110d, KILOVOLT)   || HV
        "HoeS"  || Quantities.getQuantity(220d, KILOVOLT)   || EHV_220KV
        "HoeS"  || Quantities.getQuantity(380d, KILOVOLT)   || EHV_380KV
    }
}
