package edu.ie3.datamodel.utils

import spock.lang.Specification
import tec.uom.se.AbstractQuantity
import tec.uom.se.ComparableQuantity
import tec.uom.se.quantity.DecimalQuantity
import tec.uom.se.quantity.DoubleQuantity
import tec.uom.se.quantity.IntegerQuantity
import tec.uom.se.quantity.NumberQuantity
import tec.uom.se.quantity.Quantities
import tec.uom.se.unit.Units

import javax.measure.Quantity
import javax.measure.quantity.Temperature

//MIA
class MiaTestTest extends Specification {

    def "miatest" () {
        when:
        Quantity q1 = Quantities.getQuantity(10.0, Units.CUBIC_METRE)
        ComparableQuantity q2 = ComparableQuantity.cast(q1)
        AbstractQuantity<Temperature> q3 = NumberQuantity.of(12, Units.KELVIN)

        int two = 2
        Double threedotfive = 3.5

        then:
        MiaTest.processMethodResult(q1) == "Quant"
        MiaTest.processMethodResult(q2) == "Quant"
        MiaTest.processMethodResult(q3) == "Quant"

        MiaTest.processMethodResult(two) == "2"
        MiaTest.processMethodResult(threedotfive) == "3.5"
    }

}
