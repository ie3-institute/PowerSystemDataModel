/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.container

import edu.ie3.test.common.ComplexTopology
import spock.lang.Specification


class RawGridElementsTest extends Specification {

	def "A valid collection of asset entities can be used to build a valid instance of RawGridElements"() {
		given:
		def rawGrid = ComplexTopology.grid.rawGrid

		when:
		def newlyCreatedRawGrid = new RawGridElements(rawGrid.allEntitiesAsList())

		then:
		newlyCreatedRawGrid == rawGrid
	}
}
