/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.exceptions

import spock.lang.Specification

/**
 * //ToDo: remove this test!
 *
 * @version 0.1* @since 27.10.20
 */
class FactoryExceptionTest extends Specification {

	def "A FactoryException should always work"() {
		expect:
		new FactoryException(new NullPointerException())
	}

	def "blafo"(){
		expect:
		println("test")
	}
}
