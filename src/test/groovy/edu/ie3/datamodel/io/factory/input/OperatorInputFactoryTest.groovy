/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.io.factory.SimpleEntityData
import edu.ie3.datamodel.models.input.OperatorInput
import spock.lang.Specification

class OperatorInputFactoryTest extends Specification {

	def "An OperatorInputFactory should contain exactly the expected class for parsing"() {
		given:
		def inputFactory = new OperatorInputFactory()
		def expectedClasses = [OperatorInput]

		expect:
		inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
	}

	def "An OperatorInputFactory should parse a valid OperatorInput correctly"() {
		given: "a operator input factory and model data"
		def inputFactory = new OperatorInputFactory()
		Map<String, String> parameter = [
			"uuid": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"id"  : "TestOperatorId",
		]

		def inputClass = OperatorInput

		when:
		Optional<OperatorInput> input = inputFactory.getEntity(new SimpleEntityData(parameter, inputClass))

		then:
		input.present
		input.get().getClass() == inputClass
		((OperatorInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert id == parameter["id"]
		}
	}
}
