/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.value

import edu.ie3.datamodel.models.StandardUnits
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units


class SValueTest extends Specification {

	def "A SValue's equal method should work as expected on null and provided quantities"() {
		expect:
		(sVal1 == sVal2) == res

		where:
		sVal1                                                                           | sVal2                                                                          || res
		new SValue(null, null)                                                          | new SValue(null, null)                                                         || true
		new SValue(Quantities.getQuantity(10d, StandardUnits.ACTIVE_POWER_IN), null)    | new SValue(Quantities.getQuantity(10d, StandardUnits.ACTIVE_POWER_IN), null)   || true
		new SValue(Quantities.getQuantity(10d, StandardUnits.ACTIVE_POWER_IN), null)    | new SValue(null, null)                                                         || false
		null                                                                            | new SValue(null, null)                                                         || false
		null                                                                            | null                                                                           || true
		new SValue(Quantities.getQuantity(10.23d, StandardUnits.ACTIVE_POWER_IN), null) | new SValue(Quantities.getQuantity(10.23, StandardUnits.ACTIVE_POWER_IN), null) || false
		new SValue(Quantities.getQuantity(10230, Units.WATT), null)                     | new SValue(Quantities.getQuantity(10.23, StandardUnits.ACTIVE_POWER_IN), null) || false
	}
}
