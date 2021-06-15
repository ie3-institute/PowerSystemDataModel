/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.thermal

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.test.common.ThermalUnitInputTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities


class ThermalHouseInputTest extends Specification {

	def "A ThermalHouseInput copy method should work as expected"() {
		given:
		def thermalHouseInput = ThermalUnitInputTestData.thermalHouseInput

		when:
		def alteredUnit = thermalHouseInput.copy().ethLosses(ThermalUnitInputTestData.thermalConductance)
				.ethCapa(ThermalUnitInputTestData.ethCapa)
				.targetTemperature(ThermalUnitInputTestData.TARGET_TEMPERATURE)
				.upperTemperatureLimit(ThermalUnitInputTestData.UPPER_TEMPERATURE_LIMIT)
				.lowerTemperatureLimit(ThermalUnitInputTestData.LOWER_TEMPERATURE_LIMIT)
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
			assert targetTemperature == ThermalUnitInputTestData.TARGET_TEMPERATURE
			assert upperTemperatureLimit == ThermalUnitInputTestData.UPPER_TEMPERATURE_LIMIT
			assert lowerTemperatureLimit == ThermalUnitInputTestData.LOWER_TEMPERATURE_LIMIT
		}
	}

	def "The equals methods for a ThermalHouseInput works as expected"() {
		given:
		def thermalHouseInput1 = ThermalUnitInputTestData.thermalHouseInput
		def thermalHouseInput2 = ThermalUnitInputTestData.thermalHouseInput
		def thermalHouseInput3 = ThermalUnitInputTestData.thermalHouseInput.copy().
				ethLosses(Quantities.getQuantity(100, StandardUnits.THERMAL_TRANSMISSION))

		expect:
		thermalHouseInput1.equals(thermalHouseInput2)
		!thermalHouseInput1.equals(thermalHouseInput3)
	}
}
