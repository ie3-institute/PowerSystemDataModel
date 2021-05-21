/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.result

import edu.ie3.datamodel.io.factory.SimpleEntityData
import edu.ie3.datamodel.models.result.connector.SwitchResult
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification


class SwitchResultFactoryTest extends Specification implements FactoryTestHelper {


	def "A SwitchResultFactory should contain all expected classes for parsing"() {
		given:
		def resultFactory = new SwitchResultFactory()
		def expectedClasses = [SwitchResult]

		expect:
		resultFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
	}

	def "A SwitchResultFactory should parse a valid result model correctly"() {
		given: "a switch result factory and model data"
		def resultFactory = new SwitchResultFactory()
		Map<String, String> parameter = [
			"time"      : "2020-01-30 17:26:44",
			"inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"closed"    : "true"
		]

		when:
		Optional<SwitchResult> result = resultFactory.get(new SimpleEntityData(parameter, SwitchResult))

		then:
		result.present
		result.get().getClass() == SwitchResult
		((SwitchResult) result.get()).with {
			assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
			assert inputModel == UUID.fromString(parameter["inputModel"])
			assert closed == Boolean.parseBoolean(parameter["closed"])
		}
	}
}
