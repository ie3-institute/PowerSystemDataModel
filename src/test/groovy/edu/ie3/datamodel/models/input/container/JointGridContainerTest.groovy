/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.container

import edu.ie3.test.common.GridTestData
import spock.lang.Specification

class JointGridContainerTest extends Specification {
	public static final gridName = "single_grid"

	private static final RawGridElements rawGrid = new RawGridElements(
	[GridTestData.nodeA] as Set,
	[] as Set,
	[] as Set,
	[] as Set,
	[] as Set,
	[] as Set)

	private static final SystemParticipants systemParticipants = new SystemParticipants(
	[] as Set,
	[] as Set,
	[] as Set,
	[] as Set,
	[] as Set,
	[] as Set,
	[] as Set,
	[] as Set,
	[] as Set,
	[] as Set)

	private static final GraphicElements graphicElements = new GraphicElements(
	[] as Set,
	[] as Set)

	def "A single subgrid can be used to build a JointGridContainer"() {
		when:
		def jointGridContainer = new JointGridContainer(gridName, rawGrid, systemParticipants, graphicElements)

		then:
		noExceptionThrown()
		jointGridContainer.subGridTopologyGraph.vertexSet().size() == 1
	}
}
