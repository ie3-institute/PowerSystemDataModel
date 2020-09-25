/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input

import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.test.common.GridTestData
import edu.ie3.util.quantities.EmptyQuantity
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities


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

	def "A NodeInput equals method should work as expected"() {

		when:
		nodeA
		nodeB

		then:
		(nodeA == (nodeB)) == expectedResult

		where:
		nodeA                                                                            | nodeB                                                                                            || expectedResult
		GridTestData.nodeB                                                               | GridTestData.nodeB                                                                               || true
		GridTestData.nodeB                                                               | GridTestData.nodeB.copy().build()                                                                || true
		GridTestData.nodeB                                                               | GridTestData.nodeB.copy().vTarget(Quantities.getQuantity(1, PowerSystemUnits.PU)).build()        || true
		GridTestData.nodeB                                                               | GridTestData.nodeB.copy().vTarget(Quantities.getQuantity(100, PowerSystemUnits.PERCENT)).build() || true
		GridTestData.nodeB                                                               | GridTestData.nodeB.copy().vTarget(Quantities.getQuantity(2, PowerSystemUnits.PU)).build()        || false
		GridTestData.nodeB                                                               | GridTestData.nodeB.copy().vTarget(EmptyQuantity.of(PowerSystemUnits.PU)).build()                 || false
		GridTestData.nodeB.copy().vTarget(EmptyQuantity.of(PowerSystemUnits.PU)).build() | GridTestData.nodeB                                                                               || false
		GridTestData.nodeB.copy().vTarget(EmptyQuantity.of(PowerSystemUnits.PU)).build() | GridTestData.nodeB.copy().vTarget(EmptyQuantity.of(PowerSystemUnits.PU)).build()                 || true
	}
}
