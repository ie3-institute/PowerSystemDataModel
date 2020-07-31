/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.container

import edu.ie3.datamodel.models.input.connector.LineInput

import static edu.ie3.test.common.GridTestData.*
import edu.ie3.datamodel.models.input.InputEntity
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.ChpInput
import edu.ie3.datamodel.models.input.system.type.ChpTypeInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput
import spock.lang.Shared
import spock.lang.Specification

class GridContainerTest extends Specification {

	@Shared
	GridContainer gridContainer

	@Shared
	List<InputEntity> expectedElements

	def setupSpec() {
		def gridName = "grid_container_test"

		def line = Mock(LineInput)
		line.getUuid() >> UUID.randomUUID()
		line.getType() >> lineTypeInputCtoD
		line.getNodeA() >> nodeC
		line.getNodeB() >> nodeC

		def rawGrid = new RawGridElements(
				[
					nodeC,
					nodeE
				] as Set,
				[line
				] as Set,
				[transformerCtoE
				] as Set,
				[] as Set,
				[] as Set,
				[] as Set
				)

		def operator = Mock(OperatorInput)
		def thermalBus = Mock(ThermalBusInput)
		def thermalStorage = Mock(ThermalStorageInput)
		def chpType = Mock(ChpTypeInput)
		def chp = Mock(ChpInput)
		chp.getUuid() >> UUID.randomUUID()
		chp.getNode() >> nodeC
		chp.getOperator() >> operator
		chp.getThermalBus() >> thermalBus
		chp.getThermalStorage() >> thermalStorage
		chp.getType() >> chpType

		def systemParticipants = new SystemParticipants(
				[] as Set,
				[chp
				] as Set,
				[] as Set,
				[] as Set,
				[] as Set,
				[] as Set,
				[] as Set,
				[] as Set,
				[] as Set,
				[] as Set
				)

		gridContainer = new SubGridContainer(
				gridName,
				1,
				rawGrid,
				systemParticipants,
				new GraphicElements([] as Set, [] as Set)
				)

		expectedElements = [
			lineTypeInputCtoD,
			transformerCtoE.type,
			chpType,
			transformerCtoE.operator,
			operator,
			thermalBus,
			thermalStorage,
			nodeC,
			nodeE,
			line,
			transformerCtoE,
			chp
		]
	}

	def "Flattening a GridContainer returns the correct elements" () {
		when:
		def actual = gridContainer.flatten()

		then:
		actual == expectedElements
	}
}
