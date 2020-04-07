/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.system.characteristic

import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicCoordinate
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicInput
import spock.lang.Specification

import java.util.regex.Pattern

class CharacteristicInputTest extends Specification {
	def "The CharacteristicInput is able to build the correct matching pattern"() {
		given: "A prefix and an expected pattern"
		String prefix = "test"
		String expected = "test:\\{((" + CharacteristicCoordinate.MATCHING_PATTERN + ",?)+)}"

		when: "querying the matching pattern"
		Pattern actual = CharacteristicInput.buildMatchingPattern(prefix)

		then: "it returns the correct pattern"
		actual.pattern == expected
	}
}
