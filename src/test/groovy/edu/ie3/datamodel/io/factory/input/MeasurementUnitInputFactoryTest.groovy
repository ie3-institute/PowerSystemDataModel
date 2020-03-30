/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.MeasurementUnitInput
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class MeasurementUnitInputFactoryTest extends Specification implements FactoryTestHelper {
	def "A MeasurementUnitInputFactory should contain exactly the expected class for parsing"() {
		given:
		def inputFactory = new MeasurementUnitInputFactory()
		def expectedClasses = [MeasurementUnitInput]

		expect:
		inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
	}

	def "A MeasurementUnitInputFactory should parse a valid MeasurementUnitInput correctly"() {
		given: "a system participant input type factory and model data"
		def inputFactory = new MeasurementUnitInputFactory()
		Map<String, String> parameter = [
			"uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"id"           : "TestID",
			"vmag"         : "true",
			"vang"         : "false",
			"p"            : "true",
			"q"            : "true"
		]
		def inputClass = MeasurementUnitInput
		def nodeInput = Mock(NodeInput)

		when:
		Optional<MeasurementUnitInput> input = inputFactory.getEntity(new MeasurementUnitInputEntityData(parameter, inputClass, nodeInput))

		then:
		input.present
		input.get().getClass() == inputClass
		((MeasurementUnitInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert operationTime == OperationTime.notLimited()
			assert operator == null
			assert id == parameter["id"]
			assert node == nodeInput
			assert VMag
			assert !VAng
			assert p
			assert q
		}
	}
}
