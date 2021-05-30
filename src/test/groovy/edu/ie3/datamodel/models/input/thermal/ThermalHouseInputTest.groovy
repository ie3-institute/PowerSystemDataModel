/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.thermal

import edu.ie3.test.common.ThermalUnitInputTestData
import spock.lang.Specification


class ThermalHouseInputTest extends Specification {

	def "A ThermalHouseInput copy method should work as expected"() {
		given:
		def thermalHouseInput = ThermalUnitInputTestData.thermalHouseInput

		when:
		def alteredUnit = thermalHouseInput.copy().ethLosses(ThermalUnitInputTestData.thermalConductance)
				.ethCapa(ThermalUnitInputTestData.ethCapa)
				.desiredTemperature(ThermalUnitInputTestData.desiredTemperature)
				.upperTemperatureLimit(ThermalUnitInputTestData.upperTemperatureLimit)
				.lowerTemperatureLimit(ThermalUnitInputTestData.lowerTemperatureLimit)
				.thermalBus(ThermalUnitInputTestData.thermalBus).build()


		then:
		alteredUnit.with {
			assert uuid == thermalHouseInput.uuid
			assert id == thermalHouseInput.id
			assert operator == thermalHouseInput.operator
			assert operationTime == thermalHouseInput.operationTime
			assert thermalBus == thermalHouseInput.thermalBus
			assert ethLosses == ThermalUnitInputTestData.thermalConductance
			assert ethCapa == ThermalUnitInputTestData.ethCapa
			assert desiredTemperature == ThermalUnitInputTestData.desiredTemperature
			assert upperTemperatureLimit == ThermalUnitInputTestData.upperTemperatureLimit
			assert lowerTemperatureLimit == ThermalUnitInputTestData.lowerTemperatureLimit
		}
	}
}
