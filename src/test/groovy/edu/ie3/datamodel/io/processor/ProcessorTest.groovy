/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.processor

import spock.lang.Specification

class ProcessorTest extends Specification {
	def "The Processor is able to convert camel case to snake case correctly"() {
		when:
		String actual = Processor.camel2snakeCase(input)

		then:
		actual == expected

		where:
		input           || expected
		"is_correct"    || "is_correct"
		"isCorrect"     || "is_correct"
		"b2b"           || "b_2_b"
		"2b"            || "2_b"
		"2B"            || "2_b"
		"b2"            || "b_2"
		"B2"            || "b_2"
	}
}
