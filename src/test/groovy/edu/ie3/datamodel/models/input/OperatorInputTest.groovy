/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input

import edu.ie3.test.common.GridTestData
import spock.lang.Specification


class OperatorInputTest extends Specification {

	def "An OperatorInput copy method should work as expected"() {
		given:
		def operator = GridTestData.profBroccoli

		when:
		def alteredUuid = UUID.randomUUID()
		def alteredUnit = operator.copy().uuid(alteredUuid).id("Univ.-Prof. Dr.-Ing. Christian Rehtanz").build()

		then:
		alteredUnit.with {
			assert uuid == alteredUuid
			assert id == "Univ.-Prof. Dr.-Ing. Christian Rehtanz"
		}
	}
}
