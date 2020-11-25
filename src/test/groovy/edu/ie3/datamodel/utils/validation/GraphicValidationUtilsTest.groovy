/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.exceptions.InvalidEntityException
import edu.ie3.datamodel.exceptions.UnsafeEntityException
import edu.ie3.test.common.GridTestData
import spock.lang.Specification

class GraphicValidationUtilsTest extends Specification {

	def "Smoke Test: Correct graphic input throws no exception"() {
		given:
		def lineGraphicInput = GridTestData.lineGraphicCtoD
		def nodeGraphicInput = GridTestData.nodeGraphicC

		when:
		ValidationUtils.check(lineGraphicInput)
		then:
		noExceptionThrown()

		when:
		ValidationUtils.check(nodeGraphicInput)
		then:
		noExceptionThrown()
	}

	def "GraphicValidationUtils.check() recognizes all potential errors for a graphic input"() {
		when:
		GraphicValidationUtils.check(invalidGraphicInput)

		then:
		Exception ex = thrown()
		ex.class == expectedException.class
		ex.message == expectedException.message

		where:
		invalidGraphicInput                                                           || expectedException
		GridTestData.lineGraphicCtoD.copy().graphicLayer(null).build()                || new InvalidEntityException("Graphic Layer of graphic element is not defined", invalidGraphicInput)
	}

	def "GraphicValidationUtils.checkLineGraphicInput() recognizes all potential errors for a line graphic input"() {
		when:
		GraphicValidationUtils.check(invalidLineGraphicInput)

		then:
		Exception ex = thrown()
		ex.class == expectedException.class
		ex.message == expectedException.message

		where:
		invalidLineGraphicInput                                                           || expectedException
		GridTestData.lineGraphicCtoD.copy().path(null).build()                            || new InvalidEntityException("Path of line graphic element is not defined", invalidLineGraphicInput)
	}

	def "GraphicValidationUtils.checkNodeGraphicInput() recognizes all potential errors for a line graphic input"() {
		when:
		GraphicValidationUtils.check(invalidNodeGraphicInput)

		then:
		Exception ex = thrown()
		ex.class == expectedException.class
		ex.message == expectedException.message

		where:
		invalidNodeGraphicInput                                                         || expectedException
		GridTestData.nodeGraphicC.copy().point(null).build()                            || new InvalidEntityException("Point of node graphic is not defined", invalidNodeGraphicInput)
	}
}
