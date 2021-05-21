/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.Transformer3WInput
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class Transformer3WInputFactoryTest  extends Specification implements FactoryTestHelper {
	def "A Transformer3WInputFactory should contain exactly the expected class for parsing"() {
		given:
		def inputFactory = new Transformer3WInputFactory()
		def expectedClasses = [Transformer3WInput]

		expect:
		inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
	}

	def "A Transformer3WInputFactory should parse a valid Transformer3WInput correctly"() {
		given: "a system participant input type factory and model data"
		def inputFactory = new Transformer3WInputFactory()
		Map<String, String> parameter = [
			"uuid"           : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"id"             : "TestID",
			"paralleldevices": "2",
			"tappos"         : "3",
			"autotap"        : "true"
		]
		def inputClass = Transformer3WInput
		def nodeInputA = Mock(NodeInput)
		def nodeInputB = Mock(NodeInput)
		def nodeInputC = Mock(NodeInput)
		def typeInput = Mock(Transformer3WTypeInput)

		when:
		Optional<Transformer3WInput> input = inputFactory.get(new Transformer3WInputEntityData(parameter, inputClass, nodeInputA, nodeInputB, nodeInputC, typeInput))

		then:
		input.present
		input.get().getClass() == inputClass
		((Transformer3WInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert operationTime == OperationTime.notLimited()
			assert operator == OperatorInput.NO_OPERATOR_ASSIGNED
			assert id == parameter["id"]
			assert nodeA == nodeInputA
			assert nodeB == nodeInputB
			assert nodeC == nodeInputC
			assert type == typeInput
			assert parallelDevices == Integer.parseInt(parameter["paralleldevices"])
			assert tapPos == Integer.parseInt(parameter["tappos"])
			assert autoTap
		}
	}
}
