/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input

import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.test.common.GridTestData
import spock.lang.Specification


class NodeInputTest extends Specification {

	def "A NodeInput copy method should work as expected"() {
		given:
		def node = GridTestData.nodeB

		when:
		def alteredUnit = node.copy().id("node_B_copy").slack(true).operator(GridTestData.profBroccoli).subnet(1)
				.voltLvl(GermanVoltageLevelUtils.EHV_220KV).build()

		then:
		alteredUnit.with {
			assert uuid == node.uuid
			assert operationTime == node.operationTime
			assert operator == GridTestData.profBroccoli
			assert id == "node_B_copy"
			assert vTarget == node.getvTarget()
			assert slack
			assert subnet == 1
			assert voltLvl == GermanVoltageLevelUtils.EHV_220KV
		}
	}
}
