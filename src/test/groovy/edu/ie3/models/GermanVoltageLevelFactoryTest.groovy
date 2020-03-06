package edu.ie3.models

import edu.ie3.models.voltagelevels.CommonVoltageLevel
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT
import static edu.ie3.models.voltagelevels.GermanVoltageLevelFactory.*

class GermanVoltageLevelFactoryTest extends Specification {

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
