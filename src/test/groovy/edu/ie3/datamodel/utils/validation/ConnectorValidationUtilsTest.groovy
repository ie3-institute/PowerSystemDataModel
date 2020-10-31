/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.test.common.GridTestData
import edu.ie3.util.TimeTools
import spock.lang.Specification

import java.time.ZoneId

class ConnectorValidationUtilsTest extends Specification {

	def "The check method in ValidationUtils delegates the check to ConnectorValidationUtils for a connector"() {
		given:
		def line = GridTestData.lineCtoD

		when:
		ValidationUtils.check(line)

		then:
		0 * ConnectorValidationUtils.check(line)
		// TODO NSteffan: Why is the method invoked 0 times?
	}
}