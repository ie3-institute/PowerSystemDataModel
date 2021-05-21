/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.value

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities


class SolarIrradianceValueTest extends Specification {

	def "An solar irradiance equals method should work as expected on null and provided quantities"() {
		expect:
		(iVal1 == iVal2) == res

		where:
		iVal1                                                                                                | iVal2                                                                                      || res
		new SolarIrradianceValue(null, null)                                                                     | new SolarIrradianceValue(null, null)                                                           || true
		new SolarIrradianceValue(Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIANCE), null)             | new SolarIrradianceValue(Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIANCE), null)   || true
		new SolarIrradianceValue(Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIANCE), null)             | new SolarIrradianceValue(null, null)                                                           || false
		null                                                                                                 | new SolarIrradianceValue(null, null)                                                           || false
		null                                                                                                 | null                                                                                       || true
		new SolarIrradianceValue(Quantities.getQuantity(10.23d, StandardUnits.SOLAR_IRRADIANCE), null)          | new SolarIrradianceValue(Quantities.getQuantity(10.23, StandardUnits.SOLAR_IRRADIANCE), null) || false
		new SolarIrradianceValue(Quantities.getQuantity(10230, PowerSystemUnits.WATT_PER_SQUAREMETRE), null) | new SolarIrradianceValue(Quantities.getQuantity(10.23, StandardUnits.SOLAR_IRRADIANCE), null) || false
	}
}
