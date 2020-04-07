/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.extractor

import edu.ie3.datamodel.exceptions.ExtractorException
import edu.ie3.test.common.GridTestData as gtd
import edu.ie3.test.common.SystemParticipantTestData as sptd
import edu.ie3.test.common.ThermalUnitInputTestData as tutd
import edu.ie3.util.TimeTools
import spock.lang.Specification

import java.time.ZoneId


class ExtractorTest extends Specification {

	private final class InvalidNestedExtensionClass implements NestedEntity {}

	static {
		TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
	}

	def "An Extractor should be able to extract an entity with nested elements correctly"() {

		expect:
		Extractor.extractElements(nestedEntity) == expectedExtractedEntities

		where:
		nestedEntity               || expectedExtractedEntities
		gtd.lineCtoD               || [
			gtd.lineCtoD.nodeA,
			gtd.lineCtoD.nodeB,
			gtd.lineCtoD.type,
			gtd.lineCtoD.operator
		]
		gtd.transformerAtoBtoC     || [
			gtd.transformerAtoBtoC.nodeA,
			gtd.transformerAtoBtoC.nodeB,
			gtd.transformerAtoBtoC.nodeC,
			gtd.transformerAtoBtoC.type,
			gtd.transformerAtoBtoC.operator
		]
		gtd.transformerCtoG        || [
			gtd.transformerCtoG.nodeA,
			gtd.transformerCtoG.nodeB,
			gtd.transformerCtoG.type,
			gtd.transformerCtoG.operator
		]
		gtd.switchAtoB             || [
			gtd.switchAtoB.nodeA,
			gtd.switchAtoB.nodeB,
			gtd.switchAtoB.operator
		]
		sptd.fixedFeedInInput      || [
			sptd.fixedFeedInInput.node,
			sptd.fixedFeedInInput.operator
		]
		sptd.wecInput              || [
			sptd.wecInput.node,
			sptd.wecInput.type,
			sptd.wecInput.operator
		]
		sptd.chpInput              || [
			sptd.chpInput.node,
			sptd.chpInput.type,
			sptd.chpInput.operator
		]
		sptd.bmInput               || [
			sptd.bmInput.node,
			sptd.bmInput.type,
			sptd.bmInput.operator
		]
		sptd.evInput               || [
			sptd.evInput.node,
			sptd.evInput.type,
			sptd.evInput.operator
		]
		sptd.storageInput          || [
			sptd.storageInput.node,
			sptd.storageInput.type,
			sptd.storageInput.operator
		]
		sptd.hpInput               || [
			sptd.hpInput.node,
			sptd.hpInput.type,
			sptd.hpInput.operator
		]

		gtd.lineGraphicCtoD        || [gtd.lineGraphicCtoD.line]

		gtd.nodeGraphicC           || [gtd.nodeGraphicC.node]

		gtd.measurementUnitInput   || [
			gtd.measurementUnitInput.node,
			gtd.measurementUnitInput.operator
		]

		tutd.thermalBusInput       || [
			tutd.thermalBusInput.operator
		]

		tutd.cylindricStorageInput || [
			tutd.cylindricStorageInput.operator,
			tutd.cylindricStorageInput.thermalBus
		]

		tutd.thermalHouseInput     || [
			tutd.thermalHouseInput.operator,
			tutd.thermalHouseInput.thermalBus
		]
	}

	def "An Extractor should throw an ExtractorException if the provided Nested entity is unknown and or an invalid extension of the 'Nested' interface took place"() {
		when:
		Extractor.extractElements(new InvalidNestedExtensionClass())

		then:
		ExtractorException ex = thrown()
		ex.message == "Unable to extract entity of class 'InvalidNestedExtensionClass'. " +
				"Does this class implements NestedEntity and one of its sub-interfaces correctly?"
	}
}
