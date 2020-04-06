/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import edu.ie3.test.common.GridTestData
import spock.lang.Specification

class ValidationUtilsTest extends Specification {

	def "The validation utils should determine if a collection with UniqueEntity's is distinct by their uuid"() {
		expect:
		ValidationUtils.distinctUuids(collection) == distinct

		where:
		collection                         || distinct
		[
			GridTestData.nodeF,
			GridTestData.nodeG] as Set || false
		[
			GridTestData.nodeD,
			GridTestData.nodeE] as Set || true
		[] as Set                          || true
	}
}
