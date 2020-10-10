/*
 * © 2020. TU Dortmund University,
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
		//TEST VON NSTEFFAN TODO: Test löschen
		System.out.println(node.equals(alteredUnit))
		System.out.println(node.getGeoPosition())
		System.out.println(node.getVoltLvl())
		System.out.println(node.getvTarget())
	}

	/*
	 def "Validation of values when creating an instance of NodeInput should work as expected"() {
	 given:
	 def node = GridTestData.nodeB.copy().subnet(1)
	 .build()
	 when:
	 node.validate()
	 then:
	 InvalidEntityException name = thrown()
	 System.out.println()
	 }
	 */
}
