/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.util

import spock.lang.Specification

class StringUtilsTest extends Specification {

	def "The StringUtils quote a single String correctly"() {
		when:
		String actual = StringUtils.quote(input)

		then:
		actual == expected

		where:
		input 		|| expected
		"test" 		|| "\"test\""
		"\"test" 	|| "\"test\""
		"test\"" 	|| "\"test\""
		"\"test\""	|| "\"test\""
	}

	def "The StringUtils transform a given headline string array correctly"() {
		given:
		String[] input = [
			"inputModel",
			"iAMag",
			"timestamp",
			"p",
			"nodeC",
			"tapPos",
			"noOfParallelDevices",
			"kWd",
			"mySa",
			"sRated",
			"xScA",
			"sRatedB"] as String[]
		String[] expected = [
			"\"inputModel\"",
			"\"iAMag\"",
			"\"timestamp\"",
			"\"p\"",
			"\"nodeC\"",
			"\"tapPos\"",
			"\"noOfParallelDevices\"",
			"\"kWd\"",
			"\"mySa\"",
			"\"sRated\"",
			"\"xScA\"",
			"\"sRatedB\""] as String[]

		when:
		String[] actual = StringUtils.quote(input)

		then:
		actual == expected
	}

	def "The StringUtils convert a given camel case correctly to snake case"() {
		where:
		input 		|| expected
		"helloDude"	|| "hello_dude"
		"came2win"	|| "came_2_win"
		"2be"		|| "2_be"
		"2Be"		|| "2_be"
		"orBe2"		|| "or_be_2"
		"orBE2"		|| "or_be_2"
	}

	def "The StringUtils convert a given Array of camel case Strings correctly to snake case"() {
		given:
		String[] input = [
			"inputModel",
			"iAMag",
			"timestamp",
			"p",
			"nodeC",
			"tapPos",
			"noOfParallelDevices",
			"kWd",
			"mySa",
			"sRated",
			"xScA",
			"sRatedB"] as String[]
		String[] expected = [
			"input_model",
			"i_a_mag",
			"timestamp",
			"p",
			"node_c",
			"tap_pos",
			"no_of_parallel_devices",
			"k_wd",
			"my_sa",
			"s_rated",
			"x_sc_a",
			"s_rated_b"] as String[]

		when:
		String[] actual = StringUtils.camelCaseToSnakeCase(input)

		then:
		actual == expected
	}

	def "The StringUtils are capable of cleaning up strings correctly"() {
		when:
		String actual = StringUtils.cleanString(input)

		then:
		actual == expected

		where:
		input 		|| expected
		"ab123" 	|| "ab123"
		"ab.123" 	|| "ab_123"
		"ab-123" 	|| "ab_123"
		"ab_123" 	|| "ab_123"
		"ab/123" 	|| "ab_123"
		"ab\\123" 	|| "ab_123"
		"ab!123" 	|| "ab_123"
		"ab\"123" 	|| "ab_123"
		"ab§123" 	|| "ab_123"
		"ab\$123" 	|| "ab_123"
		"ab&123" 	|| "ab_123"
		"ab{123" 	|| "ab_123"
		"ab[123" 	|| "ab_123"
		"ab}123" 	|| "ab_123"
		"ab]123" 	|| "ab_123"
		"ab(123" 	|| "ab_123"
		"ab)123" 	|| "ab_123"
		"ab=123" 	|| "ab_123"
		"ab?123" 	|| "ab_123"
		"abß123" 	|| "ab_123"
		"ab123." 	|| "ab123_"
		"ab123-" 	|| "ab123_"
		"ab123_" 	|| "ab123_"
		"ab123/" 	|| "ab123_"
		"ab123\\" 	|| "ab123_"
		"ab123!" 	|| "ab123_"
		"ab123\"" 	|| "ab123_"
		"ab123§" 	|| "ab123_"
		"ab123\$" 	|| "ab123_"
		"ab123&" 	|| "ab123_"
		"ab123{" 	|| "ab123_"
		"ab123[" 	|| "ab123_"
		"ab123}" 	|| "ab123_"
		"ab123]" 	|| "ab123_"
		"ab123(" 	|| "ab123_"
		"ab123)" 	|| "ab123_"
		"ab123=" 	|| "ab123_"
		"ab123?" 	|| "ab123_"
		"ab123ß" 	|| "ab123_"
		".ab123" 	|| "_ab123"
		"-ab123" 	|| "_ab123"
		"_ab123" 	|| "_ab123"
		"/ab123" 	|| "_ab123"
		"\\ab123" 	|| "_ab123"
		"!ab123" 	|| "_ab123"
		"\"ab123" 	|| "_ab123"
		"§ab123" 	|| "_ab123"
		"\$ab123"	|| "_ab123"
		"&ab123" 	|| "_ab123"
		"{ab123" 	|| "_ab123"
		"[ab123" 	|| "_ab123"
		"}ab123" 	|| "_ab123"
		"]ab123" 	|| "_ab123"
		"(ab123" 	|| "_ab123"
		")ab123" 	|| "_ab123"
		"=ab123" 	|| "_ab123"
		"?ab123" 	|| "_ab123"
		"ßab123" 	|| "_ab123"
	}
}
