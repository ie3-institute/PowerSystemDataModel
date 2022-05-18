/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.container

import static edu.ie3.test.common.SystemParticipantTestData.getEmptySystemParticipants
import edu.ie3.test.common.GridTestData
import spock.lang.Specification


class JointGridContainerTest extends Specification {
	private static final GRID_NAME = "single_grid"

	private static final RawGridElements RAW_GRID = new RawGridElements(
	[GridTestData.nodeA] as Set,
	[] as Set,
	[] as Set,
	[] as Set,
	[] as Set,
	[] as Set)


	private static final GraphicElements GRAPHIC_ELEMENTS = new GraphicElements(
	[] as Set,
	[] as Set)

	def "A single subgrid can be used to build a JointGridContainer"() {
		when:
		def jointGridContainer = new JointGridContainer(GRID_NAME, RAW_GRID, emptySystemParticipants, GRAPHIC_ELEMENTS)

		then:
		noExceptionThrown()
		jointGridContainer.subGridTopologyGraph.vertexSet().size() == 1
	}
}
