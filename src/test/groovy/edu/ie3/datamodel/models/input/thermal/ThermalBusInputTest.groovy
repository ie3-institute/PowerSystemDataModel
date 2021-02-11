/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.thermal

import edu.ie3.test.common.ThermalUnitInputTestData
import spock.lang.Specification


class ThermalBusInputTest extends Specification {

	def "A ThermalBusInput copy method should work as expected"() {
		given:
		def thermalBusInput = ThermalUnitInputTestData.thermalBus

		when:
		def alteredUnit = thermalBusInput.copy().build()


		then:
		alteredUnit.with {
			assert uuid == thermalBusInput.uuid
			assert id == thermalBusInput.id
			assert operator == thermalBusInput.operator
			assert operationTime == thermalBusInput.operationTime
		}
	}
}
