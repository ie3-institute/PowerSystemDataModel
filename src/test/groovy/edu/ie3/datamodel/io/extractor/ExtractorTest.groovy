/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.extractor

import edu.ie3.datamodel.exceptions.ExtractorException
import edu.ie3.test.common.GridTestData as gtd
import edu.ie3.test.common.SystemParticipantTestData as sptd
import edu.ie3.util.TimeTools
import spock.lang.Specification

import java.time.ZoneId


// todo JH more tests for all possible classes
class ExtractorTest extends Specification {

	private final class InvalidNestedExtensionClass implements NestedEntity {}

	static {
		TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
	}

	def "An Extractor should be able to extract an entity with nested elements correctly"() {

		expect:
		Extractor.extractElements(nestedEntity) == expectedExtractedEntities

		where:
		nestedEntity           || expectedExtractedEntities
		gtd.lineCtoD           || [
			gtd.lineCtoD.nodeA,
			gtd.lineCtoD.nodeB,
			gtd.lineCtoD.type
		]
		gtd.transformerAtoBtoC || [
			gtd.transformerAtoBtoC.nodeA,
			gtd.transformerAtoBtoC.nodeB,
			gtd.transformerAtoBtoC.nodeC,
			gtd.transformerAtoBtoC.type
		]
		gtd.transformerCtoG    || [
			gtd.transformerCtoG.nodeA,
			gtd.transformerCtoG.nodeB,
			gtd.transformerCtoG.type
		]
		gtd.switchAtoB         || [
			gtd.switchAtoB.nodeA,
			gtd.switchAtoB.nodeB
		]
		sptd.fixedFeedInInput  || [sptd.fixedFeedInInput.node]
		sptd.wecInput          || [
			sptd.wecInput.node,
			sptd.wecInput.type
		]
		sptd.chpInput          || [
			sptd.chpInput.node,
			sptd.chpInput.type
		]
		sptd.bmInput           || [
			sptd.bmInput.node,
			sptd.bmInput.type
		]
		sptd.evInput           || [
			sptd.evInput.node,
			sptd.evInput.type
		]
		sptd.storageInput      || [
			sptd.storageInput.node,
			sptd.storageInput.type
		]
		sptd.hpInput           || [
			sptd.hpInput.node,
			sptd.hpInput.type
		]

		// todo test for graphic input
		// todo test for thermal input
	}

	def "An Extractor should throw an ExtractorException if the provided Nested entity is unknown and or an invalid extension of the 'Nested' interface took place"() {
		when:
		Extractor.extractElements(new InvalidNestedExtensionClass())

		then:
		ExtractorException ex = thrown()
		ex.message == "The interface 'Nested' is not meant to be extended. The provided entity of class " +
				"'InvalidNestedExtensionClass' and cannot be processed by the extractor! Currently only the interfaces " +
				"'Node', 'NodeC', ‘Nodes‘ and ‘Type' are supported!"
	}
}
