package edu.ie3.datamodel.models

import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT
import static tec.uom.se.unit.Units.VOLT

import edu.ie3.datamodel.exceptions.VoltageLevelException
import edu.ie3.datamodel.models.voltagelevels.CommonVoltageLevel
import edu.ie3.util.interval.RightOpenInterval
import spock.lang.Shared
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

class CommonVoltageLevelTest extends Specification {
    @Shared
    CommonVoltageLevel dut = new CommonVoltageLevel(
            "Niederspannung",
            Quantities.getQuantity(0.4, KILOVOLT),
            new HashSet<>(Arrays.asList("lv", "ns")),
            new RightOpenInterval<>(
                    Quantities.getQuantity(0d, KILOVOLT), Quantities.getQuantity(10d, KILOVOLT)))

    def "A common voltage level should correctly check, if a valid rated voltage is covered"() {
        expect:
        dut.covers(Quantities.getQuantity(500d, VOLT))   // May be true
    }

    def "A common voltage level should correctly check, if a outlying rated voltage is covered"() {
        expect:
        !dut.covers(Quantities.getQuantity(10d, KILOVOLT)) // May be false
    }

    def "A common voltage level should correctly check, if the upper boundary of rated voltages is covered"() {
        expect:
        !dut.covers(Quantities.getQuantity(10d, KILOVOLT)) // May be false, because the interval is right open
    }

    def "A common voltage level should correctly check, if a valid id / rated voltage combination is covered"() {
        expect:
        dut.covers("Niederspannung", Quantities.getQuantity(500d, VOLT))   // May be true
    }

    def "A common voltage level should correctly check, if an invalid id / rated voltage combination is covered"() {
        expect:
        !dut.covers("HS", Quantities.getQuantity(110d, KILOVOLT))   // May be false
    }

    def "A common voltage level should thrown an exception, if an inconsistent id / rated voltage combination is checked"() {
        when:
        dut.covers("HS", Quantities.getQuantity(500d, VOLT))   // May be true

        then:
        VoltageLevelException ex = thrown()
        ex.message == "The provided id \"HS\" and rated voltage \"500.0 V\" could possibly meet the voltage level \"Niederspannung\" (Interval [0.0 kV, 10.0 kV)), but are inconsistent."
    }
}
