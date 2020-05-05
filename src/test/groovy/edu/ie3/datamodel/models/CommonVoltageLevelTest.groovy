/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
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
}
