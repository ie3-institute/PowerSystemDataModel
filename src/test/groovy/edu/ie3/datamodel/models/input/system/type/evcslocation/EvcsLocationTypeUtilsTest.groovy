/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system.type.evcslocation

import edu.ie3.datamodel.exceptions.ParsingException
import spock.lang.Specification

import static edu.ie3.datamodel.models.input.system.type.evcslocation.EvcsLocationType.*
import static edu.ie3.datamodel.models.input.system.type.evcslocation.EvcsLocationTypeUtils.*

class EvcsLocationTypeUtilsTest extends Specification {

	def "The EvcsLocationTypeUtils should throw an exception on instantiation"() {
		when:
		new EvcsLocationTypeUtils()

		then:
		IllegalStateException ex = thrown()
		ex.message == "This is a factory class. Don't try to instantiate it."
	}

	def "The EvcsLocationTypeUtils should parse valid evcs location type strings as expected"() {
		given:
		EvcsLocationType parsed = parse(parsableString)

		expect:
		parsed == expectedObj
		parsed.name() == parsableString

		where:
		parsableString           || expectedObj
		"HOME"                   || HOME
		"WORK"                   || WORK
		"CUSTOMER_PARKING"       || CUSTOMER_PARKING
		"STREET"                 || STREET
		"CHARGING_HUB_TOWN"      || CHARGING_HUB_TOWN
		"CHARGING_HUB_HIGHWAY"   || CHARGING_HUB_HIGHWAY
	}

	def "The EvcsLocationTypeUtils should throw exceptions as expected when invalid evcs location type string is provided"() {
		when:
		parse(invalidString)

		then:
		ParsingException ex = thrown()
		ex.message == expectedExceptionMsg

		where:

		invalidString       || expectedExceptionMsg
		"-- invalid --"     || "EvcsLocationType '-- invalid --' does not exist."
	}
}
