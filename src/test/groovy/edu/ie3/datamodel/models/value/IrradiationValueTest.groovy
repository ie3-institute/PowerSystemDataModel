/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.value

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities


class IrradiationValueTest extends Specification {

	def "An Irradiation's equal method should work as expected on null and provided quantities"() {
		expect:
		(iVal1 == iVal2) == res

		where:
		iVal1                                                                                                | iVal2                                                                                || res
		new IrradiationValue(null, null)                                                                     | new IrradiationValue(null, null)                                                     || true
		new IrradiationValue(Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIATION), null)                   | new IrradiationValue(Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIATION), null)   || true
		new IrradiationValue(Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIATION), null)                   | new IrradiationValue(null, null)                                                     || false
		null                                                                                                 | new IrradiationValue(null, null)                                                     || false
		null                                                                                                 | null                                                                                 || true
		new IrradiationValue(Quantities.getQuantity(10.23d, StandardUnits.SOLAR_IRRADIATION), null)                | new IrradiationValue(Quantities.getQuantity(10.23, StandardUnits.SOLAR_IRRADIATION), null) || false
		new IrradiationValue(Quantities.getQuantity(10230, PowerSystemUnits.WATTHOUR_PER_SQUAREMETRE), null) 	   | new IrradiationValue(Quantities.getQuantity(10.23, StandardUnits.SOLAR_IRRADIATION), null) || false
	}
}
