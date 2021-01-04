/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.graphics

import edu.ie3.test.common.GridTestData
import spock.lang.Specification


class LineGraphicInputTest extends Specification {

	def "A LineGraphicInput copy method should work as expected"() {
		given:
		def lineGraphic = GridTestData.lineGraphicCtoD

		when:
		def alteredUnit = lineGraphic.copy().line(GridTestData.lineAtoB).graphicLayer("second").build()


		then:
		alteredUnit.with {
			assert uuid == lineGraphic.uuid
			assert graphicLayer == "second"
			assert path == lineGraphic.path
			assert line == GridTestData.lineAtoB
		}
	}
}
