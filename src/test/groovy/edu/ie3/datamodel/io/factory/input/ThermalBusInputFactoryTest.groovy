/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class ThermalBusInputFactoryTest extends Specification implements FactoryTestHelper {
	def "A ThermalBusInputFactory should contain exactly the expected class for parsing"() {
		given:
		def inputFactory = new ThermalBusInputFactory()
		def expectedClasses = [ThermalBusInput]

		expect:
		inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
	}

	def "A ThermalBusInputFactory should parse a valid SwitchInput correctly"() {
		given: "a system participant input type factory and model data"
		def inputFactory = new ThermalBusInputFactory()
		Map<String, String> parameter = [
			"uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"operatesfrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
			"operatesuntil": "",
			"id"           : "TestID"
		]
		def inputClass = ThermalBusInput
		def operatorInput = Mock(OperatorInput)

		when:
		Optional<ThermalBusInput> input = inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass, operatorInput))

		then:
		input.present
		input.get().getClass() == inputClass
		((ThermalBusInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert operationTime.startDate.present
			assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
			assert !operationTime.endDate.present
			assert operator == operatorInput
			assert id == parameter["id"]
		}
	}
}
