/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.test.common.GridTestData
import spock.lang.Specification

class NodeValidationUtilsTest extends Specification {

	def "The check method in ValidationUtils delegates the check to NodeValidationUtils for a node"() {
		given:
		def node = GridTestData.nodeB

		when:
		ValidationUtils.check(node)

		then:
		0 * NodeValidationUtils.check(node)
	}

	def "The check method in ValidationUtils recognizes a null object"() {
		when:
		ValidationUtils.check(null)

		then:
		ValidationException ex = thrown()
		ex.message == "Expected an object, but got nothing. :-("
	}

}
