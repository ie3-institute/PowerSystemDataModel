/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.result

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.SimpleEntityData
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.test.helper.FactoryTestHelper
import edu.ie3.util.TimeTools
import spock.lang.Specification

class NodeResultFactoryTest extends Specification implements FactoryTestHelper {

	def "A NodeResultFactory should contain all expected classes for parsing"() {
		given:
		def resultFactory = new NodeResultFactory()
		def expectedClasses = [NodeResult]

		expect:
		resultFactory.classes() == Arrays.asList(expectedClasses.toArray())
	}

	def "A NodeResultFactory should parse a WecResult correctly"() {
		given: "a system participant factory and model data"
		def resultFactory = new NodeResultFactory()
		Map<String, String> parameter = [
			"timestamp" : "2020-01-30 17:26:44",
			"inputModel": "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7",
			"vmag"      : "2",
			"vang"      : "2"
		]

		when:
		Optional<? extends NodeResult> result = resultFactory.getEntity(new SimpleEntityData(parameter, NodeResult))

		then:
		result.present
		result.get().getClass() == NodeResult
		((NodeResult) result.get()).with {
			assert vMag == getQuant(parameter["vmag"], StandardUnits.VOLTAGE_MAGNITUDE)
			assert vAng == getQuant(parameter["vang"], StandardUnits.VOLTAGE_ANGLE)
			assert timestamp == TimeTools.toZonedDateTime(parameter["timestamp"])
			assert inputModel == UUID.fromString(parameter["inputModel"])
		}
	}

	def "A NodeResultFactory should throw an exception on invalid or incomplete data"() {
		given: "a system participant factory and model data"
		def resultFactory = new NodeResultFactory()
		Map<String, String> parameter = [
			"timestamp" : "2020-01-30 17:26:44",
			"inputModel": "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7",
			"vmag"      : "2"
		]

		when:
		resultFactory.getEntity(new SimpleEntityData(parameter, NodeResult))

		then:
		FactoryException ex = thrown()
		ex.message == "The provided fields [inputModel, timestamp, vmag] with data {inputModel -> 91ec3bcf-1897-4d38-af67-0bf7c9fa73c7,timestamp -> 2020-01-30 17:26:44,vmag -> 2} are invalid for instance of NodeResult. \n" +
				"The following fields to be passed to a constructor of 'NodeResult' are possible (NOT case-sensitive!):\n" +
				"0: [inputModel, timestamp, vang, vmag]\n" +
				"1: [inputModel, timestamp, uuid, vang, vmag]\n"
	}
}
