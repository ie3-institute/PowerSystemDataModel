/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input.participant

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.WecInput
import edu.ie3.datamodel.models.input.system.type.WecTypeInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class WecInputFactoryTest extends Specification implements FactoryTestHelper {
	def "A WecInputFactoryTest should contain exactly the expected class for parsing"() {
		given:
		def inputFactory = new WecInputFactory()
		def expectedClasses = [WecInput]

		expect:
		inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
	}

	def "A WecInputFactory should parse a valid WecInput correctly"() {
		given: "a system participant input type factory and model data"
		def inputFactory = new WecInputFactory()
		Map<String, String> parameter = [
			"uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"operatesfrom"    : "",
			"operatesuntil"   : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
			"id"              : "TestID",
			"qcharacteristics": "cosphi_fixed:1",
			"marketreaction"  : "true"
		]
		def inputClass = WecInput
		def nodeInput = Mock(NodeInput)
		def operatorInput = Mock(OperatorInput)
		def typeInput = Mock(WecTypeInput)

		when:
		Optional<WecInput> input = inputFactory.getEntity(
				new SystemParticipantTypedEntityData<WecTypeInput>(parameter, inputClass, operatorInput, nodeInput, typeInput))

		then:
		input.present
		input.get().getClass() == inputClass
		((WecInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert !operationTime.startDate.present
			assert operationTime.endDate.present
			assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
			assert operator == operatorInput
			assert id == parameter["id"]
			assert node == nodeInput
			assert qCharacteristics == parameter["qcharacteristics"]
			assert type == typeInput
			assert marketReaction
		}
	}
}
